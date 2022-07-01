package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.CleanRawData;
import com.projectfloyd.Q1043.models.RawData;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.repo.CleanRawDAO;
import com.projectfloyd.Q1043.repo.RawDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class RawDataService {

    //One Repository each for messy and clean raw data
    private RawDAO rawDAO;
    private CleanRawDAO cleanRawDAO;
    private SongService songService;

    @Autowired
    public RawDataService(RawDAO rawDAO, CleanRawDAO cleanRawDAO, SongService songService) {
        this.rawDAO = rawDAO;
        this.cleanRawDAO = cleanRawDAO;
        this.songService = songService;
    }

    public boolean addRawData(RawData rawData) {
        try {
            rawDAO.save(rawData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addCleanRawData(CleanRawData cleanRawData) {
        try {
            cleanRawDAO.save(cleanRawData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public RawData getRawDataById(int id) {
        return rawDAO.findById(id).orElse(null);
    }
    public CleanRawData getCleanRawDataById(int id) {
        return cleanRawDAO.findById(id).orElse(null);
    }

    public Page<RawData> getPaginatedRawData(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return rawDAO.findAll(pageable);
    }

    public List<RawData> getaRawData(int pageNumber, int pageSize) {
        //After some debugging I realized that PostgreSQL doesn't maintain the order of rows as it utilizes heap
        //memory to store things. Because of this, when I make an update to the raw data it's physically
        //moving to a different spot in the table and thus can by called again by a different pagination call.
        //Instead of calling with standard pagination I need to look at the pagenumber and size and then
        //manually get the id's I need from that.

        ArrayList<RawData> rawData = new ArrayList<>(pageSize);
        int start = pageNumber * pageSize + 1; //Ids in the database aren't zero indexed so we add 1 here
        for (int i = start; i < start + pageSize; i++ ) {
            rawData.add(rawDAO.findById(i).orElse(null));
        }
        return rawData;
    }

    public List<CleanRawData> getCleanRawData(int pageNumber, int pageSize) {
        //same as the above function, but for the data that was cleaned up by hand.
        ArrayList<CleanRawData> cleanRawData = new ArrayList<>(pageSize);
        int start = pageNumber * pageSize + 1; //Ids in the database aren't zero indexed so we add 1 here
        for (int i = start; i < start + pageSize; i++ ) {
            cleanRawData.add(cleanRawDAO.findById(i).orElse(null));
        }
        return cleanRawData;
    }

    public List<CleanRawData> getCleanRawDataWithoutId(int startId, int pageSize) {
        //same as the above function, but for data we haven't already discovered albums for. We fill our data array
        //until it reaches the page size, or until we hit the end of the data base.
        if (startId == 0) startId++; //data isn't 0 indexed so a 0 here will cause us to break out of function.
        ArrayList<CleanRawData> cleanRawData = new ArrayList<>(pageSize);
        boolean endOfDBReached = false;
        while (cleanRawData.size() < pageSize) {
            CleanRawData crd = cleanRawDAO.findById(startId++).orElse(null);
            if (crd == null) {
                endOfDBReached = true;
                break;
            }
            String songName = crd.getRawData().split("" + (char)9)[1]; //get the song name from raw data
            String artistName = crd.getRawData().split("" + (char)9)[0];
            Song song = songService.getSongByTitle(songName, artistName);
            if (song != null && song.getAlbum() == null) {

                cleanRawData.add(crd);
            }
        }
        if (endOfDBReached) {
            //pad the rest of the array with null values to keep the size appropriate for the front end.
            for (int i = 0; i < pageSize - cleanRawData.size(); i++) cleanRawData.add(null);
        }

        return cleanRawData;
    }

    public void createRawData() {
        try {
            //reads the .txt file saved in the resources folder which contains the raw data and saves it all into the db.
            File rawDataFile = new File("src/main/resources/combined_data.txt");
            Scanner myReader = new Scanner(rawDataFile);
            int currentLine = 0;
            while (myReader.hasNextLine()) {
                System.out.println(++currentLine); //There's a lot of data so this is just an indicator to me of how far along we are
                RawData rawData = new RawData(0, myReader.nextLine());
                if (!addRawData(rawData)) throw new Exception("Something went wrong");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void createCleanRawData() {
        try {
            //reads the .txt file saved in the resources folder which contains the cleaned up raw data and saves it all
            //into the database.
            File cleanRawDataFile = new File("src/main/resources/Spotify_Cleaned.txt");
            Scanner myReader = new Scanner(cleanRawDataFile);
            int currentLine = 0;
            while (myReader.hasNextLine()) {
                //In the case that the note attached to the data is too long we truncate the end of it.
                String raw = myReader.nextLine();
                CleanRawData cleanRawData = new CleanRawData(0, raw.substring(0, Math.min(255, raw.length())));
                if (!addCleanRawData(cleanRawData)) throw new Exception("Something went wrong");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Boolean updateRawData(RawData[] rawData) {
        //go through each piece of raw data in the array and call the save() method with the appropriate id
        for (RawData data : rawData) {
            RawData saved = rawDAO.save(data);
        }

        return true;
    }

    public Boolean updateCleanRawData(CleanRawData[] cleanRawData) {
        //go through each piece of raw data in the array and call the save() method with the appropriate id
        for (CleanRawData data : cleanRawData) {
            CleanRawData saved = cleanRawDAO.save(data);
        }

        return true;
    }

}

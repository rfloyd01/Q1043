package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.RawData;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.repo.SongDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class SongService {

    private SongDAO songDAO;
    private AlbumService albumService;
    private int callNumber = 0;

    @Autowired
    public SongService(SongDAO songDAO, AlbumService albumService) {
        this.songDAO = songDAO;
        this.albumService = albumService;
    }

    public Song getSongById(int id) {
        Song song = songDAO.findById(id).orElse(null);
        if (song != null) song.createRankingsArray();
        return song;
    }

    public List<Song> getSongsByTitle(String title) {
        List<Song> songs = songDAO.findSongByTitle(title).orElse(null);

        if (songs != null) {
            for (Song song : songs) {
                song.createRankingsArray();
            }
        }

        return songs;
    }

    public Song getSongByTitle(String title) {
        if (title.equals("")) return null; //no need to search for a blank title

        ArrayList<Song> songs = new ArrayList<>(getSongsByTitle(title));

        //There are some instances where songs have the same name but are by a different artist. If we search for
        //a song and get multiple results return null, this will prompt the controller layer to search again
        //with the artist name as well (if an artist name exists).
        if (songs.size() == 1) return songs.get(0);
        return null; //either there was more than one song, or there were no songs
    }

    public Song getSongByTitle(String title, String artist) {
        //If there are multiple songs in the database with the same name this function is used
        //to get the appropriate song.
        if (title.equals("") || artist.equals("")) return null; //no need to search for a null title or artist

        ArrayList<Song> songs = new ArrayList<>(getSongsByTitle(title));
        for (Song song : songs) {
            if (song.getArtist().equals(artist)) return song;
        }

        return null; //no match was found
    }

    public Song saveSong(Song song) {
        try {
            return songDAO.save(song);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean addSongs(Song[] songs) {
//        //This function allows us to both add new song data, as well as update existing song data.
//        //Before adding something new we first need to make sure that it doesn't already exist.
//        callNumber++; //increment this for debugging purposes
//        for (Song song : songs) {
//            Song existingSong = songDAO.findSongByTitle(song.getTitle()).orElse(null);
//            if (existingSong == null) {
//                //the song doesn't exist in the database yet so we can save it as is
//                songDAO.save(song);
//            }
//            else {
//                //the song already exists in the database so we need to combine the rankings data from
//                //the existing song and the new song before saving in the database.
//
//                int existingYearsRanked = 0, newYearsRanked = 0;
//                boolean conflict = false;
//                ArrayList<Integer> existingRatings = new ArrayList<>(existingSong.getRatings());
//                ArrayList<Integer> newRatings = new ArrayList<>(song.getRatings());
//
//                for (int i = 0; i < 21; i++) {
//                    //We need to make sure when combining ranking data that at least one of the values is 0. If
//                    //both songs have ranking data for the same year this means that one of the songs has the
//                    //incorrect title. In this case we let the song with more ranking data keeps it's title, and
//                    //alter slightly the title of the other song to keep them separate in the database for
//                    //inspection by eye.
//                    if (existingRatings.get(i) > 0) {
//                        if (newRatings.get(i) > 0) {
//                            //there's a conflict because both songs are rated in the same year
//                            conflict = true;
//                            newYearsRanked++; //increment this variable as we'll need it for comparison
//                        }
//                        //We add the new ratings to the existing rating array, so if the existing value is more than
//                        //0 and the new value is 0, there's nothing to do here accept increment the existingYearsRanked variable
//                        existingYearsRanked++;
//                    }
//                    else {
//                        if (newRatings.get(i) > 0) {
//                            //the new song is ranked and the existing isn't. Increment the newYearsRanked variable and add the
//                            //data to the existing array.
//
//                            newYearsRanked++;
//                            existingRatings.set(i, newRatings.get(i));
//                        }
//                    }
//                }
//
//                //See if there's a conflict after sorting through all the ratings data. If so, we change the name of the
//                //new song to include the word "conflict". If the new song has more rated years than the existing song,
//                //we swap the ratings arrays so that the non-conflict song becomes the one with more rated years. We can't
//                //change the name of the existing song in the database because the name is the primary key for the table
//                //and changing it would cause an error.
//                if (conflict) {
//                    song.setTitle(song.getTitle() + " - conflict");
//
//                    //For Debuggin purposes
//                    System.out.println("Conflict in call " + callNumber + " with song: " + existingSong.getTitle());
//                    if (existingYearsRanked >= newYearsRanked) {
//                        //we keep the existing song title and alter the new one, then save the new song in the database
//                        songDAO.save(song);
//                    }
//                    else {
//                        //We can't actually alter the title of the existing song as it's the primary key in the database,
//                        //so what we do instead is to swap the ratings arrays and make sure the conflict title gets
//                        //less.
//                        song.setRatings(existingSong.getRatings()); //grab the non-modified list of ratings
//                        existingSong.setRatings(newRatings);
//                        songDAO.save(existingSong);
//                        songDAO.save(song);
//                    }
//                }
//                else {
//                    //there was no conflict of year ratings, all we do in this scenario is update the existing songs ratings
//                    //list and re-save it into the database.
//                    existingSong.setRatings(existingRatings);
//                    songDAO.save(existingSong);
//                }
//            }
//        }
//        return true; //for now just return true, this can potentially change to some kind of error code in the future
        return true;
    }

    public void writeCleanData() {
//        try {
//            File cleanDataFile = new File("src/main/resources/clean_data.txt");
//            cleanDataFile.createNewFile(); //if file already exists it won't be recreated
//
//            FileWriter dataWriter = new FileWriter("src/main/resources/clean_data.txt");
//            ArrayList<Song> allSongs = new ArrayList<>(songDAO.findAll());
//
//            //instead of delimiting with commas delimit with something that won't actually appear in
//            //song titles and artist names. Use two '*' Symbols
//            for (int i = 0; i < allSongs.size(); i++) {
//                StringBuilder dataString = new StringBuilder(allSongs.get(i).getArtist() + "**" + allSongs.get(i).getTitle() + "**" + allSongs.get(i).getReleased() + "**");
//                ArrayList<Integer> rankings = new ArrayList<>(allSongs.get(i).getRatings());
//
//                for (int j = 0; j < 20; j++)  dataString.append(Integer.toString(rankings.get(j)) + "**");
//                dataString.append(Integer.toString(rankings.get(20)) + "\n"); //add the last ranking separately
//                dataWriter.write(new String(dataString));
//            }
//            System.out.println("Done writing data to file!");
//            dataWriter.close();
//
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
    }

    public Boolean addCompleteSongs(Song[] songs) {
        //Once we've gotten all the data we need from Spotify, we not only need to create the song in the
        //database, but also the album and artist potentially. This function will call the Album service
        //to see if the album exists, if not the album service will create the album.
        try {
            for (Song song : songs) {
                //first check to see if the song already exists
                if (songDAO.findSongByTitle(song.getTitle()).get().size() > 0) {
                    //If the song already exists it means that we've already saved it in the db and don't need to do so
                    //again, or it's a song with the same title by a different artist. Check to see if the artists are
                    //the same. There are some instances where we have three songs with the same name, so we need to check
                    //every song in the array.
                    ArrayList<Song> matchingSongs = (ArrayList<Song>) songDAO.findSongByTitle(song.getTitle()).get();
                    boolean skip = false;

                    for (Song match : matchingSongs) {
                        if (song.getArtist().equals(match.getArtist())) {
                            //As a last check, see if existing song has no album data while the incoming song does. In
                            //this case we need to update the album info.
                            if (song.getAlbum().getTitle() != null && match.getAlbum() == null) {
                                System.out.println(song.getAlbum());
                                Album album = albumService.addCompleteAlbum(song.getAlbum());
                                match.setAlbum(album);
                                songDAO.save(match);
                                //We still utilize the skip line below as there's no reason to execute the below code
                                //with the album data saved in the existing song.
                            }
                            skip = true;
                        }
                    }

                    if (skip) continue;
                    else System.out.println(song.getTitle() + " by " + song.getArtist() + " isn't in the database yet, adding it now.");
                }

                //System.out.println("Trying to create the following song: " + song.getTitle());
                song.convertRankingsArray(); //first, convert the ranking arrays into their proper variables

                if (song.getAlbum().getTitle() != null)
                {
                    //System.out.println("Checking to see if album is already in the database");
                    Album album = albumService.getAlbumByTitle(song.getAlbum().getTitle());
                    if (album == null) {
                        //We need to create the album before adding the song to the database
                        album = albumService.addCompleteAlbum(song.getAlbum());
                    }

                    //System.out.println("Adding album info to song.");
                    //the album exists so we add its id to the current song before saving it to the database
                    song.getAlbum().setId(album.getId());
                    songDAO.save(song);
                }
                else {
                    //If the album name comes in as a blank it means we couldn't find anything in Spotify,
                    //just create the song without an album for now.
                    song.setAlbum(null);
                    songDAO.save(song);
                }

            }
            return true;
        } catch (Exception e) {
            System.out.println("Failure trying to create song: " + e.getMessage());
            return false;
        }
    }
}

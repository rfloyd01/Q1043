package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.repo.SongDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

                song.convertRankingsArray(); //first, convert the ranking arrays into their proper variables

                if (song.getAlbum().getTitle() != null)
                {
                    Album album = albumService.getAlbumByTitle(song.getAlbum().getTitle());
                    if (album == null) {
                        //We need to create the album before adding the song to the database
                        album = albumService.addCompleteAlbum(song.getAlbum());
                    }

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

    public Boolean generateRankings() {
        //This function looks at each song in the database and generates two ranking values. The first ranking value
        //is obtained by looking at the average rank for the song in the years that it's ranked. For example, a song
        //with rankings of [10, 52, 0, 0, 15] would get an average of (10 + 52 + 15) / 3 = 25.67. The 0's get ignored.
        //Due to the nature of this list being that lesser numbers are better than higher numbers, averaging in 0's would
        //make songs ranked worse appear to be better. At first I considered just putting in a high number instead of 0,
        //but couldn't settle on a number that really made sense so I decided to just ignore the numbers entirely.

        //The second ranking value is what I'm calling the "overall rating". Since the "averaging rating" above ignores
        //years where a song wasn't rated, songs that only got rated once but had a fairly good rank get overvalued. A good example
        //of this is the Bruce Springsteen song "Radio Nowhere", which is only rated once and got a ranking of 379
        //(in the year 2007 when the song was released). This gives it an average rank of 379. Meanwhile the song "Surrender"
        //by Cheap Trick has an average rating of 461 but is ranked in 18 of the 21 years. In essence the overall rating
        //rewards songs that have made the list in more years. It's obtained simply by dividing the average score by the
        //number of years the song has made the list. Using the above example where the average score was 25.67, the
        //overall score would be 25.67 / 3 = 8.56. Going back to the Bruce Springsteen vs. Cheap Trick example, the Burce
        //Springsteen song would have the same average and overall score (379 / 1 = 379) whereas the Cheap Trick song
        //would get an overall score of 461 / 18 = 25.61 which indicates that it's really much better than the Bruce
        //song (sorry boss, but I've never even heard of that song).

        Iterator<Song> it = songDAO.findAll().iterator();
        while (it.hasNext()) {
            Song currentSong = it.next();
            currentSong.createRankingsArray(); //create an array to iterate through

            double averageScore = 0;
            int yearsRanked = 0;
            int[] rankings = currentSong.getRankings();
            for (int i = 0; i < rankings.length; i++) {
                if (rankings[i] > 0) {
                    averageScore += rankings[i];
                    yearsRanked++;
                }
            }

            //set the scores
            currentSong.setAverageScore(averageScore / yearsRanked);
            currentSong.setOverallScore(currentSong.getAverageScore() / yearsRanked);

            //then save the song back in the database
            songDAO.save(currentSong);
        }

        return true;
    }
}

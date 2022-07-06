package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Artist;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.repo.AlbumDAO;
import com.projectfloyd.Q1043.repo.SongDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AlbumService {

    private AlbumDAO albumDAO;
    private SongDAO songDAO;
    private ArtistService artistService;

    @Autowired
    public AlbumService(AlbumDAO albumDAO, SongDAO songDAO, ArtistService artistService) {

        this.albumDAO = albumDAO;
        this.songDAO = songDAO;
        this.artistService = artistService;
    }

    public Album getAlbumById(int id) {
        Album album = albumDAO.findById(id).orElse(null);

        if (album != null) cleanDataForJSONResponse(album);
        return album;
    }

    public Album getAlbumByTitle(String title) {
        Album album = albumDAO.findAlbumByTitle(title).orElse(null);

        if (album != null) cleanDataForJSONResponse(album);
        return album;
    }

    public Album saveAlbum(Album album) {
        try {
            return albumDAO.save(album);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }


    public Album addAlbum(int id, Song song) {
        //first check to see if the  song is in the database
        Optional<Song> Song = songDAO.findById(song.getId());

        if (Song.isPresent()) {
            Optional<Album> Album = albumDAO.findById(id);

            if (Album.isPresent()) {
                //set the album variable of the song
                Song actualSong = Song.get();
                actualSong.setAlbum(Album.get());

                //save in the db
                songDAO.save(actualSong);

                return Album.get();
            }
        }

        return null;
    }

    public Album addCompleteAlbum(Album album) {
        //Once we've gotten all the data we need from Spotify, we not only need to create albums in the
        //database, but also the artist potentially. This function will call the Artist service
        //to see if the artist exists, if not the artist service will create the artist.
        try {
            Artist artist = artistService.getArtistByName(album.getArtist().getName());
            if (artist == null) {
                //We need to create the artist before adding the album to the database
                artist = artistService.addArtist(album.getArtist());
            }

            //the artist exists so we add its id to the current album before saving it to the database
            album.getArtist().setId(artist.getId());
            return albumDAO.save(album);
        } catch (Exception e) {
            System.out.println("Failure trying to create album: " + e.getMessage());
            return null;
        }
    }

    private void createSongRankingArrays(Song song) {
        //Our Song objects don't save their rankings in array format in the database. To make sure that everything
        //looks nice in JSON we need to explicitly create these arrays.
        if (song != null) song.createRankingsArray();
    }

    private void cleanDataForJSONResponse(Album album) {
        //I opted not to use JSON back references to have more control over the JSON responses
        //so we need to remove the circular ties from each individual song to the album and from
        //the associated artist to the album
        album.getArtist().setAlbums(new ArrayList<>());
        for (Song song : album.getSongs()) {
            song.setAlbum(null);
            createSongRankingArrays(song); //we also manually create the song ranking array
        }
    }

    private void generateAlbumScores() {
        //NOTE: This function should only be called after generating average scores for all songs in database

        //This function looks at each album in the database and computes a score for the album based on
        //the ratio of its songs that made the Q1043 lists, and the average rankings of those songs. The formula
        //for the score looks like this: ((total songs on album) / (album songs on lists)) * (Average Score of List Songs).
        //A lower score is better here. The higher the ratio of songs on the album will lower the multiplier on the
        //left (for example, and album with only 1 out of 10 songs on lists will have it's song average multiplied by
        //ten, whereas an album with all songs on the lists will only have a multiplier of 1). A better average score
        //will also lower the album score.

        //Here's two examples. The album 'Who Are You' by 'The Who' has 9 tracks, however, only the title song made
        //any of the lists. The song 'Who Are You' has an overall score of 3.49 so the final album score here is
        //(9 / 1) * 3.49 = 31.41. The album 'Led Zeppelin IV', on the other hand, has 8 out of its 8 tracks make the
        //list. The overall scores for these songs are [3.07, 0.05, 2.77, 14.85, 14.85, 33.03, 45.88, 114.76] for an
        //average overall score of

        //Average scores [64.52, 1, 58.19, 267.33, 282.32, 528.44, 642.29, 803.29] --> ~330

        //Going by shear numbers of songs --> Average Overall Score / Number of Tracks on Album
        //Who Are You = 3.49 / 1 = 2.49 ------ Led Zeppelin IV = 28.66 / 8 = 3.58
        //More weight needs to be given to ratio of songs on list. Maybe a scale can be used likes this:
        //[0%, 50%, 100%]
        //[16x, 1x, .0625x] --> equation for this would be 64*e^(-0.693 * 10 * song_ratio)
        //Could do, weighted exponential multiplier x overall song score.
        //With the above exponential grading the scores change to:
        //Who Are You = 3.49 *  (64 * e ^ (-0.693 * 10 / 9)) = 3.49 * 29.63 = 103.42
        //Led Zepp IV = 28.66 * (64 * e ^ (-0.693 * 10)) = 28.66 * 0.0625 = 1.79
        //This seems more fair to me. It gets more and more rare to have multiple songs from the same album on the list
        //So the albums with very high ratios of songs on there should be rewarded.

    }
}

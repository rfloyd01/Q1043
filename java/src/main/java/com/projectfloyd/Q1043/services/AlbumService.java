package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Artist;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.repo.AlbumDAO;
import com.projectfloyd.Q1043.repo.SongDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        if (album != null) {
            for (Song song : album.getSongs()) {
                createSongRankingArrays(song);
            }
        }

        return album;
    }

    public Album getAlbumByTitle(String title) {
        Album album = albumDAO.findAlbumByTitle(title).orElse(null);

        if (album != null) {
            for (Song song : album.getSongs()) {
                createSongRankingArrays(song);
            }
        }

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
            //System.out.println("Trying to create album: " + album.getTitle());
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
}

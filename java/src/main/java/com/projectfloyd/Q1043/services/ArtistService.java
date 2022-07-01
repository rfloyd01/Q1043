package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Artist;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.repo.ArtistDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    private ArtistDAO artistDAO;

    @Autowired
    public ArtistService(ArtistDAO artistDAO) { this.artistDAO = artistDAO; }

    public Artist getArtistById(int id) {
        Artist artist = artistDAO.findById(id).orElse(null);

        if (artist != null) {
            for (Album album : artist.getAlbums()) {
                for (Song song : album.getSongs()) {
                    createSongRankingArrays(song);
                }
            }
        }

        return artist;
    }

    public Artist getArtistByName(String name) {
        Artist artist = artistDAO.findArtistByName(name).orElse(null);

        if (artist != null) {
            for (Album album : artist.getAlbums()) {
                for (Song song : album.getSongs()) {
                    createSongRankingArrays(song);
                }
            }
        }

        return artist;
    }

    public Artist addArtist(Artist artist) {
        //System.out.println("Trying to create artst: " + artist.getName());
        return artistDAO.save(artist);
    }

    private void createSongRankingArrays(Song song) {
        //Our Song objects don't save their rankings in array format in the database. To make sure that everything
        //looks nice in JSON we need to explicitly create these arrays.
        if (song != null) song.createRankingsArray();
    }

}

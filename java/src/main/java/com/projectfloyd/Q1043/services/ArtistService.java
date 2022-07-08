package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Artist;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.repo.ArtistDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ArtistService {

    private ArtistDAO artistDAO;

    @Autowired
    public ArtistService(ArtistDAO artistDAO) { this.artistDAO = artistDAO; }

    public Artist getArtistById(int id) {
        Artist artist = artistDAO.findById(id).orElse(null);

        if (artist != null) cleanDataForJSONResponse(artist);
        return artist;
    }

    public Artist getArtistByName(String name) {
        Artist artist = artistDAO.findArtistByName(name).orElse(null);

        if (artist != null) cleanDataForJSONResponse(artist);
        return artist;
    }

    public Artist addArtist(Artist artist) {
        return artistDAO.save(artist);
    }

    private void createSongRankingArrays(Song song) {
        //Our Song objects don't save their rankings in array format in the database. To make sure that everything
        //looks nice in JSON we need to explicitly create these arrays.
        if (song != null) song.createRankingsArray();
    }

    private void cleanDataForJSONResponse(Artist artist) {
        //Since I'm not using JSON managed and JSON back reference annotations I need to clean up
        //JSON responses myself. When looking at Artists we want to see a) what albums they have and
        //b) which songs are on those albums. This means we need to set the Artist reference on each
        //album to null and also go through the songs of each album and set their album references to null
        for (Album album : artist.getAlbums()) {
            album.setArtist(null);
            for (Song song : album.getSongs()) {
                song.setAlbum(null);
                createSongRankingArrays(song); //we also create the ranking arrays for each song
            }
        }
    }

    public Page<Artist> getPaginatedArtistsByRank(int pageNumber, int pageSize, String sort, String direction) {
        if (pageNumber < 0 || pageSize < 1) return null; //make sure the page request is valid before getting the page

        if (sort.equals("artistScore") || sort.equals("totalSongs")) {
            //First get the sort direction, default to ascending
            Sort.Direction dir = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            //When sorting, in the instance of a tie (which will happen a lot when sorting by total ranked songs), we
            //do a second ordering by artist name.
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(new Sort.Order(dir, sort), new Sort.Order(Sort.Direction.ASC, "name")));
            Page<Artist> page = artistDAO.findAll(pageable);

            //once we have the page, go through all of the albums and clean up the necessary variables for the JSON response.
            for (Artist artist : page.getContent()) cleanDataForJSONResponse(artist);
            return page;
        }
        else return null; //we can only sort by artist score and total songs

    }
}

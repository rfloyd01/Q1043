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

import java.util.ArrayList;
import java.util.Iterator;

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

        if (sort.equals("artistScore") || sort.equals("rankedTracks")) {
            //First get the sort direction, default to ascending
            Sort.Direction dir = direction.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            //When sorting, in the instance of a tie (which will happen a lot when sorting by total ranked songs), we
            //do a second ordering by artist name.
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(new Sort.Order(dir, sort)));
            Page<Artist> page = artistDAO.findAll(pageable);

            //once we have the page, go through all of the albums and clean up the necessary variables for the JSON response.
            for (Artist artist : page.getContent()) cleanDataForJSONResponse(artist);
            return page;
        }
        else return null; //we can only sort by artist score and ranked tracks
    }

    public void generateArtistScores() {
        //NOTE: This function should only be called after generating average scores for all songs in database

        //To get the overall artist score there are two components, a weighted average all of an artist's overall
        //song scores and a bonus multiplier for shear number of songs.
        //
        //To get the weighted overall score, we use the
        //same version as the album score. We really only care about individual songs that the artist has
        //put out so we use the same method of getting an average song score for the artist based on the different
        //overall scores for the songs. Namely, we use the same function used to calculate equivalent parallel resistance
        //in an electric circuit ( 1 / (1/score_1 + 1/score_2 + ... 1/score_n)). I like this formula because adding
        //more songs is only beneficial, but songs with higher scores aren't nearly as beneficial as those with lower
        //scores. This way, the total number of songs, as well as the quality of the songs is accounted for.
        //
        //As for the total songs multiplier, this is necessary to penalize artists with only a few songs and reward those
        //with tons of songs. For example, Don McLean only has a single song on the list, but it's a very good song.
        //If we only looked at average song scores then he would be pretty close to the top of the artist list, which
        //I don't really think is fair. This multiplier will penalize artists fairly heavily that only have 3 songs or
        //less on the list, be fairly neutral for artists that have up to 10 songs and then reward artists with more.
        //This bonus will flatten off after about 30 songs or so though.

        //First get all of the artists from the database:
        ArrayList<Artist> allArtists = new ArrayList<>();
        Iterator<Artist> it = artistDAO.findAll().iterator();
        while (it.hasNext()) allArtists.add(it.next());

        int counter = 0;

        //Then iterate through them one by one
        for (Artist artist: allArtists) {

            //if (counter++ > 10) break; //Just used while testing the algorithm

            double averageOverallScore = 0.0;
            int totalSongs = 0;

            //Calculate the average overall score, this is done by looking at overall song scores
            //and not album scores.
            for (Album album : artist.getAlbums()) {
                for (Song song : album.getSongs()) {
                    averageOverallScore += (1.0 / song.getOverallScore());
                    totalSongs++;
                }
            }

            //Then calculated the total songs multiplier based on the totalSongs variable
            double totalSongsMultiplier = 0.0;

            if (totalSongs <= 0 || averageOverallScore <= 0.0) {
                //some artists on the list are probably incorrect so as we remove songs from them we want to
                //make sure we don't try and divide by 0 here.
                artist.setArtistScore(1000000); //some arbitrarily high number
                artist.setRankedTracks(0); //update the total tracks, this value will may change as we update songs
                continue; // go to the next artist
            }

            if (totalSongs <= 3) totalSongsMultiplier = 23.75 * totalSongs * totalSongs - 138.75 * totalSongs + 203.75;
            else totalSongsMultiplier = 4.0 / totalSongs;

            //Set the artist score and save the album
            artist.setArtistScore((10 / averageOverallScore) * totalSongsMultiplier);
            artist.setRankedTracks(totalSongs); //update the total tracks, this value will may change as we update songs

            artistDAO.save(artist);
        }

    }
}

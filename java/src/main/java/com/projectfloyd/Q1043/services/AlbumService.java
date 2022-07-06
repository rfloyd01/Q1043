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

    public void generateAlbumScores() {
        //NOTE: This function should only be called after generating average scores for all songs in database

        //At first I was unsure about the best way to score an album. First and foremost, not all albums feature the
        //same number of tracks, how exactly do you compare an album with only 3 songs to one that has over 20 songs?
        //To simply look at the average scores of all the songs on the album doesn't really seem fair. Ultimately I
        //decided on a 3 part formula.
        //
        //The first part looks at the ratio of songs on the album that were on at least one of teh Q1043 lists over the
        //years. Looking at two albums that each have 10 tracks, I think it would be fair to say that if the first
        //album has 8 of its songs on the list and the second album only has 2 then the first album should get more
        //points. For the first part of the equation (which looks at ratio of songs on the list to total songs on the album) I
        //used an exponentially weighted curve where a 0% ratio would add a multiplier of 32x to the final score and
        //a ratio of 100% adds a multiplier of 1/16x. No album on the list actually has 0 songs (otherwise it wouldn't
        //be here) so for the most part the highest possible multiplier is ~16x and the minimum multiplier is the
        //inverse of that at 1/16x. The equation for this ends up being 64 * e ^ (-0.693 * 10 * songRatio)
        //
        //The second part of the formula takes into account how good the actual songs on the album are. After all, it's
        //not enough to just have a lot of ranked songs on the album, better songs inherently make the album better.
        //I decided to use the overall score as opposed to the average scores as they're intrinsically more fair. This
        //portion of the album score simply takes the averages of all of the overall scores for the ranked songs on the
        //album.
        //
        //The final part of the equation takes into account the total number of songs from the album that are on the
        //list. Think of a hypothetical scenario where we have two albums with the exact same ratio of list songs /
        //total songs, and these songs actually have the exact same overall score average. If one of the albums only
        //has 5 songs but the other has 15, I'd be a little more impressed by the album with 15. The actual scoring for
        //this part of the equation is simpler than the first part. For starters, anything that's labeled as a single
        //gets a 10x penalty to the score. Singles usually only have great songs but on them to begin with, and their
        //low song count of 2 makes it much easier for them to reach the 100% goal. Outside of this, another exponential
        //function is used, although it doesn't carry nearly as much weight as the first part of the equation. Anything
        //The equation is 2.25 * e ^ (-0.081 * numberOfSongs). I had to put a cap at 30 tracks because there are some
        //ultimate editions for albums out there with like 100 songs which were skewing this multiplier.

        //First get all of the albums from the database:
        ArrayList<Album> allAlbums = new ArrayList<>(albumDAO.findAll());

        //Then iterate through them one by one
        for (Album album : allAlbums) {

            double songRatio = ((double)album.getSongs().size()) / album.getTotalTracks();
            double ratioMultiplier = 64 * Math.exp(-0.693 * 10.0 * songRatio); //calculate the ratio score
            double averageOverallScore = 0.0;

            //Then calculate the average overall score (this will get divided before being added to total
            for (Song song : album.getSongs()) averageOverallScore += song.getOverallScore();

            //Lastly calculate the total songs multiplier
            double totalSongMultiplier = 0.0;
            if (album.getTitle().contains("(single)")) totalSongMultiplier = 10.0;
            else {
                int totalSongs = album.getTotalTracks();

                if (totalSongs > 30) totalSongs = 30;

                totalSongMultiplier = 2.25 * Math.exp(-0.081 * totalSongs);
            }

            //Set the album score and save the album
            album.setAlbumScore((averageOverallScore / album.getSongs().size()) * ratioMultiplier * totalSongMultiplier);
            albumDAO.save(album);
        }

    }
}

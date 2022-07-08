package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Artist;
import com.projectfloyd.Q1043.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/artists")
@CrossOrigin
public class ArtistController {

    private ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> findArtistById(@PathVariable("id") int id) {
        Artist artist = artistService.getArtistById(id);

        if (artist != null) {
            return ResponseEntity.status(HttpStatus.OK).body(artist);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(params = {"name"})
    public ResponseEntity<Artist> findArtistByName(@RequestParam String name) {
        Artist artist = artistService.getArtistByName(name);

        if (artist != null) {
            return ResponseEntity.status(HttpStatus.OK).body(artist);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/byId", params = {"pageNumber", "pageSize"})
    public ResponseEntity<Page<Artist>> getPaginatedArtistsById(@RequestParam int pageNumber, @RequestParam int pageSize) {
        Page<Artist> artists = artistService.getPaginatedArtistsById(pageNumber, pageSize);
        if (artists != null) return ResponseEntity.status(200).body(artists);
        else return ResponseEntity.status(400).build();
    }

    @PutMapping(value= "/byId")
    public ResponseEntity<Boolean> updateArtistsArtworkAndURIById(@RequestBody List<Artist> artists) {
        //this function was created to add artist artwork and spotify uri to artists (en masse) that already
        //exist in the data base. I don't want to mess up the current album and song relation ships so this
        //function will ONLY update the artwork url and spotify uri variables while maintaining everything else.
        boolean allWorked = true;
        for (Artist artist: artists) {
            if (!artistService.updateArtistArtworkAndURIById(artist)) allWorked = false;
        }

        if (allWorked) return ResponseEntity.status(200).body(allWorked);
        else return ResponseEntity.status(400).body(allWorked);
    }

    @GetMapping(value = "/byRank", params = {"pageNumber", "pageSize", "sort", "direction"})
    public ResponseEntity<Page<Artist>> getPaginatedArtistsByRank(@RequestParam int pageNumber, @RequestParam int pageSize, @RequestParam String sort, @RequestParam String direction) {
        Page<Artist> artists = artistService.getPaginatedArtistsByRank(pageNumber, pageSize, sort, direction);
        if (artists != null) return ResponseEntity.status(200).body(artists);
        else return ResponseEntity.status(400).build();
    }

    @GetMapping(value = "/byRank/multiple", params = {"firstPage", "pageSize", "numberOfPages", "sort", "direction"})
    public ResponseEntity<List<Page<Artist>>> getMultiplePaginatedArtistsByRank(@RequestParam int firstPage, @RequestParam int pageSize, @RequestParam int numberOfPages, @RequestParam String sort, @RequestParam String direction) {
        //Same as the above function but lets us collect multiple pages at a time.
        ArrayList<Page<Artist>> pages = new ArrayList<>();

        for (int i = 0; i < numberOfPages; i++) {
            pages.add(artistService.getPaginatedArtistsByRank(firstPage + i, pageSize, sort, direction));
        }

        if (pages != null) return ResponseEntity.status(200).body(pages);
        else return ResponseEntity.status(400).build();
    }

    @PostMapping("/createScore")
    public void createAlbumScores() {
        artistService.generateArtistScores();
    }
}

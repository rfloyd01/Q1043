package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.Artist;
import com.projectfloyd.Q1043.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}

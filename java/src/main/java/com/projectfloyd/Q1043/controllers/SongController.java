package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.services.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/songs")
@CrossOrigin
public class SongController {

    private SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<Boolean> addCompleteSongs(@RequestBody Song[] songs) {
        //This function takes an array of songs that have had their album data added in the front
        //end and creates the necessary artist, album and song data in the database
        Boolean success = songService.addCompleteSongs(songs);

        int httpCode = 200;
        if (!success) httpCode = 400;

        return ResponseEntity.status(httpCode).body(success);
    }

    @PostMapping("/test")
    public ResponseEntity<Song> addSong(@RequestBody Song song) {
        //Before adding the song, we need to convert the rankings array into the individual
        //rank variables of the class
        song.convertRankingsArray();
        Song addedSong = songService.saveSong(song);

        if (addedSong != null) return ResponseEntity.status(200).body(addedSong);
        else return ResponseEntity.status(400).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> findSongById(@PathVariable("id") int id) {
        Song song = songService.getSongById(id);

        if (song != null) {
            return ResponseEntity.status(HttpStatus.OK).body(song);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(params = {"title"})
    public ResponseEntity<ArrayList<Song>> findSongById(@RequestParam String title) {

        ArrayList<Song> songs = (ArrayList<Song>) songService.getSongsByTitle(title);

        if (songs != null) {
            return ResponseEntity.status(HttpStatus.OK).body(songs);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PatchMapping
    public ResponseEntity<Boolean> generateRankings() {
        //this function looks at all of the songs in the song database and generates their
        //ranking values by looking at all of the year rankings. (This function should really
        //only need to be called once.
        Boolean worked = songService.generateRankings();
        if (worked) return ResponseEntity.status(200).body(worked);
        else return ResponseEntity.status(400).body(worked);
    }

}

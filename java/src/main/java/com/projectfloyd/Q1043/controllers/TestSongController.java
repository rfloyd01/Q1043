package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.TestSong;
import com.projectfloyd.Q1043.services.TestSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testSongs")
@CrossOrigin
public class TestSongController {
    private TestSongService testSongService;

    @Autowired
    public TestSongController(TestSongService testSongService) {
        this.testSongService = testSongService;
    }


    @PostMapping
    public ResponseEntity<TestSong> addSong(@RequestBody TestSong song) {
        TestSong testSong = testSongService.saveSong(song);
        int httpCode = 200;
        if (testSong == null) httpCode = 400;

        return ResponseEntity.status(httpCode).body(testSong);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestSong> findSongById(@PathVariable("id") int id) {
        TestSong song = testSongService.getSongById(id);

        if (song != null) {
            return ResponseEntity.status(HttpStatus.OK).body(song);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}

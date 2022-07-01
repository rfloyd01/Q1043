package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.TestAlbum;
import com.projectfloyd.Q1043.models.TestSong;
import com.projectfloyd.Q1043.services.TestAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testAlbums")
@CrossOrigin
public class TestAlbumController {
    private TestAlbumService testAlbumService;

    @Autowired
    public TestAlbumController(TestAlbumService testAlbumService) {
        this.testAlbumService = testAlbumService;
    }


    @PostMapping
    public ResponseEntity<TestAlbum> addAlbum(@RequestBody TestAlbum Album) {
        TestAlbum testAlbum = testAlbumService.saveAlbum(Album);
        int httpCode = 200;
        if (testAlbum == null) httpCode = 400;

        return ResponseEntity.status(httpCode).body(testAlbum);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestAlbum> findAlbumById(@PathVariable("id") int id) {
        TestAlbum Album = testAlbumService.getAlbumById(id);

        if (Album != null) {
            return ResponseEntity.status(HttpStatus.OK).body(Album);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestAlbum> addSongToAlbum(@PathVariable("id") int id, @RequestBody TestSong song) {
        TestAlbum Album = testAlbumService.addAlbum(id, song);

        if (Album != null) {
            return ResponseEntity.status(HttpStatus.OK).body(Album);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}

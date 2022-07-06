package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.services.AlbumService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/albums")
@CrossOrigin
public class AlbumController {

    private AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> findAlbumById(@PathVariable("id") int id) {
        Album album = albumService.getAlbumById(id);

        if (album != null) {
            return ResponseEntity.status(HttpStatus.OK).body(album);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(params = {"title"})
    public ResponseEntity<Album> findlbumByName(@RequestParam String title) {
        Album album = albumService.getAlbumByTitle(title);

        if (album != null) {
            return ResponseEntity.status(HttpStatus.OK).body(album);
        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping(value = "/byRank", params = {"pageNumber", "pageSize", "direction"})
    public ResponseEntity<Page<Album>> getPaginatedAlbumsByRank(@RequestParam int pageNumber, @RequestParam int pageSize, @RequestParam String direction) {
        Page<Album> albums = albumService.getPaginatedAlbumsByRank(pageNumber, pageSize, direction);
        if (albums != null) return ResponseEntity.status(200).body(albums);
        else return ResponseEntity.status(400).build();
    }

    @PostMapping("/createScore")
    public void createAlbumScores() {
        albumService.generateAlbumScores();
    }
}

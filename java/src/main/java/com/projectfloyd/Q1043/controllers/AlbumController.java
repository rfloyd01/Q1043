package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.Album;
import com.projectfloyd.Q1043.models.Song;
import com.projectfloyd.Q1043.models.Year;
import com.projectfloyd.Q1043.services.AlbumService;
//import org.apache.coyote.Response;
//import org.apache.http.client.HttpClient;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;

//import javax.net.ssl.SSLContext;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/albums")
@CrossOrigin
public class AlbumController {

    private AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

//    @Value("${trust.store}")
//    private Resource trustStore;
//
//    @Value("${trust.store.password}")
//    private String trustStorePassword;

//    private RestTemplate restTemplate() throws Exception {
//        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray()).build();
//        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
//        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
//        return new RestTemplate(factory);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> findAlbumById(@PathVariable("id") int id) {
        Album album = albumService.getAlbumById(id);

        if (album != null) {
            //ResponseEntity<Album> response = restTemplate().getForEntity("https://localhost:443/")
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

    @GetMapping(value = "/byRank", params = {"pageNumber", "pageSize", "sort", "direction"})
    public ResponseEntity<Page<Album>> getPaginatedAlbumsByRank(@RequestParam int pageNumber, @RequestParam int pageSize, @RequestParam String sort, @RequestParam String direction) {
        Page<Album> albums = albumService.getPaginatedAlbumsByRank(pageNumber, pageSize, sort, direction);
        if (albums != null) return ResponseEntity.status(200).body(albums);
        else return ResponseEntity.status(400).build();
    }

    @GetMapping(value = "/byRank/multiple", params = {"firstPage", "pageSize", "numberOfPages", "sort", "direction"})
    public ResponseEntity<List<Page<Album>>> getMultiplePaginatedAlbumsByRank(@RequestParam int firstPage, @RequestParam int pageSize, @RequestParam int numberOfPages, @RequestParam String sort, @RequestParam String direction) {
        //Same as the above function but lets us collect multiple pages at a time.
        ArrayList<Page<Album>> pages = new ArrayList<>();

        for (int i = 0; i < numberOfPages; i++) {
            pages.add(albumService.getPaginatedAlbumsByRank(firstPage + i, pageSize, sort, direction));
        }

        if (pages != null) return ResponseEntity.status(200).body(pages);
        else return ResponseEntity.status(400).build();
    }

    @GetMapping(value = "/byYear")
    public ResponseEntity<List<Year>> getRankedYears() {
        //this function looks at all of the albums and creates an array containing all the years with ranked songs,
        //as well as how many songs are ranked in each year.
        ArrayList<Year> years = albumService.getRankedYears();

        if (years != null) return ResponseEntity.status(200).body(years);
        else return ResponseEntity.status(400).body(years);
    }

    @PostMapping("/createScore")
    public void createAlbumScores() {
        albumService.generateAlbumScores();
    }
}

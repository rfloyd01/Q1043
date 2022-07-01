package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.CleanRawData;
import com.projectfloyd.Q1043.models.RawData;
import com.projectfloyd.Q1043.services.RawDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rawdata")
@CrossOrigin
public class RawDataController {

    private RawDataService rawDataService;

    @Autowired
    public RawDataController(RawDataService rawDataService) {
        this.rawDataService = rawDataService;
    }

    @PostMapping("/dirty")
    public void createRawData() {
        //a special post mapping, writing anything to this mapping triggers the table of
        //raw data to be rebuilt in the database
        rawDataService.createRawData();
    }

    @PostMapping("/clean")
    public void createCleanRawData() {
        //a special post mapping, writing anything to this mapping triggers the table of
        //raw data cleaned by hand to be (re)built in the database
        rawDataService.createCleanRawData();
    }

    @GetMapping("/dirty/{id}")
    public ResponseEntity<RawData> getRawDataById(@PathVariable("id") int id) {
        RawData rawData = rawDataService.getRawDataById(id);

        if (rawData != null) return ResponseEntity.status(HttpStatus.OK).body(rawData);
        else return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/clean/{id}")
    public ResponseEntity<CleanRawData> getCleanRawDataById(@PathVariable("id") int id) {
        CleanRawData cleanRawData = rawDataService.getCleanRawDataById(id);

        if (cleanRawData != null) return ResponseEntity.status(HttpStatus.OK).body(cleanRawData);
        else return  ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    public ResponseEntity<Page<RawData>> getPaginatedRawData(@RequestParam int pageNumber, @RequestParam int pageSize) {
        PageImpl<RawData> paginatedData = (PageImpl<RawData>) rawDataService.getPaginatedRawData(pageNumber, pageSize);
        return ResponseEntity.status(200).body(paginatedData);
    }

    //Functions to find data points by id number and not using strict pagination
    @GetMapping(value = "/dirty/byid", params = {"pageNumber", "pageSize"})
    public ResponseEntity<List<RawData>> getRawData(@RequestParam int pageNumber, @RequestParam int pageSize) {
        ArrayList<RawData> rawData = new ArrayList<>(rawDataService.getaRawData(pageNumber, pageSize));
        return ResponseEntity.status(200).body(rawData);
    }

    @GetMapping(value = "/clean/byid", params = {"pageNumber", "pageSize"})
    public ResponseEntity<List<CleanRawData>> getCleanRawData(@RequestParam int pageNumber, @RequestParam int pageSize) {
        ArrayList<CleanRawData> cleanRawData = new ArrayList<>(rawDataService.getCleanRawData(pageNumber, pageSize));
        return ResponseEntity.status(200).body(cleanRawData);
    }

    @GetMapping(value = "/clean/byid/noalbum", params = {"pageNumber", "pageSize"})
    public ResponseEntity<List<CleanRawData>> getCleanRawDataWithoutAlbums(@RequestParam int pageNumber, @RequestParam int pageSize) {
        //Same as the above function, but, it will only grab raw data that we haven't already found albums for
        ArrayList<CleanRawData> cleanRawData = new ArrayList<>(rawDataService.getCleanRawDataWithoutId(pageNumber, pageSize));
        return ResponseEntity.status(200).body(cleanRawData);
    }

    @PatchMapping("/dirty")
    public ResponseEntity<Boolean> updateRawData(@RequestBody RawData[] rawData) {
        //when we query the Spotify API in the front end, we add the found artist and track name to the
        //raw data so it's easier to audit glaring errors at a glance
        Boolean success = this.rawDataService.updateRawData(rawData);
        return ResponseEntity.status(200).body(success);
    }

    @PatchMapping("/clean")
    public ResponseEntity<Boolean> updateCleanRawData(@RequestBody CleanRawData[] cleanRawData) {
        //when we query the Spotify API in the front end, we add the found artist and track name to the
        //raw data so it's easier to audit glaring errors at a glance
        Boolean success = this.rawDataService.updateCleanRawData(cleanRawData);
        return ResponseEntity.status(200).body(success);
    }

}

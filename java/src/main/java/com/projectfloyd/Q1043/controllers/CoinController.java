package com.projectfloyd.Q1043.controllers;

import com.projectfloyd.Q1043.models.FrontendCoin;
import com.projectfloyd.Q1043.models.RedbookCoin;
import com.projectfloyd.Q1043.services.CoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/coins")
public class CoinController {

    private CoinService coinService;

    @Autowired
    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RedbookCoin> getCoinById(@PathVariable("id") int id) {
        RedbookCoin coin = this.coinService.getCoinById(id);
        return ResponseEntity.status(200).body(coin);
    }

    @GetMapping
    public ResponseEntity<List<RedbookCoin>> getCoinsByName(@RequestParam String name, @RequestParam(required = false) Integer year) {
        ArrayList<RedbookCoin> coins;

        if (year == null) coins = this.coinService.getCoinsByName(name);
        else coins = this.coinService.getCoinsByNameAndYear(name, year);

        return ResponseEntity.status(200).body(coins);
    }

    @PatchMapping("/values")
    public ResponseEntity<List<FrontendCoin>> getCoinValues(@RequestBody List<FrontendCoin> coins, @RequestParam String... types) {
        //Since Angular doesn't support sending a body in a get request, instead use a post request
        //but don't actually alter anything in the database.
        System.out.println("Searching for coins of type: " + Arrays.toString(types));
        return ResponseEntity.status(200).body(this.coinService.getCoinValues(coins, types));
    }

    @PostMapping
    public ResponseEntity<Boolean> addTestCoin(@RequestBody RedbookCoin coin) {
        boolean coin_added = this.coinService.addCoin(coin);
        return ResponseEntity.status(200).body(coin_added);
    }
}

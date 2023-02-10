package com.projectfloyd.Q1043.controllers;


import com.projectfloyd.Q1043.models.PlainText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/proxy-client")
@CrossOrigin
public class ProxyController {
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    private Logger log = Logger.getLogger(ProxyController.class.getName());

    @Autowired
    public ProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        headers = new HttpHeaders();
        headers.set("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36 Edg/109.0.1518.61");
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Accept-Encoding", "en-US,en;q=0.9");
    }

    @GetMapping("/test")
    public ResponseEntity<PlainText> testProxyController() {
        //this is just to see if the controller is working as intented
        PlainText tester = new PlainText("Testing testing");
        return ResponseEntity.status(HttpStatus.OK).body(tester);
    }

    @GetMapping()
    public ResponseEntity<PlainText> getRawHTML(@RequestParam String url) {
        //This method is used to extract the raw HTML from the website given in the request parameter.
        //It returns the HTML in text form where it can be parsed on the front end depending on the
        //application. Parsing isn't done here as websites all format their HTML very differently so backend
        //services would need to be made on a URL by URL basis.

        System.out.println("Scraping URL:");
        System.out.println(url);

        HttpEntity<String> ent = new HttpEntity<>(headers);

        //log.log(Level.INFO, ent.getHeaders().toString());

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, ent, String.class);
        return ResponseEntity.status(HttpStatus.OK).body(new PlainText(response.getBody()));
    }
}

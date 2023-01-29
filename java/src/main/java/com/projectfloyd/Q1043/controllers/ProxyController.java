package com.projectfloyd.Q1043.controllers;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@RestController
@RequestMapping("/proxy-client")
public class ProxyController {
    private RestTemplate restTemplate;
    private HttpHeaders headers;

    private org.slf4j.Logger log = LoggerFactory.getLogger(ProxyController.class);

    @Autowired
    public ProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testProxyController() {
        //this is just to see if the controller is working as intented
        return ResponseEntity.status(HttpStatus.OK).body("Testing Testing...");
    }

    @GetMapping()
    public ResponseEntity<String> getRawHTML(@RequestParam String url) {
        //This method is used to extract the raw HTML from the website given in the request parameter.
        //It returns the HTML in text form where it can be parsed on the front end depending on the
        //application. Parsing isn't done here as websites all format their HTML very differently so backend
        //services would need to be made on a URL by URL basis.
        log.warn("Testing to see if logs with warn level appear in Jenkins...");
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }
}

package com.projectfloyd.Q1043.models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

public class FrontendAlbum {
    //this class is used as a means to get album data to the frontend without getting stack overflows when parsing JSON.
    //It's basically the same as the standard Album class but it excludes
    private int id;

    private String title;
    private int releaseYear;
    private int totalTracks;
    private String spotifyURI;
    private String albumArtworkURL;
    private Artist artist;


}

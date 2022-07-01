package com.projectfloyd.Q1043.models;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderColumn;
import java.util.List;
import java.util.Objects;

//save original song class in case I need to reference it

//
//@Entity
//public class OldSong {
//    @Id
//    private String title;
//    private String artist;
//    private String album;
//    private String artworkUrl;
//    private int released;
//    private int popularity;
//    private String spotifyURI;
//
//    //use the OrderColumn annotation to make sure the ratings are kept in the proper order
//    @ElementCollection
//    @OrderColumn
//    private List<Integer> ratings;
//
//    public Song() {
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getArtist() {
//        return artist;
//    }
//
//    public void setArtist(String artist) {
//        this.artist = artist;
//    }
//
//    public String getAlbum() {
//        return album;
//    }
//
//    public void setAlbum(String album) {
//        this.album = album;
//    }
//
//    public String getArtworkUrl() {
//        return artworkUrl;
//    }
//
//    public void setArtworkUrl(String artworkUrl) {
//        this.artworkUrl = artworkUrl;
//    }
//
//    public int getReleased() {
//        return released;
//    }
//
//    public void setReleased(int released) {
//        this.released = released;
//    }
//
//    public int getPopularity() {
//        return popularity;
//    }
//
//    public void setPopularity(int popularity) {
//        this.popularity = popularity;
//    }
//
//    public String getSpotifyURI() {
//        return spotifyURI;
//    }
//
//    public void setSpotifyURI(String spotifyURI) {
//        this.spotifyURI = spotifyURI;
//    }
//
//    public List<Integer> getRatings() {
//        return ratings;
//    }
//
//    public void setRatings(List<Integer> ratings) {
//        this.ratings = ratings;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Song song = (Song) o;
//        return released == song.released && popularity == song.popularity && Objects.equals(title, song.title) && Objects.equals(artist, song.artist) && Objects.equals(album, song.album) && Objects.equals(artworkUrl, song.artworkUrl) && Objects.equals(spotifyURI, song.spotifyURI) && Objects.equals(ratings, song.ratings);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(title, artist, album, artworkUrl, released, popularity, spotifyURI, ratings);
//    }
//
//    @Override
//    public String toString() {
//        return "Song{" +
//                "title='" + title + '\'' +
//                ", artist='" + artist + '\'' +
//                ", album='" + album + '\'' +
//                ", artworkUrl='" + artworkUrl + '\'' +
//                ", released=" + released +
//                ", popularity=" + popularity +
//                ", spotifyURI='" + spotifyURI + '\'' +
//                ", ratings=" + ratings +
//                '}';
//    }
//}

package com.projectfloyd.Q1043.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "albums")
public class Album {

    @Id
    @GeneratedValue
    private int id;

    private String title;
    private int releaseYear;
    private int totalTracks;
    private String spotifyURI;
    private String albumArtworkURL;
    private double albumScore;

    @ManyToOne
    //@JsonBackReference
    private Artist artist;

    @OneToMany(mappedBy = "album")
    //@JsonManagedReference
    private List<Song> songs;

    //CONSTRUCTORS
    public Album() {
    }

    public Album(int id, String title, int releaseYear, int totalTracks, String spotifyURI, String albumArtworkURL, Double albumScore, Artist artist, List<Song> songs) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.totalTracks = totalTracks;
        this.spotifyURI = spotifyURI;
        this.albumArtworkURL = albumArtworkURL;

        if (albumScore != null) this.albumScore = albumScore;
        else this.albumScore = 0.0;

        this.artist = artist;
        this.songs = songs;
    }

    //GETTERS AND SETTERS
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public String getSpotifyURI() {
        return spotifyURI;
    }

    public void setSpotifyURI(String spotifyURI) {
        this.spotifyURI = spotifyURI;
    }

    public String getAlbumArtworkURL() {
        return albumArtworkURL;
    }

    public void setAlbumArtworkURL(String albumArtworkURL) {
        this.albumArtworkURL = albumArtworkURL;
    }

    public double getAlbumScore() { return albumScore; }

    public void setAlbumScore(Double albumScore) {
        if (albumScore == null) this.albumScore = 0.0;
        else this.albumScore = albumScore;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    //SERIALIZATION
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return id == album.id && releaseYear == album.releaseYear && totalTracks == album.totalTracks && Objects.equals(title, album.title) && Objects.equals(spotifyURI, album.spotifyURI) && Objects.equals(albumScore, album.albumScore) && Objects.equals(albumArtworkURL, album.albumArtworkURL) && Objects.equals(artist, album.artist) && Objects.equals(songs, album.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, releaseYear, totalTracks, spotifyURI, albumArtworkURL, albumScore, artist, songs);
    }

    //Use the toStringWithoutAlbums() method from the Artist class to avoid an infinite recursion while printing
    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", totalTracks=" + totalTracks +
                ", spotifyURI='" + spotifyURI + '\'' +
                ", albumArtworkURL='" + albumArtworkURL + '\'' +
                ", albumScore=" + albumScore +
                ", artist=" + artist.toStringWithoutAlbums() +
                ", songs=" + songs +
                '}';
    }

    //This is used so that when printing a Song object we don't get an infinite recursion.
    public String toStringWithoutSongs(){
        return "Album{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", totalTracks=" + totalTracks +
                ", spotifyURI='" + spotifyURI + '\'' +
                ", albumArtworkURL='" + albumArtworkURL + '\'' +
                ", albumScore=" + albumScore +
                ", artist=" + artist.toStringWithoutAlbums() +
                '}';
    }
}

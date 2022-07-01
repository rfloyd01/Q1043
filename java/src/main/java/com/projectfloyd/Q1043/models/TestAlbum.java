package com.projectfloyd.Q1043.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class TestAlbum {

    @Id
    @GeneratedValue
    private int id;
    private String title;
    private String artist;
    private int released;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    //@JsonIgnoreProperties(value = {"album"}) //don't include album info when grabbing songs as this will lead to an infinite loop
    @JsonManagedReference
    private List<TestSong> songs;

    public TestAlbum() {
    }

    public TestAlbum(int id, String title, String artist, int released, List<TestSong> songs) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.released = released;
        this.songs = songs;
    }

    public void addSong(TestSong testSong) {
        songs.add(testSong);
    }

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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getReleased() {
        return released;
    }

    public void setReleased(int released) {
        this.released = released;
    }

    public List<TestSong> getSongs() {
        return songs;
    }

    public void setSongs(List<TestSong> songs) {
        this.songs = songs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestAlbum testAlbum = (TestAlbum) o;
        return id == testAlbum.id && released == testAlbum.released && Objects.equals(title, testAlbum.title) && Objects.equals(artist, testAlbum.artist) && Objects.equals(songs, testAlbum.songs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, released, songs);
    }

    @Override
    public String toString() {
        return "TestAlbum{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", released=" + released +
                ", songs=" + songs +
                '}';
    }

    public String toStringWithoutSongs() {
        return "TestAlbum{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", released=" + released +
                '}';
    }
}

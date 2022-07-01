package com.projectfloyd.Q1043.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class TestSong {

    @Id
    @GeneratedValue
    private int id;
    private String artist;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private TestAlbum album;

    private int _2001_rank;
    private int _2002_rank;
    private int _2003_rank;
    private int compositeScore;
    private String uri;
    private String notes;

    public TestSong() {
    }

    public TestSong(int id, String name, String artist, TestAlbum album, int _2001_rank, int _2002_rank, int _2003_rank, int compositeScore, String uri, String notes) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this._2001_rank = _2001_rank;
        this._2002_rank = _2002_rank;
        this._2003_rank = _2003_rank;
        this.compositeScore = compositeScore;
        this.uri = uri;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public TestAlbum getAlbum() {
        return album;
    }

    public void setAlbum(TestAlbum album) {
        this.album = album;
    }

    public int get_2001_rank() {
        return _2001_rank;
    }

    public void set_2001_rank(int _2001_rank) {
        this._2001_rank = _2001_rank;
    }

    public int get_2002_rank() {
        return _2002_rank;
    }

    public void set_2002_rank(int _2002_rank) {
        this._2002_rank = _2002_rank;
    }

    public int get_2003_rank() {
        return _2003_rank;
    }

    public void set_2003_rank(int _2003_rank) {
        this._2003_rank = _2003_rank;
    }

    public int getCompositeScore() {
        return compositeScore;
    }

    public void setCompositeScore(int compositeScore) {
        this.compositeScore = compositeScore;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestSong testSong = (TestSong) o;
        return id == testSong.id && _2001_rank == testSong._2001_rank && _2002_rank == testSong._2002_rank && _2003_rank == testSong._2003_rank && compositeScore == testSong.compositeScore && Objects.equals(artist, testSong.artist) && Objects.equals(name, testSong.name) && Objects.equals(album, testSong.album) && Objects.equals(uri, testSong.uri) && Objects.equals(notes, testSong.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artist, name, album, _2001_rank, _2002_rank, _2003_rank, compositeScore, uri, notes);
    }

    @Override
    public String toString() {
        return "TestSong{" +
                "id=" + id +
                ", artist='" + artist + '\'' +
                ", name='" + name + '\'' +
                ", album='" + album.toStringWithoutSongs() + '\'' +
                ", _2001_rank=" + _2001_rank +
                ", _2002_rank=" + _2002_rank +
                ", _2003_rank=" + _2003_rank +
                ", compositeScore=" + compositeScore +
                ", uri='" + uri + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}

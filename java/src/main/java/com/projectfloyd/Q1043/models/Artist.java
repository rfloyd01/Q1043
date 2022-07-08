package com.projectfloyd.Q1043.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @OneToMany(mappedBy = "artist")
    //@JsonManagedReference
    private List<Album> albums;

    @Column(name="artist_score", columnDefinition = "float8 default 0.0")
    private double artistScore;

    @Column(name="total_ranked_songs", columnDefinition = "integer default 0")
    private int totalRankedSongs;

    @Column(name="notes", columnDefinition = "varchar(255) default ''")
    private String notes;

    //CONSTRUCTORS
    public Artist() {
    }

    public Artist(int id, String name, List<Album> albums, double artistScore, int totalRankedSongs, String notes) {
        this.id = id;
        this.name = name;
        this.albums = albums;
        this.artistScore = artistScore;
        this.totalRankedSongs = totalRankedSongs;
        this.notes = notes;
    }

    //GETTERS AND SETTERS
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

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public double getArtistScore() { return artistScore; }

    public void setArtistScore(double artistScore) { this.artistScore = artistScore; }

    public int getTotalRankedSongs() { return totalRankedSongs; }

    public void setTotalRankedSongs(int totalRankedSongs) { this.totalRankedSongs = totalRankedSongs; }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    //SERIALIZATION
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id == artist.id && artistScore == artist.artistScore && totalRankedSongs == artist.totalRankedSongs && Objects.equals(name, artist.name) && Objects.equals(albums, artist.albums) && Objects.equals(notes, artist.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, albums, artistScore, totalRankedSongs, notes);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", albums=" + albums +
                ", artistScore=" + artistScore +
                ", totalRankedSongs=" + totalRankedSongs +
                ", notes='" + notes + '\'' +
                '}';
    }

    //This is used so that when printing an Artist object we don't get an infinite recursion.
    public String toStringWithoutAlbums() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", artistScore=" + artistScore +
                ", totalRankedSongs=" + totalRankedSongs +
                ", notes='" + notes + '\'' +
                '}';
    }
}

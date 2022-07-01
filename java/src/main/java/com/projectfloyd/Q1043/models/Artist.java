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
    @JsonManagedReference
    private List<Album> albums;

    //CONSTRUCTORS
    public Artist() {
    }

    public Artist(int id, String name, List<Album> albums) {
        this.id = id;
        this.name = name;
        this.albums = albums;
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

    //SERIALIZATION
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id == artist.id && Objects.equals(name, artist.name) && Objects.equals(albums, artist.albums);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, albums);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", albums=" + albums +
                '}';
    }

    //This is used so that when printing an Artist object we don't get an infinite recursion.
    public String toStringWithoutAlbums() {
        return "Artist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

package com.projectfloyd.Q1043.models;

import java.util.Objects;

public class Year {

    //A simple data structure for seeing which years had the most songs ranked in them
    private int year;
    private int rankedTracks;

    public Year() {
    }

    public Year(int year, int rankedTracks) {
        this.year = year;
        this.rankedTracks = rankedTracks;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getRankedTracks() {
        return rankedTracks;
    }

    public void setRankedTracks(int rankedTracks) {
        this.rankedTracks = rankedTracks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Year year1 = (Year) o;
        return year == year1.year && rankedTracks == year1.rankedTracks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, rankedTracks);
    }

    @Override
    public String toString() {
        return "Year{" +
                "year=" + year +
                ", rankedTracks=" + rankedTracks +
                '}';
    }
}

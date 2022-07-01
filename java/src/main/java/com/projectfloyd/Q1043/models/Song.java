package com.projectfloyd.Q1043.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "songs")
public class Song {

    @Transient
    private static int totalYears = 21; //the current number of years of ranked data. This isn't saved in database

    //FIELDS
    @Id
    @GeneratedValue
    private int id;

    private String title;
    private String artist;

    @ManyToOne
    @JsonBackReference
    private Album album;

    private String spotifyURI;
    private int popularity;

    //Rank for each year of the Q1043 list. Each year gets its own variable as opposed to
    //storing the values in a list as it's much cleaner to work with. New columns can be added
    //in database every year. The @JsonIgnore annotation is used for these variables as they
    //aren't sent to the front end individually
    @JsonIgnore private int rank2001;
    @JsonIgnore private int rank2002;
    @JsonIgnore private int rank2003;
    @JsonIgnore private int rank2004;
    @JsonIgnore private int rank2005;
    @JsonIgnore private int rank2006;
    @JsonIgnore private int rank2007;
    @JsonIgnore private int rank2008;
    @JsonIgnore private int rank2009;
    @JsonIgnore private int rank2010;
    @JsonIgnore private int rank2011;
    @JsonIgnore private int rank2012;
    @JsonIgnore private int rank2013;
    @JsonIgnore private int rank2014;
    @JsonIgnore private int rank2015;
    @JsonIgnore private int rank2016;
    @JsonIgnore private int rank2017;
    @JsonIgnore private int rank2018;
    @JsonIgnore private int rank2019;
    @JsonIgnore private int rank2020;
    @JsonIgnore private int rank2021;

    //An array is used when passing ranking values via JSON for ease, but the @Transient annotation is used
    //as to not try and persist an array in the database.
    @Transient
    private int[] rankings;

    //overall data
    private double averageScore;
    private double overallScore;
    private int averageRank;
    private int overallRank;

    //notes that are used for storing anything interesting about the song, or the data itself
    private String notes;

    //CONSTRUCTORS
    public Song() {
    }

    public Song(int id, String title, String artist, Album album, String spotifyURI, int popularity, int[] rankings, double averageScore, double overallScore, int averageRank, int overallRank, String notes) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.spotifyURI = spotifyURI;
        this.popularity = popularity;
        this.rankings = rankings;
        this.averageScore = averageScore;
        this.overallScore = overallScore;
        this.averageRank = averageRank;
        this.overallRank = overallRank;
        this.notes = notes;

        convertRankingsArray();
    }

    //GETTERS, SETTERS
    public static int getTotalYears() {
        return totalYears;
    }

    public static void setTotalYears(int totalYears) {
        Song.totalYears = totalYears;
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

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getSpotifyURI() {
        return spotifyURI;
    }

    public void setSpotifyURI(String spotifyURI) {
        this.spotifyURI = spotifyURI;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int[] getRankings() {
        return rankings;
    }

    public void setRankings(int[] rankings) {
        this.rankings = rankings;
    }

    public void convertRankingsArray() {
        //takes the rankings variable and extracts its values into the appropriate individual
        //ranking variables.

        //if the rankings array doesn't have the correct number of data points then we don't initialize the rankings
        if (rankings.length == totalYears) {
            rank2001 = this.rankings[0];
            rank2002 = this.rankings[1];
            rank2003 = this.rankings[2];
            rank2004 = this.rankings[3];
            rank2005 = this.rankings[4];
            rank2006 = this.rankings[5];
            rank2007 = this.rankings[6];
            rank2008 = this.rankings[7];
            rank2009 = this.rankings[8];
            rank2010 = this.rankings[9];
            rank2011 = this.rankings[10];
            rank2012 = this.rankings[11];
            rank2013 = this.rankings[12];
            rank2014 = this.rankings[13];
            rank2015 = this.rankings[14];
            rank2016 = this.rankings[15];
            rank2017 = this.rankings[16];
            rank2018 = this.rankings[17];
            rank2019 = this.rankings[18];
            rank2020 = this.rankings[19];
            rank2021 = this.rankings[20];
        }
    }

    public void createRankingsArray() {
        //the opposite of the above function, it's for creating our ranking array when pulling a song from the db.
        this.rankings = new int[21];

        this.rankings[0] = rank2001;
        this.rankings[1] = rank2002;
        this.rankings[2] = rank2003;
        this.rankings[3] = rank2004;
        this.rankings[4] = rank2005;
        this.rankings[5] = rank2006;
        this.rankings[6] = rank2007;
        this.rankings[7] = rank2008;
        this.rankings[8] = rank2009;
        this.rankings[9] = rank2010;
        this.rankings[10] = rank2011;
        this.rankings[11] = rank2012;
        this.rankings[12] = rank2013;
        this.rankings[13] = rank2014;
        this.rankings[14] = rank2015;
        this.rankings[15] = rank2016;
        this.rankings[16] = rank2017;
        this.rankings[17] = rank2018;
        this.rankings[18] = rank2019;
        this.rankings[19] = rank2020;
        this.rankings[20] = rank2021;

    }

    public int getRank2001() {
        return rank2001;
    }

    public void setRank2001(int rank2001) {
        this.rank2001 = rank2001;
    }

    public int getRank2002() {
        return rank2002;
    }

    public void setRank2002(int rank2002) {
        this.rank2002 = rank2002;
    }

    public int getRank2003() {
        return rank2003;
    }

    public void setRank2003(int rank2003) {
        this.rank2003 = rank2003;
    }

    public int getRank2004() {
        return rank2004;
    }

    public void setRank2004(int rank2004) {
        this.rank2004 = rank2004;
    }

    public int getRank2005() {
        return rank2005;
    }

    public void setRank2005(int rank2005) {
        this.rank2005 = rank2005;
    }

    public int getRank2006() {
        return rank2006;
    }

    public void setRank2006(int rank2006) {
        this.rank2006 = rank2006;
    }

    public int getRank2007() {
        return rank2007;
    }

    public void setRank2007(int rank2007) {
        this.rank2007 = rank2007;
    }

    public int getRank2008() {
        return rank2008;
    }

    public void setRank2008(int rank2008) {
        this.rank2008 = rank2008;
    }

    public int getRank2009() {
        return rank2009;
    }

    public void setRank2009(int rank2009) {
        this.rank2009 = rank2009;
    }

    public int getRank2010() {
        return rank2010;
    }

    public void setRank2010(int rank2010) {
        this.rank2010 = rank2010;
    }

    public int getRank2011() {
        return rank2011;
    }

    public void setRank2011(int rank2011) {
        this.rank2011 = rank2011;
    }

    public int getRank2012() {
        return rank2012;
    }

    public void setRank2012(int rank2012) {
        this.rank2012 = rank2012;
    }

    public int getRank2013() {
        return rank2013;
    }

    public void setRank2013(int rank2013) {
        this.rank2013 = rank2013;
    }

    public int getRank2014() {
        return rank2014;
    }

    public void setRank2014(int rank2014) {
        this.rank2014 = rank2014;
    }

    public int getRank2015() {
        return rank2015;
    }

    public void setRank2015(int rank2015) {
        this.rank2015 = rank2015;
    }

    public int getRank2016() {
        return rank2016;
    }

    public void setRank2016(int rank2016) {
        this.rank2016 = rank2016;
    }

    public int getRank2017() {
        return rank2017;
    }

    public void setRank2017(int rank2017) {
        this.rank2017 = rank2017;
    }

    public int getRank2018() {
        return rank2018;
    }

    public void setRank2018(int rank2018) {
        this.rank2018 = rank2018;
    }

    public int getRank2019() {
        return rank2019;
    }

    public void setRank2019(int rank2019) {
        this.rank2019 = rank2019;
    }

    public int getRank2020() {
        return rank2020;
    }

    public void setRank2020(int rank2020) {
        this.rank2020 = rank2020;
    }

    public int getRank2021() {
        return rank2021;
    }

    public void setRank2021(int rank2021) {
        this.rank2021 = rank2021;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public int getAverageRank() {
        return averageRank;
    }

    public void setAverageRank(int averageRank) {
        this.averageRank = averageRank;
    }

    public int getOverallRank() {
        return overallRank;
    }

    public void setOverallRank(int overallRank) {
        this.overallRank = overallRank;
    }

    public String getNotes() { return notes; }

    public void setNotes(String notes) { this.notes = notes; }

    //SERIALIZATION
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && popularity == song.popularity && rank2001 == song.rank2001 && rank2002 == song.rank2002 && rank2003 == song.rank2003 && rank2004 == song.rank2004 && rank2005 == song.rank2005 && rank2006 == song.rank2006 && rank2007 == song.rank2007 && rank2008 == song.rank2008 && rank2009 == song.rank2009 && rank2010 == song.rank2010 && rank2011 == song.rank2011 && rank2012 == song.rank2012 && rank2013 == song.rank2013 && rank2014 == song.rank2014 && rank2015 == song.rank2015 && rank2016 == song.rank2016 && rank2017 == song.rank2017 && rank2018 == song.rank2018 && rank2019 == song.rank2019 && rank2020 == song.rank2020 && rank2021 == song.rank2021 && Objects.equals(title, song.title) && Objects.equals(artist, song.artist) && Objects.equals(album, song.album) && Objects.equals(spotifyURI, song.spotifyURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, artist, album, spotifyURI, popularity, rank2001, rank2002, rank2003, rank2004, rank2005, rank2006, rank2007, rank2008, rank2009, rank2010, rank2011, rank2012, rank2013, rank2014, rank2015, rank2016, rank2017, rank2018, rank2019, rank2020, rank2021);
    }

    //Use the toStringWithoutSongs() method from the Album class to avoid an infinite recursion while printing
    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album=" + album.toStringWithoutSongs() +
                ", spotifyURI='" + spotifyURI + '\'' +
                ", popularity=" + popularity +
                ", rankings=" + Arrays.toString(rankings) +
                ", averageScore=" + averageScore +
                ", overallScore=" + overallScore +
                ", averageRank=" + averageRank +
                ", overallRank=" + overallRank +
                ", notes=" + notes +
                '}';
    }
}

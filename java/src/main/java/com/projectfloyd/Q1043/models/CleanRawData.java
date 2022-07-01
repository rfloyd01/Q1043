package com.projectfloyd.Q1043.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "clean_raw_data")
public class CleanRawData {

    //I couldn't quickly figure out how to make to separate tables in the same database with a single entity so I
    //decided to just make a separate entity to store the cleaned raw data, even though they're functionally the same.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String rawData;

    public CleanRawData() {
    }

    public CleanRawData(int id, String rawData) {
        this.id = id;
        this.rawData = rawData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CleanRawData that = (CleanRawData) o;
        return id == that.id && Objects.equals(rawData, that.rawData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rawData);
    }

    @Override
    public String toString() {
        return "RawData{" +
                "id=" + id +
                ", rawData='" + rawData + '\'' +
                '}';
    }
}

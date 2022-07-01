package com.projectfloyd.Q1043.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "raw_data")
public class RawData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String rawData;

    public RawData() {
    }

    public RawData(int id, String rawData) {
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
        RawData rawData1 = (RawData) o;
        return id == rawData1.id && Objects.equals(rawData, rawData1.rawData);
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

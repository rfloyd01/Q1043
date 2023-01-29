package com.projectfloyd.Q1043.models;

import java.util.Objects;

public class PlainText {
    private String contents = "";

    public PlainText() {
    }

    public PlainText(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlainText plainText = (PlainText) o;
        return Objects.equals(contents, plainText.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contents);
    }

    @Override
    public String toString() {
        return "PlainText{" +
                "contents='" + contents + '\'' +
                '}';
    }
}

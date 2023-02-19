package com.projectfloyd.Q1043.models;

import java.util.Objects;

public class CoinValue {
    //acts like a struct, holds a numerical grade for a coin (i.e. G-4, VF-20, etc.)
    //and its corresponding red book value
    public int grade;
    public double value;

    public CoinValue() {
    }

    public CoinValue(int grade, double value) {
        this.grade = grade;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinValue coinValue = (CoinValue) o;
        return grade == coinValue.grade && value == coinValue.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade, value);
    }

    @Override
    public String toString() {
        return "CoinValue{" +
                "grade=" + grade +
                ", value=" + value +
                '}';
    }
}

package com.projectfloyd.Q1043.models;

import java.util.Objects;

public class FrontendCoin {
    //similar to the redbook coin model, but holds the value of the
    //grade of the coin, along with its current bid from GreatCollections
    //and corresponding value based on grade and details

    private String coinType;
    private String coinName;
    private Integer manufactureYear;
    private Integer mintage;
    private Integer grade;
    private String variant;
    private Double currentBid;
    private Integer redbookValue; //should consider changing this to a float or double
    private Boolean details;
    private String mint;

    public FrontendCoin() {
    }

    public FrontendCoin(String coinType, String coinName, Integer manufactureYear, Integer mintage, Integer grade, String variant, Double currentBid, Integer redbookValue, Boolean details, String mint) {
        this.coinType = coinType;
        this.coinName = coinName;
        this.manufactureYear = manufactureYear;
        this.mintage = mintage;
        this.grade = grade;
        this.variant = variant;
        this.currentBid = currentBid;
        this.redbookValue = redbookValue;
        this.details = details;
        this.mint = mint;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public Integer getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(Integer manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public Integer getMintage() {
        return mintage;
    }

    public void setMintage(Integer mintage) {
        this.mintage = mintage;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public Double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(Double currentBid) {
        this.currentBid = currentBid;
    }

    public Integer getRedbookValue() {
        return redbookValue;
    }

    public void setRedbookValue(Integer redbookValue) {
        this.redbookValue = redbookValue;
    }

    public Boolean getDetails() {
        return details;
    }

    public void setDetails(Boolean details) {
        this.details = details;
    }

    public String getMint() {
        return mint;
    }

    public void setMint(String mint) {
        this.mint = mint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrontendCoin that = (FrontendCoin) o;
        return Objects.equals(coinType, that.coinType) && Objects.equals(coinName, that.coinName) && Objects.equals(manufactureYear, that.manufactureYear) && Objects.equals(mintage, that.mintage) && Objects.equals(grade, that.grade) && Objects.equals(variant, that.variant) && Objects.equals(currentBid, that.currentBid) && Objects.equals(redbookValue, that.redbookValue) && Objects.equals(details, that.details) && Objects.equals(mint, that.mint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coinType, coinName, manufactureYear, mintage, grade, variant, currentBid, redbookValue, details, mint);
    }

    @Override
    public String toString() {
        return "FrontendCoin{" +
                "coinType='" + coinType + '\'' +
                ", coinName='" + coinName + '\'' +
                ", manufactureYear=" + manufactureYear +
                ", mintage=" + mintage +
                ", grade=" + grade +
                ", variant='" + variant + '\'' +
                ", currentBid=" + currentBid +
                ", redbookValue=" + redbookValue +
                ", details=" + details +
                ", mint='" + mint + '\'' +
                '}';
    }
}

package com.projectfloyd.Q1043.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "coins")
public class RedbookCoin {

    //use the Integer class instead of the int built-in type because some values are allowed
    //to be null in the table, but you can't have a null int

    @Id
    private int coin_id;

    @Column(nullable = false)
    private String coinType;

    @Column(nullable = false)
    private String coinName;

    @Column(nullable = false)
    private Integer manufacture_year;

    private Long mintage; //the mintage on some coins gets as high as 7,000,000,000 so a long is needed

    private Double ag_3;
    private Double g_4;
    private Double vg_8;
    private Double f_12;
    private Double vf_20;
    private Double xf_40;
    private Double au_50;
    private Double ms_60;
    private Double ms_63;
    private Double ms_65;
    private Double pf_63;
    private Double pf_65;

    @Transient
    private ArrayList<CoinValue> coinValues;

    private String variant;
    private String notes;
    private String mint;

    public RedbookCoin() {
    }

    public RedbookCoin(int coin_id, String coinType, String coinName, Integer manufacture_year, Long mintage, Double ag_3, Double g_4, Double vg_8, Double f_12, Double vf_20, Double xf_40, Double au_50, Double ms_60, Double ms_63, Double ms_65, Double pf_63, Double pf_65, String variant, String notes, String mint) {
        this.coin_id = coin_id;
        this.coinType = coinType;
        this.coinName = coinName;
        this.manufacture_year = manufacture_year;
        this.mintage = mintage;
        this.ag_3 = ag_3;
        this.g_4 = g_4;
        this.vg_8 = vg_8;
        this.f_12 = f_12;
        this.vf_20 = vf_20;
        this.xf_40 = xf_40;
        this.au_50 = au_50;
        this.ms_60 = ms_60;
        this.ms_63 = ms_63;
        this.ms_65 = ms_65;
        this.pf_63 = pf_63;
        this.pf_65 = pf_65;
        this.variant = variant;
        this.notes = notes;
        this.mint = mint;

        //change any null grade values to 0 and put all values into coinValues array
        this.setValueArray();
    }

    public void setValueArray() {
        coinValues = new ArrayList<>();

        //add in a value of 0 for a coin with grade 0, this acts as our minimum value
        //for extrapolation
        coinValues.add(new CoinValue(0, 0));

        if (ag_3 == null) ag_3 = 0.0;
        else coinValues.add(new CoinValue(3, ag_3));

        if (g_4 == null) g_4 = 0.0;
        else coinValues.add(new CoinValue(4, g_4));

        if (vg_8 == null) vg_8= 0.0;
        else coinValues.add(new CoinValue(8, vg_8));

        if (f_12 == null) f_12 = 0.0;
        else coinValues.add(new CoinValue(12, f_12));

        if (vf_20 == null) vf_20 = 0.0;
        else coinValues.add(new CoinValue(20, vf_20));

        if (xf_40 == null) xf_40 = 0.0;
        else coinValues.add(new CoinValue(40, xf_40));

        if (au_50 == null) au_50 = 0.0;
        else coinValues.add(new CoinValue(50, au_50));

        if (ms_60 == null) ms_60 = 0.0;
        else coinValues.add(new CoinValue(60, ms_60));

        if (ms_63 == null) ms_63 = 0.0;
        else coinValues.add(new CoinValue(63, ms_63));

        if (ms_65 == null) ms_65 = 0.0;
        else coinValues.add(new CoinValue(65, ms_65));

        //Proof values are a little weird in that they have the
        //same numbers of MS coins. To make things easier, I'm going
        //to say that a proof coin is 11 points higher then it's MS
        //equivalent. The maximum coin grade in this case is pf-70 = 81
        if (pf_63 == null) pf_63 = 0.0;
        else coinValues.add(new CoinValue(74, pf_63));

        if (pf_65 == null) pf_65 = 0.0;
        else coinValues.add(new CoinValue(76, pf_65));

        //Add in a value for MS-70, the highest grade any coin can get (proof strikes didn't exist until the mid-1800's).
        //Unlike the value for a 0 graded coin the MS-70 value is much more subjective. Depending on the coin, it can be as
        //low as $30.00, or as high as $1,500,000.00. Because of this, a separate function is defined to
        //help figure out a reasonable value.
        coinValues.add(new CoinValue(70, this.calculatePF70Value()));
    }

    private int calculatePF70Value() {
        //TODO: Flesh this out at somepoint, should return values based on a combination
        //      of the release year and mintage of the coin

        if (manufacture_year < 1800) {
            //coins from this era are rare, anything in the PF range will be worth
            //hundreds of thousands to over a million dollars. No need to look at mintage
            return 1000000;
        }
        else if (manufacture_year < 1850) {
            return 500000;
        }
        else if (manufacture_year < 1900) {
            return 250000;
        }
        else if (manufacture_year < 1950) {
            return 50000;
        }
        else return 1000;
    }

    public ArrayList<CoinValue> getCoinValues() {
        return coinValues;
    }

    public int getCoin_id() {
        return coin_id;
    }

    public void setCoin_id(int coin_id) {
        this.coin_id = coin_id;
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

    public Integer getManufacture_year() {
        return manufacture_year;
    }

    public void setManufacture_year(Integer manufacture_year) {
        this.manufacture_year = manufacture_year;
    }

    public Long getMintage() {
        return mintage;
    }

    public void setMintage(Long mintage) { this.mintage = mintage; }

    public Double getAg_3() {
        return ag_3;
    }

    public void setAg_3(Double ag_3) {
        this.ag_3 = ag_3;
    }

    public Double getG_4() {
        return g_4;
    }

    public void setG_4(Double g_4) {
        this.g_4 = g_4;
    }

    public Double getVg_8() {
        return vg_8;
    }

    public void setVg_8(Double vg_8) {
        this.vg_8 = vg_8;
    }

    public Double getF_12() {
        return f_12;
    }

    public void setF_12(Double f_12) {
        this.f_12 = f_12;
    }

    public Double getVf_20() {
        return vf_20;
    }

    public void setVf_20(Double vf_20) {
        this.vf_20 = vf_20;
    }

    public Double getXf_40() {
        return xf_40;
    }

    public void setXf_40(Double xf_40) {
        this.xf_40 = xf_40;
    }

    public Double getAu_50() {
        return au_50;
    }

    public void setAu_50(Double au_50) {
        this.au_50 = au_50;
    }

    public Double getMs_60() {
        return ms_60;
    }

    public void setMs_60(Double ms_60) {
        this.ms_60 = ms_60;
    }

    public Double getMs_63() {
        return ms_63;
    }

    public void setMs_63(Double ms_63) {
        this.ms_63 = ms_63;
    }

    public Double getMs_65() {
        return ms_65;
    }

    public void setMs_65(Double ms_65) {
        this.ms_65 = ms_65;
    }

    public Double getPf_63() {
        return pf_63;
    }

    public void setPf_63(Double pf_63) {
        this.pf_63 = pf_63;
    }

    public Double getPf_65() {
        return pf_65;
    }

    public void setPf_65(Double pf_65) {
        this.pf_65 = pf_65;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        RedbookCoin that = (RedbookCoin) o;
        return coin_id == that.coin_id && Objects.equals(coinType, that.coinType) && Objects.equals(coinName, that.coinName) && Objects.equals(manufacture_year, that.manufacture_year) && Objects.equals(mintage, that.mintage) && Objects.equals(ag_3, that.ag_3) && Objects.equals(g_4, that.g_4) && Objects.equals(vg_8, that.vg_8) && Objects.equals(f_12, that.f_12) && Objects.equals(vf_20, that.vf_20) && Objects.equals(xf_40, that.xf_40) && Objects.equals(au_50, that.au_50) && Objects.equals(ms_60, that.ms_60) && Objects.equals(ms_63, that.ms_63) && Objects.equals(ms_65, that.ms_65) && Objects.equals(pf_63, that.pf_63) && Objects.equals(pf_65, that.pf_65) && Objects.equals(variant, that.variant) && Objects.equals(notes, that.notes) && Objects.equals(mint, that.mint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coin_id, coinType, coinName, manufacture_year, mintage, ag_3, g_4, vg_8, f_12, vf_20, xf_40, au_50, ms_60, ms_63, ms_65, pf_63, pf_65, variant, notes, mint);
    }

    @Override
    public String toString() {
        return "RedbookCoin{" +
                "coin_id=" + coin_id +
                ", coinType='" + coinType + '\'' +
                ", coinName='" + coinName + '\'' +
                ", manufacture_year=" + manufacture_year +
                ", mintage=" + mintage +
                ", ag_3=" + ag_3 +
                ", g_4=" + g_4 +
                ", vg_8=" + vg_8 +
                ", f_12=" + f_12 +
                ", vf_20=" + vf_20 +
                ", xf_40=" + xf_40 +
                ", au_50=" + au_50 +
                ", ms_60=" + ms_60 +
                ", ms_63=" + ms_63 +
                ", ms_65=" + ms_65 +
                ", pf_63=" + pf_63 +
                ", pf_65=" + pf_65 +
                ", variant='" + variant + '\'' +
                ", notes='" + notes + '\'' +
                ", mint='" + mint + '\'' +
                '}';
    }

}

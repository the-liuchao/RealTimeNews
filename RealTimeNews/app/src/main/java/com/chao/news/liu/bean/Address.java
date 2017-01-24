package com.chao.news.liu.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by hp on 2017/1/18.
 */
@Table(name = "address")
public class Address implements Serializable, Comparable<Address> {


    @Column(name = "id", isId = true)
    private int mId;
    @Column(name = "pronvice")
    private String mPronvice;
    @Column(name = "city")
    private String mCity;
    @Column(name = "district")
    private String mDistrict;
    @Column(name = "type")
    private String mType;
    @Column(name = "pinying")
    private String mCityPY;
    private char letter;

    public Address() {
    }

    public Address(String mPronvice, String mDistrict, String mCity, String mType) {
        this.mPronvice = mPronvice;
        this.mCity = mCity;
        this.mDistrict = mDistrict;
        this.mType = mType;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmPronvice() {
        return mPronvice;
    }

    public void setmPronvice(String mPronvice) {
        this.mPronvice = mPronvice;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmCityPY() {
        return mCityPY;
    }

    public void setmCityPY(String mCityPY) {
        this.mCityPY = mCityPY;
    }

    public String getmDistrict() {
        return mDistrict;
    }

    public void setmDistrict(String mDistrict) {
        this.mDistrict = mDistrict;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public char getLetter() {
        this.letter = mCityPY.toCharArray()[0];
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return "Address{" +
                "mId=" + mId +
                ", mPronvice='" + mPronvice + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mDistrict='" + mDistrict + '\'' +
                ", mType='" + mType + '\'' +
                '}';
    }

    @Override
    public int compareTo(Address another) {
        if (getLetter() < another.getLetter()) {
            return -1;
        } else if (letter > another.getLetter()) {
            return 1;
        }
        return 0;
    }
}

package com.chao.news.liu.bean;

import java.io.Serializable;

/**
 * Created by hp on 2017/1/15.
 */

public class CurrLocation implements Serializable {
    private String mProvince;
    private String mLatitude;
    private String mLongitude;
    private String mCity;
    private String mDistrict;

    public String getmDistrict() {
        return mDistrict;
    }

    public void setmDistrict(String mDistrict) {
        this.mDistrict = mDistrict;
    }

    public String getmProvince() {
        return mProvince;
    }

    public void setmProvince(String mProvince) {
        this.mProvince = mProvince;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    @Override
    public String toString() {
        return "CurrLocation{" +
                "mProvince='" + mProvince + '\'' +
                ", mLatitude='" + mLatitude + '\'' +
                ", mLongitude='" + mLongitude + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mDistrict='" + mDistrict + '\'' +
                '}';
    }
}

package com.chao.news.liu.bean;

import org.json.JSONObject;

/**
 * Created by hp on 2017/1/22.
 */

public class PostCode {

    public String mPostNumber;
    public String mProvince;
    public String mCity;
    public String mDistrict;
    public String mAddress;
    public String mJD;

    public PostCode parser(JSONObject json) {
        if (json != null) {
            mPostNumber = json.optString("postnumber");
            mProvince = json.optString("province");
            mCity = json.optString("city");
            mDistrict = json.optString("district");
            mAddress = json.optString("address");
            mJD = json.optString("jd");
        }
        return this;
    }

    @Override
    public String toString() {
        return "PostCode{" +
                "mPostNumber='" + mPostNumber + '\'' +
                ", mProvince='" + mProvince + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mDistrict='" + mDistrict + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mJD='" + mJD + '\'' +
                '}';
    }
}

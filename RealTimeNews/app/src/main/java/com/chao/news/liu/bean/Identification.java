package com.chao.news.liu.bean;

import org.json.JSONObject;

/**
 * Created by hp on 2017/1/22.
 */

public class Identification {
    public String mSex;
    public String mBirthday;
    public String mAddress;

    public Identification parser(JSONObject json) {
        if (null != json) {
            String sex = json.optString("sex");
            this.mSex = "M" .equals(sex) ? "男" : "F" .equals(sex) ? "女" : "其他";
            this.mBirthday = json.optString("birthday");
            this.mAddress = json.optString("address");
        }
        return this;
    }

    @Override
    public String toString() {
        return "Identification{" +
                "mSex='" + mSex + '\'' +
                ", mBirthday='" + mBirthday + '\'' +
                ", mAddress='" + mAddress + '\'' +
                '}';
    }
}

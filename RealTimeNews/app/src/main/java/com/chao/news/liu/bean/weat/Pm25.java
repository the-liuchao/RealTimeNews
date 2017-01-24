package com.chao.news.liu.bean.weat;

import org.json.JSONObject;

/**
 * Created by hp on 2017/1/16.
 */

public class Pm25 {

    public String mKey;
    public int mShow_desc;
    public Pm25Detail mPm25;
    public String mDateTime;
    public String mCityName;

    public boolean mIsEmpty;


    public Pm25 parser(JSONObject json) {
        if (null == json || "".equals(json)) {
            mIsEmpty = true;
            mPm25 = new Pm25Detail();
            return this;
        }
        mKey = json.optString("key");
        mShow_desc = json.optInt("show_desc");
        mPm25 = new Pm25Detail().paraser(json.optJSONObject("pm25"));
        mDateTime = json.optString("dateTime");
        mCityName = json.optString("cityName");
        return this;
    }
}

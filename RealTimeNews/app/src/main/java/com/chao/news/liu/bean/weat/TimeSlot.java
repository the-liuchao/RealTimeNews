package com.chao.news.liu.bean.weat;

import org.json.JSONArray;

/**
 * Created by hp on 2017/1/18.
 */

public class TimeSlot {
    public String mMinTemp;
    public String mMaxTemp;
    public String mWind;
    public String mWindDer;
    public String mWeather;
    public String mTime;

    public TimeSlot parser(JSONArray json) {
        if (null == json) return this;
        mMaxTemp = String.valueOf(json.opt(0));
        mWeather = String.valueOf(json.opt(1));
        mMinTemp = String.valueOf(json.opt(2));
        mWindDer = String.valueOf(json.opt(3));
        mWind = String.valueOf(json.opt(4));
        mTime = String.valueOf(json.opt(5));
        return this;
    }
}

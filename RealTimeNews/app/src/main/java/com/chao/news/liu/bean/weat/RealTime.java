package com.chao.news.liu.bean.weat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 2017/1/16.
 */
public class RealTime {
    public Wind mWind;
    public Weather0 mWeather;
    public String mTime;
    public String mDataUptime;
    public String mDate;
    public String mCity_code;
    public String mCity_name;
    public String mWeek;
    public String mMoon;

    public RealTime parser(JSONObject json) throws JSONException {
        if (null == json) return this;
        mWind = new Wind().parser(json.optJSONObject("wind"));
        mWeather = new Weather0().parser(json.optJSONObject("weather"));
        mTime = json.optString("time");
        mDataUptime = json.optString("dataUptime");
        mDate = json.optString("date");
        mCity_code = json.optString("city_code");
        mCity_name = json.optString("city_name");
        mWeek = json.optString("week");
        mMoon = json.optString("moon");
        return this;
    }
}

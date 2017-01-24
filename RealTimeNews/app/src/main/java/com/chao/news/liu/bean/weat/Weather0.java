package com.chao.news.liu.bean.weat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hp on 2017/1/16.
 */

public class Weather0 {

    public String mHumidity;
    public String mImg;
    public String mInfo;
    public String mTemperature;

    public Weather0 parser(JSONObject json) throws JSONException {
        mHumidity = json.getString("humidity");
        mImg = json.getString("img");
        mInfo = json.getString("info");
        mTemperature = json.getString("temperature");
        return this;
    }
}

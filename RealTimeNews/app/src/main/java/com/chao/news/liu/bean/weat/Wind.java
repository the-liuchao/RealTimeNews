package com.chao.news.liu.bean.weat;

import org.json.JSONObject;

/**
 * Created by hp on 2017/1/16.
 */

public class Wind {

    public String mWindspeed;
    public String mDirect;
    public String mPower;
    public String mOffset;

    public Wind parser(JSONObject json) {
        if(json==null)return this;
        mWindspeed = json.optString("windspeed");
        mDirect = json.optString("direct");
        mPower = json.optString("power");
        mOffset = json.optString("offset");
        return this;
    }
}

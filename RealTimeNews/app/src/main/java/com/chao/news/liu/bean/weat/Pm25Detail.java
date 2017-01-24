package com.chao.news.liu.bean.weat;

import org.json.JSONObject;

/**
 * Created by hp on 2017/1/18.
 */

public class Pm25Detail {
    public String mPm10;
    public String mLevel;
    public String mQuality;
    public String mCurPm;
    public String mPm25;
    public String mDes;


    public Pm25Detail paraser(JSONObject json) {
        if (null == json) return this;
        mPm10 = json.optString("pm10");
        mLevel = json.optString("level");
        mQuality = json.optString("quality");
        mCurPm = json.optString("curPm");
        mPm25 = json.optString("pm25");
        mDes = json.optString("des");
        return this;
    }
}

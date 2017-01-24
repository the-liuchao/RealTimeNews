package com.chao.news.liu.bean.weat;

import org.json.JSONObject;

/**
 * Created by hp on 2017/1/16.
 */

public class Info {

    public TimeSlot mNights;
    public TimeSlot mDays;
    public TimeSlot mDawns;

    public Info parser(JSONObject json) {
        if (null == json) return this;
        mNights = new TimeSlot().parser(json.optJSONArray("night"));
        mDays = new TimeSlot().parser(json.optJSONArray("day"));
        mDawns = new TimeSlot().parser(json.optJSONArray("dawn"));
        return this;
    }

}

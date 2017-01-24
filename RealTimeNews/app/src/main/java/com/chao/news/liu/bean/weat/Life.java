package com.chao.news.liu.bean.weat;

import org.json.JSONObject;

/**
 * Created by hp on 2017/1/16.
 */

public class Life {

    public String mDate;
    public LifeInfo mInfo;

    public Life parser(JSONObject json) {
        if (json == null) return this;
        mDate = json.optString("date");
        mInfo = new LifeInfo().parser(json.optJSONObject("info"));
        return this;
    }
}

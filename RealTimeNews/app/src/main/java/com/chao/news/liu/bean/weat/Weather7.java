package com.chao.news.liu.bean.weat;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/1/16.
 */

public class Weather7 {

   public List<OneDay> mOneDays;

    public Weather7 parser(JSONArray json) {
        if (null == json) return this;
        mOneDays = new ArrayList<>();
        int len = json.length();
        for (int i = 0; i < len; i++) {
            mOneDays.add(new OneDay().parser(json.optJSONObject(i)));
        }
        return this;
    }

}

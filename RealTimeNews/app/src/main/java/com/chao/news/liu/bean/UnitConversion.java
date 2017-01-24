package com.chao.news.liu.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hp on 2017/1/22.
 */

public class UnitConversion implements Serializable {

    public int mUnit;
    public String mName;
    public String mValue;

    public UnitConversion parser(JSONArray json) {
        try {
            if (null == json) return this;
            int len = json.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj = json.getJSONObject(i);
                mUnit = obj.optInt("unit");
                mName = obj.optString("name");
                mValue = obj.optString("value");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return this;
        }
        return this;
    }

    @Override
    public String toString() {
        return "UnitConversion{" +
                "mUnit=" + mUnit +
                ", mName='" + mName + '\'' +
                ", mValue='" + mValue + '\'' +
                '}';
    }
}

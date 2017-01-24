package com.chao.news.liu.bean;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 作者：guojuan
 * 创建时间：2017/1/23 11:26
 * 功能描述：MD5实体类
 */


public class MD5 {
    public String mMd5;
    public String mMd5Src;

    public MD5 parser(JSONObject json) {
        if (null != json) {
            try {
                JSONObject object = json.getJSONObject("result");
                Log.e("MD5","=====error_code:"+json.getJSONObject("error_code"));
                this.mMd5 = object.optString("md5");
                this.mMd5Src = object.optString("md5_src");
            } catch (JSONException e) {
                e.printStackTrace();
                return this;
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "MD5{" +
                "mMd5='" + mMd5 + '\'' +
                ", mMd5Src='" + mMd5Src + '\'' +
                '}';
    }
}

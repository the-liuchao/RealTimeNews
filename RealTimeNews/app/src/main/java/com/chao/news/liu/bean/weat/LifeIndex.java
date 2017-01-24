package com.chao.news.liu.bean.weat;

import android.text.TextUtils;

import org.json.JSONArray;

/**
 * Created by hp on 2017/1/18.
 */

public class LifeIndex {
    public String mTip;
    public String mDesc;
    public String mType;

    public LifeIndex(String mType) {
        this.mType = mType;
    }

    public LifeIndex parser(JSONArray json) {
        if (null != json) {
            mTip = String.valueOf(json.opt(0));
            mDesc = String.valueOf(json.opt(1));
        }
        if (TextUtils.isEmpty(mDesc)) mDesc = "暂无数据";
        if (TextUtils.isEmpty(mTip)) mTip = "无";
        return this;
    }
}

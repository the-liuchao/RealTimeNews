package com.chao.news.liu.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：guojuan
 * 创建时间：2017/1/23 16:53
 * 功能描述：健康知识列表
 */

public class LoreList {
    public int mTotal;//返回记录数

    public int mId;//ID编码
    public String mDescription;//简介
    public String mImg;//图片
    public String mKeywords;//关键词
    public String mTitle;//标题
    public int mLoreclass;//分类
    public long mReleaseTime;//发布时间
    public int mFcount;//收藏数
    public int mCount;//访问数

    private List<LoreList> mLoreList = new ArrayList<>();

    public List<LoreList> parserLoreLists(JSONArray jsonArray){
        if (jsonArray == null || jsonArray.length() == 0) return null;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                this.mId = obj.optInt("id");
                this.mDescription = obj.optString("description");
                this.mImg = obj.optString("img");
                this.mKeywords = obj.optString("keywords");
                this.mTitle = obj.optString("title");
                this.mLoreclass = obj.optInt("loreclass");
                this.mReleaseTime = obj.optLong("loreclass");
                this.mFcount = obj.optInt("fcount");
                this.mCount = obj.optInt("count");
                mLoreList.add(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mLoreList;
    }

    public int parserTotal(JSONObject object){
        if (object == null) return 0;
        try {
          this.mTotal = object.getInt("total");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mTotal;
    }

}

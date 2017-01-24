package com.chao.news.liu.bean.healthy;

import android.util.Log;

import org.json.JSONObject;

/**
 * 作者：guojuan
 * 创建时间：2017/1/23 15:14
 * 功能描述：健康知识分类实体类
 */

public class Classify {
    public int mId;//分类id
    public String mName;//分类名称
    public String mKeywords;//分类的关键词
    public String mTitle;//分类的标题
    public String mDescription;//分类的描述
    public int mSeq;//分类的排序： 从小到大的递增排序


    public Classify parserClassify(JSONObject object) {
        if (null != object) {
            Log.e("Classify","=====Classify::::::"+object.toString());
            this.mId = object.optInt("id");
            this.mName = object.optString("name");
            this.mKeywords = object.optString("keywords");
            this.mTitle = object.optString("title");
            this.mDescription = object.optString("description");
            this.mSeq = object.optInt("seq");
        }
        return this;
    }

    @Override
    public String toString() {
        return "Classify{" +
                "mId=" + mId +
                ", mName='" + mName + '\'' +
                ", mKeywords='" + mKeywords + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSeq=" + mSeq +
                '}';
    }
}

package com.chao.news.liu.bean;

import org.json.JSONObject;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 各类新闻,国内，国际，科技，体育，奇闻轶事
 * Created by hp on 2017/1/13.
 */
@Table(name = "basenews")
public class BaseNews implements Serializable {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "ctime")
    private String mCtime;        //发生时间
    @Column(name = "title")
    private String mTitle;        //新闻标题
    @Column(name = "description")
    private String mDescription;  //新闻描述
    @Column(name = "picurl")
    private String mPicUrl;       //图片地址
    @Column(name = "url")
    private String mUrl;          //新闻详情
    @Column(name = "newstype")
    private String mType;         //新闻类型

    public BaseNews parser(String type, JSONObject jsonObject) {
        if (jsonObject.has("ctime"))
            mCtime = jsonObject.optString("ctime");
        else
            mCtime = jsonObject.optString("hottime");
        mTitle = jsonObject.optString("title");
        mDescription = jsonObject.optString("description");
        mPicUrl = jsonObject.optString("picUrl");
        mUrl = jsonObject.optString("url");
        mType = type;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmCtime() {
        return mCtime;
    }

    public void setmCtime(String mCtime) {
        this.mCtime = mCtime;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmPicUrl() {
        return mPicUrl;
    }

    public void setmPicUrl(String mPicUrl) {
        this.mPicUrl = mPicUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public String toString() {
        return "BaseNews{" +
                "id=" + id +
                ", mCtime='" + mCtime + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mPicUrl='" + mPicUrl + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mType='" + mType + '\'' +
                '}';
    }
}

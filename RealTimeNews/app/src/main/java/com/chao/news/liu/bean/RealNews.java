package com.chao.news.liu.bean;

import org.json.JSONObject;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 时事新闻
 * Created by hp on 2017/1/12.
 */
@Table(name = "realnews")
public class RealNews implements Serializable {

    @Column(name = "id", isId = true)
    private String mId;
    @Column(name = "title")
    private String mTitle;            //新闻标题
    @Column(name = "content")
    private String mContent;          //新闻内容
    @Column(name = "img_width")
    private String mImg_width;        //图片宽度
    @Column(name = "full_title")
    private String mFull_title;       //完整标题
    @Column(name = "pdate")
    private String mPdate;            //发生了多久
    @Column(name = "src")
    private String mSrc;              //新闻来源
    @Column(name = "img_length")
    private String mImg_Length;       //图片长度
    @Column(name = "img")
    private String mImg;              //图片地址
    @Column(name = "url")
    private String mUrl;              //详情地址
    @Column(name = "pdate_src")
    private String mPdate_src;        //发生时间
    @Column(name = "keyword")
    private String mKeyword;          //检索关键字

    public RealNews parser(String keyword, JSONObject jsonObject) {
        mTitle = jsonObject.optString("title");
        mContent = jsonObject.optString("content");
        mImg_width = jsonObject.optString("img_width");
        mFull_title = jsonObject.optString("full_title");
        mPdate = jsonObject.optString("pdate");
        mSrc = jsonObject.optString("src");
        mImg_Length = jsonObject.optString("img_length");
        mImg = jsonObject.optString("img");
        mUrl = jsonObject.optString("url");
        mPdate_src = jsonObject.optString("pdate_src");
        mKeyword = keyword;
        return this;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmImg_width() {
        return mImg_width;
    }

    public void setmImg_width(String mImg_width) {
        this.mImg_width = mImg_width;
    }

    public String getmFull_title() {
        return mFull_title;
    }

    public void setmFull_title(String mFull_title) {
        this.mFull_title = mFull_title;
    }

    public String getmPdate() {
        return mPdate;
    }

    public void setmPdate(String mPdate) {
        this.mPdate = mPdate;
    }

    public String getmSrc() {
        return mSrc;
    }

    public void setmSrc(String mSrc) {
        this.mSrc = mSrc;
    }

    public String getmImg_Length() {
        return mImg_Length;
    }

    public void setmImg_Length(String mImg_Length) {
        this.mImg_Length = mImg_Length;
    }

    public String getmImg() {
        return mImg;
    }

    public void setmImg(String mImg) {
        this.mImg = mImg;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmPdate_src() {
        return mPdate_src;
    }

    public void setmPdate_src(String mPdate_src) {
        this.mPdate_src = mPdate_src;
    }

    public String getmKeyword() {
        return mKeyword;
    }

    public void setmKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
    }

    @Override
    public String toString() {
        return "RealNews{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mImg_width='" + mImg_width + '\'' +
                ", mFull_title='" + mFull_title + '\'' +
                ", mPdate='" + mPdate + '\'' +
                ", mSrc='" + mSrc + '\'' +
                ", mImg_Length='" + mImg_Length + '\'' +
                ", mImg='" + mImg + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mPdate_src='" + mPdate_src + '\'' +
                ", mKeyword='" + mKeyword + '\'' +
                '}';
    }
}

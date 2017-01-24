package com.chao.news.liu.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 搜索关键字
 * Created by hp on 2017/1/14.
 */
@Table(name = "searchkey")
public class SearchKey implements Serializable {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "keyword")
    private String mKeyword;
    @Column(name = "searchtime")
    private long mSearchTime;
    @Column(name = "type")
    private String mType;

    public SearchKey() {
    }

    public SearchKey(String mKeyword, long mSearchTime, String mType) {
        this.mKeyword = mKeyword;
        this.mSearchTime = mSearchTime;
        this.mType = mType;
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

    public String getmKeyword() {
        return mKeyword;
    }

    public void setmKeyword(String mKeyword) {
        this.mKeyword = mKeyword;
    }

    public long getmSearchTime() {
        return mSearchTime;
    }

    public void setmSearchTime(long mSearchTime) {
        this.mSearchTime = mSearchTime;
    }

    @Override
    public String toString() {
        return "SearchKey{" +
                "mKeyword='" + mKeyword + '\'' +
                ", mSearchTime=" + mSearchTime +
                ", mType='" + mType + '\'' +
                '}';
    }
}

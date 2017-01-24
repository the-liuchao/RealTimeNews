package com.chao.news.liu.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 实时热点
 * Created by hp on 2017/1/14.
 */
@Table(name = "hotspotnews")
public class HotspotNews implements Serializable {
    @Column(name = "id", isId = true)
    private int mId;
    @Column(name = "hotspot")
    private String mHotspot;
    @Column(name = "currtime")
    private long mCurrTime;
    @Column(name = "isread")
    private int mIsRead;

    public int getmIsRead() {
        return mIsRead;
    }

    public void setmIsRead(int mIsRead) {
        this.mIsRead = mIsRead;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmHotspot() {
        return mHotspot;
    }

    public void setmHotspot(String mHotspot) {
        this.mHotspot = mHotspot;
    }

    public long getmCurrTime() {
        return mCurrTime;
    }

    public void setmCurrTime(long mCurrTime) {
        this.mCurrTime = mCurrTime;
    }


    @Override
    public String toString() {
        return "HotspotNews{" +
                "mId=" + mId +
                ", mHotspot='" + mHotspot + '\'' +
                ", mCurrTime=" + mCurrTime +
                ", mIsRead=" + mIsRead +
                '}';
    }
}

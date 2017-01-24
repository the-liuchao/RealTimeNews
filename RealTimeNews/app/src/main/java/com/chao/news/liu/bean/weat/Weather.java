package com.chao.news.liu.bean.weat;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by hp on 2017/1/16.
 */
@Table(name = "weather")
public class Weather implements Serializable {

    @Column(name = "id", isId = true)
    private int mId;

    /**
     * 当前天气
     */
    private RealTime mRealTime;
    @Column(name = "realtime")
    private String mRealTimes;
    /**
     * 生活常识
     */
    private Life mLife;
    @Column(name = "life")
    private String mLifes;
    /**
     * 未来七天天气
     */
    private Weather7 mWeather7;
    @Column(name = "weather7")
    private String mWeather7s;
    /**
     * 空气质量
     */
    private Pm25 mPm25;
    @Column(name = "pm25")
    private String mPm25s;

    @Column(name = "date")
    private String mDate;

    @Column(name = "isforeign")
    private int mIsForeign;

    @Column(name = "city")
    private String mCity;


    public Weather parser(String city, JSONObject json) throws JSONException {
        if (null == json) return this;
        mRealTimes = json.optString("realtime");
        mLifes = json.optString("life");
        mWeather7s = json.optString("weather");
        mPm25s = json.optString("pm25");
        mIsForeign = json.optInt("isForeign");
        mDate = json.optString("date");
        mRealTime = new RealTime().parser(json.optJSONObject("realtime"));
        mLife = new Life().parser(json.optJSONObject("life"));
        mWeather7 = new Weather7().parser(json.optJSONArray("weather"));
        mPm25 = new Pm25().parser(json.optJSONObject("pm25"));
        mCity = city;
        mPm25s = TextUtils.isEmpty(mPm25s) || "null".equals(mPm25s) ? "" : mPm25s;
        return this;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public RealTime getmRealTime() {
        try {
            if (mRealTime == null)
                return new RealTime().parser(new JSONObject(mRealTimes));
            else
                return mRealTime;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new RealTime();
    }

    public void setmRealTime(RealTime mRealTime) {
        this.mRealTime = mRealTime;
    }

    public String getmRealTimes() {
        return mRealTimes;
    }

    public void setmRealTimes(String mRealTimes) {
        this.mRealTimes = mRealTimes;
    }

    public Life getmLife() {
        try {
            if (null == mLife)
                return new Life().parser(new JSONObject(mLifes));
            else return mLife;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Life();
    }

    public void setmLife(Life mLife) {
        this.mLife = mLife;
    }

    public String getmLifes() {
        return mLifes;
    }

    public void setmLifes(String mLifes) {
        this.mLifes = mLifes;
    }

    public Weather7 getmWeather7() {
        try {
            if (null == mWeather7)
                return new Weather7().parser(new JSONArray(mWeather7s));
            else return mWeather7;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Weather7();
    }

    public void setmWeather7(Weather7 mWeather7) {
        this.mWeather7 = mWeather7;
    }

    public String getmWeather7s() {
        return mWeather7s;
    }

    public void setmWeather7s(String mWeather7s) {
        this.mWeather7s = mWeather7s;
    }

    public Pm25 getmPm25() {
        try {
            if (null == mPm25) {
                if (TextUtils.isEmpty(mPm25s)) return new Pm25().parser(null);
                else return new Pm25().parser(new JSONObject(mPm25s));
            } else {
                return mPm25;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new Pm25();
    }

    public void setmPm25(Pm25 mPm25) {
        this.mPm25 = mPm25;
    }

    public String getmPm25s() {
        return mPm25s;
    }

    public void setmPm25s(String mPm25s) {
        this.mPm25s = mPm25s;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public int getmIsForeign() {
        return mIsForeign;
    }

    public void setmIsForeign(int mIsForeign) {
        this.mIsForeign = mIsForeign;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "mId=" + mId +
                ", mRealTime=" + mRealTime +
                ", mRealTimes='" + mRealTimes + '\'' +
                ", mLife=" + mLife +
                ", mLifes='" + mLifes + '\'' +
                ", mWeather7=" + mWeather7 +
                ", mWeather7s='" + mWeather7s + '\'' +
                ", mPm25=" + mPm25 +
                ", mPm25s='" + mPm25s + '\'' +
                ", mDate='" + mDate + '\'' +
                ", mIsForeign=" + mIsForeign +
                ", mCity='" + mCity + '\'' +
                '}';
    }
}

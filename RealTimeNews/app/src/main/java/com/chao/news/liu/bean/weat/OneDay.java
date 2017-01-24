package com.chao.news.liu.bean.weat;

import android.text.TextUtils;

import com.chao.news.liu.api.Constants;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by hp on 2017/1/17.
 */

public class OneDay {
    public String mDate;
    public Info mInfo;
    public String mWeek;
    public String mNongli;
    private int[] mTemps;

    public OneDay parser(JSONObject json) {
        if (null == json) return this;
        mDate = json.optString("date");
        mInfo = new Info().parser(json.optJSONObject("info"));
        mWeek = json.optString("week");
        mNongli = json.optString("nongli");
        return this;
    }

    public String getMinTemp() {
        sortTemp();
        return mTemps[0] + "℃";
    }

    public String getMaxTemp() {
        sortTemp();
        return mTemps[mTemps.length - 1] + "℃";
    }

    private void sortTemp() {
        if (mTemps == null || mTemps.length <= 0) {
            int dTemp = TextUtils.isEmpty(mInfo.mDays.mMinTemp) ? 0 : Integer.parseInt(mInfo.mDays.mMinTemp);
            int nTemp = TextUtils.isEmpty(mInfo.mNights.mMinTemp) ? 0 : Integer.parseInt(mInfo.mNights.mMinTemp);
            int daTemp = TextUtils.isEmpty(mInfo.mDawns.mMinTemp) ? 0 : Integer.parseInt(mInfo.mDawns.mMinTemp);
            int dTemp1 = TextUtils.isEmpty(mInfo.mDays.mMaxTemp) ? 0 : Integer.parseInt(mInfo.mDays.mMaxTemp);
            int nTemp2 = TextUtils.isEmpty(mInfo.mNights.mMaxTemp) ? 0 : Integer.parseInt(mInfo.mNights.mMaxTemp);
            int daTemp3 = TextUtils.isEmpty(mInfo.mDawns.mMaxTemp) ? 0 : Integer.parseInt(mInfo.mDawns.mMaxTemp);
            mTemps = new int[]{dTemp, nTemp, daTemp, dTemp1, nTemp2, daTemp3};
            Arrays.sort(mTemps);
        }
    }

    public String getTemp() {
        return getMinTemp() + Constants.SPACE + Constants.SPACE + getMaxTemp();
    }

}

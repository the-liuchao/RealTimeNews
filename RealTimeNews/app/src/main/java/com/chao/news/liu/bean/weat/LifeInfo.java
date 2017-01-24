package com.chao.news.liu.bean.weat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 生活指数
 * Created by hp on 2017/1/16.
 */

public class LifeInfo {

    private List<LifeIndex> mLifeIndexs;
    public LifeIndex mKongtiao;
    public LifeIndex mYundong;
    public LifeIndex mZiwaixian;
    public LifeIndex mGanmao;
    public LifeIndex mXiche;
    public LifeIndex mWuran;
    public LifeIndex mChuanyi;

    public LifeInfo parser(JSONObject json) {
        if (null == json) return this;
        mKongtiao = new LifeIndex("空调").parser(json.optJSONArray("kongtiao"));
        mYundong = new LifeIndex("运动").parser(json.optJSONArray("yundong"));
        mZiwaixian = new LifeIndex("紫外新").parser(json.optJSONArray("ziwaixian"));
        mGanmao = new LifeIndex("感冒").parser(json.optJSONArray("ganmao"));
        mXiche = new LifeIndex("洗车").parser(json.optJSONArray("xiche"));
        mWuran = new LifeIndex("污染").parser(json.optJSONArray("wuran"));
        mChuanyi = new LifeIndex("穿衣").parser(json.optJSONArray("chuanyi"));
        return this;
    }

    public List<LifeIndex> getmLifeIndexs() {
        if (null == mLifeIndexs) mLifeIndexs = new ArrayList<>();
        mLifeIndexs.add(mKongtiao);
        mLifeIndexs.add(mYundong);
        mLifeIndexs.add(mZiwaixian);
        mLifeIndexs.add(mGanmao);
        mLifeIndexs.add(mXiche);
        mLifeIndexs.add(mWuran);
        mLifeIndexs.add(mChuanyi);
        return mLifeIndexs;
    }
}

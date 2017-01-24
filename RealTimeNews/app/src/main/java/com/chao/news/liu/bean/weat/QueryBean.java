package com.chao.news.liu.bean.weat;

/**
 * Created by hp on 2017/1/19.
 */

public class QueryBean {
    public String mName;
    public int mIon;

    public QueryBean(String mName, int mIon) {
        this.mName = mName;
        this.mIon = mIon;
    }

    public QueryBean(String mName) {
        this.mName = mName;
    }
}

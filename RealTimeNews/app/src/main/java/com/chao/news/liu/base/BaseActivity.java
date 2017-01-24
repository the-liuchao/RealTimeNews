package com.chao.news.liu.base;

import com.liuming.mylibrary.XActivity;

/**
 * Created by hp on 2017/1/12.
 */

public abstract class BaseActivity extends XActivity {


    /**
     * 移除activity
     */
    protected void remove() {
        BaseApplication.getInstance().removeAct(this);
    }

}

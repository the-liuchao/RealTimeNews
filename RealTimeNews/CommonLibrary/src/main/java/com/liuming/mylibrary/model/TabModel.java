package com.liuming.mylibrary.model;

import android.support.v4.app.Fragment;

/**
 * Created by hp on 2017/1/11.
 */

public class TabModel {

    public String mTabName;
    public int mNormalIcon;
    public int mCheckedIcon;
    public Fragment mFragment;

    public TabModel(String mTabName, int mNormalIcon, int mCheckedIcon, Fragment mFragment) {
        this.mTabName = mTabName;
        this.mNormalIcon = mNormalIcon;
        this.mCheckedIcon = mCheckedIcon;
        this.mFragment = mFragment;
    }
}

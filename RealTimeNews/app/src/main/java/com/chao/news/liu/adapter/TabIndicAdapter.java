package com.chao.news.liu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by hp on 2017/1/12.
 */

public class TabIndicAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public TabIndicAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments == null ? null : fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getItem(position).getArguments().get("title").toString();
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }
}

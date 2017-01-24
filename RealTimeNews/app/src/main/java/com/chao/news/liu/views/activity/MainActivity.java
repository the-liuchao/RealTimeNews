package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.TabIndicAdapter;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.views.fragment.HotspotFragment;
import com.chao.news.liu.views.fragment.NewsFragment;
import com.chao.news.liu.views.fragment.HelpFragment;
import com.chao.news.liu.views.fragment.WeatFragment;
import com.liuming.mylibrary.widge.ViewPagerIndicator;
import com.liuming.mylibrary.widge.viewflow.XViewFlow;
import com.umeng.socialize.UMShareAPI;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(value = R.id.indicator)
    private ViewPagerIndicator mIndicator;
    @ViewInject(value = R.id.viewpager)
    private ViewPager mViewPager;

    private List<Fragment> fragments;
    private HotspotFragment mSearchFragment;
    private NewsFragment mRealFragment;
    private WeatFragment mWeatFragment;
    private HelpFragment mServFragment;

    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, MainActivity.class);
        act.startActivity(intent);
    }

    @Override
    protected void initView() {
        mRealFragment = new NewsFragment();
        mSearchFragment = new HotspotFragment();
        mWeatFragment = new WeatFragment();
        mServFragment = new HelpFragment();
        mViewPager.setOffscreenPageLimit(3);
    }

    /**
     * 绑定ViewPager，防止ViewFlow与ViewPager滑动冲突
     *
     * @param viewFlow
     */
    public void bindViewPager(XViewFlow viewFlow) {
        viewFlow.setViewPager(mViewPager);
    }

    @Override
    protected void initData() {
        fragments = new ArrayList<>();
//        fragments.add(mSearchFragment);
        fragments.add(mWeatFragment);
        fragments.add(mRealFragment);
        fragments.add(mServFragment);
        mViewPager.setAdapter(new TabIndicAdapter(getSupportFragmentManager(), fragments));
        mIndicator.setTabItemTitles(Arrays.asList(new String[]{ "天气","新闻", "助手"}));
        mIndicator.setViewPager(mViewPager, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    long lastBackTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastBackTime > 2000) {
            lastBackTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次,退出程序", Toast.LENGTH_SHORT).show();
        } else {
            BaseApplication.getInstance().finishAll();
            finish();
            BaseApplication.getInstance().removeAct(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mViewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getString("position");
        mViewPager.setCurrentItem(savedInstanceState.getInt("position"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}

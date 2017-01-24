package com.liuming.mylibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liuming.mylibrary.model.TabModel;
import com.liuming.mylibrary.widge.NavigateItemView;
import com.liuming.mylibrary.widge.TitleBar;

import java.util.ArrayList;

/**
 * Created by hp on 2017/1/11.
 */
public abstract class XNaviActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener
        , View.OnClickListener
        , View.OnTouchListener {
    protected ArrayList<TabModel> mModels;
    protected TitleBar mTitleBar;
    private ViewGroup mNaviLayout;
    private ViewPager mViewPager;
    private boolean mIsTouch;
    private boolean mIsProhibit;

    /**
     * 设置切换页面
     *
     * @param models
     */
    protected abstract void setFragments(ArrayList<TabModel> models);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_layout);
        XApplication.getInstance().addAct(this);
        initView();
        initData();
    }

    protected void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mNaviLayout = (ViewGroup) findViewById(R.id.navi_layout);
    }

    protected void initData() {
        mModels = new ArrayList<>();
        setFragments(mModels);
        initNaviBar();
        mViewPager.setAdapter(new TabFragmentAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOnTouchListener(this);
    }

    private void initNaviBar() {
        if (mModels == null || mModels.size() <= 0) return;
        int size = mModels.size();
        mNaviLayout.removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2);
        layoutParams.weight = 1;
        for (int i = 0; i < size; i++) {
            TabModel model = mModels.get(i);
            NavigateItemView itemView = new NavigateItemView.Builder(this)
                    .setmIconNormal(model.mNormalIcon)
                    .setmIconSelected(model.mCheckedIcon)
                    .setmLabel(model.mTabName)
                    .setmTextSize(32)
                    .setmTextColor(0xFF555555, 0xFF1A85FF)
                    .build();
            itemView.setId(i);
            itemView.setOnClickListener(this);
            if (i == 0) itemView.setmAlpha(1.0f);           //默认选中第一个
            mNaviLayout.addView(itemView, layoutParams);
        }
    }

    /**
     * 是否有标题栏
     *
     * @param hasTitle
     */
    protected void hasTitleBar(boolean hasTitle) {
        mTitleBar.setVisibility(hasTitle ? View.VISIBLE : View.GONE);
    }

    /**
     * 是否禁止滑动切换
     *
     * @param isProhibit
     */
    protected void isProhibitScroll(boolean isProhibit) {
        mIsProhibit = isProhibit;
    }


    public void onClick(View v) {
        if (mIsTouch) mIsTouch = false;//如果在拖拽就能点击
        int naviItemCount = mNaviLayout.getChildCount();
        for (int i = 0; i < naviItemCount; i++) {
            NavigateItemView naviItem = (NavigateItemView) mNaviLayout.getChildAt(i);
            if (i == v.getId()) {
                mViewPager.setCurrentItem(i, !mIsProhibit);  //是否添加切换动画
                naviItem.setmAlpha(1.0f);
            } else {
                naviItem.setmAlpha(0f);
            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mIsProhibit)
            return true;
        else
            return super.onTouchEvent(event);
    }

    /**
     * tab适配器
     */
    public final class TabFragmentAdapter extends FragmentPagerAdapter {

        public TabFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int position) {
            return mModels.get(position).mFragment;
        }

        public int getCount() {
            return mModels.size();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (mIsTouch && positionOffset > 0) {
            NavigateItemView leftView = (NavigateItemView) mNaviLayout.getChildAt(position);
            NavigateItemView rightView = (NavigateItemView) mNaviLayout.getChildAt(position + 1);
            leftView.setmAlpha(1 - positionOffset);
            rightView.setmAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            mIsTouch = true;
        }
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            mIsTouch = false;
        }
    }

    @Override
    public void onBackPressed() {
        XApplication.getInstance().removeAct(this);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        XApplication.getInstance().removeAct(this);
        super.onDestroy();
    }
}

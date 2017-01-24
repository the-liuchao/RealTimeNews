package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.WxNewsAdapter;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.CurrLocation;
import com.chao.news.liu.bean.WeixinNews;
import com.liuming.mylibrary.utils.SPHelper;
import com.liuming.mylibrary.widge.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/14.
 */
@ContentView(R.layout.activity_weixinlist)
public class WxNewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String KEYWORD = "中国";
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @ViewInject(R.id.listview)
    ListView mListView;

    private String mCurrCity;
    private List<WeixinNews> mWxNews;
    private WxNewsAdapter mWxNewsAdapter;

    public static void startActivity(Activity act) {
        act.startActivity(new Intent(act, WxNewsActivity.class));
    }

    @Override
    protected void initView() {
        mTitleBar.setTitle("微信精选", View.VISIBLE)
                .setLeftIcon(R.mipmap.btn_back)
                .setBarClickListener(new TitleBar.BarClickListener() {
                    @Override
                    public void onClick(int position, View v) {
                        BaseApplication.getInstance().finish(WxNewsActivity.this);
                    }
                });
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        mWxNewsAdapter = new WxNewsAdapter(mWxNews, this);
        mListView.setAdapter(mWxNewsAdapter);
        showProgressDialog();
        try {
            initCurrCity();
            requestNews(mCurrCity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCurrCity() throws ClassNotFoundException, IOException {
        CurrLocation location = SPHelper.getObject("location");
        if (location == null) mCurrCity = KEYWORD;
        else mCurrCity = location.getmCity() == null ? KEYWORD : location.getmCity();
        if (mCurrCity.equals("上海市")
                || mCurrCity.equals("重庆市")
                || mCurrCity.equals("北京市")
                || mCurrCity.equals("天津市"))
            mCurrCity = mCurrCity.substring(0, 2);
    }

    /**
     * 请求数据
     */
    private void requestNews(String keyword) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", "123d45c7a7e44e5ebf65abb6f1fa662f");
        params.put("keyword", keyword);
        HttpAPI.getInstance()
                .requestWxNews(params, new HttpAPI.WxNewsListener() {
                    @Override
                    public void onSuccess(List<WeixinNews> results) {
                        hideProgressDialog();
                        mRefreshLayout.setRefreshing(false);
                        if (null == results) return;
                        mWxNews = results;
                        mWxNewsAdapter.refresh(results);
                    }

                    @Override
                    public void onFail(String errMsg, int errCode) {
                    }
                });
    }

    @Override
    public void onRefresh() {
        requestNews(mCurrCity);
    }
}

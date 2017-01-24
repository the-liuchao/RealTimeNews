package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.BaseNewsAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.BaseNews;
import com.liuming.mylibrary.widge.TitleBar;
import com.liuming.mylibrary.widge.XSwipeLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/13.
 */
@ContentView(R.layout.activity_newslist)
public class BaseNewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, XSwipeLayout.OnLoadListener {
    private static int mNewsType;

    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.refresh_layout)
    XSwipeLayout mRefreshLayout;
    @ViewInject(R.id.listview)
    ListView mListView;


    private String[] mNewsNames;
    private List<BaseNews> mBaseNews;
    private BaseNewsAdapter mBaseNewsAdapter;
    private int mPage = 1;
    private int mRows = 20;
    private String[] mNewsUrls = {Constants.WORLDURL, Constants.GOUNEIURL
            , Constants.SPORTSURL, Constants.TECHURL
            , Constants.TRAVELURL, Constants.QIWEIURL};
    private String[] mNewsKeys = {"d5f1bcc3d488429387521adf5b7c86f2", "946dcdb0fd9c46f09a1bdaea1b72c66f"
            , "3d11e9f3026a4a4a8b4116989322c95e", "9fe8e377f35d44e992b0f259c2a6b5b6"
            , "73b9d83c3eae4fdfaa3fa45f21b0aaac", "271765280e924e1ca9649e6117fccc25"};

    public static void startActivity(Activity act, String type) {
        mNewsType = Integer.parseInt(type);
        act.startActivity(new Intent(act, BaseNewsActivity.class));
    }

    @Override
    protected void initView() {
        mNewsNames = getResources().getStringArray(R.array.mNewsNames);
        mTitleBar.setTitle(mNewsNames[mNewsType], View.VISIBLE)
                .setLeftIcon(R.mipmap.btn_back)
                .setBarClickListener(new TitleBar.BarClickListener() {
                    @Override
                    public void onClick(int position, View v) {
                        BaseApplication.getInstance().finish(BaseNewsActivity.this);
                    }
                });
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadListener(this);
    }

    @Override
    protected void initData() {
        mBaseNewsAdapter = new BaseNewsAdapter(mBaseNews, this);
        mListView.setAdapter(mBaseNewsAdapter);
        showProgressDialog();
        requestNews(true);
    }

    /**
     * 请求数据
     */
    private void requestNews(final boolean isRefresh) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", mNewsKeys[mNewsType]);
        params.put("page", String.valueOf(mPage));
        params.put("rows", String.valueOf(mRows));
        HttpAPI.getInstance()
                .requestBaseNews(mNewsUrls[mNewsType], params, new HttpAPI.BaseNewsListener() {
                    @Override
                    public void onSuccess(List<BaseNews> results) {
                        if (null == results || results.size() < mRows) {
                            requestFinish(false);
                            return;
                        } else {
                            requestFinish(true);
                        }
                        if (isRefresh) {
                            mBaseNews = results;
                            mBaseNewsAdapter.refresh(results);
                        } else {
                            mBaseNews.addAll(results);
                            mBaseNewsAdapter.loadMore(results);
                        }
                    }

                    @Override
                    public void onFail(String errMsg, int errCode) {
                        requestFinish(true);
                    }
                });
    }

    /**
     * 加载完成
     */
    private void requestFinish(boolean hasMore) {
        hideProgressDialog();
        mRefreshLayout.setRefreshing(false);
        mRefreshLayout.setLoading(false, hasMore);
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        mRefreshLayout.setLoading(false, true);
        requestNews(true);
    }

    @Override
    public void onLoad() {
        mPage = mBaseNewsAdapter.getCount() / mRows + 1;
        requestNews(false);
    }
}

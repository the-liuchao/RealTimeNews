package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.HotNewsAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.RealNews;
import com.chao.news.liu.bean.SearchKey;
import com.liuming.mylibrary.widge.TitleBar;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;

/**
 * 新闻检索界面
 * Created by hp on 2017/1/14.
 */
@ContentView(R.layout.activity_hotspot)
public class HotNewsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @ViewInject(R.id.listview)
    ListView mListView;
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;

    private HotNewsAdapter mHotAdapter;
    private List<RealNews> mRealNews;
    private static String mKeyword;

    public static void startActivity(Activity act, String keyword) {
        mKeyword = keyword;
        act.startActivity(new Intent(act, HotNewsActivity.class));
    }

    @Override
    protected void initView() {
        mRefreshLayout.setOnRefreshListener(this);
        mTitleBar.setTitle(mKeyword, View.VISIBLE)
                .setLeftIcon(R.mipmap.btn_back)
                .setBarClickListener(new TitleBar.BarClickListener() {
                    @Override
                    public void onClick(int position, View v) {
                        if (1 == position) {
                            BaseApplication.getInstance().finish(HotNewsActivity.this);
                        }
                    }
                });
    }

    @Override
    protected void initData() {
        mHotAdapter = new HotNewsAdapter(mRealNews, this);
        mListView.setAdapter(mHotAdapter);
        searchNews(mKeyword);
        saveKey();
    }

    /**
     * 保存搜索关键字
     */
    private void saveKey() {
        try {
            mDbManager.delete(SearchKey.class, WhereBuilder.b("keyword", "=", mKeyword));
            mDbManager.save(new SearchKey(mKeyword, System.currentTimeMillis(), "1"));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Event(R.id.back)
    private void back(View view) {
        BaseApplication.getInstance().finish(this);
    }

    /**
     * 请求数据
     *
     * @param keyword
     */
    private void searchNews(String keyword) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.REALKEY);
        params.put("keyword", keyword);
        HttpAPI.getInstance()
                .requestKeyRealNews(params, new HttpAPI.RealNewsListener() {
                    @Override
                    public void onSuccess(List<RealNews> results) {
                        hideProgressDialog();
                        mRefreshLayout.setRefreshing(false);
                        if (null == results) return;
                        mRealNews = results;
                        mHotAdapter.refresh(results);
                    }

                    @Override
                    public void onFail(String errMsg, int errCode) {
                        hideProgressDialog();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        searchNews(mKeyword);
    }
}

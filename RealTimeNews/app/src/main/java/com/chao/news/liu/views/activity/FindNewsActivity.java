package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.HotNewsAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.RealNews;
import com.chao.news.liu.bean.SearchKey;
import com.liuming.mylibrary.widge.XEditText;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 新闻检索界面
 * Created by hp on 2017/1/14.
 */
@ContentView(R.layout.activity_search)
public class FindNewsActivity extends BaseActivity implements XEditText.OnClickSearchListener, SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @ViewInject(R.id.listview)
    ListView mListView;
    @ViewInject(R.id.search_view)
    XEditText mSearchView;

    private HotNewsAdapter mRealAdapter;
    private List<RealNews> mRealNews;
    private ArrayAdapter<String> mKeyAdapter;
    private List<String> mKeywords;
    private static String mKeyword;

    public static void startActivity(Activity act, String keyword) {
        mKeyword = keyword;
        act.startActivity(new Intent(act, FindNewsActivity.class));
    }

    @Override
    protected void initView() {
        mSearchView.setmOnClickSearchListener(this);
        mRefreshLayout.setOnRefreshListener(this);
        mKeywords = new ArrayList<>();
        mSearchView.setText(mKeyword);
    }

    @Override
    protected void initData() {
        mRealAdapter = new HotNewsAdapter(mRealNews, this);
        mListView.setAdapter(mRealAdapter);
        mKeyAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, mKeywords);
        mSearchView.setAdapter(mKeyAdapter);
        showProgressDialog();
        searchNews(mKeyword);
        saveKey();
        refreshKey();
    }

    /**
     * 刷新关键字适配器
     */
    private void refreshKey() {
        try {
            List<SearchKey> keys = mDbManager.selector(SearchKey.class)
                    .where("searchtime", ">", System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000)
                    .orderBy("searchtime", true)
                    .limit(10).findAll();
            mKeywords.clear();
            for (SearchKey key : keys) {
                mKeywords.add(key.getmKeyword());
            }
            mKeyAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, mKeywords);
            mSearchView.setAdapter(mKeyAdapter);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存搜索关键字
     */
    private void saveKey() {
        try {
            mDbManager.delete(SearchKey.class, WhereBuilder.b("keyword", "=", mKeyword));
            mDbManager.save(new SearchKey(mKeyword, System.currentTimeMillis(), "1"));
            refreshKey();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Event(R.id.back)
    private void back(View view) {
        BaseApplication.getInstance().finish(this);
    }

    @Override
    public void onClick(XEditText view) {
        Editable edit = mSearchView.getText();
        if (TextUtils.isEmpty(edit)) {
            Toast.makeText(this, "请输入关键字", Toast.LENGTH_SHORT).show();
            return;
        }
        mKeyword = edit.toString();
        searchNews(mKeyword);
        saveKey();
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
                        mRealAdapter.refresh(results);
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

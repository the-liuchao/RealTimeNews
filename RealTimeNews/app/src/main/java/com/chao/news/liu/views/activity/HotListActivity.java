package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.HotspotAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.bean.HotspotNews;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/16.
 */
@ContentView(R.layout.fragment_hot)
public class HotListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @ViewInject(R.id.listview)
    ListView mListView;

    private HotspotAdapter mHotspotAdapter;
    private List<HotspotNews> mHotspotNews;

    public static void startActivity(Activity act) {
        act.startActivity(new Intent(act, HotListActivity.class));
    }

    @Override
    protected void initView() {
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        mHotspotAdapter = new HotspotAdapter(mHotspotNews, HotListActivity.this);
        mListView.setAdapter(mHotspotAdapter);
        requestHotspot();
    }

    private void requestHotspot() {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.REALKEY);
        HttpAPI.getInstance()
                .requestHotspotNews(params, new HttpAPI.HotspotNewsListener() {
                    @Override
                    public void onSuccess(List<HotspotNews> hotspotNewses) {
                        mRefreshLayout.setRefreshing(false);
                        if (hotspotNewses == null || hotspotNewses.size() <= 0) return;
                        mHotspotNews = hotspotNewses;
                        mHotspotAdapter.refresh(mHotspotNews);
                    }

                    @Override
                    public void onFail(String errMsg, int errCode) {
                        Toast.makeText(HotListActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        requestHotspot();
    }
}

package com.chao.news.liu.views.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.HotspotAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.bean.HotspotNews;
import com.liuming.mylibrary.XFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/12.
 */
@ContentView(R.layout.fragment_hot)
public class HotspotFragment extends XFragment implements SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @ViewInject(R.id.listview)
    ListView mListView;

    private HotspotAdapter mHotspotAdapter;
    private List<HotspotNews> mHotspotNews;

    @Override
    protected void initView(View mRootView) {
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initData() {
        mHotspotAdapter = new HotspotAdapter(mHotspotNews, getActivity());
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
                        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
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

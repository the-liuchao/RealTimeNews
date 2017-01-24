package com.chao.news.liu.views.fragment;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.HotNewsAdapter;
import com.chao.news.liu.adapter.NaviImageAdapter;
import com.chao.news.liu.adapter.NewsListAdapter;
import com.chao.news.liu.adapter.WxNewsAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.bean.CurrLocation;
import com.chao.news.liu.bean.RealNews;
import com.chao.news.liu.bean.WeixinNews;
import com.chao.news.liu.utils.Utils;
import com.chao.news.liu.views.activity.FindNewsActivity;
import com.chao.news.liu.views.activity.HotListActivity;
import com.chao.news.liu.views.activity.MainActivity;
import com.chao.news.liu.views.activity.WxNewsActivity;
import com.liuming.mylibrary.XFragment;
import com.liuming.mylibrary.utils.SPHelper;
import com.liuming.mylibrary.widge.ReboundScrollView;
import com.liuming.mylibrary.widge.XEditText;
import com.liuming.mylibrary.widge.XGridView;
import com.liuming.mylibrary.widge.viewflow.CircleFlowIndicator;
import com.liuming.mylibrary.widge.viewflow.XViewFlow;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/12.
 */
@ContentView(R.layout.fragment_search)
public class NewsFragment extends XFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, XEditText.OnClickSearchListener {
    private static final String KEYWORD = "中国";

    @ViewInject(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @ViewInject(R.id.hot_listview)
    ListView mHotListView;
    @ViewInject(R.id.hot_area)
    TextView mHotArea;
    @ViewInject(R.id.wx_listview)
    ListView mWxListView;
    @ViewInject(R.id.wx_area)
    TextView mWxArea;
    @ViewInject(R.id.news_list)
    XGridView mGridView;
    @ViewInject(R.id.navi_layout)
    View mNaviLayout;
    @ViewInject(R.id.viewflow)
    XViewFlow mViewFlow;
    @ViewInject(R.id.indicator)
    CircleFlowIndicator mIndicator;
    @ViewInject(R.id.search_view)
    XEditText mSearchView;
    @ViewInject(R.id.rebound_layout)
    ReboundScrollView mReboundLayout;

    private String mCurrCity;
    private View mWxFooterView;
    private View mHotFooterView;
    private HotNewsAdapter mRealAdapter;
    private WxNewsAdapter mWxAdapter;
    private NewsListAdapter mNewsListAdapter;
    private NaviImageAdapter mNewsImgAdapter;
    private List<RealNews> mRealNews;
    private List<WeixinNews> mWxNews;
    private List<HashMap<String, String>> mNewsLists;
    private List<HashMap<String, String>> mNewsImgs;
    private String[] mNewsNames;
    private int[] mNewsIcons = {R.mipmap.icon_guoji, R.mipmap.icon_guonei
            , R.mipmap.icon_sport, R.mipmap.icon_tech
            , R.mipmap.icon_travel, R.mipmap.icon_qiwen};

    @Override
    protected void initView(View mRootView) {
        initFooterView();
        initCurrCity();
        mNewsNames = getResources().getStringArray(R.array.mNewsNames);
        mRefreshLayout.setOnRefreshListener(this);
        mSearchView.setmOnClickSearchListener(this);
        mReboundLayout.setTopRebound(false);
        mNewsLists = new ArrayList<>();
        for (int i = 0; i < mNewsNames.length; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("icon", String.valueOf(mNewsIcons[i]));
            hashMap.put("name", mNewsNames[i]);
            hashMap.put("type", String.valueOf(i));
            mNewsLists.add(hashMap);
        }
    }

    /**
     * 初始化热点新闻和微信精选的FooterView
     */
    private void initFooterView() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mWxFooterView = inflater.inflate(R.layout.footer_look_more, null);
        mHotFooterView = inflater.inflate(R.layout.footer_look_more, null);
        mHotListView.addFooterView(mHotFooterView);
        mWxListView.addFooterView(mWxFooterView);
        mWxFooterView.setId(mWxFooterView.hashCode());
        mHotFooterView.setId(mHotFooterView.hashCode());
        mWxFooterView.setOnClickListener(this);
        mHotFooterView.setOnClickListener(this);
    }

    /**
     * 初始化当前城市 文本
     */
    private void initCurrCity() {
        CurrLocation location = SPHelper.getObject("location");
        if (location == null) mCurrCity = KEYWORD;
        else mCurrCity = location.getmCity() == null ? KEYWORD : location.getmCity();
        if (mCurrCity.equals("上海市")
                || mCurrCity.equals("重庆市")
                || mCurrCity.equals("北京市")
                || mCurrCity.equals("天津市"))
            mCurrCity = mCurrCity.substring(0, 2);
        SPHelper.put(getActivity(), Constants.CACHECITY, mCurrCity);
    }

    @Override
    protected void initData() {
        mRealAdapter = new HotNewsAdapter(mRealNews, getActivity());               //热点新闻
        mHotListView.setAdapter(mRealAdapter);
        mWxAdapter = new WxNewsAdapter(mWxNews, getActivity());                    //微信精选
        mWxListView.setAdapter(mWxAdapter);
        mNewsListAdapter = new NewsListAdapter(mNewsLists, getActivity());         //新闻分类
        mGridView.setAdapter(mNewsListAdapter);
        mNewsImgAdapter = new NaviImageAdapter(mNewsImgs, getActivity());          //滚动图片
        mViewFlow.setAdapter(mNewsImgAdapter);
        mIndicator.setFillColor(Color.RED);
        mWxArea.setText(mCurrCity);
        mHotArea.setText(mCurrCity);
        showProgressDialog();
        searchHotNews(mCurrCity);
        searchWxNews(mCurrCity);
    }

    /**
     * 请求热点新闻数据
     *
     * @param keyword
     */
    private void searchHotNews(String keyword) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.REALKEY);
        params.put("keyword", keyword);
        HttpAPI.getInstance()
                .requestRealNews(params, new HttpAPI.RealNewsListener() {
                    @Override
                    public void onSuccess(List<RealNews> results) {
                        hideProgressDialog();
                        mRefreshLayout.setRefreshing(false);
                        if (null == results) return;
                        List<HashMap<String, String>> newsImgs = new ArrayList<>();
                        for (RealNews realNews : results) {
                            if (!TextUtils.isEmpty(realNews.getmImg())) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("icon", realNews.getmImg());
                                hashMap.put("title", realNews.getmTitle());
                                hashMap.put("url", realNews.getmUrl());
                                newsImgs.add(hashMap);
                            }
                        }
                        refreshNewImgs(newsImgs);
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

    /**
     * 请求微信精选数据
     *
     * @param keyword
     */
    private void searchWxNews(String keyword) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.WEIXINKEY);
        params.put("keyword", keyword);
        HttpAPI.getInstance()
                .requestWxNews(params, new HttpAPI.WxNewsListener() {
                    @Override
                    public void onSuccess(List<WeixinNews> results) {
                        hideProgressDialog();
                        mRefreshLayout.setRefreshing(false);
                        if (null == results) return;
                        if (results.size() > 4) results = results.subList(0, 4);
                        mWxNews = results;
                        mWxAdapter.refresh(results);
                    }

                    @Override
                    public void onFail(String errMsg, int errCode) {
                        hideProgressDialog();
                        mRefreshLayout.setRefreshing(false);
                    }
                });
    }

    /**
     * 刷新滚动图片
     *
     * @param newsImgs
     */
    private void refreshNewImgs(List<HashMap<String, String>> newsImgs) {
        if (null == newsImgs || newsImgs.size() <= 0) {
            mViewFlow.setVisibility(View.GONE);
            mNaviLayout.setVisibility(View.GONE);
            return;
        }
        mNewsImgAdapter = new NaviImageAdapter(newsImgs, getActivity());
        mNewsImgs = newsImgs;
        mViewFlow.setAdapter(mNewsImgAdapter);
        ((MainActivity) getActivity()).bindViewPager(mViewFlow);
        mViewFlow.setFlowIndicator(mIndicator);
        mViewFlow.setSelection(900 * 100);
        mViewFlow.setmSideBuffer(mNewsImgs.size());
        mViewFlow.startAutoFlowTimer();
    }

    @Override
    public void onRefresh() {
        searchHotNews(mCurrCity);
        searchWxNews(mCurrCity);
    }

    @Event(R.id.icon_share)
    private void share(View v) {
//        new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.QQ)
//                .withText("hello")
//                .setCallback(new UMShareListener() {
//                    @Override
//                    public void onResult(SHARE_MEDIA share_media) {
//                        Toast.makeText(getActivity(), "分享成功", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                        Toast.makeText(getActivity(), "分享失败", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA share_media) {
//                        Toast.makeText(getActivity(), "分享取消", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .share();
        Utils.share(getActivity());

    }

    @Override
    public void onClick(XEditText view) {
        Editable edit = mSearchView.getText();
        if (TextUtils.isEmpty(edit)) {
            Toast.makeText(getActivity(), "请输入关键字", Toast.LENGTH_SHORT).show();
            return;
        }
        FindNewsActivity.startActivity(getActivity(), edit.toString());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mHotFooterView.hashCode()) {
            HotListActivity.startActivity(getActivity());
        } else if (v.getId() == mWxFooterView.hashCode()) {
            WxNewsActivity.startActivity(getActivity());
        }
    }
}
package com.chao.news.liu.views.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.QueryAdatper;
import com.chao.news.liu.adapter.ToolAdatper;
import com.chao.news.liu.bean.ToolBean;
import com.chao.news.liu.bean.weat.QueryBean;
import com.liuming.mylibrary.widge.XViewPagerFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/1/12.
 */
@ContentView(R.layout.fragment_help)
public class HelpFragment extends XViewPagerFragment implements SwipeRefreshLayout.OnRefreshListener {

    @ViewInject(R.id.tool_grid)
    GridView mToolGrid;
    @ViewInject(R.id.listview)
    ListView mQueryList;

    private List<ToolBean> mTools;
    private ToolAdatper mToolAdapter;
    private ListAdapter mQueryAdapter;
    private List<QueryBean> mQuerys;

    @Override
    protected void initView(View mRootView) {
        mTools = new ArrayList<>();
        mTools.add(new ToolBean("北京时间", R.mipmap.icon_tool_time));
        mTools.add(new ToolBean("证件认证", R.mipmap.icon_tool_bank));
        mTools.add(new ToolBean("单位换算", R.mipmap.icon_tool_unit));
        mTools.add(new ToolBean("在线翻译", R.mipmap.icon_tool_translate));
//        mTools.add(new ToolBean("身份证验证", R.mipmap.icon_tool_card));
//        mTools.add(new ToolBean("号码归属地", R.mipmap.icon_tool_phone));
        mTools.add(new ToolBean("MD5破解", R.mipmap.icon_tool_md5));
        mTools.add(new ToolBean("邮编地址", R.mipmap.icon_tool_postcode));
        mToolAdapter = new ToolAdatper(mTools, getActivity());
        mToolGrid.setAdapter(mToolAdapter);
        mQuerys = new ArrayList<>();
        mQuerys.add(new QueryBean("手机号码归属地查询"));
        mQuerys.add(new QueryBean("股票查询"));
        mQuerys.add(new QueryBean("驾照题库查询"));
        mQuerys.add(new QueryBean("彩票查询"));
        mQuerys.add(new QueryBean("IP地址查询"));
        mQuerys.add(new QueryBean("NBA赛事查询"));
        mQuerys.add(new QueryBean("火车票查询"));
        mQuerys.add(new QueryBean("电视节目查询"));
        mQuerys.add(new QueryBean("健康知识查询"));
        mQueryAdapter = new QueryAdatper(mQuerys,getActivity());
        mQueryList.setAdapter(mQueryAdapter);
    }

    @Override
    protected void onFirstUserVisiable() {
        showProgressDialog();

        hideProgressDialog();
    }

    @Override
    protected void initData() {
    }

    private void requestHotspot() {
    }

    @Override
    public void onRefresh() {
    }
}

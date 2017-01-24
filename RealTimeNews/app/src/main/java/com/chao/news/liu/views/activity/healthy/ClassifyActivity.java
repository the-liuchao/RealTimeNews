package com.chao.news.liu.views.activity.healthy;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.ClassifyAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.MD5;
import com.chao.news.liu.bean.healthy.Classify;
import com.liuming.mylibrary.widge.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/22.
 * md5密文：16位，32位,sha1(40位)的 MD5密文查询
 */
@ContentView(R.layout.activity_classify)
public class ClassifyActivity extends BaseActivity implements TitleBar.BarClickListener {
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.listview)
    ListView mListClassify;
    private ClassifyAdapter mAdapter;

    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, ClassifyActivity.class);
        act.startActivity(intent);
    }

    @Override
    protected void initView() {
        mTitleBar.setBarClickListener(this);
    }

    @Override
    protected void initData() {
        query();

    }

    @Event(value = {R.id.tv_query})
    private void onClick(View v) {
        switch (v.getId()) {

        }
    }

    /**
     * 创建时间 :
     * 功能描述 :连网查询
     */
    private void query() {
        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.CLASSIFYKEY);
        HttpAPI.getInstance().requestClassify(params, new HttpAPI.ClassifyListener() {
            @Override
            public void onSuccess(List<Classify> classifys) {
                hideProgressDialog();
                mAdapter = new ClassifyAdapter(classifys,ClassifyActivity.this);
                mListClassify.setAdapter(mAdapter);
            }

            @Override
            public void onFail(String errMsg, int errCode) {
                hideProgressDialog();
                Log.e("Classify","======errMsg="+errMsg+"==errCode=="+errCode);
            }
        });

    }

    @Override
    public void onClick(int position, View v) {
        if (position == 1)
            BaseApplication.getInstance().finish(this);
    }
}

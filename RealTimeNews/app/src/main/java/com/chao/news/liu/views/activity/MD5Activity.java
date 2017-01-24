package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.MD5;
import com.liuming.mylibrary.widge.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;

/**
 * Created by hp on 2017/1/22.
 * md5密文：16位，32位,sha1(40位)的 MD5密文查询
 */
@ContentView(R.layout.activity_md5)
public class MD5Activity extends BaseActivity implements TitleBar.BarClickListener {
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.edit_ciphertext)
    EditText mEditCiphertext;
    @ViewInject(R.id.tv_query_result)
    TextView mTvQueryResult;


    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, MD5Activity.class);
        act.startActivity(intent);
    }

    @Override
    protected void initView() {
        mTitleBar.setBarClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Event(value = {R.id.tv_query})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_query:
                int textLength = mEditCiphertext.getText().toString().length();
                if (textLength == 16 || textLength == 32 || textLength == 40) {
                    query();
                } else {
                    Toast.makeText(MD5Activity.this, "密文格式有误！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 创建时间 :
     * 功能描述 :连网查询
     */
    private void query() {
        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.MD5KEY);
        params.put("md5", mEditCiphertext.getText().toString());
        HttpAPI.getInstance().requestMD5(params, new HttpAPI.MD5Listener() {
            @Override
            public void onSuccess(MD5 md5) {
                hideProgressDialog();
                mTvQueryResult.setText(md5.mMd5Src);
            }

            @Override
            public void onFail(String errMsg, int errCode) {
                hideProgressDialog();
                Log.e("MD5", "errMsg===" + errMsg + "errCode===" + errCode);
            }
        });
    }

    @Override
    public void onClick(int position, View v) {
        if (position == 1)
            BaseApplication.getInstance().finish(this);
    }
}
package com.liuming.mylibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import com.liuming.mylibrary.dialog.LoadingProgressDialog;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by hp on 2017/1/5.
 */
public abstract class XActivity extends AppCompatActivity {
    protected DbManager mDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        mDbManager = XApplication.getInstance().getDbManager();
        initView();
        initData();
    }

    protected abstract void initView();

    protected abstract void initData();


    /**
     * 显示加载对话框
     */
    protected void showProgressDialog() {
        LoadingProgressDialog.getInstance()
                .setCompleteListener(new LoadingProgressDialog.CompleteListener() {
                    @Override
                    public void complete(int type, DialogFragment dialog) {
                        dialog.dismiss();
                    }
                })
                .setTitle("提示")
                .setMessage("正在加载中...")
                .showDialog(getSupportFragmentManager(), "progress");
    }

    /**
     * 隐藏加载对话框
     */
    protected void hideProgressDialog() {
        LoadingProgressDialog.getInstance().dismiss();
    }


    /**
     * 重新加载页面
     */
    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        XApplication.getInstance().removeAct(this);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        XApplication.getInstance().removeAct(this);
        super.onDestroy();
    }
}

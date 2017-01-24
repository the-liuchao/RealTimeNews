package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.liuming.mylibrary.widge.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by hp on 2017/1/22.
 */
@ContentView(R.layout.activity_translate)
public class TranslateActivity extends BaseActivity implements TitleBar.BarClickListener {
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.text_from)
    EditText mTextFrom;
    @ViewInject(R.id.text_to)
    EditText mTextTo;


    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, TranslateActivity.class);
        act.startActivity(intent);
    }

    @Override
    protected void initView() {
        mTitleBar.setBarClickListener(this);
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        mTextFrom.setText(data.getItemAt(0).getText());
    }

    @Override
    protected void initData() {
    }

    @Event(value = {R.id.translate})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.translate:
                translate();
                break;
        }
    }

    private void translate() {
        if (TextUtils.isEmpty(mTextFrom.getText())) {
            Toast.makeText(this, "请输入翻译原文", Toast.LENGTH_SHORT).show();
            mTextFrom.setError("请输入翻译原文");
            return;
        }
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put("to", "en");
            params.put("from", "cn");
            String content = mTextFrom.getText().toString().trim();
            content = new String(content.getBytes(), "UTF-8");
            params.put("query",content);
            params.put("key", Constants.TRANSLATEKEY);

            HttpAPI.getInstance().requestTranslate(params,
                    new HttpAPI.TranslateListener() {
                        @Override
                        public void onSuccess(String result) {
                            mTextTo.setText(result);
                        }

                        @Override
                        public void onFail(String errMsg, int errCode) {

                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(int position, View v) {
        BaseApplication.getInstance().finish(this);
    }
}

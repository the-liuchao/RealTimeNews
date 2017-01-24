package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.liuming.mylibrary.widge.TitleBar;
import com.liuming.mylibrary.widge.XClockView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by hp on 2017/1/20.
 */
@ContentView(R.layout.activity_beijing_time)
public class BJTimeActivity extends BaseActivity implements XClockView.OnTimeChangeListener {

    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.clock_view)
    XClockView mClockView;
    @ViewInject(R.id.beijing_time)
    TextView mCurrTime;
    @ViewInject(R.id.update_system)
    TextView mUpdateTime;

    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, BJTimeActivity.class);
        act.startActivity(intent);
    }

    @Override
    protected void initView() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int top = (outMetrics.heightPixels - 96 - outMetrics.widthPixels) / 2;
        mCurrTime.setPadding(0, top / 2, 0, 0);
        mUpdateTime.setPadding(0, top / 3, 0, 0);
        mClockView.setmOnTimeChangeListener(this);
        mTitleBar.setBarClickListener(new TitleBar.BarClickListener() {
            @Override
            public void onClick(int position, View v) {
                if (1 == position) BaseApplication.getInstance().finish(BJTimeActivity.this);
            }
        });
        mUpdateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSystemTime(System.currentTimeMillis() - 1000 * 60 * 60);
            }
        });
    }

    @Override
    protected void initData() {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.BEIJINGTIMEKEY);
        HttpAPI.getInstance().requestBeijingTime(params, new HttpAPI.LBJTimeListener() {
            @Override
            public void onSuccess(String stime) {
                if (!TextUtils.isEmpty(stime)) {
                    long time = Long.parseLong(stime);
                    Calendar.getInstance().setTimeInMillis(time);
                    Log.e("print", "tiime:" + time);
                }
                mClockView.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        , Calendar.getInstance().get(Calendar.MINUTE)
                        , Calendar.getInstance().get(Calendar.SECOND));
            }

            @Override
            public void onFail(String errMsg, int errCode) {
                Calendar.getInstance().setTimeInMillis(System.currentTimeMillis());
                mClockView.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        , Calendar.getInstance().get(Calendar.MINUTE)
                        , Calendar.getInstance().get(Calendar.SECOND));
            }
        });
    }

    @Override
    public void onTimeChange(View view, int hour, int minute, int second) {
        mCurrTime.setText(String.format("%02d", hour) + " : " + String.format("%02d", minute) + " : " + String.format("%02d", second));
    }

    public void updateSystemTime(long time) {
        try {
            Calendar.getInstance().setTimeInMillis(time);
            StringBuilder sb = new StringBuilder();
            sb.append(Calendar.getInstance().get(Calendar.YEAR))
                    .append(String.format("%02d", Calendar.getInstance().get(Calendar.MONTH)))
                    .append(String.format("%02d", Calendar.getInstance().get(Calendar.DAY_OF_MONTH)))
                    .append(".")
                    .append(String.format("%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)))
                    .append(String.format("%02d", Calendar.getInstance().get(Calendar.MINUTE)))
                    .append(String.format("%02d", Calendar.getInstance().get(Calendar.SECOND)));
            Process process = Runtime.getRuntime().exec("su");
            String datetime = sb.toString(); //测试的设置的时间【时间格式 yyyyMMdd.HHmmss】
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("setprop persist.sys.timezone GMT\n");
            os.writeBytes("/system/bin/date -s " + datetime + "\n");
            os.writeBytes("clock -w\n");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

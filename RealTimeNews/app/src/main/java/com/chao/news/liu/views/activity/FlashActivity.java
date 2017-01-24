package com.chao.news.liu.views.activity;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 过渡界面
 * Created by hp on 2017/1/12.
 */
@ContentView(value = R.layout.activity_flash)
public class FlashActivity extends BaseActivity {
    @ViewInject(R.id.marquee_text)
    TextView mMarqueeText;

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            enterApp(null);
        }
    };

    @Override
    protected void initView() {
        mMarqueeText.setSelected(true);
    }

    @Override
    protected void initData() {
        mHandler.postDelayed(mRunnable, 3000);
    }

    @Event(value = R.id.enter_app, type = View.OnClickListener.class)
    private void enterApp(View view) {
        mHandler.removeCallbacks(mRunnable);
        String topName = BaseApplication.getInstance().getTopAct();
        if (MainActivity.class.getName().equals(topName)) return;
        MainActivity.startActivity(this);
        BaseApplication.getInstance().finish(this);
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(mRunnable);
        super.onBackPressed();
    }
}

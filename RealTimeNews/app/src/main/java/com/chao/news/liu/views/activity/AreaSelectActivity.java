package com.chao.news.liu.views.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.AreaSelectAdapter;
import com.chao.news.liu.api.DatabaseDao;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.Address;
import com.liuming.mylibrary.widge.TitleBar;
import com.liuming.mylibrary.widge.XSideBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * 天气查看添加地区
 * Created by hp on 2017/1/18.
 */
@ContentView(value = R.layout.area_select_layout)
public class AreaSelectActivity extends BaseActivity {

    @ViewInject(R.id.listview)
    ListView mListView;
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.sidebar)
    XSideBar mSidebar;
    @ViewInject(R.id.tv_word_dialog)
    TextView mWord;

    private static List<String> mAreas;
    private int mPosition;
    private List<Address> mCities;
    private AreaSelectAdapter mAreaAdaper;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                hideProgressDialog();
                mCities = (List<Address>) msg.obj;
                mAreaAdaper = new AreaSelectAdapter(mCities, AreaSelectActivity.this,mAreas);
                mListView.setAdapter(mAreaAdaper);
            } else if (msg.what == -1) {
                hideProgressDialog();
            }
        }
    };


    public static void startActivity(Fragment fragment, int requestCode, List<String> areas) {
        Intent in = new Intent(fragment.getActivity(), AreaSelectActivity.class);
        fragment.startActivityForResult(in, requestCode);
        mAreas = areas;
    }

    @Override
    protected void initView() {
        showProgressDialog();
        DatabaseDao.getInstance().queryArea(new DatabaseDao.QueryAreaListener() {
            @Override
            public void onResult(List<Address> mCities) {
                Message msg = mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = mCities;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onError(String errorMsg) {
                mHandler.sendEmptyMessage(-1);
            }
        });
        mTitleBar.setBarClickListener(new TitleBar.BarClickListener() {
            @Override
            public void onClick(int position, View v) {
                if (1 == position) BaseApplication.getInstance().finish(AreaSelectActivity.this);
            }
        });
        mSidebar.setTextView(mWord);
        mSidebar.setOnTouchingLetterChangedListener(
                new XSideBar.OnTouchingLetterChangedListener() {
                    @Override
                    public void onTouchingLetterChanged(String s) {
                        char c = s.toLowerCase().toCharArray()[0];
                        for (int i = 0; i < mCities.size(); i++) {
                            if (mCities.get(i).getLetter() == c) {
                                mPosition = i;
                                break;
                            }
                        }
                        mListView.setSelection(mPosition);
                    }
                });
    }

    @Override
    protected void initData() {
    }
}

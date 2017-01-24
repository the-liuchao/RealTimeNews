package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.PostCodeAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.CurrLocation;
import com.chao.news.liu.bean.PostCode;
import com.liuming.mylibrary.dialog.AreaPopupwindow;
import com.liuming.mylibrary.utils.SPHelper;
import com.liuming.mylibrary.widge.LoadmoreListView;
import com.liuming.mylibrary.widge.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/22.
 */
@ContentView(R.layout.activity_postcode)
public class PostcodeActivity extends BaseActivity implements TitleBar.BarClickListener, LoadmoreListView.OnLoadListener {
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.btn_query_addr)
    TextView mQueryAddr;
    @ViewInject(R.id.btn_query_code)
    TextView mQueryCode;
    @ViewInject(R.id.viewflipper)
    ViewFlipper mViewFlip;
    @ViewInject(R.id.input_postcode)
    EditText mInputCode;
    @ViewInject(R.id.select_address)
    TextView mSelectAddr;
    @ViewInject(R.id.result_addr)
    LoadmoreListView mListAddr;
    @ViewInject(R.id.result_code)
    LoadmoreListView mListCode;
    @ViewInject(R.id.code_fail)
    TextView mCodeFail;
    @ViewInject(R.id.addr_fail)
    TextView mAddrFail;

    private int mCurrPage;
    private int mCodePage, mAddrPage;
    private int mCodeRows = 20, mAddrRows = 20;
    private CurrLocation mLocation;
    private AreaPopupwindow mPopupwindow;
    private PostCodeAdapter mCodeAdapter;
    private PostCodeAdapter mAddrAdapter;


    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, PostcodeActivity.class);
        act.startActivity(intent);
    }

    @Override
    protected void initView() {
        mTitleBar.setBarClickListener(this);
        mQueryAddr.setSelected(true);
        mQueryAddr.setTextColor(Color.WHITE);
        mListAddr.setOnLoadListener(this);
        mListCode.setOnLoadListener(this);
        mListCode.initBottomView();
        mListAddr.initBottomView();
        mAddrAdapter = new PostCodeAdapter(this, true);
        mCodeAdapter = new PostCodeAdapter(this, false);
        mListCode.setAdapter(mCodeAdapter);
        mListAddr.setAdapter(mAddrAdapter);
    }

    @Override
    protected void initData() {
        mLocation = SPHelper.getObject(Constants.LOCATION);
        mPopupwindow = new AreaPopupwindow.Builder()
                .setmCity(null != mLocation.getmCity() ? mLocation.getmCity().replace("市", "") : "")
                .setmDistrict(null != mLocation.getmDistrict() ? mLocation.getmDistrict().replace("区", "") : "")
                .setmPronvice(null != mLocation.getmProvince() ? mLocation.getmProvince().replace("市", "") : "")
                .setmListener(new AreaPopupwindow.ResultListener() {
                    @Override
                    public void onResult(String pronvice, String city, String district) {
                        if (city.equals("上海")
                                || "重庆".equals(city)
                                || "北京".equals(city)
                                || "天津".equals(city)
                                )
                            mSelectAddr.setText(city + "市 " + district + "区");
                        else if ("香港".equals(city)
                                || "澳门".equals(city))
                            mSelectAddr.setText(city + " " + district + "区");
                        else
                            mSelectAddr.setText(pronvice + "省 " + city + "市 " + district);
                    }
                }).build();
    }

    @Event(value = {R.id.btn_query, R.id.btn_query_addr,
            R.id.btn_query_code, R.id.select_address})
    private void post(View v) {
        switch (v.getId()) {
            case R.id.btn_query:
                query(false);
                break;
            case R.id.btn_query_addr:
                toggle(0);
                break;
            case R.id.btn_query_code:
                toggle(1);
                break;
            case R.id.select_address:
                mPopupwindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    private void toggle(int index) {
        if (index == mCurrPage) return;
        mCurrPage = index;
        mQueryCode.setSelected(!mQueryCode.isSelected());
        mQueryAddr.setSelected(!mQueryAddr.isSelected());
        mQueryCode.setTextColor(mQueryCode.isSelected() ? Color.WHITE : Color.BLACK);
        mQueryAddr.setTextColor(mQueryAddr.isSelected() ? Color.WHITE : Color.BLACK);
        mViewFlip.showNext();
    }

    private void query(final boolean isLoadMore) {
        if (!isLoadMore) {
            mAddrPage = 1;
            mCodePage = 1;
        }
        if (mCurrPage == 0) {
            if (TextUtils.isEmpty(mInputCode.getText().toString().trim())) {
                mInputCode.setError("请输入查询邮编");
                return;
            }
            if (!isLoadMore) showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("key", Constants.POSTCODEKEY);
            params.put("page", String.valueOf(mAddrPage));
            params.put("rows", String.valueOf(mAddrRows));
            params.put("postnumber", mInputCode.getText().toString().trim());
            HttpAPI.getInstance().requestPostcode(mCurrPage, params, new HttpAPI.PostcodeListener() {
                @Override
                public void onSuccess(List<PostCode> codes) {
                    mAddrFail.setVisibility(View.GONE);
                    mListAddr.setVisibility(View.VISIBLE);
                    if (!isLoadMore) {
                        hideProgressDialog();
                        mListAddr.hideNomoreView();
                        mAddrAdapter.refresh(codes);
                    } else {
                        mAddrAdapter.add(codes);
                    }
                    if (codes == null || codes.size() < mAddrRows) {
                        mListAddr.showNomoreView();
                    } else {
                        mAddrPage++;
                    }
                }

                @Override
                public void onFail(String errMsg, int errCode) {
                    if (!isLoadMore)
                        hideProgressDialog();
                    mAddrFail.setText(errMsg);
                    mAddrFail.setVisibility(View.VISIBLE);
                    mListAddr.setVisibility(View.GONE);
                }
            });
        } else {
            String pcd = mSelectAddr.getText().toString().replace(" ", "");
            if (TextUtils.isEmpty(pcd)) {
                mInputCode.setError("请选择地址");
                return;
            }
            if (!isLoadMore) showProgressDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("key", Constants.POSTCODEKEY);
            params.put("address", pcd);
            params.put("page", String.valueOf(mCodePage));
            params.put("rows", String.valueOf(mCodeRows));
            HttpAPI.getInstance().requestPostcode(mCurrPage, params, new HttpAPI.PostcodeListener() {
                @Override
                public void onSuccess(List<PostCode> codes) {
                    mCodeFail.setVisibility(View.GONE);
                    mListCode.setVisibility(View.VISIBLE);
                    if (!isLoadMore) {
                        hideProgressDialog();
                        mListCode.hideNomoreView();
                        mCodeAdapter.refresh(codes);
                    } else {
                        mCodeAdapter.add(codes);
                    }
                    if (codes == null || codes.size() < mCodeRows) {
                        mListCode.showNomoreView();
                    } else {
                        mCodePage++;
                    }
                }

                @Override
                public void onFail(String errMsg, int errCode) {
                    if (!isLoadMore)
                        hideProgressDialog();
                    mCodeFail.setText(errMsg);
                    mCodeFail.setVisibility(View.VISIBLE);
                    mListCode.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onClick(int position, View v) {
        BaseApplication.getInstance().finish(this);
    }

    @Override
    public void onLoad() {
        query(true);
    }
}

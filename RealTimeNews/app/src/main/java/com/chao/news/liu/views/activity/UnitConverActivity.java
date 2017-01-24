package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.UnitConverAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.UnitConversion;
import com.liuming.mylibrary.widge.CommaView;
import com.liuming.mylibrary.widge.TitleBar;
import com.liuming.mylibrary.widge.loopview.LoopView;
import com.liuming.mylibrary.widge.loopview.OnItemSelectedListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/20.
 */
@ContentView(R.layout.activity_unit_conver)
public class UnitConverActivity extends BaseActivity implements TitleBar.BarClickListener {

    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.conversion_type)
    TextView mConverType;
    @ViewInject(R.id.conversion_unit)
    TextView mConverUnit;
    @ViewInject(R.id.conversion_value)
    CommaView mConverValue;
    @ViewInject(R.id.listview)
    ListView mListView;

    int mConversionType;
    int mConversionUnit;
    UnitConverAdapter mUnitConverAdapter;
    HashMap<String, String[]> mUnitMaps;

    private String[] mConversionTypes = {"长度转换", "面积转换", "体积转换", "重量转换", "功转换", "功率转换", "压强转换", "存储单位转换"};

    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, UnitConverActivity.class);
        act.startActivity(intent);
    }

    @Override
    protected void initView() {
        mTitleBar.setBarClickListener(this);
        mConverValue.setCommaNum(3);
        mUnitMaps = new HashMap<>();
        mUnitMaps.put("0", getResources().getStringArray(R.array.length_unit));
        mUnitMaps.put("1", getResources().getStringArray(R.array.area_unit));
        mUnitMaps.put("2", getResources().getStringArray(R.array.volume_unit));
        mUnitMaps.put("3", getResources().getStringArray(R.array.weight_unit));
        mUnitMaps.put("4", getResources().getStringArray(R.array.work_unit));
        mUnitMaps.put("5", getResources().getStringArray(R.array.power_unit));
        mUnitMaps.put("6", getResources().getStringArray(R.array.pressure_unit));
        mUnitMaps.put("7", getResources().getStringArray(R.array.storage_unit));
    }

    @Override
    protected void initData() {
        mConverValue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER)
                    conver();
                return false;
            }
        });
        mUnitConverAdapter = new UnitConverAdapter(this);
        mListView.setAdapter(mUnitConverAdapter);
    }

    @Event(value = {R.id.conversion_type, R.id.conversion_unit, R.id.btn_conversion})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.conversion_type:
                showPopup(mConversionTypes, true);
                break;
            case R.id.conversion_unit:
                if (TextUtils.isEmpty(mConverType.getText().toString().trim())) {
                    Toast.makeText(this, "请先选择转换单位类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                showPopup(mUnitMaps.get(String.valueOf(mConversionType)), false);
                break;
            case R.id.btn_conversion:
                conver();
//                AreaPopupwindow.getInstance().showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    /**
     * 转换
     */
    private void conver() {
        if (TextUtils.isEmpty(mConverValue.getText().toString().trim())
                || TextUtils.isEmpty(mConverType.getText().toString().trim())
                || TextUtils.isEmpty(mConverUnit.getText().toString().trim())) {
            mConverValue.setError("请输入转换数据");
            return;
        }
        showProgressDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.UNITCONVERSIONKEY);
        params.put("unit", String.valueOf(mConversionUnit + 1));
        params.put("value", mConverValue.getText().toString().replace(mConverValue.getChar(), ""));
        HttpAPI.getInstance().requestUnitConver(mConversionType, params,
                new HttpAPI.UnitConverListener() {
                    @Override
                    public void onSuccess(List<UnitConversion> results) {
                        hideProgressDialog();
                        mUnitConverAdapter.refresh(results);
                    }

                    @Override
                    public void onFail(String errMsg, int errCode) {
                        hideProgressDialog();

                    }
                });
    }

    public void showPopup(String[] datas, final boolean isType) {
        int mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        final PopupWindow popup = new PopupWindow();
        popup.setAnimationStyle(R.style.PopupwindowAnimtion);
        popup.setWidth(mScreenWidth);
        popup.setHeight(-2);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new ColorDrawable());
        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.unit_wheel_layout, null);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.wheel_view);
        contentView.findViewById(R.id.wheel_confirm).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                        if (isType) {
                            String srcText = mConverType.getText() == null ? "" : mConverType.getText().toString();
                            String newType = mConversionTypes[mConversionType];
                            if (!srcText.equals(newType)) {
                                mConverType.setText(newType);
                                mConversionUnit = 0;
                                mConverUnit.setText(mUnitMaps.get(String.valueOf(mConversionType))[mConversionUnit]);
                                mUnitConverAdapter.refresh(null);
                            }
                        } else {
                            String srcUnit = mConverUnit.getText() == null ? "" : mConverUnit.getText().toString();
                            String newUnit = mUnitMaps.get(String.valueOf(mConversionType))[mConversionUnit];
                            if (!srcUnit.equals(newUnit)) {
                                mConverUnit.setText(newUnit);
                                mUnitConverAdapter.refresh(null);
                            }
                        }
                    }
                }
        );
        //设置是否循环播放
//        loopView.setNotLoop();
        //滚动监听
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (isType && index != mConversionType) {
                    mConversionType = index;
                    mConversionUnit = 0;
                } else {
                    mConversionUnit = index;
                }
            }
        });
        //设置原始数据
        loopView.setItems(Arrays.asList(datas), isType ? mConversionType : mConversionUnit);
        popup.setContentView(contentView);
        popup.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(int position, View v) {
        if (position == 1)
            BaseApplication.getInstance().finish(this);
    }
}

package com.liuming.mylibrary.dialog;

import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.liuming.mylibrary.R;
import com.liuming.mylibrary.XApplication;
import com.liuming.mylibrary.utils.FileHelper;
import com.liuming.mylibrary.widge.loopview.LoopView;
import com.liuming.mylibrary.widge.loopview.OnItemSelectedListener;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 省市三级联动Popupwindow
 * Created by hp on 2017/1/21.
 * <p>
 * AreaPopupwindow popupwindow = new AreaPopupwindow.Builder()
 * .setmCity(null != mLocation.getmCity() ? mLocation.getmCity().replace("市", "") : "")
 * .setmDistrict(null != mLocation.getmDistrict() ? mLocation.getmDistrict().replace("区", "") : "")
 * .setmPronvice(null != mLocation.getmProvince() ? mLocation.getmProvince().replace("市", "") : "")
 * .setmListener(new AreaPopupwindow.ResultListener() {
 *
 * @Override public void onResult(String pronvice, String city, String district) {
 * Toast.makeText(UnitConverActivity.this, "你选择了" + pronvice + " " + city + "" + district, Toast.LENGTH_SHORT).show();
 * }
 * }).build();
 * popupwindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
 */

public class AreaPopupwindow extends PopupWindow implements View.OnClickListener {
    private static AreaPopupwindow mPopupWindow;
    private static Hashtable<String, List<String>> mCitys;
    private static Hashtable<String, List<String>> mDistricts;
    private static CopyOnWriteArrayList<String> mProvinces;
    private int mPronviceIndex;
    private int mCityIndex;
    private int mDistrictIndex;
    private String mProvince;               //默认省
    private String mCity;                   //默认市
    private String mDistrict;               //默认区
    private ResultListener mListener;       //选择监听
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (0 == msg.what)
                init();
        }
    };

    public interface ResultListener {
        void onResult(String pronvice, String city, String district);
    }


    public static AreaPopupwindow getInstance() {
        if (null == mPopupWindow) mPopupWindow = new AreaPopupwindow();
        return mPopupWindow;
    }

    private AreaPopupwindow() {
        queryArea();
    }


    @Override
    public void onClick(View v) {
        dismiss();
        String pronvice = mProvinces.get(mPronviceIndex);
        String city = mCitys.get(pronvice).get(mCityIndex);
        String district = mDistricts.get(city).get(mDistrictIndex);
        if (null != mListener)
            mListener.onResult(pronvice, city, district);
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(XApplication.getInstance());
        View contentView = inflater.inflate(R.layout.popup_wheel_layout, null);
        final LoopView cityLoopView = (LoopView) contentView.findViewById(R.id.wheel_city);
        final LoopView districtLoopView = (LoopView) contentView.findViewById(R.id.wheel_district);
        setAnimationStyle(R.style.PopupwindowAnimtion);
        setWidth(-1);
        setHeight(-2);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
        LoopView provinceLoopView = (LoopView) contentView.findViewById(R.id.wheel_province);
        contentView.findViewById(R.id.wheel_confirm).setOnClickListener(this);
        //设置是否循环播放
//        provinceLoopView.setNotLoop();
        //滚动监听
        provinceLoopView.setListener(
                new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        mPronviceIndex = index;
                        if (mCitys != null && mProvinces != null) {
                            if (mPronviceIndex == -1)
                                mPronviceIndex = 0;
                            String pronvice = mProvinces.get(mPronviceIndex);
                            if (mCitys.get(pronvice).contains(mCity)) {
                                int p = mCitys.get(pronvice).indexOf(mCity);
                                cityLoopView.setItems(mCitys.get(pronvice), p == -1 ? 0 : p);
                            } else {
                                cityLoopView.setItems(mCitys.get(pronvice));
                            }
                        }
                    }
                }
        );
        cityLoopView.setListener(
                new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        mCityIndex = index;
                        index = index == -1 ? 0 : index;
                        String zone = mCitys.get(mProvinces.get(mPronviceIndex)).get(index);
                        if (mDistricts.get(zone).contains(mDistrict)) {
                            int p = mDistricts.get(zone).indexOf(mDistrict);
                            districtLoopView.setItems(mDistricts.get(zone), p == -1 ? 0 : p);
                        } else {
                            districtLoopView.setItems(mDistricts.get(zone));
                        }
                    }
                }
        );
        districtLoopView.setListener(
                new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        mDistrictIndex = index;
                    }
                }
        );
        //设置原始数据
        provinceLoopView.setItems(mProvinces, mProvinces.indexOf(mProvince));
        setContentView(contentView);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (mProvinces == null || mCitys == null || mDistricts == null) {
            Toast.makeText(XApplication.getInstance(), "数据加载中,请稍后...", Toast.LENGTH_SHORT).show();
            return;
        }
        super.showAtLocation(parent, gravity, x, y);
    }

    public static class Builder {
        AreaPopupwindow mPopupWindow;

        public Builder() {
            this.mPopupWindow = AreaPopupwindow.getInstance();
        }

        public Builder setmDistrict(String mDistrict) {
            mPopupWindow.mDistrict = mDistrict;
            return this;
        }

        public Builder setmCity(String mCity) {
            mPopupWindow.mCity = mCity;
            return this;
        }

        public Builder setmPronvice(String mPronvice) {
            mPopupWindow.mProvince = mPronvice;
            return this;
        }

        public Builder setmListener(ResultListener mListener) {
            mPopupWindow.mListener = mListener;
            return this;
        }

        public AreaPopupwindow build() {
            return this.mPopupWindow;
        }
    }

    /**
     * 地址数据查询
     */

    public void queryArea() {
        new Thread() {
            @Override
            public void run() {
                try {
                    CopyOnWriteArrayList<String> provinces = new CopyOnWriteArrayList<>();
                    List<String> cities = new ArrayList<>();
                    Hashtable<String, List<String>> provinceCities = new Hashtable<>();
                    Hashtable<String, List<String>> cityDistricts = new Hashtable<>();
                    FileHelper helper = new FileHelper(XApplication.getInstance());
                    String path = XApplication.getInstance().getDatabasePath("city").getPath();
                    if (!helper.exist(path + "/" + "city.db")) {
                        helper.copyAssets("city.db", path);
                    }
                    DbManager.DaoConfig config = new DbManager.DaoConfig()
                            .setDbDir(new File(path))
                            .setDbName("city.db");
                    DbManager mDm = x.getDb(config);
                    Cursor c = mDm.execQuery("select * from t_city");
                    while (c.moveToNext()) {
                        String provcn = c.getString(c.getColumnIndex("PROVCN"));
                        String city = c.getString(c.getColumnIndex("DISTRICTCN"));
                        if (!provinces.contains(provcn))
                            provinces.add(provcn);
                        if (!cities.contains(city))
                            cities.add(city);
                    }
                    List<String> list;
                    for (String province : provinces) {
                        list = new ArrayList<>();
                        Cursor cityCursor = mDm.execQuery("select * from t_city WHERE PROVCN='" + province + "'");
                        while (cityCursor.moveToNext()) {
                            String city = cityCursor.getString(c.getColumnIndex("DISTRICTCN"));
                            if (!list.contains(city))
                                list.add(city);
                        }
                        provinceCities.put(province, list);
                        cityCursor.close();
                    }

                    for (String city : cities) {
                        list = new ArrayList<>();
                        Cursor dCursor = mDm.execQuery("select * from t_city WHERE DISTRICTCN='" + city + "'");
                        while (dCursor.moveToNext()) {
                            list.add(dCursor.getString(c.getColumnIndex("NAMECN")));
                        }
                        cityDistricts.put(city, list);
                        dCursor.close();
                    }
                    mProvinces = provinces;
                    mCitys = provinceCities;
                    mDistricts = cityDistricts;
                    mHandler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}

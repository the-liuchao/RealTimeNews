package com.chao.news.liu.views.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.chao.news.liu.adapter.LifeIndexAdapter;
import com.chao.news.liu.adapter.NextWeathAdapter;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.bean.Address;
import com.chao.news.liu.bean.CurrLocation;
import com.chao.news.liu.bean.weat.Weather;
import com.chao.news.liu.utils.Utils;
import com.chao.news.liu.views.activity.AreaSelectActivity;
import com.liuming.mylibrary.utils.ImageHelper;
import com.liuming.mylibrary.utils.SPHelper;
import com.liuming.mylibrary.widge.ReboundScrollView;
import com.liuming.mylibrary.widge.TitleBar;
import com.liuming.mylibrary.widge.XViewPagerFragment;
import com.liuming.mylibrary.widge.jellyrefresh.JellyRefreshLayout;
import com.liuming.mylibrary.widge.jellyrefresh.PullToRefreshLayout;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.animation.ObjectAnimator.ofFloat;

/**
 * Created by hp on 2017/1/12.
 */
@ContentView(R.layout.fragment_weat)
public class WeatFragment extends XViewPagerFragment implements PullToRefreshLayout.PullToRefreshListener, TitleBar.BarClickListener {

    @ViewInject(R.id.weath_root)
    View mRootView;
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.refresh_layout)
    JellyRefreshLayout mRefreshLayout;
    @ViewInject(R.id.anim_bg1)
    ImageView mAnimBg1;
    @ViewInject(R.id.anim_bg2)
    ImageView mAnimBg2;
    @ViewInject(R.id.weath_scroll_view)
    ReboundScrollView mScrollView;
    //========================= 当前时刻天气
    @ViewInject(R.id.weath_text)
    TextView mWeathText;
    @ViewInject(R.id.weath_temp)
    TextView mWeathTemp;
    @ViewInject(R.id.weath_today_weak)
    TextView mTodayWeek;
    @ViewInject(R.id.weath_today_temp)
    TextView mTodayTemp;
    @ViewInject(R.id.today_icon)
    ImageView mTodayIcon;
    @ViewInject(R.id.horizontal_line1)
    ImageView mHoriLine1;
    //========================= 未来5天天气
    @ViewInject(R.id.listview)
    ListView mNextWeath;
    @ViewInject(R.id.horizontal_line2)
    ImageView mHoriLine2;
    //========================= 生活指数
    @ViewInject(R.id.life_listview)
    ListView mLifeIndexs;
    //========================= 空气质量
    @ViewInject(R.id.atmos_layout)
    View mAtmosLayout;
    @ViewInject(R.id.atmos_desc)
    TextView mAtmosDesc;
    @ViewInject(R.id.atmos_level)
    TextView mAtmosLevel;
    @ViewInject(R.id.atmos_pm)
    TextView mAtmosPm;
    @ViewInject(R.id.atmos_pm25)
    TextView mAtmosPm25;
    @ViewInject(R.id.atmos_pm10)
    TextView mAtmosPm10;
    @ViewInject(R.id.horizontal_line3)
    ImageView mHoriLine3;


    private ObjectAnimator mAnimbg;
    private String mCurrCity;
    private String mCurrDistrict;
    private int mScreenWidth;
    private List<String> mAreas;
    private ArrayAdapter mAreaAdapter;
    private View mFooterView;

    @Override
    protected void initView(View mRootView) {
        mScreenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        mTitleBar.setRightIcon(R.mipmap.icon_share).setBarClickListener(this);
        mAreas = new ArrayList<>();
        initAnimation(null);
        initCurrCity();
        mScrollView.setTopRebound(false);
        View loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.view_loading, null);
        mRefreshLayout.setLoadingView(loadingView);
        mRefreshLayout.setPullToRefreshListener(this);
    }

    @Override
    protected void initData() {
        try {
            List<Address> addresses = mDbManager.selector(Address.class)
                    .where("type", "=", "weather")
                    .findAll();
            if (null == mAreas) mAreas = new ArrayList<>();
            if (null != addresses) {
                for (Address address : addresses) {
                    mAreas.add(address.getmCity());
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
            if (null == mAreas) mAreas = new ArrayList<>();
        }
        mTitleBar.setTitle(mCurrCity + mCurrDistrict, View.VISIBLE, R.mipmap.icon_area_selecter);
    }

    /**
     * 添加地区
     */
    private void showPopup() {
        final PopupWindow areaPopup = new PopupWindow();
        areaPopup.setAnimationStyle(R.style.area_animation);
        areaPopup.setWidth(mScreenWidth * 2 / 3);
        areaPopup.setHeight(-2);
        areaPopup.setFocusable(true);
        areaPopup.setOutsideTouchable(true);
        areaPopup.setBackgroundDrawable(new ColorDrawable());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View contentView = inflater.inflate(R.layout.area_layout, null);
        ListView areaListView = (ListView) contentView.findViewById(R.id.area_listview);
        final AbsListView.LayoutParams params;

        mFooterView = inflater.inflate(R.layout.area_add_layout, null);
        params = new AbsListView.LayoutParams(mScreenWidth * 2 / 3, -2);
        areaListView.addFooterView(mFooterView);
        mFooterView.setLayoutParams(params);
        mFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAreas.size() >= 5) {
                    Toast.makeText(getActivity(), "城市超过上限！", Toast.LENGTH_SHORT).show();
                    return;
                }
                areaPopup.dismiss();
                AreaSelectActivity.startActivity(WeatFragment.this, 100, mAreas);
            }
        });
        mFooterView.setBackgroundColor(Color.parseColor("#00000000"));
        if (mAreas.size() >= 5) {
            mFooterView.setBackgroundResource(R.drawable.add_footer_bg);
        }
        mAreaAdapter = new ArrayAdapter<String>(getActivity()
                , R.layout.area_item, R.id.area_name, mAreas) {
            @NonNull
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View contentView = super.getView(position, convertView, parent);
                contentView.setLayoutParams(params);
                contentView.findViewById(R.id.area_delete).setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    mFooterView.setBackgroundColor(Color.parseColor("#00000000"));
                                    String area = getItem(position);
                                    mAreas.remove(area);
                                    notifyDataSetChanged();
                                    mDbManager.delete(Address.class, WhereBuilder.b("city", "=", area).and("type", "=", "weather"));
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                contentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        areaPopup.dismiss();
                        mCurrCity = mAreas.get(position);
                        mTitleBar.setTitle(mCurrCity);
                        requestWeather();
                    }
                });
                return contentView;
            }
        };
        areaListView.setAdapter(mAreaAdapter);
//        areaPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                backgroundAlpha(1);
//            }
//        });
        areaPopup.setContentView(contentView);
//        backgroundAlpha(0.6f);
        areaPopup.showAsDropDown(mTitleBar, mScreenWidth / 6, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            try {
                mCurrCity = data.getStringExtra("area");
                String district = data.getStringExtra("district");
                String pronvice = data.getStringExtra("pronvice");
                if (mAreaAdapter != null) mAreaAdapter.notifyDataSetChanged();
                if (null != mFooterView)
                    mFooterView.setBackgroundColor(Color.parseColor("#00000000"));
                if (!mAreas.contains(mCurrCity)) {
                    mDbManager.delete(Address.class
                            , WhereBuilder.b("city", "=", mCurrCity)
                                    .and("type", "=", "weather"));
                    mDbManager.save(new Address(pronvice, district, mCurrCity, "weather"));
                    mAreas.add(mCurrCity);
                }
                requestWeather();
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    protected void onFirstUserVisiable() {
        super.onFirstUserVisiable();
        requestWeather();
    }

    private void initAnimation(String weather) {
        if (mAnimbg != null) mAnimbg.cancel();
        mRootView.setBackgroundResource(Utils.getWeathBg(weather));
        mAnimbg = ofFloat(mAnimBg2, "TranslationX", mScreenWidth, -mScreenWidth);
        Bitmap bm2 = ImageHelper.getInstance().horizontalFlip(Utils.getWeathBg(weather));
        Bitmap bm1 = ImageHelper.getInstance().readBitmapById(Utils.getWeathBg(weather));
        mAnimBg2.setImageBitmap(bm2);
        mAnimBg1.setImageBitmap(bm1);
        mAnimBg2.setTranslationX(-mAnimBg2.getLeft());
        mAnimBg1.setTranslationX(-mAnimBg1.getLeft());
        mAnimbg.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mAnimBg2.getTranslationX() > 0) {
                    mAnimBg1.setTranslationX(mAnimBg2.getTranslationX() - mScreenWidth);
                } else {
                    mAnimBg1.setTranslationX(mAnimBg2.getTranslationX() + mScreenWidth - 2);
                }
            }
        });
        mAnimbg.cancel();
        mAnimbg.setRepeatCount(ObjectAnimator.INFINITE);
        mAnimbg.setRepeatMode(ObjectAnimator.RESTART);
        mAnimbg.setDuration(60 * 1000).start();
    }

    /**
     * 请求数据
     */
    private void requestWeather() {
        HashMap<String, String> params = new HashMap<>();
        params.put("key", Constants.WEATHERKEY);
        params.put("cityname", mCurrCity);
        HttpAPI.getInstance().requestWeather(params, new HttpAPI.WeatherListener() {
            @Override
            public void onSuccess(Weather weathers) {
                if (weathers != null) {
                    if (weathers.getmRealTime() == null) mHoriLine1.setVisibility(View.GONE);
                    else mHoriLine1.setVisibility(View.VISIBLE);
                    mTitleBar.setTitle(weathers.getmRealTime().mCity_name);
                    String todayInfo = weathers.getmRealTime().mWeather.mInfo;
                    initAnimation(todayInfo);
                    mWeathText.setText(todayInfo);
                    mTodayIcon.setImageBitmap(Utils.getWeathIcon(todayInfo));
                    mWeathTemp.setText(weathers.getmRealTime().mWeather.mTemperature + "℃");
                    if (weathers.getmWeather7() == null) mHoriLine2.setVisibility(View.GONE);
                    else mHoriLine2.setVisibility(View.VISIBLE);
                    mTodayWeek.setText("星期" + weathers.getmWeather7().mOneDays.get(0).mWeek + " 今天");
                    mTodayTemp.setText(weathers.getmWeather7().mOneDays.get(0).getTemp());
                    mNextWeath.setAdapter(new NextWeathAdapter(weathers.getmWeather7().mOneDays, getActivity()));
                    if (weathers.getmPm25().mIsEmpty) mAtmosLayout.setVisibility(View.GONE);
                    else mAtmosLayout.setVisibility(View.VISIBLE);
                    mAtmosDesc.setText(weathers.getmPm25().mPm25.mDes);
                    mAtmosLevel.setText(weathers.getmPm25().mPm25.mQuality);
                    mAtmosPm.setText(weathers.getmPm25().mPm25.mCurPm);
                    mAtmosPm25.setText(weathers.getmPm25().mPm25.mPm25);
                    mAtmosPm10.setText(weathers.getmPm25().mPm25.mPm10);
                    if (weathers.getmLife() == null) mHoriLine3.setVisibility(View.GONE);
                    else mHoriLine3.setVisibility(View.VISIBLE);
                    mLifeIndexs.setAdapter(new LifeIndexAdapter(getActivity(), weathers.getmLife().mInfo.getmLifeIndexs()));
                }
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFail(String errMsg, int errCode) {
                mRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "刷新失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 初始化当前城市 文本
     */
    private void initCurrCity() {
        CurrLocation location = SPHelper.getObject("location");
        if (location == null) {
            mCurrCity = "上海";
            return;
        } else mCurrCity = location.getmCity() == null ? "上海" : location.getmCity();
        if (mCurrCity.equals("上海市")
                || mCurrCity.equals("重庆市")
                || mCurrCity.equals("北京市")
                || mCurrCity.equals("天津市")) {
            boolean isEmpty = TextUtils.isEmpty(location.getmDistrict());
            mCurrDistrict = isEmpty ? "" : "-" + location.getmDistrict();
            mCurrCity = mCurrCity.substring(0, 2);
        }
        SPHelper.put(getActivity(), Constants.CACHECITY, mCurrCity);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        requestWeather();
    }

    @Override
    public void onClick(int position, View v) {
        if (position == 3)
            Utils.share(getActivity());
        else if (position == 2) {
            showPopup();
        }
    }
}

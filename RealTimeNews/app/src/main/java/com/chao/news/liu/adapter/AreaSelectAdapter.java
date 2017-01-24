package com.chao.news.liu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.Address;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by hp on 2017/1/18.
 */

public class AreaSelectAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<Address> mAddress;
    private List<String> mAreas;
    private LayoutInflater mInflater;
    private Resources mRes;

    public AreaSelectAdapter(List<Address> mAreas, Activity mActivity, List<String> areas) {
        this.mAddress = mAreas;
        this.mActivity = mActivity;
        this.mAreas = areas;
        this.mRes = mActivity.getResources();
        this.mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mAddress == null ? 0 : mAddress.size();
    }

    @Override
    public Address getItem(int position) {
        return mAddress.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.area_select_item, null);
            x.view().inject(viewHolder, convertView);
            convertView.setOnClickListener(viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String cityName = getItem(position).getmCity();
        if (null != mAreas && mAreas.contains(cityName))
            viewHolder.mCityName.setTextColor(mRes.getColor(R.color.colorPrimary));
        else
            viewHolder.mCityName.setTextColor(Color.parseColor("#FF414141"));
        viewHolder.mCityName.setText(cityName);
        viewHolder.setPosition(position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener {
        @ViewInject(R.id.area_cityname)
        TextView mCityName;
        int mPosition;

        public void setPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            Address address = getItem(mPosition);
            String area = address.getmCity();
            String district = address.getmDistrict();
            String pronvice = address.getmPronvice();
            Intent intent = mActivity.getIntent();
            intent.putExtra("area", area);
            intent.putExtra("district", district);
            intent.putExtra("pronvice", pronvice);
            mActivity.setResult(Activity.RESULT_OK, intent);
            BaseApplication.getInstance().finish(mActivity);
        }
    }
}

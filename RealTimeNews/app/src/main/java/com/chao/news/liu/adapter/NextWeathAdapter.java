package com.chao.news.liu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.bean.weat.OneDay;
import com.chao.news.liu.utils.Utils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/1/14.
 */

public class NextWeathAdapter extends BaseAdapter {

    private List<OneDay> mOneDays;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public NextWeathAdapter(List<OneDay> mWeathers, Activity mActivity) {
        this.mOneDays = mWeathers;
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
    }

    public void refresh(List<OneDay> hotspotNewses) {
        if (mOneDays == null) {
            mOneDays = new ArrayList<>();
        } else {
            mOneDays.clear();
        }
        mOneDays.addAll(mOneDays);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mOneDays == null ? 0 : mOneDays.size();
    }

    @Override
    public OneDay getItem(int position) {
        return mOneDays.get(position);
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
            convertView = mInflater.inflate(R.layout.adapter_nextwea_item, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OneDay oneDay = getItem(position);
        viewHolder.mNextWeek.setText("星期" + oneDay.mWeek);
        viewHolder.mNextTemp.setText(getItem(position).getTemp());
        viewHolder.mNextIcon.setImageBitmap(Utils.getWeathIcon(oneDay.mInfo.mDays.mWeather));
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.next_icon)
        ImageView mNextIcon;
        @ViewInject(R.id.next_week)
        TextView mNextWeek;
        @ViewInject(R.id.next_temp)
        TextView mNextTemp;
    }
}

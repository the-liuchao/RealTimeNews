package com.chao.news.liu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.bean.UnitConversion;
import com.liuming.mylibrary.widge.CommaView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/1/22.
 */

public class UnitConverAdapter extends BaseAdapter {

    private Activity mAticity;
    private LayoutInflater mInflater;
    private List<UnitConversion> mDatas;

    public UnitConverAdapter(Activity mAticity) {
        this.mAticity = mAticity;
        this.mInflater = LayoutInflater.from(mAticity);
    }

    public void refresh(List<UnitConversion> results) {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        } else {
            mDatas.clear();
        }
        if (null != results) {
            mDatas.addAll(results);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public UnitConversion getItem(int position) {
        return mDatas.get(position);
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
            convertView = mInflater.inflate(R.layout.adapter_unit_conver, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mNameView.setText(getItem(position).mName);
        double num = Double.parseDouble(getItem(position).mValue);
        viewHolder.mValueView.setCommaNum(3);
        viewHolder.mValueView.setChar(" ");
        viewHolder.mValueView.setDeletable(false);
        viewHolder.mValueView.setText(String.format("%.10f", num));
        return convertView;
    }


    class ViewHolder {
        @ViewInject(R.id.unit_name)
        TextView mNameView;
        @ViewInject(R.id.unit_value)
        CommaView mValueView;
    }
}

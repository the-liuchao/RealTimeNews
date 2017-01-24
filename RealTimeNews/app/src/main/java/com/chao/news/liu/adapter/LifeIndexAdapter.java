package com.chao.news.liu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.bean.weat.LifeIndex;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 天气模块  生活指数适配器
 * Created by hp on 2017/1/18.
 */

public class LifeIndexAdapter extends BaseAdapter {
    List<LifeIndex> mLifes;
    LayoutInflater mInflater;
    Activity mAct;

    public LifeIndexAdapter(Activity mAct, List<LifeIndex> mLifes) {
        this.mAct = mAct;
        this.mLifes = mLifes;
        mInflater = LayoutInflater.from(mAct);
    }

    @Override
    public int getCount() {
        return mLifes == null ? 0 : mLifes.size();
    }

    @Override
    public LifeIndex getItem(int position) {
        return mLifes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler viewHoler = null;
        if (null == convertView) {
            viewHoler = new ViewHoler();
            convertView = mInflater.inflate(R.layout.adapter_lifeindex_item, null);
            x.view().inject(viewHoler, convertView);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) convertView.getTag();
        }
        LifeIndex info = getItem(position);
        viewHoler.mLifeType.setText(info.mType);
        viewHoler.mLifeTips.setText(info.mTip);
        viewHoler.mLifeDesc.setText(info.mDesc);
        return convertView;
    }

    class ViewHoler {
        @ViewInject(R.id.lifeindex_type)
        TextView mLifeType;
        @ViewInject(R.id.lifeindex_tips)
        TextView mLifeTips;
        @ViewInject(R.id.lifeindex_desc)
        TextView mLifeDesc;
    }
}

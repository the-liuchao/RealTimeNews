package com.chao.news.liu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.bean.PostCode;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/1/23.
 */

public class PostCodeAdapter extends BaseAdapter {

    private Activity mActivity;
    private List<PostCode> mDatas;
    private LayoutInflater mInflater;
    private boolean mIsAddress;

    public void refresh(List<PostCode> datas) {
        if (mDatas == null) mDatas = new ArrayList<>();
        else mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void add(List<PostCode> datas) {
        if (mDatas == null) mDatas = new ArrayList<>();
        else mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public PostCodeAdapter(Activity mActivity, boolean isLookAddr) {
        this.mActivity = mActivity;
        this.mIsAddress = isLookAddr;
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public PostCode getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        PostCode code = getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_postcode, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mIsAddress) {
            viewHolder.mAreaLayout.setVisibility(View.VISIBLE);
            viewHolder.mArea.setText(code.mJD);
        }else{
            viewHolder.mAreaLayout.setVisibility(View.GONE);
        }
        viewHolder.mPostCode.setText(code.mPostNumber);
        viewHolder.mAddress.setText(code.mAddress);
        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.area_layout)
        View mAreaLayout;
        @ViewInject(R.id.item_area)
        TextView mArea;
        @ViewInject(R.id.item_postcode)
        TextView mPostCode;
        @ViewInject(R.id.item_address)
        TextView mAddress;

    }
}

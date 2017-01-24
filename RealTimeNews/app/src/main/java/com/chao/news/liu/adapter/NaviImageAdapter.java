package com.chao.news.liu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.base.BaseWebActivity;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/13.
 */

public class NaviImageAdapter extends BaseAdapter {

    private List<HashMap<String, String>> mNewsLists;
    private Activity mActivity;
    private LayoutInflater mInflater;
    private ImageOptions mOptions;

    public NaviImageAdapter(List<HashMap<String, String>> mNewsLists, Activity mActivity) {
        this.mNewsLists = mNewsLists;
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
        mOptions = options();
    }

    public void refresh(List<HashMap<String, String>> mNewsLists) {
        if (this.mNewsLists == null) {
            this.mNewsLists = new ArrayList<>();

        } else {
//            this.mNewsLists.clear();
        }
        this.mNewsLists.addAll(mNewsLists);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mNewsLists == null ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return mNewsLists == null ? null : mNewsLists.get(position % mNewsLists.size());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == mNewsLists || 0 == mNewsLists.size()) return null;
        HashMap<String, String> hashMap = getItem(position % mNewsLists.size());
        if (null == hashMap) return null;
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_newsimg_item, null);
            viewHolder.mNewsIcon = (ImageView) convertView.findViewById(R.id.news_icon);
            viewHolder.mNewsTitle = (TextView) convertView.findViewById(R.id.news_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        x.image().bind(viewHolder.mNewsIcon, hashMap.get("icon"), mOptions);
        viewHolder.mNewsTitle.setText(hashMap.get("title"));
        viewHolder.setmPosition(position);
        convertView.setOnClickListener(viewHolder);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener {
        ImageView mNewsIcon;
        TextView mNewsTitle;
        int mPosition;

        public void setmPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            String url = getItem(mPosition).get("url");
            BaseWebActivity.startActivity(mActivity, url);
        }
    }

    private ImageOptions options() {
        ImageOptions options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCrop(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFadeIn(true)
                .build();
        return options;
    }
}

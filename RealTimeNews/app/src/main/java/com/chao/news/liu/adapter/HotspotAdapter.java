package com.chao.news.liu.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.HotspotNews;
import com.chao.news.liu.views.activity.HotNewsActivity;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/1/14.
 */

public class HotspotAdapter extends BaseAdapter {

    private List<HotspotNews> mHotspotNews;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public HotspotAdapter(List<HotspotNews> hotspotNewses, Activity mActivity) {
        this.mHotspotNews = hotspotNewses;
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
    }

    public void refresh(List<HotspotNews> hotspotNewses) {
        if (mHotspotNews == null) {
            mHotspotNews = new ArrayList<>();
        } else {
            mHotspotNews.clear();
        }
        mHotspotNews.addAll(hotspotNewses);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mHotspotNews == null ? 0 : mHotspotNews.size();
    }

    @Override
    public HotspotNews getItem(int position) {
        return mHotspotNews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        HotspotNews hotspot = mHotspotNews.get(position);
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_hotspot_item, null);
            convertView.setOnClickListener(viewHolder);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mHotspot.setText(getItem(position).getmHotspot());
        if (1 == hotspot.getmIsRead()) {
            viewHolder.mHotspot.setTextColor(Color.parseColor("#FF888888"));
        } else {
            viewHolder.mHotspot.setTextColor(Color.parseColor("#FF414141"));
        }
        viewHolder.setmPosition(position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener {
        @ViewInject(R.id.tv_hotspot)
        TextView mHotspot;
        int mPosition;

        public void setmPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            HotspotNews hotspot = mHotspotNews.get(mPosition);
            mHotspot.setTextColor(Color.parseColor("#FF888888"));
            try {
                hotspot.setmIsRead(1);
                KeyValue keyvalue = new KeyValue("isread",1);
                BaseApplication.getInstance().getDbManager().update(HotspotNews.class, WhereBuilder.b("hotspot", "=", hotspot.getmHotspot()),keyvalue);
                HotNewsActivity.startActivity(mActivity,hotspot.getmHotspot());
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
    }
}

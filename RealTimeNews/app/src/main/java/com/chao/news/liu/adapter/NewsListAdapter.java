package com.chao.news.liu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.listen.BaseAnimationListener;
import com.chao.news.liu.views.activity.BaseNewsActivity;

import java.util.HashMap;
import java.util.List;

/**
 * 新闻分类列表是适配器
 * Created by hp on 2017/1/13.
 */

public class NewsListAdapter extends BaseAdapter {

    private List<HashMap<String, String>> mNewsLists;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public NewsListAdapter(List<HashMap<String, String>> mNewsLists, Activity mActivity) {
        this.mNewsLists = mNewsLists;
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
    }

    @Override
    public int getCount() {
        return mNewsLists == null ? 0 : mNewsLists.size();
    }


    @Override
    public HashMap<String, String> getItem(int position) {
        return mNewsLists == null ? null : mNewsLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HashMap<String, String> hashMap = getItem(position);
        if (null == hashMap) return null;
        convertView = mInflater.inflate(R.layout.adapter_newslist_item, null);
        TextView newsItem = (TextView) convertView.findViewById(R.id.news_item);
        int drawableId = Integer.parseInt(hashMap.get("icon"));
        newsItem.setCompoundDrawablesWithIntrinsicBounds(0, drawableId, 0, 0);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookNewsList(v, hashMap);
            }
        });
        return convertView;
    }

    /**
     * 查看新闻列表
     *
     * @param v
     * @param hashMap
     */
    private void lookNewsList(View v, final HashMap<String, String> hashMap) {
        ScaleAnimation scale = new ScaleAnimation(1f, 0.95f, 1f, 0.95f, v.getMeasuredWidth() / 2, v.getMeasuredHeight() / 2);
        scale.setDuration(200);
        scale.setFillBefore(true);
        scale.setAnimationListener(new BaseAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                int type = Integer.parseInt(hashMap.get("type"));
                BaseNewsActivity.startActivity(mActivity, hashMap.get("type"));
            }
        });
        v.startAnimation(scale);
    }
}

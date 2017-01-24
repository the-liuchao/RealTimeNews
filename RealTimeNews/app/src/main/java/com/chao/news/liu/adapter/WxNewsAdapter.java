package com.chao.news.liu.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.base.BaseWebActivity;
import com.chao.news.liu.bean.WeixinNews;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 时事新闻适配器
 * Created by hp on 2017/1/13.
 */

public class WxNewsAdapter extends BaseAdapter {

    private List<WeixinNews> mWeixinNewses;
    private LayoutInflater mInflater;
    private ImageOptions mOptions;
    private Activity mActivity;

    public WxNewsAdapter(List<WeixinNews> mWeixinNewses, Activity mActivity) {
        this.mWeixinNewses = mWeixinNewses;
        this.mActivity = mActivity;
        this.mInflater = LayoutInflater.from(mActivity);
        this.mOptions = options();
    }

    public void refresh(List<WeixinNews> results) {
        if (mWeixinNewses == null) {
            mWeixinNewses = new ArrayList<>();
        } else {
            mWeixinNewses.clear();
        }
        mWeixinNewses.addAll(results);
        notifyDataSetChanged();
    }

    public void loadMore(List<WeixinNews> results) {
        if (mWeixinNewses == null) {
            mWeixinNewses = new ArrayList<>();
        }
        mWeixinNewses.addAll(results);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mWeixinNewses == null ? 0 : mWeixinNewses.size();
    }

    @Override
    public WeixinNews getItem(int position) {
        return mWeixinNewses == null ? null : mWeixinNewses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WeixinNews baseNews = getItem(position);
        if (null == baseNews) return null;
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_wxnews_item, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (0 == position) viewHolder.mDivi.setVisibility(View.GONE);            //去掉第一条分割线
        loadData(baseNews, viewHolder);
        viewHolder.setmPosition(position);
        convertView.setOnClickListener(viewHolder);
        return convertView;
    }

    private void loadData(WeixinNews wxNews, ViewHolder viewHolder) {
        viewHolder.mTitle.setText(wxNews.getmTitle());
        viewHolder.mCdate.setText(wxNews.getmHotTime());
        if(TextUtils.isEmpty(wxNews.getmHotTime())){
            viewHolder.mCdate.setTextColor(Color.parseColor("#3F51B5"));
            viewHolder.mCdate.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
            viewHolder.mCdate.setText(Html.fromHtml(wxNews.getmDescription()));
            viewHolder.mContent.setText(null);
        }else{
            viewHolder.mCdate.setTextColor(Color.parseColor("#dddddd"));
            viewHolder.mCdate.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
            viewHolder.mCdate.setText(Html.fromHtml(wxNews.getmHotTime()));
            viewHolder.mContent.setText(Html.fromHtml(wxNews.getmDescription()));
        }
        if (TextUtils.isEmpty(wxNews.getmPicUrl())) {
            viewHolder.mImg.setVisibility(View.GONE);
        } else {
            viewHolder.mImg.setVisibility(View.VISIBLE);
            x.image().bind(viewHolder.mImg, wxNews.getmPicUrl(), mOptions);
        }
    }

    private ImageOptions options() {
        ImageOptions options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
                .setCrop(true)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFadeIn(true)
                .build();
        return options;
    }

    class ViewHolder implements View.OnClickListener {
        @ViewInject(R.id.tv_title)
        TextView mTitle;
        @ViewInject(R.id.tv_cdate)
        TextView mCdate;
        @ViewInject(R.id.tv_content)
        TextView mContent;
        @ViewInject(R.id.iv_img)
        ImageView mImg;
        @ViewInject(R.id.divider)
        ImageView mDivi;
        int mPosition;

        public void setmPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            WeixinNews realNews = getItem(mPosition);
            BaseWebActivity.startActivity(mActivity, realNews.getmUrl());

        }
    }
}

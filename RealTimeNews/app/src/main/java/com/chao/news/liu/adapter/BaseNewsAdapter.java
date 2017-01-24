package com.chao.news.liu.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.base.BaseWebActivity;
import com.chao.news.liu.bean.BaseNews;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 时事新闻适配器
 * Created by hp on 2017/1/13.
 */

public class BaseNewsAdapter extends BaseAdapter {

    private List<BaseNews> mBaseNewses;
    private LayoutInflater mInflater;
    private ImageOptions mOptions;
    private Activity mActivity;

    public BaseNewsAdapter(List<BaseNews> mBaseNewses, Activity mActivity) {
        this.mBaseNewses = mBaseNewses;
        this.mActivity = mActivity;
        this.mInflater = LayoutInflater.from(mActivity);
        this.mOptions = options();
    }

    public void refresh(List<BaseNews> results) {
        if (mBaseNewses == null) {
            mBaseNewses = new ArrayList<>();
        } else {
            mBaseNewses.clear();
        }
        mBaseNewses.addAll(results);
        notifyDataSetChanged();
    }

    public void loadMore(List<BaseNews> results) {
        if (mBaseNewses == null) {
            mBaseNewses = new ArrayList<>();
        }
        mBaseNewses.addAll(results);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mBaseNewses == null ? 0 : mBaseNewses.size();
    }

    @Override
    public BaseNews getItem(int position) {
        return mBaseNewses == null ? null : mBaseNewses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseNews baseNews = getItem(position);
        if (null == baseNews) return null;
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_basenews_item, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        loadData(baseNews, viewHolder);
        viewHolder.setmPosition(position);
        convertView.setOnClickListener(viewHolder);
        return convertView;
    }

    private void loadData(BaseNews baseNews, ViewHolder viewHolder) {
        viewHolder.mTitle.setText(baseNews.getmTitle());
        viewHolder.mCdate.setText(baseNews.getmCtime());
        viewHolder.mContent.setText(Html.fromHtml(baseNews.getmDescription()));
        if (TextUtils.isEmpty(baseNews.getmPicUrl())) {
            viewHolder.mImg.setVisibility(View.GONE);
        } else {
            viewHolder.mImg.setVisibility(View.VISIBLE);
            x.image().bind(viewHolder.mImg, baseNews.getmPicUrl(), mOptions);
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
        ImageView mDiv;
        int mPosition;

        public void setmPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            BaseNews realNews = getItem(mPosition);
            BaseWebActivity.startActivity(mActivity, realNews.getmUrl());

        }
    }
}

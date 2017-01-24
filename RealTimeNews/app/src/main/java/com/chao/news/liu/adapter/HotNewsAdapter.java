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
import com.chao.news.liu.bean.RealNews;

import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 热点新闻适配器
 * Created by hp on 2017/1/13.
 */

public class HotNewsAdapter extends BaseAdapter {

    private List<RealNews> mRealNewses;
    private LayoutInflater mInflater;
    private ImageOptions mOptions;
    private Activity mActivity;

    public HotNewsAdapter(List<RealNews> mRealNewses, Activity mActivity) {
        this.mRealNewses = mRealNewses;
        this.mActivity = mActivity;
        this.mInflater = LayoutInflater.from(mActivity);
        this.mOptions = options();
    }

    public void refresh(List<RealNews> results) {
        if (mRealNewses == null) {
            mRealNewses = new ArrayList<>();
        } else {
            mRealNewses.clear();
        }
        mRealNewses.addAll(results);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return mRealNewses == null ? 0 : mRealNewses.size();
    }

    @Override
    public RealNews getItem(int position) {
        return mRealNewses == null ? null : mRealNewses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RealNews realNews = getItem(position);
        if (null == realNews) return null;
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter_realnews_item, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (0 == position) viewHolder.mDiv.setVisibility(View.GONE);            //去掉第一条分割线
        loadData(realNews, viewHolder);
        viewHolder.setmPosition(position);
        convertView.setOnClickListener(viewHolder);
        return convertView;
    }

    private void loadData(RealNews realNews, ViewHolder viewHolder) {
        viewHolder.mTitle.setText(realNews.getmTitle());
        viewHolder.mContent.setText(Html.fromHtml(realNews.getmContent()));
        viewHolder.mSrc.setText(realNews.getmSrc());
        viewHolder.mPdate.setText(realNews.getmPdate());
        if (TextUtils.isEmpty(realNews.getmImg())) {
            viewHolder.mImg.setVisibility(View.GONE);
        } else {
            viewHolder.mImg.setVisibility(View.VISIBLE);
            x.image().bind(viewHolder.mImg, realNews.getmImg(), mOptions);
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
        @ViewInject(R.id.tv_src)
        TextView mSrc;
        @ViewInject(R.id.tv_pdate)
        TextView mPdate;
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
            RealNews realNews = getItem(mPosition);
            BaseWebActivity.startActivity(mActivity,realNews.getmUrl());
        }
    }
}

package com.chao.news.liu.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.bean.healthy.Classify;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 作者：guojuan
 * 创建时间：2017/1/23 16:37
 * 功能描述：
 */


public class ClassifyAdapter extends BaseAdapter {
    private List<Classify> mClassifys;
    private LayoutInflater mInflater;
    private Activity mActivity;

    public ClassifyAdapter(List<Classify> mClassifys, Activity mActivity) {
        this.mClassifys = mClassifys;
        this.mActivity = mActivity;
        this.mInflater = mActivity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return mClassifys == null ? 0 : mClassifys.size();
    }

    @Override
    public Object getItem(int position) {
        return mClassifys == null ? null : mClassifys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Classify classify = (Classify) getItem(position);
        if (null == classify) return null;
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_classify_list, null);
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == mClassifys.size() - 1){
            viewHolder.mLlClassify.setPadding(0,0,0,60);
        }
        viewHolder.mTitle.setText(classify.mTitle);
        viewHolder.mDes.setText("描述:\t"+classify.mDescription);
        viewHolder.setmPosition(position);
        convertView.setOnClickListener(viewHolder);
        return convertView;
    }
    class ViewHolder implements View.OnClickListener {
        @ViewInject(R.id.tv_title)
        TextView mTitle;
        @ViewInject(R.id.tv_des)
        TextView mDes;
        @ViewInject(R.id.ll_calssify)
        LinearLayout mLlClassify;

        int mPosition;

        public void setmPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {


        }
    }
}

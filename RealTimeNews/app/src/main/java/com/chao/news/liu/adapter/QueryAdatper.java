package com.chao.news.liu.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.bean.weat.QueryBean;
import com.chao.news.liu.views.activity.healthy.ClassifyActivity;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by hp on 2017/1/18.
 */

public class QueryAdatper extends BaseAdapter {

    private Activity mActivity;
    private List<QueryBean> mQuerys;
    private LayoutInflater mInflater;
    private Resources mRes;

    public QueryAdatper(List<QueryBean> mQuerys, Activity mActivity) {
        this.mQuerys = mQuerys;
        this.mActivity = mActivity;
        this.mRes = mActivity.getResources();
        this.mInflater = LayoutInflater.from(this.mActivity);
    }

    @Override
    public int getCount() {
        return mQuerys == null ? 0 : mQuerys.size();
    }

    @Override
    public QueryBean getItem(int position) {
        return mQuerys.get(position);
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
            convertView = mInflater.inflate(R.layout.query_item, null);
            x.view().inject(viewHolder, convertView);
            convertView.setOnClickListener(viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        QueryBean query = getItem(position);
        viewHolder.mQueryText.setText(query.mName);
        viewHolder.setPosition(position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener {
        @ViewInject(R.id.query_text)
        TextView mQueryText;
        int mPosition;

        public void setPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            switch (mPosition){
                case 8:
                    startAct(ClassifyActivity.class);
                    break;
            }
        }
        private void startAct(Class activity){
            Intent intent = new Intent(mActivity,activity);
            mActivity.startActivity(intent);
        }
    }
}

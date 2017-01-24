package com.chao.news.liu.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.bean.ToolBean;
import com.chao.news.liu.listen.BaseAnimationListener;
import com.chao.news.liu.views.activity.BJTimeActivity;
import com.chao.news.liu.views.activity.CertificateActivity;
import com.chao.news.liu.views.activity.MD5Activity;
import com.chao.news.liu.views.activity.PostcodeActivity;
import com.chao.news.liu.views.activity.TranslateActivity;
import com.chao.news.liu.views.activity.UnitConverActivity;
import com.liuming.mylibrary.utils.ImageHelper;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by hp on 2017/1/18.
 */

public class ToolAdatper extends BaseAdapter {

    private Activity mActivity;
    private List<ToolBean> mTools;
    private LayoutInflater mInflater;
    private Resources mRes;

    public ToolAdatper(List<ToolBean> mTools, Activity mActivity) {
        this.mTools = mTools;
        this.mActivity = mActivity;
        this.mRes = mActivity.getResources();
        this.mInflater = LayoutInflater.from(this.mActivity);
    }

    @Override
    public int getCount() {
        return mTools == null ? 0 : mTools.size();
    }

    @Override
    public ToolBean getItem(int position) {
        return mTools.get(position);
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
            convertView = mInflater.inflate(R.layout.tools_item, null);
            x.view().inject(viewHolder, convertView);
            convertView.setOnClickListener(viewHolder);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ToolBean tool = getItem(position);
        viewHolder.mToolText.setText(tool.mName);
        viewHolder.mToolIcon.setImageBitmap(ImageHelper.getInstance().readBitmapById(tool.mIon));
        viewHolder.setPosition(position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener {
        @ViewInject(R.id.tool_text)
        TextView mToolText;
        @ViewInject(R.id.tool_icon)
        ImageView mToolIcon;
        int mPosition;
        ScaleAnimation mScaleAnim;

        public void setPosition(int mPosition) {
            this.mPosition = mPosition;
        }

        @Override
        public void onClick(View v) {
            mScaleAnim = new ScaleAnimation(1f, 0.95f, 1f, 0.95f, v.getMeasuredWidth() / 2, v.getMeasuredHeight() / 2);
            mScaleAnim.setDuration(200);
            mScaleAnim.setFillBefore(true);
            mScaleAnim.setAnimationListener(new BaseAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    switch (mPosition) {
                        case 0:
                            BJTimeActivity.startActivity(mActivity);
                            break;
                        case 1:
                            CertificateActivity.startActivity(mActivity);
                            break;
                        case 2:
                            UnitConverActivity.startActivity(mActivity);
                            break;
                        case 3:
                            TranslateActivity.startActivity(mActivity);
                            break;
                        case 4:
                            MD5Activity.startActivity(mActivity);
                            break;
                        case 5:
                            PostcodeActivity.startActivity(mActivity);
                            break;
                    }
                }
            });
            v.startAnimation(mScaleAnim);
        }
    }
}

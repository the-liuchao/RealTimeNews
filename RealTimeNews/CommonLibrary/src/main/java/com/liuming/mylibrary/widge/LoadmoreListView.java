package com.liuming.mylibrary.widge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.liuming.mylibrary.R;

/**
 * Created by hp on 2017/1/23.
 */

public class LoadmoreListView extends ListView implements AbsListView.OnScrollListener {

    private Context mContext;
    private View mFooterView;
    private View mNomoreView;
    private View mLoadingView;
    private int mFirstVisibleItem;
    private OnLoadListener mOnLoadListener;
    private boolean mHasMore = true;

    public interface OnLoadListener {
        void onLoad();
    }

    public void setOnLoadListener(OnLoadListener mOnLoadListener) {
        this.mOnLoadListener = mOnLoadListener;
    }

    public LoadmoreListView(Context context) {
        this(context, null);
    }

    public LoadmoreListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadmoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        // 为ListView设置滑动监听
        setOnScrollListener(this);
        // 去掉底部分割线
        setFooterDividersEnabled(false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //当滑动到底部时
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && mFirstVisibleItem != 0 && mHasMore) {
            mOnLoadListener.onLoad();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.mFirstVisibleItem = firstVisibleItem;

        if (mFooterView != null) {
            //判断可视Item是否能在当前页面完全显示
            if (visibleItemCount == totalItemCount) {
                // removeFooterView(footerView);
                mFooterView.setVisibility(View.GONE);//隐藏底部布局
            } else {
                // addFooterView(footerView);
                mFooterView.setVisibility(View.VISIBLE);//显示底部布局
                if (mHasMore){
                    mNomoreView.setVisibility(GONE);
                    mLoadingView.setVisibility(VISIBLE);
                }
                else {
                    mNomoreView.setVisibility(VISIBLE);
                    mLoadingView.setVisibility(GONE);
                }
            }
        }
    }

    /**
     * 初始化话底部页面
     */
    public void initBottomView() {

        if (mFooterView == null) {
            mFooterView = LayoutInflater.from(this.mContext).inflate(R.layout.listview_footer, null);
            mNomoreView = mFooterView.findViewById(R.id.pull_to_nomore);
            mLoadingView = mFooterView.findViewById(R.id.loading);
        }
        addFooterView(mFooterView);
        mFooterView.setVisibility(View.GONE);
        mNomoreView.setVisibility(GONE);
    }

    /**
     * 显示无更多数据底部页面
     */
    public void showNomoreView() {
        mHasMore = false;
        if (mNomoreView != null) {
            mNomoreView.setVisibility(VISIBLE);
            mLoadingView.setVisibility(GONE);
        }
    }

    /**
     * 显示无更多数据底部页面
     */
    public void hideNomoreView() {
        mHasMore = true;
        if (mNomoreView != null) {
            mNomoreView.setVisibility(GONE);
            mFooterView.setVisibility(View.GONE);
            mLoadingView.setVisibility(VISIBLE);
        }
    }
}

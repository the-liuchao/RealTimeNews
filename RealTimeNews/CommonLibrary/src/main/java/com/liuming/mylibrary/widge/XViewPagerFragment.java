package com.liuming.mylibrary.widge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.liuming.mylibrary.XFragment;

/**
 * Created by hp on 2017/1/17.
 */

public abstract class XViewPagerFragment extends XFragment {
    private boolean mInitView;  //是否创建View完成
    /**
     * 第一次onResume中的调用onUserVisible避免操作与onFirstUserVisible操作重复
     */
    private boolean mIsFirstResume = true;
    private boolean mIsFirstVisiable = true;
    private boolean mIsFirstInVisiable = true;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewComplete();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFirstResume) {
            mIsFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisiable();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInVisiable();
        }
    }

    private synchronized void initViewComplete() {
        if (mInitView) {
            onFirstUserVisiable();   //View创建完成，而且可见
        } else {
            mInitView = true;        //因为第一次初始化View完成在onViewCreated调用的，创建View完成
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (mIsFirstVisiable) {                  //第一次可见，调用初始化View完成方法
                mIsFirstVisiable = false;
                initViewComplete();
            } else {
                onUserVisiable();
            }
        } else {
            if (mIsFirstInVisiable) {
                mIsFirstInVisiable = false;          //第一次可见
                onFirstUserInVisiable();
            } else {
                onUserInVisiable();
            }

        }

    }

    protected void onUserInVisiable() {
    }
    protected void onFirstUserInVisiable() {
    }
    protected void onUserVisiable() {
    }
    protected void onFirstUserVisiable() {
    }

}

package com.chao.news.liu.listen;

import org.xutils.common.Callback;

/**
 * Created by hp on 2017/1/22.
 */

public class CommonCallbackImpl<T> implements Callback.CommonCallback<T> {
    @Override
    public void onSuccess(T result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}

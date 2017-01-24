package com.liuming.mylibrary.inter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.liuming.mylibrary.utils.OkHttpHelper;

import java.lang.ref.WeakReference;

/**
 * Created by hp on 2017/1/5.
 */

public abstract class UIProgressRequestListener implements OkHttpHelper.ProgressListener {
    private static final int REQUEST_UPDATE = 0x01;

    //处理UI层的Handler子类
    private static class UIHandler extends Handler {
        //弱引用
        private final WeakReference<UIProgressRequestListener> mUIProgressRequestListenerWeakReference;

        public UIHandler(Looper looper, UIProgressRequestListener uiProgressRequestListener) {
            super(looper);
            mUIProgressRequestListenerWeakReference = new WeakReference<UIProgressRequestListener>(uiProgressRequestListener);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_UPDATE:
                    UIProgressRequestListener uiProgressRequestListener = mUIProgressRequestListenerWeakReference.get();
                    if (uiProgressRequestListener != null) {
                        //获得进度实体类
                        //回调抽象方法
                        uiProgressRequestListener.onUIRequestProgress(msg.arg1, msg.arg2, (Boolean) msg.obj);
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    //主线程Handler
    private final Handler mHandler = new UIHandler(Looper.getMainLooper(), this);

    @Override
    public void onProgress(long bytesRead, long contentLength, boolean done) {
        //通过Handler发送进度消息
        Message message = Message.obtain();
        message.arg1 = (int) bytesRead;
        message.arg2 = (int) contentLength;
        message.obj = done;
        message.what = REQUEST_UPDATE;
        mHandler.sendMessage(message);
    }

    /**
     * UI层回调抽象方法
     *
     * @param bytesWrite    当前写入的字节长度
     * @param contentLength 总字节长度
     * @param done          是否写入完成
     */
    public abstract void onUIRequestProgress(long bytesWrite, long contentLength, boolean done);
}

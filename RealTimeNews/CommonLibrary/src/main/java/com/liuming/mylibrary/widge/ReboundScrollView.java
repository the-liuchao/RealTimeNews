package com.liuming.mylibrary.widge;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class ReboundScrollView extends ScrollView {

    public OnScrollChangeListener listener;
    Context mContext;
    private View mView;
    private float touchY;
    private float touchX;
    private int scrollY = 0;
    private boolean handleStop = false;
    private int eachStep = 0;
    private boolean topRebound = true;

    private static final int MAX_SCROLL_HEIGHT = 800;// 最大滑动距离
    private static final float SCROLL_RATIO = 0.5f;// 阻尼系数,越小阻力就越大

    public ReboundScrollView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public ReboundScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    //	public void setScrollListener(ReBoundScrollviewListener scrollViewListener){
//		this.listener = scrollViewListener;
//	}
    public void setTopRebound(boolean isTopRebound) {
        this.topRebound = isTopRebound;
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            this.mView = getChildAt(0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (arg0.getAction() == MotionEvent.ACTION_DOWN) {
            touchY = arg0.getY();
            touchX = arg0.getX();
        }
        if (arg0.getAction() == MotionEvent.ACTION_MOVE) {
            int distanceX = (int) Math.abs(arg0.getX() - touchX);
            int distanceY = (int) Math.abs(arg0.getY() - touchY);

            if (distanceX > distanceY) {
                return false;
            }
        }
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mView == null) {
            return super.onTouchEvent(ev);
        } else {
            return commonOnTouchEvent(ev);
        }
    }

    private boolean commonOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (mView.getScrollY() != 0) {
                    handleStop = true;
                    animation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float nowY = ev.getY();
                int deltaY = (int) (touchY - nowY);
                touchY = nowY;
                if (isNeedMove()) {
                    int offset = mView.getScrollY();
                    if (offset < MAX_SCROLL_HEIGHT && offset > -MAX_SCROLL_HEIGHT) {
                        mView.scrollBy(0, (int) (deltaY * SCROLL_RATIO));
                        handleStop = false;
                    }
                }
                if (listener != null) {
                    listener.OnScrollChange(0, (int) (deltaY * SCROLL_RATIO), 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private boolean isNeedMove() {
        int viewHight = mView.getMeasuredHeight();
        int srollHight = getHeight();
        int offset = viewHight - srollHight;
        int scrollY = getScrollY();
        if (!topRebound) {
            return scrollY == offset;
        }
        return scrollY == 0 || scrollY == offset;
    }

    private void animation() {
        scrollY = mView.getScrollY();
        eachStep = scrollY / 10;
        resetPositionHandler.sendEmptyMessage(0);
    }

    Handler resetPositionHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (scrollY != 0 && handleStop) {
                scrollY -= eachStep;
                if ((eachStep < 0 && scrollY > 0)
                        || (eachStep > 0 && scrollY < 0)) {
                    scrollY = 0;
                }
                mView.scrollTo(0, scrollY);
                this.sendEmptyMessageDelayed(0, 15);
            }
        }
    };

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener != null) {
            listener.OnScrollChange(l, t, oldl, oldt);
        }
    }

    public interface OnScrollChangeListener {

        void OnScrollChange(int l, int t, int oldl, int oldt);
    }

    public void setOnCheckedChanged(OnScrollChangeListener listener) {

        this.listener = listener;
    }
}

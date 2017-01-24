package com.liuming.mylibrary.widge;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by hp on 2017/1/13.
 */

public class XLinearlayout extends LinearLayout {
    public XLinearlayout(Context context) {
        super(context);
    }

    public XLinearlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XLinearlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}

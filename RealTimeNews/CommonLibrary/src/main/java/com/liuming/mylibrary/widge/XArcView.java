package com.liuming.mylibrary.widge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by hp on 2017/1/19.
 */

public class XArcView extends LinearLayout {
    private Paint mPaint;


    public XArcView(Context context) {
        super(context);
    }

    public XArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XArcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mPaint = new Paint();
        Rect outRect = new Rect();
        getWindowVisibleDisplayFrame(outRect);
        mPaint.setAntiAlias(true);                         //设置画笔为无锯齿
        mPaint.setColor(Color.RED);                      //设置画笔颜色
        canvas.drawColor(Color.WHITE);                       //白色背景
        mPaint.setStrokeWidth((float) 3.0);                //线宽
        mPaint.setStyle(Paint.Style.FILL);
        RectF oval = new RectF();                          //RectF对象
        oval.left = -100;                                     //左边
        oval.top = -200;                                   //上边
        oval.right = 100 + outRect.right;                        //右边
        oval.bottom = 200;                                 //下边
        canvas.drawArc(oval, 0, 180, true, mPaint);      //绘制圆弧
    }
}

package com.liuming.mylibrary.widge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.liuming.mylibrary.R;

import java.util.Calendar;

/**
 * Created by hp on 2017/1/20.
 */

public class XClockView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private final static int DEFAULT_RADIUS = 400;
    private Canvas mCanvas;                                              //画布
    private Thread mThread;                                              //异步画
    private boolean mFlag;                                               //线程开关标记
    private int mCanvasWidth, mCanvasHeight;
    private Paint mPaint;                                                //绘制刻度画笔
    private Paint mPointerPaint;                                         //绘制指针画笔
    private SurfaceHolder mHolder;
    private int mHour, mMinute, mSecond;
    private int mRadius = DEFAULT_RADIUS;
    private int mHourDegreeLen, mSecondDegreeLen;                        //时刻度，秒刻度长度
    private int mHourPointerLen, mMinutePointerLen, mSecondPointerLen;   //时，分，秒指针长度

    private OnTimeChangeListener mOnTimeChangeListener;

    public void setmOnTimeChangeListener(OnTimeChangeListener mOnTimeChangeListener) {
        this.mOnTimeChangeListener = mOnTimeChangeListener;
    }

    public XClockView(Context context) {
        this(context, null);
    }

    public XClockView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public XClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化当前时间
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mSecond = Calendar.getInstance().get(Calendar.SECOND);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new Thread(this);

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mPointerPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointerPaint.setTextSize(22);
        mPointerPaint.setTextAlign(Paint.Align.CENTER);

        setFocusable(true);
        setFocusableInTouchMode(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int realWidth, realHeight;

        //计算真实宽度是多少
        if (widthMode == MeasureSpec.EXACTLY) {
            realWidth = widthSize;
        } else {
            realWidth = mRadius * 2 + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                realWidth = Math.min(widthSize, realWidth);
            }
        }

        //计算真实高度是多少
        if (heightMode == MeasureSpec.EXACTLY) {
            realHeight = heightSize;
        } else {
            realHeight = mRadius * 2 + getPaddingBottom() + getPaddingTop();
            if (heightMode == MeasureSpec.AT_MOST) {
                realHeight = Math.min(heightSize, realWidth);
            }
        }

        // +4是为了设置默认的2px的内边距，因为绘制时钟的圆的画笔设置的宽度是2px
        setMeasuredDimension(mCanvasWidth = realWidth + 4, mCanvasHeight = realHeight + 4);
        mRadius = (int) (Math.min(mCanvasWidth - getPaddingLeft() - getPaddingRight(), mCanvasHeight - getPaddingTop() - getPaddingBottom()) * 1.0f / 2);


        //计算时针和刻度的长度
        mHourDegreeLen = (int) (mRadius * 1.0f / 7f);
        mSecondDegreeLen = (int) (mHourDegreeLen * (1.0f / 2));      //秒刻度是时刻度的1/2

        mHourPointerLen = (int) (mRadius * (1.0f / 2));
        mMinutePointerLen = (int) (mHourPointerLen * 1.25f);
        mSecondPointerLen = (int) (mHourPointerLen * 1.5f);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mFlag = true;
        if (mThread == null)
            mThread = new Thread(this);
        mThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mFlag = false;
        mThread = null;
    }

    //回调出去到主线程，当前时间是多少
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (null != mOnTimeChangeListener) {
                    mOnTimeChangeListener.onTimeChange(XClockView.this, mHour, mMinute, mSecond);
                }
            }
        }
    };

    @Override
    public void run() {
        long start, end;
        while (mFlag) {
            start = System.currentTimeMillis();
            mHandler.sendEmptyMessage(0);
            draw();          //画
            logic();         //下一秒，重新赋值时间
            end = System.currentTimeMillis();
            if (end - start < 1000) {
                try {
                    Thread.sleep(1000 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 时间增加一秒的逻辑
     */
    private void logic() {
        mSecond++;
        if (mSecond == 60) {
            mSecond = 0;
            mMinute++;
            if (mMinute == 60) {
                mMinute = 0;
                mHour++;
                if (mHour == 24) {
                    mHour = 0;
                }
            }
        }
    }

    /**
     * 画时间逻辑
     */
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (null != mCanvas) {
                //刷屏
                mCanvas.drawColor(Color.WHITE);
                drawView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (null != mCanvas) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 画出所有视图
     */
    private void drawView() {
        //现在开始具体的绘制内容（画什么由画布决定，怎么画由画笔决定，这也就是我们上面给画笔设置一系列属性的原因）：
        mPointerPaint.setColor(Color.BLACK);
        //1.将画笔移到画布中心位置
        mCanvas.translate(mCanvasWidth * 1.0f / 2 + getPaddingLeft() - getPaddingRight(), mCanvasHeight * 1.0f / 2 + getPaddingTop() - getPaddingBottom());

        // 2.绘制圆盘
        mPaint.setStrokeWidth(2);
        mCanvas.drawCircle(0, 0, mRadius + 2, mPaint);

        Paint paint = new Paint();
        Shader shader = new RadialGradient(0, 0, mRadius, Color.parseColor("#FFFFFF"), Color.parseColor("#3F51B5"), Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        mCanvas.drawCircle(0, 0, mRadius, paint);
        mCanvas.drawCircle(0, 0, 8, mPointerPaint);
        // 3.绘制时刻度
        mPaint.setColor(Color.WHITE);
        for (int i = 0; i < 12; i++) {
            mCanvas.drawLine(mRadius, 0, mRadius - mHourDegreeLen, 0, mPaint);
            mCanvas.rotate(30);
        }

        // 4.绘制秒刻度
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1.5f);
        for (int i = 0; i < 60; i++) {
            //时刻度绘制过的位置不再绘制
            if (i % 5 != 0) {
                mCanvas.drawLine(mRadius, 0, mRadius - mSecondDegreeLen, 0, mPaint);
            }
            mCanvas.rotate(6);
        }

        //5.绘制数字
        mPointerPaint.setColor(Color.WHITE);
        mPointerPaint.setTextSize(28);
        for (int i = 0; i < 12; i++) {
            int number = 3 + i < 12 ? 3 + i : 3 + i > 12 ? Math.abs(i - 9) : 12;
            mCanvas.save();
            mCanvas.translate(mRadius * 5.5f / 7, 0);
            mCanvas.rotate(-30 * i);
            mCanvas.drawText(String.valueOf(number), 0, 0, mPointerPaint);
            mCanvas.restore();
            mCanvas.rotate(30);
        }

        // 6.绘制上下午
        mCanvas.drawText(mHour < 12 ? "AM" : "PM", 0, mRadius * 1.5f / 4, mPointerPaint);
        //7.绘制时针
        mPointerPaint.setColor(Color.parseColor("#66000000"));
        Path path = new Path();
        path.moveTo(0, 0);
        int[] hourPointerCoordinates = getHoursPointerCoordinates(mHourPointerLen);
        path.lineTo(hourPointerCoordinates[0], hourPointerCoordinates[1]);
        path.lineTo(hourPointerCoordinates[2], hourPointerCoordinates[3]);
        path.lineTo(hourPointerCoordinates[4], hourPointerCoordinates[5]);
        path.close();
        mCanvas.save();
        mCanvas.rotate((mHour - 3) * 30 + mMinute * 0.5f);
        mCanvas.drawPath(path, mPointerPaint);
        mCanvas.restore();
        //8.绘制分钟
        float strokeWidth = mPointerPaint.getStrokeWidth();
        mPointerPaint.setColor(getResources().getColor(R.color.colorPrimary));
        mPointerPaint.setStrokeWidth(4);
        path.reset();
        path.moveTo(mSecondPointerLen * 1.0f / 2 - mSecondDegreeLen * 1.0f / 2, 0);
        path.lineTo(mSecondPointerLen * 1.0f / 2, mSecondDegreeLen * 1.0f / 4);
        path.lineTo(mMinutePointerLen, 0);
        path.lineTo(mSecondPointerLen * 1.0f / 2, -mSecondDegreeLen * 1.0f / 4);
        path.close();
        mCanvas.save();
        mCanvas.rotate(((mMinute - 15) * 6 + mSecond * 0.1f));
        mCanvas.drawLine(-mSecondDegreeLen, 0, mSecondPointerLen * 1.0f / 2 - mSecondDegreeLen * 1.0f / 2, 0, mPointerPaint);
        mCanvas.drawPath(path, mPointerPaint);
//        mCanvas.drawLine(mSecondPointerLen * 1.0f / 2 + mSecondDegreeLen * 1.0f / 2, 0, mMinutePointerLen, 0, mPointerPaint);
        mCanvas.restore();
        //9. 绘制秒钟
        mPointerPaint.setColor(Color.RED);
        mPointerPaint.setStyle(Paint.Style.STROKE);
        mPointerPaint.setStrokeWidth(2);
        mCanvas.save();
        mCanvas.rotate((mSecond - 15) * 6);
        mCanvas.drawLine(-mSecondDegreeLen, 0, mSecondPointerLen * 4.0f / 5 - mSecondDegreeLen * 1.0f / 4, 0, mPointerPaint);
        mCanvas.drawCircle(mSecondPointerLen * 4.0f / 5, 0, mSecondDegreeLen * 1.0f / 4, mPointerPaint);
        mCanvas.drawLine(mSecondPointerLen * 4.0f / 5 + mSecondDegreeLen * 1.0f / 4, 0, mSecondPointerLen, 0, mPointerPaint);
        mCanvas.restore();
        //10.还原某些画笔设置
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointerPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(Color.BLACK);
    }

    /**
     * 获取指针坐标
     *
     * @param pointerLength 指针长度
     * @return int[]{x1,y1,x2,y2,x3,y3}
     */
    private int[] getHoursPointerCoordinates(int pointerLength) {
        int y = (int) (mHourDegreeLen * 1.0f / 4);
        int x = (int) (pointerLength * 3.0f / 4);
        return new int[]{x, -y, pointerLength, 0, x, y};
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = Math.abs(hour) % 24;
        if (mOnTimeChangeListener != null) {
            mOnTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = Math.abs(minute) % 60;
        if (mOnTimeChangeListener != null) {
            mOnTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public int getSecond() {
        return mSecond;
    }

    public void setSecond(int second) {
        mSecond = Math.abs(second) % 60;
        if (mOnTimeChangeListener != null) {
            mOnTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public void setTime(Integer... time) {
        if (time.length > 3) {
            throw new IllegalArgumentException("the length of argument should bo less than 3");
        }
        if (time.length > 2)
            setSecond(time[2]);
        if (time.length > 1)
            setMinute(time[1]);
        if (time.length > 0)
            setHour(time[0]);
    }

    /**
     * 当时间改变的时候提供回调的接口
     */
    public interface OnTimeChangeListener {
        /**
         * 时间发生改变时调用
         *
         * @param view   时间正在改变的view
         * @param hour   改变后的小时时刻
         * @param minute 改变后的分钟时刻
         * @param second 改变后的秒时刻
         */
        void onTimeChange(View view, int hour, int minute, int second);
    }
}

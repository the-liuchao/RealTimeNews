package com.chao.news.liu.views.widge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Calendar;

/**
 * Created by hp on 2017/1/20.
 */
public class ClockView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    /**
     * 使用SurfaceView的简单介绍surface这个单词是“表面、表层”的意思。。它的特性是：可以在主线程之外的线程中向屏幕绘图上。
     * 这样可以避免画图任务繁重的时候造成主线程阻塞，从而提高了程序的反应速度。在游戏开发中多用到SurfaceView，游戏中的背景
     * 、人物、动画等等尽量在画布canvas中画出。下面来介绍一下它的简单的使用吧
     * 1，写一个类继承SurfaceView
     * 2，实现SurfaceHolder.Callback的接口，需要重写的方法一共有三个
     * surfaceCreated-->表示SurfaceView的创建，一般在这个方法调用画图的子线程
     * surfaceChanged-->表示SurfaceView发生改变，
     * surfaceDestroyed-->表示SurfaceView的销毁，一般在这里释放线程
     */

    private static final int DEFAULT_RADIUS = 200;

    private SurfaceHolder mHolder;
    private Thread mThread;
    private boolean flag; //用于标识surface销毁，停止绘制操作

    //添加挥之所需要的画笔、时间等
    private Canvas mCanvas; //画布
    private Paint mPaint; //绘制圆和刻度的画笔
    private Paint mPointerPaint; //绘制指针的画笔
    private int mCanvasWidth, mCanvasHeight; //画布的宽高
    private int mRadius = DEFAULT_RADIUS;//时钟的半径
    // 秒针长度
    private int mSecondPointerLength;
    // 分针长度
    private int mMinutePointerLength;
    // 时针长度
    private int mHourPointerLength;
    // 时刻度长度
    private int mHourDegreeLength;
    // 秒刻度
    private int mSecondDegreeLength;
    // 时钟显示的时、分、秒
    private int mHour, mMinute, mSecond;

    private OnTimeChangeListener onTimeChangeListener;

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener;
    }

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化当前显示的时间
        mHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mMinute = Calendar.getInstance().get(Calendar.MINUTE);
        mSecond = Calendar.getInstance().get(Calendar.SECOND);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mThread = new Thread(this);

        mPaint = new Paint();
        mPointerPaint = new Paint();

        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mPointerPaint.setColor(Color.BLACK);
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointerPaint.setTextSize(22);
        mPointerPaint.setTextAlign(Paint.Align.CENTER); //属性待研究

        //下面这两句没懂
        setFocusable(true);
        setFocusableInTouchMode(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int desiredWidth, desiredHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            desiredWidth = widthSize;
        } else {
            desiredWidth = mRadius * 2 + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                desiredWidth = Math.min(widthSize, desiredWidth);
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            desiredHeight = heightSize;
        } else {
            desiredHeight = mRadius * 2 + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                desiredHeight = Math.min(heightSize, desiredHeight);
            }
        }

        // +4是为了设置默认的2px的内边距，因为绘制时钟的圆的画笔设置的宽度是2px
        setMeasuredDimension(mCanvasWidth = desiredWidth + 4, mCanvasHeight = desiredHeight + 4);
        mRadius = (int) (Math.min(desiredWidth - getPaddingLeft() - getPaddingRight(),
                desiredHeight - getPaddingTop() - getPaddingBottom()) * 1.0f / 2);

        calculateLengths();
    }

    /**
     * 计算时针和刻度的长度
     */
    private void calculateLengths() {
        //设置时针长度为半径的1/7
        mHourDegreeLength = (int) (mRadius * 1.0f / 7);
        // 秒分刻度长度为时刻度长度的一半
        mSecondDegreeLength = (int) (mHourDegreeLength * 1.0f / 2);

        //设置指针的长度
        mHourPointerLength = (int) (mRadius * 1.0 / 2);
        mMinutePointerLength = (int) (mHourPointerLength * 1.25f);
        mSecondPointerLength = (int) (mHourPointerLength * 1.5f);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启绘制的子线程
        flag = true;
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void run() {
        //放置无时无刻的绘制，这里我们做的是秒钟的行走，则需要限制一下，让其每隔1秒才绘制一次
        long start, end;
        while (flag) {
            start = System.currentTimeMillis();
            handler.sendEmptyMessage(0);
            draw();
            logic();
            end = System.currentTimeMillis();

            try {
                if (end - start < 1000) {
                    Thread.sleep(1000 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //操作逻辑
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

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (onTimeChangeListener != null) {
                onTimeChangeListener.onTimeChange(ClockView.this, mHour, mMinute, mSecond);
            }
            return false;
        }
    });


    //绘制操作
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas(); // 得到画布
            if (mCanvas != null) {
                // 在这里绘制内容
                //刷屏
                mCanvas.drawColor(Color.WHITE);
                drawSomthing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private void drawSomthing() {
        //        现在开始具体的绘制内容（画什么由画布决定，怎么画由画笔决定，这也就是我们上面给画笔设置一系列属性的原因）：
        mPointerPaint.setColor(Color.BLACK);
        // 1.将坐标系原点移至去除内边距后的画布中心
        // 默认在画布左上角，这样做是为了更方便的绘制
        mCanvas.translate(mCanvasWidth * 1.0f / 2 + getPaddingLeft() - getPaddingRight(), mCanvasHeight * 1.0f / 2 + getPaddingTop() - getPaddingBottom());
        // 2.绘制圆盘
        mPaint.setStrokeWidth(2f); // 画笔设置2个像素的宽度
        mCanvas.drawCircle(0, 0, mRadius, mPaint); // 到这一步就能知道第一步的好处了，否则害的去计算园的中心点坐标
        // 3.绘制时刻度
        for (int i = 0; i < 12; i++) {
            mCanvas.drawLine(0, mRadius, 0, mRadius - mHourDegreeLength, mPaint);
            mCanvas.rotate(30); // 360°平均分成12份，每份30°
        }
        // 4.绘制秒刻度
        mPaint.setStrokeWidth(1.5f);
        for (int i = 0; i < 60; i++) {
            //时刻度绘制过的区域不在绘制
            if (i % 5 != 0) {
                mCanvas.drawLine(0, mRadius, 0, mRadius - mSecondDegreeLength, mPaint);
            }
            mCanvas.rotate(6); // 360°平均分成60份，每份6°
        }
        // 5.绘制数字
//        mPointerPaint.setColor(Color.BLACK);
//        for (int i = 0; i < 12; i++) {
//            String number = 6 + i < 12 ? String.valueOf(6 + i) : (6 + i) > 12
//                    ? String.valueOf(i - 6) : "12";
//            mCanvas.drawText(number, 0, mRadius * 5.5f / 7, mPointerPaint);
//            mCanvas.rotate(30);
//        }
        for (int i = 0; i < 12; i++) {
//            String number = 6 + i < 12 ? String.valueOf(6 + i) : (6 + i) > 12
//                    ? String.valueOf(i - 6) : "12";
//            mCanvas.save();
//            mCanvas.translate(0, mRadius * 5.5f / 7);
//            mCanvas.rotate(-i * 30);
//            mCanvas.drawText(number, 0, 0, mPointerPaint);
//            mCanvas.restore();
//            mCanvas.rotate(30);

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
        // 7.绘制时针
        Path path = new Path();
        path.moveTo(0, 0);
        int[] hourPointerCoordinates = getPointerCoordinates(mHourPointerLength);
        path.lineTo(hourPointerCoordinates[0], hourPointerCoordinates[1]);
        path.lineTo(hourPointerCoordinates[2], hourPointerCoordinates[3]);
        path.lineTo(hourPointerCoordinates[4], hourPointerCoordinates[5]);
        path.close();
        mCanvas.save();
        mCanvas.rotate(180 + mHour % 12 * 30 + mMinute * 1.0f / 60 * 30);
        mCanvas.drawPath(path, mPointerPaint);
        mCanvas.restore();
        // 8.绘制分针
        path.reset();
        path.moveTo(0, 0);
        int[] minutePointerCoordinates = getPointerCoordinates(mMinutePointerLength);
        path.lineTo(minutePointerCoordinates[0], minutePointerCoordinates[1]);
        path.lineTo(minutePointerCoordinates[2], minutePointerCoordinates[3]);
        path.lineTo(minutePointerCoordinates[4], minutePointerCoordinates[5]);
        path.close();
        mCanvas.save();
        mCanvas.rotate(180 + mMinute * 6);
        mCanvas.drawPath(path, mPointerPaint);
        mCanvas.restore();
        // 9.绘制秒针
        mPointerPaint.setColor(Color.RED);
        path.reset();
        path.moveTo(0, 0);
        int[] secondPointerCoordinates = getPointerCoordinates(mSecondPointerLength);
        path.lineTo(secondPointerCoordinates[0], secondPointerCoordinates[1]);
        path.lineTo(secondPointerCoordinates[2], secondPointerCoordinates[3]);
        path.lineTo(secondPointerCoordinates[4], secondPointerCoordinates[5]);
        path.close();
        mCanvas.save();
        mCanvas.rotate(180 + mSecond * 6);
        mCanvas.drawPath(path, mPointerPaint);
        mCanvas.restore();


    }

    //        这里比较难的可能就是指针的绘制，因为我们的指针是个规则形状，其中getPointerCoordinates便是得到这个不规则形状的3个定点坐标，
    // 有兴趣的同学可以去研究一下我的逻辑，也可以定义你自己的逻辑。我的逻辑如下（三角函数学的号的同学应该一眼就能看懂）：

    /**
     * 获取指针坐标
     *
     * @param pointerLength 指针长度
     * @return int[]{x1,y1,x2,y2,x3,y3}
     */
    private int[] getPointerCoordinates(int pointerLength) {
        int y = (int) (pointerLength * 3.0f / 4);
        int x = (int) (y * Math.tan(Math.PI / 180 * 5));
        return new int[]{-x, y, 0, pointerLength, x, y};
    }

    //-----------------Setter and Getter start-----------------//
    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = Math.abs(hour) % 24;
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = Math.abs(minute) % 60;
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
        }
    }

    public int getSecond() {
        return mSecond;
    }

    public void setSecond(int second) {
        mSecond = Math.abs(second) % 60;
        if (onTimeChangeListener != null) {
            onTimeChangeListener.onTimeChange(this, mHour, mMinute, mSecond);
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
    //-----------------Setter and Getter end-------------------//

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

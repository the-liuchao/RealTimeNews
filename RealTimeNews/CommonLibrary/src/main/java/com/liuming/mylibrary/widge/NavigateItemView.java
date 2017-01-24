package com.liuming.mylibrary.widge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.liuming.mylibrary.R;

import java.io.InputStream;


/**
 * Created by 刘超 on 2016/2/20.
 */
public class NavigateItemView extends View {

    private final static int DEFAULT_COLOR = 0xFF1A85FF;
    private int mColor = DEFAULT_COLOR;//选中颜色
    private Bitmap mIconNormal, mIconSelected;//图标
    private String mLabel;//名称
    private float mTextSize;//文本大小
    private Paint mTextPaint;//画笔
    private Rect textBounds, mIconRect, mTextRect;
    private float mAlpha;//背景透明度
    private Bitmap bgBitmap;//缓存bitmap
    private Canvas bgCanvas;//缓存画布
    private Paint bgPaint;//生成缓存bitmap的画笔
    private int mNormalColor = 0xFF555555;//默认字体颜色

    public NavigateItemView(Context context) {
        this(context, null);
    }

    public NavigateItemView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NavigateItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NavigateItemView);
        this.mColor = a.getColor(R.styleable.NavigateItemView_navigation_item_color, DEFAULT_COLOR);
        BitmapDrawable drawableNormal = (BitmapDrawable) a.getDrawable(R.styleable.NavigateItemView_navigation_item_icon_normal);
        if (null != drawableNormal) {
            this.mIconNormal = drawableNormal.getBitmap();
        }
        BitmapDrawable drawableSelected = (BitmapDrawable) a.getDrawable(R.styleable.NavigateItemView_navigation_item_icon_selected);
        if (null != drawableNormal) {
            this.mIconSelected = drawableSelected.getBitmap();
        }
        this.mLabel = a.getString(R.styleable.NavigateItemView_navigation_item_name);
        this.mTextSize = a.getDimension(R.styleable.NavigateItemView_navigation_item_textsize, 14);
        a.recycle();
        initPaint();
        initTextBounds();
    }

    /**
     * 设置背景图和文本的透明度 以便显示前景颜色
     *
     * @param mAlpha
     */
    public void setmAlpha(float mAlpha) {
        this.mAlpha = mAlpha;
        this.invalidate();
    }

    /**
     * 构建Item
     */
    public static class Builder {
        private NavigateItemView itemView;

        public Builder(Context context) {
            itemView = new NavigateItemView(context);
        }

        public Builder setmIconNormal(int drawableId) {
            itemView.mIconNormal = itemView.readBitMap(drawableId);
            return this;
        }

        public Builder setmIconSelected(int drawableId) {
            itemView.mIconSelected = itemView.readBitMap(drawableId);
            return this;
        }

        public Builder setmLabel(String tab) {
            itemView.mLabel = tab;
            return this;
        }

        public Builder setmTextSize(float textSize) {
            itemView.mTextSize = textSize;
            return this;
        }

        public Builder setmTextColor(int normalColor, int checkedColor) {
            itemView.mColor = checkedColor;
            itemView.mNormalColor = normalColor;
            return this;
        }


        public NavigateItemView build() {
            itemView.initPaint();
            itemView.initTextBounds();
            itemView.invalidate();
            return itemView;
        }
    }


    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param resId
     * @return
     */
    private Bitmap readBitMap(int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(0xFF555555);//默认颜色
        mTextPaint.setTextSize(mTextSize);
    }

    //计算图片和文字所在屏幕的区域

    /**
     * 计算文本区域
     */
    private void initTextBounds() {
        textBounds = new Rect();
        if (mLabel != null) {
            mTextPaint.getTextBounds(mLabel, 0, mLabel.length(), textBounds);
        }
    }

    /**
     * 在视图大小改变回调方法里面 ，指定图标 和 文本的绘制区域
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //自定义图片高度
        int bitmapWidth, bitmapHeight;
        bitmapWidth = bitmapHeight = (int) (h * 0.6F);
        setIconRect(w, h, bitmapWidth, bitmapHeight);//设置图标的区域
        setTextRect(w, h, bitmapWidth, bitmapHeight);//设置文本区域
    }

    /**
     * 设置画图标的区域
     *
     * @param w
     * @param h
     * @param bitmapWidth
     * @param bitmapHeight
     */
    private void setIconRect(int w, int h, int bitmapWidth, int bitmapHeight) {
        int left = (w - bitmapWidth) / 2;
        int top = (h - bitmapHeight - textBounds.height()) / 2;
        int right = left + bitmapWidth;
        int bottom = top + bitmapHeight;
        mIconRect = new Rect(left, top, right, bottom);

    }

    /**
     * 设置画文本的区域
     *
     * @param w
     * @param h
     * @param bitmapWidth
     * @param bitmapHeight
     */
    private void setTextRect(int w, int h, int bitmapWidth, int bitmapHeight) {
        int left = (w - textBounds.width()) / 2;
        int top = mIconRect.bottom + 30;
        int right = left + textBounds.width();
        int bottom = top + textBounds.height();
        mTextRect = new Rect(left, top, right, bottom);
    }

    // 1 清空画板
    // 2 绘制图片
    // 3 绘制图片背景图
    // 4 绘制前景图
    // 5 绘制背景文字
    // 6 绘制前景文字
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvasClear(canvas);
        canvasBitmap(canvas);// 绘制渐变图片
        canvasText(canvas);// 绘制渐变文本
    }

    /**
     * 清空画板
     *
     * @param canvas
     */
    private void canvasClear(Canvas canvas) {
        canvas.drawBitmap(mIconNormal, null, mIconRect, null);
    }

    /**
     * 绘制渐变图片
     *
     * @param canvas
     */
    private void canvasBitmap(Canvas canvas) {
        int alpha = (int) Math.ceil(255 * mAlpha);//图标渐变设置透明度变化
        //采用二级缓存bitmap
        bgBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //创建缓存画布
        bgCanvas = new Canvas(bgBitmap);

        //绘制背景图
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        bgPaint.setColor(mColor);
        bgPaint.setAlpha(alpha);
        bgPaint.setDither(true);
        bgCanvas.drawBitmap(mIconSelected, null, mIconRect, bgPaint);
        // 在图标区域范围 绘制背景色图
//        bgCanvas.drawRect(mIconRect, bgPaint);

        //绘制前景图
        bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));//设置绘画模式
        //恢复透明度
        bgPaint.setAlpha(255);
        bgCanvas.drawBitmap(mIconNormal, null, mIconRect, bgPaint);//绘制前景Icon的图片

        //把缓存bitmap画到canvas上去
        canvas.drawBitmap(bgBitmap, 0, 0, null);
    }

    /**
     * 绘制渐变文字
     *
     * @param canvas
     */
    private void canvasText(Canvas canvas) {
        int alpha = (int) Math.ceil(255 * mAlpha);//图标渐变设置透明度变化
        mTextPaint.setColor(mNormalColor);
        mTextPaint.setAlpha(255);
        canvas.drawText(mLabel, mTextRect.left, mTextRect.top, mTextPaint);
        //文本前景图
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mLabel, mTextRect.left, mTextRect.top, mTextPaint);
    }
}

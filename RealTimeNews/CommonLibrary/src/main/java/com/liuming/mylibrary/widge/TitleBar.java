package com.liuming.mylibrary.widge;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liuming.mylibrary.R;

/**
 * 标题BAR
 * Created by hp on 2017/1/6.
 */

public class TitleBar extends RelativeLayout implements View.OnClickListener {
    private Context ctx;
    private int mScreenWidth;
    private String mLeftText;
    private String mRightText;
    private int mLeftVisiable;
    private int mTitleVisiable;
    private int mRightVisiable;
    private TextView mLeftView;
    private TextView mTitleView;
    private TextView mRightView;
    /**
     * 标题
     */
    private String mTitleText;
    /**
     * 标题字体大小
     */
    private float mTitleSize;
    /**
     * 标题字体颜色
     */
    private int mTitleColor;
    /**
     * 左边图标
     */
    private int mLeftIcon;
    /**
     * 右边图标
     */
    private int mRightIcon;
    /**
     * 是否有返回按钮
     */
    private boolean mHasBack;
    /**
     * 标题栏上按钮点击监听
     */
    private BarClickListener mBarClickListener;

    public interface BarClickListener {
        /**
         * 标题栏点击事件回调
         *
         * @param position 1右边按钮，2，标题 3，右边按钮
         * @param v
         */
        void onClick(int position, View v);
    }

    public TitleBar(Context context) {
        this(context, null);
    }


    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar, defStyleAttr, defStyleRes);
        mTitleSize = a.getDimension(R.styleable.TitleBar_title_size, 30);
        mTitleText = a.getString(R.styleable.TitleBar_title_text);
        mLeftText = a.getString(R.styleable.TitleBar_left_text);
        mRightText = a.getString(R.styleable.TitleBar_right_text);
        mTitleColor = a.getColor(R.styleable.TitleBar_title_color, Color.WHITE);
        mLeftIcon = a.getResourceId(R.styleable.TitleBar_left_icon, 0);
        mRightIcon = a.getResourceId(R.styleable.TitleBar_right_icon, 0);
        mHasBack = a.getBoolean(R.styleable.TitleBar_has_back, true);
        a.recycle();
    }

    private void init(Context context) {
        ctx = context;
        Rect rect = new Rect();
        getWindowVisibleDisplayFrame(rect);
        mScreenWidth = rect.width();
        mLeftVisiable = mHasBack ? GONE : VISIBLE;
        mRightVisiable = VISIBLE;
        mTitleVisiable = VISIBLE;
        setPadding(15, 20, 15, 20);
        post(new Runnable() {
            @Override
            public void run() {
                setTitle(mTitleText, mTitleVisiable);
                setLeftIcon(mLeftIcon, mLeftVisiable, mLeftText);
                setRightIcon(mRightIcon, mRightVisiable, mRightText);
            }
        });
    }

    /**
     * 设置 标题栏点击监听
     *
     * @param barClickListener
     * @return
     */
    public TitleBar setBarClickListener(BarClickListener barClickListener) {
        this.mBarClickListener = barClickListener;
        return this;
    }

    /**
     * 设置右边ICON
     *
     * @param drawableResId
     * @return
     */
    public TitleBar setRightIcon(int drawableResId) {
        setRightIcon(drawableResId, null);
        return this;
    }

    public TitleBar setRightIcon(int drawableResId, String text) {
        setRightIcon(drawableResId, VISIBLE, text);
        return this;
    }

    public TitleBar setRightIcon(int drawableResId, int visiable, String text) {
        drawableResId = drawableResId == -1 ? 0 : drawableResId;
        this.mRightIcon = drawableResId;
        this.mRightText = text;
        this.mRightVisiable = visiable;
        if (null == mRightView) {
            mRightView = new TextView(ctx);
            mRightView.setSingleLine();
            mRightView.setId(mRightView.hashCode());
            mRightView.setEllipsize(TextUtils.TruncateAt.END);
            mRightView.setTextColor(mTitleColor);
            mRightView.setVisibility(visiable);
            mRightView.setGravity(Gravity.CENTER);
            mRightView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize - 12);
            mRightView.setMaxWidth(mScreenWidth / 5);
            mRightView.setCompoundDrawablePadding(10);
            mRightView.setPadding(10, 10, 10, 10);
            mRightView.setOnClickListener(this);
            if (!isChildOrHidden(mRightView)) {
                addRightView(visiable);
            }
        }
        if (null != mRightView) {
            mRightView.setCompoundDrawablesWithIntrinsicBounds(0, 0, mRightIcon, 0);
            mRightView.setText(this.mRightText);
        }
        return this;
    }

    private void addRightView(int visiable) {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        layoutParams.addRule(CENTER_VERTICAL);
        mRightView.setVisibility(visiable);
        addView(mRightView, layoutParams);
    }

    /**
     * 设置左边ICON
     *
     * @param drawableResId
     * @return
     */
    public TitleBar setLeftIcon(int drawableResId) {
        setLeftIcon(drawableResId, null);
        return this;
    }

    public TitleBar setLeftIcon(int drawableResId, String text) {
        setLeftIcon(drawableResId, VISIBLE, text);
        return this;
    }

    public TitleBar setLeftIcon(int drawableId, int visiable, String text) {
        drawableId = drawableId == -1 ? 0 : drawableId;
        this.mLeftIcon = drawableId;
        this.mLeftText = text;
        this.mLeftVisiable = visiable;
        if (null == mLeftView) {
            mLeftView = new TextView(ctx);
            mLeftView.setSingleLine();
            mLeftView.setId(mLeftView.hashCode());
            mLeftView.setEllipsize(TextUtils.TruncateAt.END);
            mLeftView.setTextColor(mTitleColor);
            mLeftView.setVisibility(visiable);
            mLeftView.setGravity(Gravity.CENTER);
            mLeftView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize - 12);
            mLeftView.setMaxWidth(mScreenWidth / 5);
            mLeftView.setCompoundDrawablePadding(10);
            mLeftView.setPadding(10, 10, 10, 10);
            mLeftView.setOnClickListener(this);
            if (!isChildOrHidden(mLeftView)) {
                addLeftView(visiable);
            }
        }
        if (null != mLeftView) {
            mLeftView.setCompoundDrawablesWithIntrinsicBounds(mLeftIcon, 0, 0, 0);
            mLeftView.setText(this.mLeftText);
        }
        return this;
    }

    private void addLeftView(int visiable) {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(ALIGN_PARENT_LEFT | CENTER_VERTICAL);
        mLeftView.setVisibility(visiable);
        addView(mLeftView, layoutParams);
    }

    /**
     * 设置标题
     *
     * @param title
     * @param visiable
     * @return
     */
    public TitleBar setTitle(String title, int visiable) {
        setTitle(title, visiable, 0);
        return this;
    }

    /**
     * 设置标题
     *
     * @param title
     * @param visiable
     * @return
     */
    public TitleBar setTitle(String title, int visiable, int drawableId) {
        this.mTitleVisiable = visiable;
        this.mTitleText = title;
        if (null == mTitleView) {
            mTitleView = new TextView(ctx);
            mTitleView.setSingleLine();
            mTitleView.setId(mTitleView.hashCode());
            mTitleView.setEllipsize(TextUtils.TruncateAt.END);
            mTitleView.setTextColor(mTitleColor);
            mTitleView.setCompoundDrawablePadding(30);
            mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
            mTitleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0);
            mTitleView.setOnClickListener(this);
            mTitleView.setMaxWidth(mScreenWidth * 3 / 5);
            if (!isChildOrHidden(mTitleView)) {
                addTitleView(visiable);
            }
        }
        if (null != mTitleView) {
            mTitleView.setText(title);
        }
        return this;
    }

    public TitleBar setTitle(String title) {
        setTitle(title, VISIBLE);
        return this;
    }

    private void addTitleView(int visiable) {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(CENTER_IN_PARENT);
        mTitleView.setVisibility(visiable);
        addView(mTitleView, layoutParams);
    }

    private boolean isChildOrHidden(View child) {
        return child.getParent() == this;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == mLeftView.hashCode()) {
            if (this.mBarClickListener != null) {
                mBarClickListener.onClick(1, v);
            }
        } else if (v.getId() == mRightView.hashCode()) {
            if (this.mBarClickListener != null) {
                mBarClickListener.onClick(3, v);
            }
        }else if(v.getId()==mTitleView.hashCode()){
            if (this.mBarClickListener != null) {
                mBarClickListener.onClick(2, v);
            }
        }
    }
}

package com.liuming.mylibrary.widge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.liuming.mylibrary.R;

/**
 * 输入三位加逗号
 * Created by hp on 2017/1/22.
 */

public class CommaView extends EditText implements TextWatcher {
    private final static int COMMA_NUMBER = 4;
    private final static String CHAR = ",";
    private int mCommaNum = COMMA_NUMBER;
    private String mChar = CHAR;
    private Context context;
    @SuppressWarnings("unused")
    private Drawable imgInable;
    private Drawable imgAble;


    public CommaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public CommaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CommaView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        imgInable = context.getResources().getDrawable(R.mipmap.et_delete_gray);
        imgAble = context.getResources().getDrawable(R.mipmap.delete);
        addTextChangedListener(this);
        setEllipsize(TextUtils.TruncateAt.END);//
        setIncludeFontPadding(false);
        setMinHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        setShadowLayer(0.3f, 0, 0, Color.GRAY);// 设置文本阴影半径，x方向开始位置，y方向开始位置，阴影颜色
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (deletable)
            setDrawable();
        if (count != before) {
            String sss = "";
            String string = s.toString().replace(mChar, "");
            int b = string.length() / mCommaNum;
            if (string.length() >= mCommaNum) {
                int yushu = string.length() % mCommaNum;
                if (yushu == 0) {
                    b = string.length() / mCommaNum - 1;
                    yushu = mCommaNum;
                }
                for (int i = 0; i < b; i++) {
                    sss = sss + string.substring(0, yushu) + mChar + string.substring(yushu, mCommaNum);
                    string = string.substring(mCommaNum, string.length());
                }
                sss = sss + string;
                setText(sss);
            }
        }
        setSelection(getText().length());
    }

    /**
     * 设置编辑框的删除图片
     */
    private void setDrawable() {
        if (length() < 1)
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        else
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgAble, null);

    }

    /**
     * 是否使删除按钮生效
     **/
    private boolean deletable = true;

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
        if (!deletable) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    /**
     * 处理删除事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (deletable && imgAble != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 50;
            if (rect.contains(eventX, eventY))
                setText("");
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setCommaNum(int mCommaNum) {
        this.mCommaNum = mCommaNum;
    }

    public void setChar(String mChar) {
        this.mChar = mChar;
    }

    public String getChar() {
        return mChar;
    }

}

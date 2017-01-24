package com.liuming.mylibrary.widge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.AutoCompleteTextView;

import com.liuming.mylibrary.R;


/**
 * ProjectName:aneapp
 *
 * @author liuchao
 * @自定义EditText
 * @ClassName:AneEditText 2014-12-17
 */
public class XEditText extends AutoCompleteTextView implements TextWatcher {

    private Context context;
    @SuppressWarnings("unused")
    private Drawable imgInable;
    private Drawable imgAble;

    private OnClickSearchListener mOnClickSearchListener;

    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public XEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public void setmOnClickSearchListener(OnClickSearchListener mOnClickSearchListener) {
        this.mOnClickSearchListener = mOnClickSearchListener;
    }

    public interface OnClickSearchListener {
        void onClick(XEditText view);
    }

    /**
     * 初始化控件
     */
    private void init() {
        imgInable = context.getResources().getDrawable(R.drawable.search_bg);
        imgAble = context.getResources().getDrawable(R.drawable.search_bg);
        addTextChangedListener(this);
        setEllipsize(TruncateAt.END);//
        setIncludeFontPadding(false);
        setMinHeight(LayoutParams.MATCH_PARENT);
        setShadowLayer(0.3f, 0, 0, Color.GRAY);// 设置文本阴影半径，x方向开始位置，y方向开始位置，阴影颜色
        setDrawable();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
    }

    /**
     * 设置编辑框的删除图片
     */
    private void setDrawable() {
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
            if (rect.contains(eventX, eventY) && null != this.mOnClickSearchListener) {
                this.mOnClickSearchListener.onClick(this);
            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}

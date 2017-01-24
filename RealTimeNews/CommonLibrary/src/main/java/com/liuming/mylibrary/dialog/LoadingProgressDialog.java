package com.liuming.mylibrary.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liuming.mylibrary.R;

/**
 * Created by hp on 2017/1/5.
 */
public class LoadingProgressDialog extends DialogFragment {
    public static final String DIALOG_STYLE_KEY = "dialog_style";
    private static LoadingProgressDialog mDialog;
    private int mTheme = R.style.Progress_Dialog;
    private int mStyle = 1;
    private String mTitle = "提示";
    private String mMessage = "确认退出？";
    private TextView mTitleView;
    private TextView mMessageView;
    private TextView mCancelView;
    private TextView mConfirmView;
    private CompleteListener mCompleteListener;

    public interface CompleteListener {
        void complete(int type, DialogFragment dialog);
    }

    /**
     * 获取dialogfragment实例
     *
     * @param style 样式 0 默认样式 1无标题样式 2无边框样式 3 不可输入，不可获取焦点
     * @return
     */
    public static LoadingProgressDialog newInstance(int style) {
        mDialog = new LoadingProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_STYLE_KEY, style);
        mDialog.setArguments(bundle);
        return mDialog;
    }

    public static LoadingProgressDialog getInstance() {
        if (mDialog == null) {
            mDialog = newInstance(-1);
        }
        if (mDialog.isVisible()) mDialog.dismiss();
        return mDialog;
    }

    @Override
    public void dismiss() {
        if (getFragmentManager() != null) {
            super.dismiss();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getArguments().getInt(DIALOG_STYLE_KEY, 1)) {
            case 0:
                mStyle = DialogFragment.STYLE_NORMAL;  // 默认样式
                break;
            case 1:
                mStyle = DialogFragment.STYLE_NO_TITLE;// 无标题样式
                break;
            case 2:
                mStyle = DialogFragment.STYLE_NO_FRAME;// 无边框样式
                break;
            case 3:
                mStyle = DialogFragment.STYLE_NO_INPUT;// 不可输入，不可获取焦点
                break;
        }
        setStyle(mStyle, mTheme);
    }

    public LoadingProgressDialog setTitle(String title) {
        this.mTitle = title;
        if (null != this.mTitleView)
            this.mTitleView.setText(this.mTitle);
        return this;
    }

    public LoadingProgressDialog setMessage(String message) {
        this.mMessage = message;
        if (null != this.mMessageView)
            this.mMessageView.setText(this.mMessage);
        return this;
    }

    public LoadingProgressDialog setCompleteListener(CompleteListener completeListener) {
        this.mCompleteListener = completeListener;
        return this;
    }

    public void showDialog(FragmentManager fm, String tag) {
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment instanceof LoadingProgressDialog) {
            mDialog = (LoadingProgressDialog) fragment;
        }
        FragmentTransaction ft = fm.beginTransaction();
        // 在显示对话框前移除正在显示的dialog
        if (mDialog != null)
            ft.remove(mDialog);
        show(fm, tag);
    }

    /**
     * 设置点击外部是否dismiss对话框
     *
     * @param isCancel
     */
    public LoadingProgressDialog setOutClickCancel(boolean isCancel) {
        getDialog().setCanceledOnTouchOutside(isCancel);
        return this;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        getDialog().setCanceledOnTouchOutside(true);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        //动态修改布局大小
        getDialog().getWindow().setLayout(outMetrics.widthPixels - 160, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_layout, container, false);
        mTitleView = (TextView) view.findViewById(R.id.dialog_title);
        mMessageView = (TextView) view.findViewById(R.id.dialog_message);
        mCancelView = (TextView) view.findViewById(R.id.dialog_NO);
        mConfirmView = (TextView) view.findViewById(R.id.dialog_YES);
        mTitleView.setText(mTitle);
        mMessageView.setText(mMessage);
        mCancelView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
                if (null != mCompleteListener) {
                    mCompleteListener.complete(0, mDialog);
                }
            }
        });
        mConfirmView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (null != mCompleteListener) {
                    mCompleteListener.complete(1, mDialog);
                }
            }
        });
        return view;
    }


//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		AlertDialog.Builder builder = new Builder(getActivity());
//		builder.setPositiveButton("Sure", new OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		builder.setMessage("提示");
//		return builder.create();
//	}
}

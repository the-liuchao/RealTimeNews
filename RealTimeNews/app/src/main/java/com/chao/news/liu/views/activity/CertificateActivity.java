package com.chao.news.liu.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chao.news.liu.R;
import com.chao.news.liu.api.Constants;
import com.chao.news.liu.api.HttpAPI;
import com.chao.news.liu.base.BaseActivity;
import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.BankCard;
import com.chao.news.liu.bean.Identification;
import com.liuming.mylibrary.widge.CommaView;
import com.liuming.mylibrary.widge.TitleBar;
import com.liuming.mylibrary.widge.loopview.LoopView;
import com.liuming.mylibrary.widge.loopview.OnItemSelectedListener;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 身份证，银行卡信认证
 * Created by hp on 2017/1/20.
 */
@ContentView(R.layout.activity_certificate)
public class CertificateActivity extends BaseActivity implements TitleBar.BarClickListener {
    @ViewInject(R.id.title_bar)
    TitleBar mTitleBar;
    @ViewInject(R.id.certi_type)
    TextView mCertiType;
    @ViewInject(R.id.certi_number)
    CommaView mCertiNumb;
    @ViewInject(R.id.certi_username)
    EditText mUsername;
    @ViewInject(R.id.show_info)
    TextView mShowInfo;
    private int mCurrType;
    private String[] mTypes = {"身份证", "银行卡"};


    public static void startActivity(Activity act) {
        Intent intent = new Intent(act, CertificateActivity.class);
        act.startActivity(intent);

    }

    @Override
    protected void initView() {
        mTitleBar.setBarClickListener(this);
        mUsername.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {

    }

    @Event(value = {R.id.certi_type, R.id.aquire_info})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.certi_type:
                showPopup(mTypes);
                break;
            case R.id.aquire_info:
                aquire();
                break;
        }
    }

    private void aquire() {
        if (TextUtils.isEmpty(mCertiType.getText())) {
            return;
        }
        if (TextUtils.isEmpty(mCertiNumb.getText())) {
            mCertiNumb.setError("请输入正确的" + (mCurrType == 0 ? "身份证号码" : "银行卡号"));
            return;
        }
        if (mCurrType == 1 && TextUtils.isEmpty(mUsername.getText())) {
            mUsername.setError("请输入正确的银行卡用户真实姓名");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("key", mCurrType == 0 ? Constants.IDAUTHEKEY : Constants.BANKCARDAUTHKEY);
        if (mCurrType == 0) {
            params.put("id", mCertiNumb.getText().toString().replace(mCertiNumb.getChar(), ""));
            HttpAPI.getInstance().requestIdAuthent(params,
                    new HttpAPI.IdentificationListener() {
                        @Override
                        public void onSuccess(Identification id) {
                            appendId(id);
                        }

                        @Override
                        public void onFail(String errMsg, int errCode) {
                            mShowInfo.setTextColor(Color.RED);
                            mShowInfo.setText(errMsg);
                        }
                    });
        } else if (mCurrType == 1) {
            params.put("cardnum", mCertiNumb.getText().toString().replace(mCertiNumb.getChar(), ""));
            params.put("realname", mUsername.getText().toString());
            HttpAPI.getInstance().requestBankCard(params,
                    new HttpAPI.BankCardListener() {
                        @Override
                        public void onSuccess(BankCard card) {
                            appendBankcard(card);
                        }

                        @Override
                        public void onFail(String errMsg, int errCode) {
                            mShowInfo.setTextColor(Color.RED);
                            mShowInfo.setText(errMsg);
                        }
                    });
        }
    }

    private void appendBankcard(BankCard card) {
        if ("1002" .equals(card.mCode)) {
            mShowInfo.setTextColor(Color.RED);
            mShowInfo.setText("无法认证，不支持的卡号");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<p><big><font color='#000000'>").append("认证结果: </font></big><font color='#3F51B5'>").append(card.mMessage).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("卡类别: </font></big><font color='#3F51B5'>").append(card.mCardtype).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("卡号长度: </font></big><font color='#3F51B5'>").append(card.mCardlength).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("卡号前缀: </font></big><font color='#3F51B5'>").append(card.mCardprefixnum).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("银行内部类型: </font></big><font color='#3F51B5'>").append(card.mCardname).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("所属银行: </font></big><font color='#3F51B5'>").append(card.mBankname).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("所属银行编号: </font></big><font color='#3F51B5'>").append(card.mBanknum).append("</font></p>");
        mShowInfo.setText(Html.fromHtml(sb.toString()));
    }

    private void appendId(Identification id) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p><big><font color='#000000'>").append("性别: </font></big><font color='#3F51B5'>").append(id.mSex).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("生日: </font></big><font color='#3F51B5'>").append(id.mBirthday).append("</font></p>")
                .append("<p><big><font color='#000000'>").append("地址: </font></big><font color='#3F51B5'>").append(id.mAddress).append("</font></p>");
        mShowInfo.setText(Html.fromHtml(sb.toString()));
    }

    public void showPopup(String[] datas) {
        int mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        final PopupWindow popup = new PopupWindow();
        popup.setAnimationStyle(R.style.PopupwindowAnimtion);
        popup.setWidth(mScreenWidth);
        popup.setHeight(-2);
        popup.setFocusable(true);
        popup.setOutsideTouchable(true);
        popup.setBackgroundDrawable(new ColorDrawable());
        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(R.layout.unit_wheel_layout, null);
        final LoopView loopView = (LoopView) contentView.findViewById(R.id.wheel_view);
        contentView.findViewById(R.id.wheel_confirm).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.dismiss();
                        mUsername.setVisibility(mCurrType == 0 ? View.GONE : View.VISIBLE);
                        mCertiType.setText(mTypes[mCurrType]);
                    }
                }
        );
        //设置是否循环播放
        loopView.setNotLoop();
        //滚动监听
        loopView.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (index != mCurrType) {
                    mCurrType = index;
                }
            }
        });
        //设置原始数据
        List<String> list = Arrays.asList(datas);
        loopView.setItems(list, mCurrType);
        popup.setContentView(contentView);
        popup.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void onClick(int position, View v) {
        BaseApplication.getInstance().finish(this);
    }
}

package com.chao.news.liu.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.liuming.mylibrary.XWebActivity;
import com.liuming.mylibrary.widge.TitleBar;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by hp on 2017/1/15.
 */

public class BaseWebActivity extends XWebActivity {

    public static void startActivity(Activity act, String url) {
        mURL = url;
        Intent intent = new Intent(act, BaseWebActivity.class);
        intent.putExtra("webUrl", url);
        act.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitleBar.setRightIcon(R.mipmap.icon_share, View.VISIBLE, null)
                .setBarClickListener(new TitleBar.BarClickListener() {
                    @Override
                    public void onClick(int position, View v) {
                        if (3 == position) {
                            share();
                        } else if (1 == position) {
                            BaseApplication.getInstance().finish(BaseWebActivity.this);
                        }
                    }
                });
    }

    private void share() {
        new ShareAction(this)
                .withMedia(new UMImage(BaseWebActivity.this, R.mipmap.ic_launcher))
                .withTitle("新闻速递")
                .withText(mWebView.getTitle())
                .withTargetUrl(mURL)
                .setDisplayList(SHARE_MEDIA.SINA
                        , SHARE_MEDIA.QQ
                        , SHARE_MEDIA.QZONE
                        , SHARE_MEDIA.WEIXIN
                        , SHARE_MEDIA.RENREN
                        , SHARE_MEDIA.SMS
                        , SHARE_MEDIA.EMAIL)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        Toast.makeText(BaseWebActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                    }
                }).open();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}

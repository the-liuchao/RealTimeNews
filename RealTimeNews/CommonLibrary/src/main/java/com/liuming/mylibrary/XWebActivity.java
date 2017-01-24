package com.liuming.mylibrary;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.liuming.mylibrary.dialog.LoadingProgressDialog;
import com.liuming.mylibrary.widge.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * WebView基类
 */
public class XWebActivity extends AppCompatActivity {

    protected static String mURL;
    protected WebView mWebView;
    protected TitleBar mTitleBar;
    protected List<String> mTitles;// 页面跳转标题集合

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_layout);
        XApplication.getInstance().addAct(this);
        mTitles = new ArrayList<>();
        mWebView = (WebView) findViewById(R.id.webView);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setTitle("", View.VISIBLE)
                .setLeftIcon(R.mipmap.btn_back)
                .setBarClickListener(new TitleBar.BarClickListener() {
                    @Override
                    public void onClick(int position, View v) {
                        back();
                    }
                });
        setWebView();
        mURL = getIntent().getStringExtra("webUrl");
        mWebView.loadUrl(mURL);
        LoadingProgressDialog.newInstance(-1)
                .setMessage("加载中...")
                .showDialog(getSupportFragmentManager(), "webview");
    }

    /**
     * WebView设置
     */
    private void setWebView() {
        /** 设置WebView属性,能够执行JavaScript脚本 */
        mWebView.getSettings().setJavaScriptEnabled(true);
        /** 设置WebView可以加载更多格式页面 */
        mWebView.getSettings().setLoadWithOverviewMode(true);
        /** 设置WebView使用广泛的视窗 */
        mWebView.getSettings().setUseWideViewPort(true);
        /** 设置WebView的用户代理字符串。如果字符串"ua"是null，它将使用系统默认的用户代理字符串 */
        mWebView.getSettings().setUserAgentString("ua");
        /** 支持手势缩放 */
        mWebView.getSettings().setBuiltInZoomControls(true);
        /** 告诉webView启动应用程序缓存api */
        mWebView.getSettings().setAppCacheEnabled(true);
        /** 设置是否启用DOM storage API */
        mWebView.getSettings().setDomStorageEnabled(true);
        /** 自动打开窗口 */
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        /** 没有的话会黑屏支持插件 */
        // mWebView.getSettings().setPluginState(PluginState.OFF);
        /** 设置加载缓存方式（不缓存） */
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        /** 设置是否支持变焦 */
        mWebView.getSettings().setSupportZoom(true);
        /** 设置客户端浏览 */
        mWebView.setWebChromeClient(new DefaultWebChromeClient());
        /** 设置WebView客户端视图监听 */
        mWebView.setWebViewClient(new DefaultWebViewClient());
    }

    /**
     * WebView客户端浏览器监听
     */
    private class DefaultWebChromeClient extends WebChromeClient {
        /**
         * 一个回调接口使用的主机应用程序通知当前页面的自定义视图已被撤职
         */
        CustomViewCallback customCallback;

        /**
         * 加载完标题回调
         */
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mTitles.add(title);// 保存title
            mTitleBar.setTitle(title, View.VISIBLE);
            LoadingProgressDialog.getInstance().dismiss();
        }

        /**
         * 进入全屏的时候
         */
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            /** 赋值给callback */
            this.customCallback = callback;
            /** 设置WebView隐藏 */
            mWebView.setVisibility(View.GONE);
            /** 声明video，把之后的视频放到这里面去 */
//			FrameLayout video = (FrameLayout) findViewById(R.id.video);
            /** 将video放到当前视图中 */
//			video.addView(view);
            /** 设置横屏 */
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            /** 设置全屏 */
            setFullScreen();
        }

        /**
         * 退出全屏的时候
         */
        @Override
        public void onHideCustomView() {
            if (customCallback != null) {
                /**隐藏掉*/
                customCallback.onCustomViewHidden();
            }
            /**用户当前的首先方向*/
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            /** 退出全屏 */
            quitFullScreen();
            /** 设置WebView可见 */
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 进去全屏设置
     */
    private void setFullScreen() {
        /** 设置全屏的相关属性，获取当前屏幕状态，然后设置全屏 */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 全屏下的状态码：1098974464
        //
    }

    /**
     * 退出全屏设置
     */
    private void quitFullScreen() {
        /** 声明当前屏幕状态的参数并获取 */
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    /**
     * 设置客户端视图监听
     */
    private class DefaultWebViewClient extends WebViewClient {
        /**
         * 如果要下载页面中的游戏或者继续点击网页中的链接进入下一个网页的话，重写此方法下，不然就会跳到手机自带的浏览器了，而不继续在你这个webview里面展现了
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
        }

        /**
         * 想在收到错误信息的时候，执行一些操作，走此方法
         */
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        /**
         * 想在页面开始加载的时候，执行一些操作，走此方法
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /**
         * 想在页面加载结束的时候，执行一些操作，走此方法
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    /**
     * 监听返回键返回上一个页面否则退出WebView
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回
     */
    private void back() {
        if (mWebView.canGoBack()) {
            // goBack()表示返回WebView的上一页面
            mWebView.goBack();
            if (mTitles.size() > 1)
                mTitles.remove(mTitles.size() - 1);
            int size = mTitles.size() - 1;
            if (size >= 0)
                mTitleBar.setTitle(mTitles.get(size), View.VISIBLE);
            else
                mTitleBar.setTitle(mTitles.get(0), View.VISIBLE);
            // 退出全屏
            quitFullScreen();
        } else {
            XApplication.getInstance().finish(XWebActivity.this);
        }
    }
}

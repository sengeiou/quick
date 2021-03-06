package top.jplayer.baseprolibrary.ui.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import top.jplayer.baseprolibrary.R;
import top.jplayer.baseprolibrary.listener.observer.CustomObservable;
import top.jplayer.baseprolibrary.utils.LogUtil;
import top.jplayer.baseprolibrary.utils.StringUtils;
import top.jplayer.baseprolibrary.utils.ToastUtils;

public class WebViewActivity extends SuperBaseActivity {

    ProgressBar pbWebBase;
    WebView webBase;
    private String webPath = "";
    private ViewGroup mRootView;
    private CustomObservable mObservable;

    @Override
    protected int initRootLayout() {
        return R.layout.activity_web;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        mImmersionBar.keyboardEnable(true).init();
    }

    @Override
    public void initRootData(View rootView) {
        super.initRootData(rootView);
        mRootView = (ViewGroup) rootView;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        pbWebBase = rootView.findViewById(R.id.pb_web_base);
        webBase = rootView.findViewById(R.id.web_base);
        rootView.findViewById(R.id.tvBack).setOnClickListener(v -> finish());
        webPath = mBundle.getString("url");
        initData();// 初始化控件的数据及监听事件
        mObservable = new CustomObservable();
        mObservable.addObserver("func");
    }

    private void initData() {
        pbWebBase.setMax(100);//设置加载进度最大值

        if (webPath.equals("")) {
            webPath = "";
            ToastUtils.init().showInfoToast(mActivity, "外部链接错误");
            finish();
        }
        WebSettings webSettings = webBase.getSettings();
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//加载缓存否则网络
        }

        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);//图片自动缩放 打开
        } else {
            webSettings.setLoadsImagesAutomatically(false);//图片自动缩放 关闭
        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            webBase.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//软件解码
//        }
//        webBase.setLayerType(View.LAYER_TYPE_HARDWARE, null);//硬件解码

//        webSettings.setAllowContentAccess(true);
//        webSettings.setAllowFileAccessFromFileURLs(true);
//        webSettings.setAppCacheEnabled(true);
   /*     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/

        // setMediaPlaybackRequiresUserGesture(boolean require) //是否需要用户手势来播放Media，默认true

        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
//        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setSupportZoom(true);// 设置可以支持缩放
        webSettings.setBuiltInZoomControls(true);// 设置出现缩放工具 是否使用WebView内置的缩放组件，由浮动在窗口上的缩放控制和手势缩放控制组成，默认false

        webSettings.setDisplayZoomControls(false);//隐藏缩放工具
        webSettings.setUseWideViewPort(true);// 扩大比例的缩放

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//自适应屏幕
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setDatabaseEnabled(true);//
        webSettings.setSavePassword(true);//保存密码
        webSettings.setDomStorageEnabled(true);//是否开启本地DOM存储  鉴于它的安全特性（任何人都能读取到它，尽管有相应的限制，将敏感数据存储在这里依然不是明智之举），Android 默认是关闭该功能的。
        webBase.setSaveEnabled(true);
        webBase.setKeepScreenOn(true);


        // 设置setWebChromeClient对象
        webBase.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                mTvToolTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                pbWebBase.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        //设置此方法可在WebView中打开链接，反之用浏览器打开
        webBase.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!webBase.getSettings().getLoadsImagesAutomatically()) {
                    webBase.getSettings().setLoadsImagesAutomatically(true);
                }
                pbWebBase.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                pbWebBase.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return false;
                }


                // Otherwise allow the OS to handle things like tel, mailto, etc.
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        webBase.setDownloadListener((paramAnonymousString1, paramAnonymousString2, paramAnonymousString3, paramAnonymousString4, paramAnonymousLong) -> {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(Uri.parse(paramAnonymousString1));
            startActivity(intent);
        });
        syncCookie();
        webBase.loadUrl(webPath);
        LogUtil.e("帮助类完整连接" + webPath);
//        webBase.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,webBase.getHeight()));
    }

    public void syncCookie() {
        if (mBundle != null) {
            String cookie = mBundle.getString("cookie");
            if (StringUtils.isNotBlank(cookie)) {
                CookieManager cookieManager = CookieManager.getInstance();
                String url = "http://wx.weimeitao.net";
                cookieManager.setCookie(url, cookie);
                String newCookie = cookieManager.getCookie(url);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(mActivity);
                    cookieSyncManager.sync();
                }
                LogUtil.e(newCookie);
            }
        }
    }

    protected void onSaveInstanceState(Bundle paramBundle) {
        super.onSaveInstanceState(paramBundle);
        paramBundle.putString("url", webBase.getUrl());
    }


    @Override
    public void onBackPressed() {
        if (webBase.canGoBack()) {
            webBase.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webBase != null) {
            webBase.onResume();
            webBase.resumeTimers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webBase != null) {
            webBase.onPause();
            webBase.pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRootView.removeAllViews();
        if (webBase != null) {
            webBase.stopLoading();
            webBase.clearHistory();
            webBase.clearCache(true);
            webBase.loadUrl("about:blank");
            webBase.setVisibility(View.GONE);
            webBase.destroy();
            webBase = null;
        }
        if (mObservable != null) {
            mObservable.change("destory");
            mObservable.deleteObservers();
        }
    }
}


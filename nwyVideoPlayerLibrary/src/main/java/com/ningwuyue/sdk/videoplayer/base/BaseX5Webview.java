package com.ningwuyue.sdk.videoplayer.base;

/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ningwuyue.sdk.videoplayer.util.LogUtils;
import com.ningwuyue.sdk.videoplayer.util.NetworkUtils;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebStorage;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.util.Map;

import static com.ningwuyue.sdk.videoplayer.data.constant.Constant.PATH_CACHE_WEBVIEW;


/**
 * Created by ${武跃} on 2017/6/30.
 * <p>
 * {一句话描述功能:----腾讯X5 Webview    }
 */
public class BaseX5Webview extends WebView implements DownloadListener {
    public WebSettings mWebSetting;
    public String mTitle;
    public String mCurrUrl;
    public BaseWebViewClient mBaseWebViewClient;
    public BaseWebChromeClient mBaseWebChromeClient;
    public OnScrollChangeListener mOnScrollChangeListener;
    //public BaseDownloadListener mBaseDownloadListener;
    private Context mContext;

    public BaseX5Webview(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public BaseX5Webview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    public BaseX5Webview(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        mContext = context;
        init();
    }

    public BaseX5Webview(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
        mContext = context;
        init();
    }

    public BaseX5Webview(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean b) {
        super(context, attributeSet, i, map, b);
        mContext = context;
        init();
    }

    /**
     * 检测url中是否包含数组中的关键字
     *
     * @param keywordArray
     * @param url
     * @return
     */
    public static boolean isHasKeyword(String[] keywordArray, String url) {
        if (keywordArray != null) {
            for (String keywork : keywordArray) {
                if (url.contains(keywork)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float webcontent = getContentHeight() * getScale();// webview的高度
        float webnow = getHeight() + getWebScrollY();// 当前webview的高度
        //Log.e("kuxunyou", "webview.getWebScrollY()====>>" + getWebScrollY() + "--" + t + "--" + oldt);
        if (Math.abs(webcontent - webnow) < 1) {
            //Log.e("kuxunyou", "已经处于底端");
            if (mOnScrollChangeListener != null) {
                mOnScrollChangeListener.onPageEnd(l, t, oldl, oldt);
            }
        } else if (getWebScrollY() == 0) {
            //Log.e("kuxunyou", "已经处于顶端");
            if (mOnScrollChangeListener != null) {
                mOnScrollChangeListener.onPageTop(l, t, oldl, oldt);
            }
        } else {
            if (mOnScrollChangeListener != null) {
                mOnScrollChangeListener.onScrollChanged(l, t, oldl, oldt);
            }
        }
    }

    @SuppressLint("NewApi")
    private void init() {
        mBaseWebViewClient = new BaseWebViewClient();
        mBaseWebChromeClient = new BaseWebChromeClient();
        // mBaseDownloadListener = new BaseDownloadListener();
        setWebViewClient(mBaseWebViewClient);
        setWebChromeClient(mBaseWebChromeClient);
        //setDownloadListener(mBaseDownloadListener);
        setDownloadListener(this);

        mWebSetting = this.getSettings();
        //1.屏幕--窗口
        mWebSetting.setSupportZoom(false);//是否支持使用屏幕控件或手势进行缩放,默认是true，支持缩放。
        mWebSetting.setMediaPlaybackRequiresUserGesture(true);//是否通过手势触发播放媒体，默认是true，需要手势触发
        mWebSetting.setBuiltInZoomControls(false);//是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
        mWebSetting.setDisplayZoomControls(false);//使用内置缩放机制时，是否展现在屏幕缩放控件上，默认true，展现在控件上。

        mWebSetting.setAllowContentAccess(true);//是否使用其内置的变焦机制，该机制结合屏幕缩放控件使用，默认是false，不使用内置变焦机制。

        /*
        设置WebView是否使用viewport，当该属性被设置为false时，加载页面的宽度总是适应WebView控件宽度；
        当被设置为true，当前页面包含viewport属性标签，在标签中指定宽度值生效，
        如果页面不包含viewport标签，无法提供一个宽度值，这个时候该方法将被使用。
         */
        mWebSetting.setUseWideViewPort(false);

        mWebSetting.setLoadWithOverviewMode(true);//是否使用预览模式加载界面。
        mWebSetting.setSupportMultipleWindows(false);//是否支持多屏窗口，参考WebChromeClient#onCreateWindow，默认false，不支持。
        //WebView底层的布局算法，参考LayoutAlgorithm#NARROW_COLUMNS，将会重新生成WebView布局
        mWebSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);//NORMAL  //NARROW_COLUMNS  //SINGLE_COLUMN
        mWebSetting.setTextSize(WebSettings.TextSize.NORMAL);


        //2.数据相关
        if (NetworkUtils.isConnected()) {
            //有网络连接，设置默认缓存模式
            mWebSetting.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            //无网络连接，设置本地缓存模式
            mWebSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        mWebSetting.setSaveFormData(true);//是否保存表单数据，默认true，保存数据。
        mWebSetting.setAppCacheEnabled(true);//设置Application缓存API是否开启，默认false，
        mWebSetting.setDomStorageEnabled(true);// 设置是否开启DOM存储API权限，默认false，未开启，
        mWebSetting.setDatabaseEnabled(true);//设置是否开启数据库存储API权限，默认false，未开启
        mWebSetting.setGeolocationEnabled(true);//设置是否开启定位功能，默认true，开启定位
        mWebSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        mWebSetting.setAppCachePath(PATH_CACHE_WEBVIEW);//设置  Application Caches 缓存目录
        mWebSetting.setDatabasePath(PATH_CACHE_WEBVIEW);//设置数据库缓存路径
        mWebSetting.setGeolocationDatabasePath(PATH_CACHE_WEBVIEW);

        //3.图片
        mWebSetting.setLoadsImagesAutomatically(true);//是否加载图片资源，默认true，自动加载图片
        mWebSetting.setBlockNetworkImage(true);//是否以http、https方式访问从网络加载图片资源，默认false
        mWebSetting.setBlockNetworkLoads(false);//是否从网络加载资源，Application需要设置访问网络权限，否则报异常
        mWebSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);//设置渲染的优先级

        //4. js  文件
        mWebSetting.setJavaScriptEnabled(true);//是否允许执行JavaScript脚本，默认false，不允许
        mWebSetting.setAllowFileAccess(true);//内部是否允许访问文件，默认允许访问。
        mWebSetting.setAllowUniversalAccessFromFileURLs(true);//脚本可以是否访问任何原始起点内容，默认true
        mWebSetting.setAllowFileAccessFromFileURLs(true);//设置WebView运行中的一个文件方案被允许访问其他文件方案中的内容，默认值true
        mWebSetting.setJavaScriptCanOpenWindowsAutomatically(true);//是否允许自动打开弹窗，默认false，不允许
        mWebSetting.setNeedInitialFocus(true);//设置WebView是否需要设置一个节点获取焦点当被回调的时候，默认true
        mWebSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);////支持插件
        mWebSetting.setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1; zh-cn; OPPO R9tm Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.6 Mobile Safari/537.36 Ykxia");

        /**
         *  Webview在安卓5.0之前默认允许其加载混合网络协议内容
         *  在安卓5.0之后，默认不允许加载http与https混合内容，需要设置webview允许其加载混合网络协议内容
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
            mWebSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        enablePageVideoFunc(1);
        this.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        // LogUtils.e("webview---init---");
    }

    @Override
    protected void onVisibilityChanged(View view, int visibility) {
        // LogUtils.e("webview---onVisibilityChanged---" + visibility);
        try {
            super.onVisibilityChanged(view, visibility);
        } catch (Exception e) {
            e.getMessage();
        }
    }


    public void enablePageVideoFunc(int defaultVideoScreen) {//todo  改的
        if (this.getX5WebViewExtension() != null) {
            try {
                Bundle data = new Bundle();
                data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
                data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，
                data.putInt("DefaultVideoScreen", defaultVideoScreen);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
                this.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.mOnScrollChangeListener = listener;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        LogUtils.d("kuxunyou", "webview下载---url--" + url
                + "\n--userAgent---" + userAgent
                + "\n--contentDisposition---" + contentDisposition
                + "\n--mimetype---" + mimetype
                + "\n--contentLength---" + contentLength
                + "\n--title---" + mTitle
        );
        try {
            if (url != null && !url.contains(".mp4")) {
                String name = "";
                if (url.contains("?") && url.contains("=")) {
                    String url0 = url.substring(0, url.lastIndexOf("?"));
                    name = url0.substring(url0.lastIndexOf("/") + 1);
                } else {
                    name = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 打开外部浏览器
     *
     * @param url
     */
    private void openOutBrowser(String url) {
        if (!TextUtils.isEmpty(url)) {
            LogUtils.d("外跳url---" + url);
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri parse = Uri.parse(url);
                if (parse != null) {
                    intent.setData(parse);
                    mContext.startActivity(intent);
                }
            } catch (Exception e) {
                if (url.startsWith("alipay")) {
                    // ToastUtils.showShort("您还未安装支付宝");
                } else if (url.startsWith("weixin")) {
                    // ToastUtils.showShort("您还未安装微信");
                } else if (url.contains("taobao")) {
                    // ToastUtils.showShort("您还未安装淘宝");
                } else if (url.contains("mqqapi") || url.contains("mqq")) {
                    // ToastUtils.showShort("您还未安装QQ");
                } else if (url.contains("jdmobile")) {
                    // ToastUtils.showShort("您还未安装京东");
                } else if (url.contains("imeituan")) {
                    // ToastUtils.showShort("您还未安装美团");
                }
            }
        }
    }


    public interface OnScrollChangeListener {
        public void onPageEnd(int l, int t, int oldl, int oldt);

        public void onPageTop(int l, int t, int oldl, int oldt);

        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    public interface OnWebViewClientListener {
        public void shouldOverrideUrlLoading(WebView webView, String url);

        public void onPageFinished(WebView webView, String url);

        public void onPageStarted(WebView webView, String url, Bitmap bitmap);

        public WebResourceResponse shouldInterceptRequest(WebView webView, String url);

        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload);
    }


    public interface OnWebChromeClientListener {
        public void onProgressChanged(WebView webView, int newProgress);

        public void onReceivedTitle(WebView webView, String title);
    }

    public interface OnDownloadListener {
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength);
    }

    public class BaseWebViewClient extends WebViewClient {
        public OnWebViewClientListener mWebViewClientListener;

        public void setWebViewClientListener(OnWebViewClientListener webViewClientListener) {
            this.mWebViewClientListener = webViewClientListener;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            if (TextUtils.isEmpty(url)) {
                return true;
            }
            if (url.startsWith("alipays://") || url.startsWith("alipay://") //支付宝
                    || url.startsWith("weixin://") || url.startsWith("weixins://")//微信
                    || url.startsWith("taobao://") || url.startsWith("tbopen://")//淘宝
                    || url.startsWith("mqqapi://") || url.startsWith("mqqapis://")//QQ支付
                    || url.startsWith("mqq://")//QQ
                    || url.startsWith("imeituan://")//美团
                    || url.startsWith("openapp.jdmobile://") || url.contains("jdmobile://")//京东
                    || url.startsWith("tel:")//电话

                    ) {// 微信
                openOutBrowser(url);
            } else {
                try {
                    if (mWebViewClientListener != null) {
                        mWebViewClientListener.shouldOverrideUrlLoading(webView, url);
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
            super.onPageStarted(webView, url, bitmap);
            if (mWebSetting != null) {
                mWebSetting.setBlockNetworkImage(true);
            }
            //LogUtils.d( "onPageStarted--->" + url);
            if (mWebViewClientListener != null) {
                mWebViewClientListener.onPageStarted(webView, url, bitmap);
            }
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            //LogUtils.d( "onPageFinished--->" + url);
            super.onPageFinished(webView, url);
            if (mWebSetting != null) {
                mWebSetting.setBlockNetworkImage(false);
            }
            if (mWebViewClientListener != null) {
                mWebViewClientListener.onPageFinished(webView, url);
            }
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
            // LogUtils.d("shouldInterceptRequest--->" + url);
            if (mWebViewClientListener != null) {
                WebResourceResponse webResourceResponse = mWebViewClientListener.shouldInterceptRequest(webView, url);
                if (webResourceResponse != null) {
                    return webResourceResponse;
                } else {
                    return super.shouldInterceptRequest(webView, url);
                }
            }
            return super.shouldInterceptRequest(webView, url);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (mWebViewClientListener != null) {
                mWebViewClientListener.doUpdateVisitedHistory(view, url, isReload);
            }
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            //  super.onReceivedSslError(webView, sslErrorHandler, sslError);
            sslErrorHandler.proceed();
        }


    }

    public class BaseWebChromeClient extends WebChromeClient {
        private OnWebChromeClientListener mWebChromeClientListener;

        public void setWebChromeClientListener(OnWebChromeClientListener listener) {
            this.mWebChromeClientListener = listener;
        }

        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            // LogUtils.d("kuxunyou","onJsAlert--->"+s+"  "+"  "+s1+"  "+webView.getUrl());
            return super.onJsAlert(webView, s, s1, jsResult);
        }

        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            // LogUtils.d("kuxunyou","onJsConfirm--->"+s+"  "+"  "+s1+"  "+webView.getUrl());
            return super.onJsConfirm(webView, s, s1, jsResult);
        }

        @Override
        public boolean onJsPrompt(WebView webView, String s, String s1, String s2, JsPromptResult jsPromptResult) {
            // LogUtils.d("kuxunyou","onJsPrompt--->"+s+"  "+"  "+s1+"  "+webView.getUrl());
            return super.onJsPrompt(webView, s, s1, s2, jsPromptResult);
        }

        @Override
        public boolean onJsBeforeUnload(WebView webView, String s, String s1, JsResult jsResult) {
            //  LogUtils.d("kuxunyou","onJsBeforeUnload--->"+s+"  "+"  "+s1+"  "+webView.getUrl());
            return super.onJsBeforeUnload(webView, s, s1, jsResult);
        }

        @Override
        public boolean onJsTimeout() {
            // LogUtils.d("kuxunyou","onJsTimeout--->");
            return super.onJsTimeout();
        }

        @Override
        public void onProgressChanged(WebView webView, int newProgress) {
            super.onProgressChanged(webView, newProgress);
            if (mWebChromeClientListener != null) {
                mWebChromeClientListener.onProgressChanged(webView, newProgress);
            }
            if (newProgress == 100) {
                if (mWebSetting != null) {
                    mWebSetting.setBlockNetworkImage(false);
                }
            }
        }

        @Override
        public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(spaceNeeded * 2);
        }

        @Override
        public void onReceivedTitle(WebView webView, String title) {
            super.onReceivedTitle(webView, title);
            mTitle = title;
            if (mWebChromeClientListener != null) {
                mWebChromeClientListener.onReceivedTitle(webView, title);
            }
        }

        @Override
        public View getVideoLoadingProgressView() {
            return super.getVideoLoadingProgressView();
        }
    }

    public class BaseDownloadListener implements DownloadListener {
        private OnDownloadListener mOnDownloadListener;

        public void setOnDownloadListener(OnDownloadListener listener) {
            this.mOnDownloadListener = listener;
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            if (mOnDownloadListener != null) {
               /*
                Log.e("kuxunyou", "webview下载---url--" + url
                        + "\n--userAgent---" + userAgent
                        + "\n--contentDisposition---" + contentDisposition
                        + "\n--mimetype---" + mimetype
                        + "\n--contentLength---" + contentLength
                );
                */
                if (!TextUtils.isEmpty(url)) {
                    mOnDownloadListener.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength);
                }
            }
        }
    }
}

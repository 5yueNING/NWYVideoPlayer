package com.ningwuyue.sdk.nwyvideoplayersdk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.base.BaseActivity;
import com.ningwuyue.sdk.nwyvideoplayersdk.base.BaseX5Webview;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.Constant;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.SpField;
import com.ningwuyue.sdk.nwyvideoplayersdk.http.httpwork.HttpWorkUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.SvipplayEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.SPUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by ${武跃} on 2017/6/17.
 * {一句话描述功能:----banner,  点击位,  发现  ,游戏中心 , 支付 , 打开的界面}
 */
public class WebviewActivity extends BaseActivity {
    public static final String BUNDLE_URL_KEY = "url";
    public static final String BUNDLE_FROMTYPE_KEY = "fromtype";
    public static final String BUNDLE_NAME_KEY = "name";
    public static final String BUNDLE_LOADJS_KEY = "loadjs";
    public static final String BUNDLE_USER_AGENT_KEY = "user_agent";
    public static final String BUNDLE_JS1_KEY = "js1";
    public static final String BUNDLE_JS2_KEY = "js2";

    public static final String FROMTYPE_FIND = "fromtype_find";//发现
    public static final String FROMTYPE_JS1_JS2 = "fromtype_js1_js2";//直接传js1 js2值


    private BaseX5Webview mX5WebView = null;

    private String mURL = null, mName = null, mLoadjs = null, mFromType = null;
    private String mfind_Js_1 = null, mfind_Js_2 = null;// 发现---传过来的
    private String mUser_agent = null;
    private String mTopLeftUrl;
    private String mTopLeftName;
    private String[] mKeywordArray;
    private boolean isShield_Src = true;//是否是拦截的
    private String mSvip_url;
    private String mSvip_title;
    private ProgressBar mProgressBar;
    private TwinklingRefreshLayout mRefreshLayout;
    private TextView mTvTitle;

    /**
     * 公共方法----跳转
     *
     * @param activity
     * @param name
     * @param url
     * @param mLoadjs
     * @param fromType
     * @param mUser_agent
     */
    public static void startToWebviewActivity(Context activity, String name, String url, String mLoadjs, String fromType, String mUser_agent) {
        Intent intent = new Intent(activity, WebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_NAME_KEY, name);
        bundle.putString(BUNDLE_URL_KEY, url);
        bundle.putString(BUNDLE_LOADJS_KEY, mLoadjs);
        bundle.putString(BUNDLE_FROMTYPE_KEY, fromType);
        bundle.putString(BUNDLE_USER_AGENT_KEY, mUser_agent);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 公共方法----跳转------方便发现模块跳转
     *
     * @param activity
     * @param name
     * @param url
     * @param js1
     * @param js2
     * @param fromType
     * @param mUser_agent
     */
    public static void startToWebviewActivity_js1js2(Activity activity, String name, String url, String js1, String js2, String fromType, String mUser_agent) {
        Intent intent = new Intent(activity, WebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_NAME_KEY, name);
        bundle.putString(BUNDLE_URL_KEY, url);
        bundle.putString(BUNDLE_JS1_KEY, js1);
        bundle.putString(BUNDLE_JS2_KEY, js2);
        bundle.putString(BUNDLE_FROMTYPE_KEY, fromType);
        bundle.putString(BUNDLE_USER_AGENT_KEY, mUser_agent);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    protected int setLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.nwyvideo_activity_webview;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getBundleData();
        configView();
    }

    private void configView() {
        RelativeLayout mRlRootView = findViewById(R.id.rl_rootview);
        RelativeLayout mRlTopback = findViewById(R.id.rl_topback);
        TextView mTvTopclose = findViewById(R.id.tv_topclose_btn);
        mTvTitle = findViewById(R.id.tv_toptitle);
        RelativeLayout mRlRightBtn = findViewById(R.id.rl_right_btn);
        ImageView mIvTopRight = findViewById(R.id.iv_top_right);
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mProgressBar = findViewById(R.id.pb);

        mRlTopback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mTvTopclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initView();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mURL = bundle.getString(BUNDLE_URL_KEY, "");
            mName = bundle.getString(BUNDLE_NAME_KEY, "");
            mLoadjs = bundle.getString(BUNDLE_LOADJS_KEY, "0");//是否加载js ------0表示不加载
            mfind_Js_1 = bundle.getString(BUNDLE_JS1_KEY, "");
            mfind_Js_2 = bundle.getString(BUNDLE_JS2_KEY, "");
            mFromType = bundle.getString(BUNDLE_FROMTYPE_KEY, "");
            mUser_agent = bundle.getString(BUNDLE_USER_AGENT_KEY, "");
        }

        if (!TextUtils.isEmpty(mName)) {
            if (mName.contains(Constant.TAG_RELEAS_SRC)) {
                int i = mName.indexOf(Constant.TAG_RELEAS_SRC);
                String tag_releas_src = mName.replace(Constant.TAG_RELEAS_SRC, "").substring(i);
                isShield_Src = false;
                mKeywordArray = tag_releas_src.split(",");
                mName = mName.substring(0, i);
            } else if (mName.contains(Constant.TAG_SHIELD_SRC)) {
                int i = mName.indexOf(Constant.TAG_SHIELD_SRC);
                String tag_shield_src = mName.replace(Constant.TAG_SHIELD_SRC, "").substring(i);
                isShield_Src = true;
                mKeywordArray = tag_shield_src.split(",");
                mName = mName.substring(0, i);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        if (!TextUtils.isEmpty(mLoadjs) && !mLoadjs.equals("0")) {//判断是否需要请求api 加载js
            //HttpWorkUtils.getWebViewJs(mLoadjs);
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        mX5WebView = new BaseX5Webview(this);
        TwinklingRefreshLayout.LayoutParams params = new TwinklingRefreshLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mX5WebView.setLayoutParams(params);
        mRefreshLayout.addView(mX5WebView, 0);
        SinaRefreshView headerView = new SinaRefreshView(this);
        headerView.setArrowResource(R.drawable.nwyvideo_ic_pulltorefresh_arrow);
        headerView.setTextColor(Color.GRAY);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setEnableLoadmore(false);
        mRefreshLayout.setEnableOverScroll(false);
        mRefreshLayout.setTargetView(mX5WebView);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mX5WebView != null) {
                            mX5WebView.reload();
                        }
                        refreshLayout.finishRefreshing();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
            }
        });

        // mX5WebView.setWebChromeClient(new MyWebChromeClient());
        // mX5WebView.setWebViewClient(new MyWebViewClient());

        mX5WebView.mBaseWebViewClient.setWebViewClientListener(new BaseX5Webview.OnWebViewClientListener() {
            @Override
            public void shouldOverrideUrlLoading(WebView webView, String url) {
                webviewReplaceTag(webView, url);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                if (!TextUtils.isEmpty(mFromType) && (mFromType.equals(FROMTYPE_FIND) || (mFromType.equals(FROMTYPE_JS1_JS2)))) {
                    if (!TextUtils.isEmpty(mfind_Js_2)) {//这个是传值传过来的----发现
                        webView.loadUrl("javascript:" + mfind_Js_2);
                    }
                } else {
                    if (!TextUtils.isEmpty(mLoadjs) && !mLoadjs.equals("0")) {//这个是需要请求api添加的
                        String js_2 = SPUtils.getInstance().getString(SpField.SP_JS_2, "");
                        if (!TextUtils.isEmpty(js_2)) {
                            webView.loadUrl("javascript:" + js_2);
                        }
                    }
                }
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {

            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
                return getWebResourceResponse(url);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {

            }
        });

        mX5WebView.mBaseWebChromeClient.setWebChromeClientListener(new BaseX5Webview.OnWebChromeClientListener() {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                    }
                } else {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                        mProgressBar.setProgress(newProgress);//设置进度值
                    }
                    if (!TextUtils.isEmpty(mFromType) && (mFromType.equals(FROMTYPE_FIND) || mFromType.equals(FROMTYPE_JS1_JS2))) {
                        if (!TextUtils.isEmpty(mfind_Js_1)) {
                            if (newProgress >= 30 && newProgress % 20 == 0) {
                                webView.loadUrl("javascript:" + mfind_Js_1);
                            }
                        }
                    } else {
                        if (!TextUtils.isEmpty(mLoadjs) && !mLoadjs.equals("0")) {//这个是需要请求api添加的
                            String js_1 =  SPUtils.getInstance().getString(SpField.SP_JS_1, "");
                            if (!TextUtils.isEmpty(js_1)) {
                                if (newProgress >= 30 && newProgress % 20 == 0) {
                                    webView.loadUrl("javascript:" + js_1);
                                    // Log.e("youkanxia", "js_1-----" + js_1);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, final String title) {
                if (!TextUtils.isEmpty(mFromType) && (mFromType.equals(FROMTYPE_FIND))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (title != null && mTvTitle != null) {
                                mTvTitle.setText(title);
                            }
                        }
                    });
                }
            }
        });


        mX5WebView.setOnScrollChangeListener(new BaseX5Webview.OnScrollChangeListener() {
            @Override
            public void onPageEnd(int l, int t, int oldl, int oldt) {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setEnableRefresh(false);
                }
            }

            @Override
            public void onPageTop(int l, int t, int oldl, int oldt) {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setEnableRefresh(true);
                }
            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setEnableRefresh(false);
                }
            }
        });

        if (!TextUtils.isEmpty(mName)) {
            mTvTitle.setText(mName);
        }
        if (!TextUtils.isEmpty(mURL)) {
            mX5WebView.loadUrl(mURL);
        }
        if (!TextUtils.isEmpty(mUser_agent) && !mUser_agent.equals("null")) {
            mX5WebView.mWebSetting.setUserAgentString(mUser_agent);
        }
    }

    /**
     * shouldOverrideUrlLoading(WebView webView, String url) 中
     *
     * @param webView
     * @param url
     */
    private void webviewReplaceTag(WebView webView, String url) {
        if (url == null) {
            return;
        }
        if (url.contains(Constant.URL_REPLACE_uu_new1) || url.contains(Constant.URL_REPLACE_uu_new2)) {
            String newUrl = url.replace(Constant.URL_REPLACE_uu_new1, "").replace(Constant.URL_REPLACE_uu_new2, "");
            if (!TextUtils.isEmpty(mFromType) && mFromType.equals(FROMTYPE_FIND)) {
                //AdActivity.toAdActivity(WebviewActivity.this, newUrl, mName, mfind_Js_1, mfind_Js_2, mUser_agent);
            } else {
                String js_1 = (String) SPUtils.getInstance().getString(SpField.SP_JS_1, "");
                String js_2 = (String) SPUtils.getInstance().getString(SpField.SP_JS_2, "");
                //AdActivity.toAdActivity(WebviewActivity.this, newUrl, mName, js_1, js_2, mUser_agent);
            }
        } else if (url.contains(Constant.URL_REPLACE_uu_svip1) || url.contains(Constant.URL_REPLACE_uu_svip2)) {
            showWaitingDialog();
            String replaceUlr = url.replace(Constant.URL_REPLACE_uu_svip1, "").replace(Constant.URL_REPLACE_uu_svip2, "");
            mSvip_url = replaceUlr;
            mSvip_title = mName;
            HttpWorkUtils.getSvipPaly(1, mName, replaceUlr, "0", Constant.TAG_SVIP_PLAY_AD_YES);
        } else if (url.contains(Constant.URL_REPLACE_uu_down1) || url.contains(Constant.URL_REPLACE_uu_down2)) {
            url = url.replace(Constant.URL_REPLACE_uu_down1, "").replace(Constant.URL_REPLACE_uu_down2, "");

        } else {
            mURL = url;
            webView.loadUrl(url);
        }
    }

    @Override
    public void receiveEventBus(EventObject eventObject) {
        super.receiveEventBus(eventObject);
        if (isResume) {
            String tag = eventObject.tag;

            if (tag.startsWith(HttpWorkUtils.HTTP_TAG_WEB_SVIP_GET_PLAY_URL)) {
                dismissWaitingDialog();
                SvipplayEntity event = (SvipplayEntity) eventObject.object;
                if (event != null) {
                    String play_type = event.play_type;
                    String url_status = event.url_status;
                    String svip_ad_open = event.svip_ad_open;
                    if (!TextUtils.isEmpty(play_type) && play_type.equals("1")) {//webview播放
                        SvippalyActivity.startSvipAvtivity(this, event, "0", mSvip_title, mSvip_url, mSvip_title);
                    } else if (!TextUtils.isEmpty(play_type) && play_type.equals("2")) {//原生播放
                        VideoPlayerActivity.startActivity(this, event.play_url, mSvip_title, Constant.VIDEO_TYPE_HTTP,
                                mSvip_title, mSvip_url, "0", true, svip_ad_open, event);
                    }
                }
            }
        }
    }

    /**
     * 跳转到WebviewActivity
     *
     * @param url
     * @param title
     * @param js_1
     * @param js_2
     * @param fromType
     * @param user_agen
     */
    private void toWebviewActivity(String url, String title, String js_1, String js_2, String fromType, String user_agen) {
        if (!TextUtils.isEmpty(url)) {
            if (url.contains(Constant.URL_REPLACE_uu_new1) || url.contains(Constant.URL_REPLACE_uu_new2)) {
                String replaceUlr = url.replace(Constant.URL_REPLACE_uu_new1, "").replace(Constant.URL_REPLACE_uu_new2, "");
            } else if (url.contains(Constant.URL_REPLACE_uu_ext1) || url.contains(Constant.URL_REPLACE_uu_ext2)) {
                String replaceUlr = url.replace(Constant.URL_REPLACE_uu_ext1, "").replace(Constant.URL_REPLACE_uu_ext2, "");
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri parse = Uri.parse(replaceUlr);
                    if (parse != null) {
                        intent.setData(parse);
                        this.startActivity(intent);
                    }
                } catch (Exception e) {
                }
            } else if (url.contains(Constant.URL_REPLACE_uu_svip1) || url.contains(Constant.URL_REPLACE_uu_svip2)) {//svip
                String replaceUlr = url.replace(Constant.URL_REPLACE_uu_svip1, "").replace(Constant.URL_REPLACE_uu_svip2, "");
                mSvip_url = replaceUlr;
                mSvip_title = title;
                showWaitingDialog();
                HttpWorkUtils.getSvipPaly(0, title, replaceUlr, "0", Constant.TAG_SVIP_PLAY_AD_YES);
            } else if (url.contains(Constant.URL_REPLACE_uu_down1) || url.contains(Constant.URL_REPLACE_uu_down2)) {
            } else {
                WebviewActivity.startToWebviewActivity_js1js2(this, title, url, js_1, js_2, fromType, user_agen);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mX5WebView.canGoBack()) {
            mX5WebView.goBack();////返回上一浏览页面
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        destroyWebView();
        SPUtils.getInstance().remove(SpField.SP_JS_1);
        SPUtils.getInstance().remove(SpField.SP_JS_2);
        super.onDestroy();
    }

    /**
     * 解决WebView持有mContext导致的内存泄漏问题
     */
    private void destroyWebView() {
        if (mX5WebView != null) {
            ViewParent parent = mX5WebView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mX5WebView);
            }
            mX5WebView.stopLoading();
            mX5WebView.getSettings().setJavaScriptEnabled(false);
            mX5WebView.clearHistory();
            mX5WebView.clearView();
            mX5WebView.removeAllViews();
            try {
                mX5WebView.destroy();
            } catch (Throwable e) {
            }
        }
    }

    /**
     * @param url
     * @return
     */
    private WebResourceResponse getWebResourceResponse(String url) {
        if (url != null) {
            if (isShield_Src) {
                if (BaseX5Webview.isHasKeyword(mKeywordArray, url)) {
                    // Log.e("kuxunyou", "拦截了广告--->>" + url);
                    return new WebResourceResponse(null, null, null);
                } else {
                    return null;
                }
            } else {
                if (BaseX5Webview.isHasKeyword(mKeywordArray, url)) {
                    return null;
                } else {
                    return new WebResourceResponse(null, null, null);
                }
            }
        } else {
            return null;
        }
    }
}

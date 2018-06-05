package com.ningwuyue.sdk.videoplayer.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.ningwuyue.sdk.videoplayer.R;
import com.ningwuyue.sdk.videoplayer.base.BaseActivity;
import com.ningwuyue.sdk.videoplayer.base.BaseX5Webview;
import com.ningwuyue.sdk.videoplayer.util.LogUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class VipWebviewActivity extends BaseActivity implements View.OnClickListener {
    public static final String BUNDLE_COOKIEENTITY_KEY = "cookieentity";
    public static final String BUNDLE_VIPWEBENTITY_KEY = "vipwebentity";

    private static final int WHAT_VIP_LOGIN = 1001;
    private static final int WHAT_LOADING_SVIP = 1003;
    private static final int WHAT_AD_EXIT = 1004;
    private static final int WHAT_GUIDE_SHOW = 1005;

    private static final int WHAT_GETURL = 1009;
    private static final int DELAYED_TIME = 500; //毫秒
    private static final long GETURL_TIME_OUT = 20 * 1000l;//最长时间获取src
    private BaseX5Webview mX5WebView = null;
    private String mURL = null, mName = null, mLoadjs = null, mHtmlTitle = null;
    private String mPing = null;
    private String mVweb = null;
    private String mGroupid = null;
    private String mUser_agent = null;
    //弹窗对话框;
    private boolean isResume = true;
    private boolean isFreeback = false;
    private String mHomeUrl;
    private boolean isClearHistory;
    //这些变量是下载上传的
    private boolean isDestroy = false, mIsOpenCookie = true; //是否当前页需要获取
    private String mUid;
    private String mToken;

    private boolean isFront = false;
    // 按两次返回键返回首页
    private long exitTime = 0;
    private String[] mKeywordArray;
    private boolean isShield_Src = true;//是否是拦截的
    private TwinklingRefreshLayout mRefreshLayout;

    @Override
    protected int setLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.activity_vip_webview;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        configView();
    }


    private void configView() {
        RelativeLayout mRlTopback = (RelativeLayout) findViewById(R.id.rl_topback);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_toptitle);
        mRefreshLayout = (TwinklingRefreshLayout) findViewById(R.id.refreshLayout);
        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.pb);
        TextView mTvTopcloseBtn = (TextView) findViewById(R.id.tv_topclose_btn);
        LinearLayout mLLTopsvipPlayBtn = (LinearLayout) findViewById(R.id.ll_topsvip_play_btn);
        ImageView mIvVipIcon = (ImageView) findViewById(R.id.iv_vip_icon);
        LinearLayout ll_guide_play = (LinearLayout) findViewById(R.id.ll_guide_play);

        mRlTopback.setOnClickListener(this);
        mLLTopsvipPlayBtn.setOnClickListener(this);

        TwinklingRefreshLayout.LayoutParams params = new TwinklingRefreshLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mX5WebView = new BaseX5Webview(this);
        mX5WebView.setLayoutParams(params);
        mRefreshLayout.addView(mX5WebView, 0);
        SinaRefreshView headerView = new SinaRefreshView(this);
        headerView.setArrowResource(R.drawable.ic_pulltorefresh_arrow);
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
                        mX5WebView.reload();
                        refreshLayout.finishRefreshing();
                    }
                }, 1000);
            }

            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
            }
        });

        mX5WebView.mBaseWebChromeClient.setWebChromeClientListener(new BaseX5Webview.OnWebChromeClientListener() {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (webView != null) {
                    mURL = webView.getUrl();
                }
                if (mProgressBar != null) {
                    mProgressBar.setProgress(newProgress);//设置进度值
                }
                if (newProgress >= 99) {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                } else {
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                }
                if (!TextUtils.isEmpty(mLoadjs) && !mLoadjs.equals("0")) {
                    String js_1 = "";//todo

                    if (newProgress >= 80 && newProgress % 20 == 0) {
                        if (!TextUtils.isEmpty(js_1)) {
                            webView.loadUrl("javascript:" + js_1);
                        }
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                if (webView != null) {
                    mURL = webView.getUrl();
                }
                mHtmlTitle = title;
                // LogUtils.d("onReceivedTitle标题--->>" + mHtmlTitle+"   "+mURL+"   "+webView.getOriginalUrl());
            }
        });

        mX5WebView.mBaseWebViewClient.setWebViewClientListener(new BaseX5Webview.OnWebViewClientListener() {
            @Override
            public void shouldOverrideUrlLoading(WebView webView, String url) {
                LogUtils.d("shouldOverrideUrlLoading--->>" + url);
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    mURL = url;
                    webView.loadUrl(url);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                //LogUtils.d("onPageFinished--" + mIsGetM3u8 + "---" + mIsGetM3u8Down + "---" + mSvip_js_2);
                mURL = url;
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
                String js_2 = "";//todo
                if (!TextUtils.isEmpty(js_2)) {
                    webView.loadUrl("javascript:" + js_2);
                }
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                LogUtils.d("onPageStarted   " + url);
                mURL = url;
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
                return getWebResourceResponse(url);
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (isClearHistory) {
                    isClearHistory = false;
                    view.clearHistory();//清除历史记录
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
        if (TextUtils.isEmpty(mPing) || !mPing.equals("1")) {
            mLLTopsvipPlayBtn.setVisibility(View.INVISIBLE);
            ll_guide_play.setVisibility(View.GONE);
        } else {
            boolean mIsHideGuidePlay = false;//todo
            if (mIsHideGuidePlay) {
                ll_guide_play.setVisibility(View.GONE);
            } else {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(WHAT_GUIDE_SHOW, 2000);
                }
            }
        }
        initSettings();
        if (!TextUtils.isEmpty(mURL)) {
            mX5WebView.loadUrl(mURL);
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
                    //  Log.e("kuxunyou", "拦截了广告--->>" + url);
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

    /**
     * webview 设置
     */
    private void initSettings() {
        if (mX5WebView == null) {
            return;
        }
        WebSettings webSetting = mX5WebView.mWebSetting;
        if (!TextUtils.isEmpty(mUser_agent) && !mUser_agent.equals("null")) {
            //LogUtils.d("setUserAgentString   " + mUser_agent);
            //String ua = "Mozilla/5.0 (iPhone 92; CPU iPhone OS 10_3_2 like Mac OS X) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.0 MQQBrowser/7.6.0 Mobile/14F89 Safari/8536.25 MttCustomUA/2 QBWebViewType/1 WKType/1";
            webSetting.setUserAgentString(mUser_agent);//设置用户代理;
            //webSetting.setUserAgentString("Mozilla/5.0 (Linux; U; Android 5.1; zh-cn; OPPO R9tm Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.6 Mobile Safari/537.36 Ykxia");
            //webSetting.setUserAgentString(ua);//设置用户代理;
        }
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_topback) {
            finish();
        } else if (id == R.id.ll_topsvip_play_btn) {


        }
    }
}
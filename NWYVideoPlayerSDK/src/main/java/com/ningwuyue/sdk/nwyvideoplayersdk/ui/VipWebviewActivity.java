package com.ningwuyue.sdk.nwyvideoplayersdk.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Message;
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
import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.base.BaseActivity;
import com.ningwuyue.sdk.nwyvideoplayersdk.base.BaseX5Webview;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.VipwebEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.LogUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class VipWebviewActivity extends BaseActivity implements View.OnClickListener {
    public static final String BUNDLE_VIPWEBENTITY_KEY = "vipwebentity";

    private static final int WHAT_VIP_LOGIN = 1001;
    private static final int WHAT_LOADING_SVIP = 1003;
    private static final int WHAT_AD_EXIT = 1004;
    private static final int WHAT_GUIDE_SHOW = 1005;
    private static final int WHAT_FINISH_REFRESHING = 1006;

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
    private LinearLayout mLl_guide_play;

    public static void startVipWebViewActivity(Activity activity, VipwebEntity vipwebEntity) {
        Intent intent = new Intent(activity,VipWebviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_VIPWEBENTITY_KEY, vipwebEntity);
        activity.startActivity(intent);
    }

    @Override
    protected int setLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.nwyvideo_activity_vip_webview;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        getBundleData();
        configView();
    }

    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            VipwebEntity vipwebEntity = bundle.getParcelable(BUNDLE_VIPWEBENTITY_KEY);
            if (vipwebEntity != null) {
                mURL = vipwebEntity.url;
                mHomeUrl = mURL;
                mName = vipwebEntity.name;
                mLoadjs = vipwebEntity.loadjs;
                mPing = vipwebEntity.ping;
                mVweb = vipwebEntity.vweb;
                String shield_src = vipwebEntity.shield_src;
                String release_src = vipwebEntity.release_src;
                shieldOrrelease(shield_src, release_src);
            }
        }
    }

    private void shieldOrrelease(String shield_src, String release_src) {
        if (TextUtils.isEmpty(shield_src)) {
            if (!TextUtils.isEmpty(release_src)) {// 需要放行
                mKeywordArray = release_src.split(",");
                isShield_Src = false;
            }
        } else {//需要拦截
            mKeywordArray = shield_src.split(",");
            isShield_Src = true;
        }
    }

    private void configView() {
        RelativeLayout mRlTopback = (RelativeLayout) findViewById(R.id.rl_topback);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_toptitle);
        mRefreshLayout = (TwinklingRefreshLayout) findViewById(R.id.refreshLayout);
        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.pb);
        TextView mTvTopcloseBtn = (TextView) findViewById(R.id.tv_topclose_btn);
        LinearLayout mLLTopsvipPlayBtn = (LinearLayout) findViewById(R.id.ll_topsvip_play_btn);
        ImageView mIvVipIcon = (ImageView) findViewById(R.id.iv_vip_icon);
        mLl_guide_play = (LinearLayout) findViewById(R.id.ll_guide_play);

        mRlTopback.setOnClickListener(this);
        mLLTopsvipPlayBtn.setOnClickListener(this);

        TwinklingRefreshLayout.LayoutParams params = new TwinklingRefreshLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mX5WebView = new BaseX5Webview(this);
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
                mHandler.sendEmptyMessageDelayed(WHAT_FINISH_REFRESHING, 1000);
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
                // LogUtils.d("onPageStarted   " + url);
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
            mLl_guide_play.setVisibility(View.GONE);
        } else {
            boolean mIsHideGuidePlay = false;//todo
            if (mIsHideGuidePlay) {
                mLl_guide_play.setVisibility(View.GONE);
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
            webSetting.setUserAgentString(mUser_agent);//设置用户代理;
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

    @Override
    protected void handlerMsg(Message msg) {
        super.handlerMsg(msg);
        if (msg != null) {
            switch (msg.what) {
                case WHAT_FINISH_REFRESHING:
                    if (mX5WebView != null) {
                        mX5WebView.reload();
                    }
                    if (mRefreshLayout != null) {
                        mRefreshLayout.finishRefreshing();
                    }
                    break;
                case WHAT_GUIDE_SHOW:
                    if (mLl_guide_play != null) {
                        mLl_guide_play.setVisibility(View.VISIBLE);
                    }
                    break;

            }

        }


    }
}
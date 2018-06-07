package com.ningwuyue.sdk.nwyvideoplayersdk.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.base.BaseActivity;
import com.ningwuyue.sdk.nwyvideoplayersdk.base.BaseX5Webview;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.Constant;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.SpField;
import com.ningwuyue.sdk.nwyvideoplayersdk.dialog.AnthologyDialog;
import com.ningwuyue.sdk.nwyvideoplayersdk.dialog.VideoPlayerAdDialog;
import com.ningwuyue.sdk.nwyvideoplayersdk.http.httpwork.HttpWorkUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.AnthologyEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.SvipplayEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.AnimatorUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.LogUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.SPUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.ToastUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.util.ArrayList;


/**
 * Created by ${武跃} on 2017/6/27.
 * <p>
 * {一句话描述功能:----优看---视频播放界面----Svip播放}
 */
public class SvippalyActivity extends BaseActivity {
    public static final String FROMTYPE_ADVANCEDPLAYACTIVITY = "fromtype_advancedplayactivity";//从AdvancedPlayActivity界面跳转过来
    private static final String BUNDLE_SVIPENTITY_KEY = "bundle_svipentity_key";
    private static final String BUNDLE_VWEB_KEY = "bundle_vweb_key";
    private static final String BUNDLE_HTMLURL_KEY = "bundle_htmlurl_key";
    private static final String BUNDLE_HTMLTITLE_KEY = "bundle_htmltitle_key";
    private static final String BUNDLE_NAME_KEY = "bundle_name_key";
    private static final String BUNDLE_KEY_FROMTYPE = "bundle_key_fromtype";//跳转过来的类型
    private static final String BUNDLE_KEY_INTLINE = "bundle_key_intline";//第几条线路
    private static final String BUNDLE_KEY_ANTHOLOGY_POSITION = "bundle_key_anthology_position";//第几集

    private static final long WHAT_TIME_AD = 1000;
    private static final int WHAT_AD = 1111;

    private BaseX5Webview mX5WebView = null;

    private String mVwebName;//顶部标题
    private String mSvipPlayUrl;//服务器获取的URL
    private String mVweb;

    private String mHtmltitle;
    private String mHtmlurl;//请求的url

    private String mJs_1 = null, mJs_2 = null;
    private String mUser_agent = null;
    private String mDown_url;

    private int intLine = 2;

    // String html_js = "document.getElementsByTagName('html')[0].innerHTML;";
    // private static final String SCRIPT_SRC = "document.getElementById('video').src";
    private ArrayList<AnthologyEntity> mAnthologyEntityList;
    private boolean isResume = false;
    private String mFromType;
    private String[] mKeywordArray;
    private boolean isShield_Src = true;//是否是拦截的
    private boolean mIsClickAnthology;
    private VideoPlayerAdDialog mVideoPlayerAdDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null && msg.what == WHAT_AD) {
                startShowAdDialog();
            }
        }
    };
    private ProgressBar mProgressBar;
    private RelativeLayout mRl;
    private int mDefaultVideoScreen = 1;
    private TextView mTvToptitle;

    /**
     * @param context
     * @param event------svip实体类
     * @param vweb-----平台        号
     * @param htmlTitle----网页标题
     * @param htmlUrl------网页url
     * @param vwebName-----平台名
     */
    public static void startSvipAvtivity(
            Context context, SvipplayEntity event, String vweb, String htmlTitle, String htmlUrl,
            String vwebName) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SvippalyActivity.BUNDLE_SVIPENTITY_KEY, event);
        bundle.putString(SvippalyActivity.BUNDLE_VWEB_KEY, vweb);
        bundle.putString(SvippalyActivity.BUNDLE_HTMLTITLE_KEY, htmlTitle);
        bundle.putString(SvippalyActivity.BUNDLE_HTMLURL_KEY, htmlUrl);
        bundle.putString(SvippalyActivity.BUNDLE_NAME_KEY, vwebName);

        Intent intent = new Intent(context, SvippalyActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 这个只是原生播放时刷新跳转过来的
     */
    public static void startAdvancedplayActivitytoSvipAvtivity(
            Context context, SvipplayEntity event, String vweb, String htmlTitle, String htmlUrl,
            String vwebName, int intLine) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_SVIPENTITY_KEY, event);
        bundle.putString(BUNDLE_VWEB_KEY, vweb);
        bundle.putString(BUNDLE_HTMLTITLE_KEY, htmlTitle);
        bundle.putString(BUNDLE_HTMLURL_KEY, htmlUrl);
        bundle.putString(BUNDLE_NAME_KEY, vwebName);
        bundle.putString(BUNDLE_KEY_FROMTYPE, FROMTYPE_ADVANCEDPLAYACTIVITY);
        bundle.putInt(BUNDLE_KEY_INTLINE, intLine);
        Intent intent = new Intent(context, SvippalyActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected int setLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        return R.layout.nwyvideo_activity_svippaly;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        getBundleData();
        configView();
    }




    private void getBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mVweb = bundle.getString(BUNDLE_VWEB_KEY, "");
            mHtmltitle = bundle.getString(BUNDLE_HTMLTITLE_KEY, "");
            mHtmlurl = bundle.getString(BUNDLE_HTMLURL_KEY, "");
            mVwebName = bundle.getString(BUNDLE_NAME_KEY, "");
            mFromType = bundle.getString(BUNDLE_KEY_FROMTYPE, "");
            intLine = bundle.getInt(BUNDLE_KEY_INTLINE, 2);
            SvipplayEntity svipplayEntity = bundle.getParcelable(BUNDLE_SVIPENTITY_KEY);
            if (svipplayEntity != null) {
                mSvipPlayUrl = svipplayEntity.url;
                mJs_1 = svipplayEntity.js_1;
                mJs_2 = svipplayEntity.js_2;
                mUser_agent = svipplayEntity.user_agent;
                mDown_url = svipplayEntity.down_url;
                String shield_src = svipplayEntity.shield_src;
                String release_src = svipplayEntity.release_src;
                String svip_ad_open = svipplayEntity.svip_ad_open;
                mDefaultVideoScreen = svipplayEntity.defaultVideoScreen;//todo

                if (!TextUtils.isEmpty(svip_ad_open) && svip_ad_open.equals("1")) {//1 是开启广告
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(WHAT_AD, WHAT_TIME_AD);
                    }
                }
                mAnthologyEntityList = (ArrayList<AnthologyEntity>) svipplayEntity.anthologyEntityList;
                shieldOrrelease(shield_src, release_src);
            }
            //String ios_ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_4 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12H143";
            LogUtils.d(mVwebName + "    " + mSvipPlayUrl);
        }
    }

    private void configView() {
        RelativeLayout mRlTopback = findViewById(R.id.rl_topback);
        mRlTopback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView mTvHelpBtn = findViewById(R.id.tv_help_btn);
        mTvHelpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        mTvToptitle = findViewById(R.id.tv_toptitle);
        TextView mTvTopQiehuan = findViewById(R.id.tv_top_qiehuan);
        mTvTopQiehuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsClickAnthology = false;
                showWaitingDialog();
                HttpWorkUtils.getSvipPaly(intLine++, mHtmltitle, mHtmlurl, mVweb, Constant.TAG_SVIP_PLAY_AD_NO);////sVip播放
            }
        });

        TextView mTvAnthologyBtn = findViewById(R.id.tv_anthology_btn);
        mTvAnthologyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAnthologyEntityList != null && mAnthologyEntityList.size() > 0) {
                    try {
                        AnthologyDialog dialog = new AnthologyDialog();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(AnthologyDialog.BUNDLE_KEY_LIST, mAnthologyEntityList);
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), "anthologydialog");
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        });

        TextView mTvFeedbackBtn = findViewById(R.id.tv_feedback_btn);
        ImageButton mIbtnDown = findViewById(R.id.ibtn_down);
        mProgressBar = findViewById(R.id.pb);
        mRl = findViewById(R.id.rl);
        // @BindView(R.id.tv_bottom_bg)
        //TextView mTvBottomBg=findViewById(R.id.rl_topback);
        ImageView mIvVipIcon = findViewById(R.id.iv_vip_icon);
        final LinearLayout ll_guide_play = findViewById(R.id.ll_guide_play);
        TextView tv_guide_close = findViewById(R.id.tv_guide_close);
        tv_guide_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtils.getInstance().put(SpField.CLICK_TOP_REFLUSH_PLAY, true);
                if (ll_guide_play != null) {
                    ll_guide_play.setVisibility(View.GONE);
                }
            }
        });


        String vip_type = SPUtils.getInstance().getString(SpField.VIP_TYPE, "");
        if (!TextUtils.isEmpty(vip_type) && vip_type.equals("0")) {
            gone(mIvVipIcon);
        } else {
            visible(mIvVipIcon);
        }
        if (!TextUtils.isEmpty(mDown_url)) {
            visible(mIbtnDown);
        } else {
            gone(mIbtnDown);
        }
        if (mAnthologyEntityList != null && mAnthologyEntityList.size() > 0) {
            visible(mTvAnthologyBtn);
        } else {
            gone(mTvAnthologyBtn);
        }
        mTvToptitle.setText(mVwebName != null ? mVwebName : "");
        mTvAnthologyBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnimatorUtils.startCcaleAnimator2(v);
                return false;
            }
        });
        mTvFeedbackBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                AnimatorUtils.startCcaleAnimator2(v);
                return false;
            }
        });
        boolean mIsHideGuidePlay = SPUtils.getInstance().getBoolean(SpField.CLICK_TOP_REFLUSH_PLAY, false);
        if (mIsHideGuidePlay) {
            ll_guide_play.setVisibility(View.GONE);
        } else {
            ll_guide_play.setVisibility(View.VISIBLE);
        }
        initWebView();
    }

    /**
     * @param shield_src
     * @param release_src
     */
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

    //初始化view
    private void initWebView() {
        mX5WebView = new BaseX5Webview(this);
        mX5WebView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRl.addView(mX5WebView, 0);
        mX5WebView.setVerticalScrollBarEnabled(false);
        mX5WebView.setHorizontalScrollBarEnabled(false);
        WebSettings webSetting = mX5WebView.mWebSetting;
        if (!TextUtils.isEmpty(mUser_agent) && !mUser_agent.equals("null")) {
            webSetting.setUserAgentString(mUser_agent);
        }

        mX5WebView.mBaseWebChromeClient.setWebChromeClientListener(new BaseX5Webview.OnWebChromeClientListener() {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    mProgressBar.setProgress(newProgress);//设置进度值
                }
                if (!TextUtils.isEmpty(mJs_1)) {
                    if (newProgress >= 30 && newProgress % 20 == 0) {
                        webView.loadUrl("javascript:" + mJs_1);
                    }
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String s) {

            }
        });
        mX5WebView.mBaseWebViewClient.setWebViewClientListener(new BaseX5Webview.OnWebViewClientListener() {
            @Override
            public void shouldOverrideUrlLoading(WebView webView, String url) {
                webviewReplaceTag(webView, url);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                if (!TextUtils.isEmpty(mJs_2)) {
                    webView.loadUrl("javascript:" + mJs_2);
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
        loadSvipUrl();
    }

    private void loadSvipUrl() {//todo  改的
        if (mX5WebView != null) {
            LogUtils.d("全屏参数   " + mDefaultVideoScreen);
            mX5WebView.enablePageVideoFunc(mDefaultVideoScreen);
            mX5WebView.loadUrl(mSvipPlayUrl);
        }
    }


    /**
     * shouldOverrideUrlLoading(WebView webView, String url) 中
     *
     * @param webView
     * @param url
     */
    private void webviewReplaceTag(WebView webView, String url) {
        if (TextUtils.isEmpty(url) || !url.startsWith("http") || url.contains("download_general")) {
            return;
        } else if (url.contains(Constant.URL_REPLACE_uu_svip1) || url.contains(Constant.URL_REPLACE_uu_svip2)) {//svip播放;
            String newUrl = url.replace(Constant.URL_REPLACE_uu_svip1, "").replace(Constant.URL_REPLACE_uu_svip2, "");
            //showLoadingDialogSvip();
            HttpWorkUtils.getSvipPaly(intLine, mHtmltitle, newUrl, mVweb, Constant.TAG_SVIP_PLAY_AD_YES);
        } else if (url.contains(Constant.URL_REPLACE_uu_new1) || url.contains(Constant.URL_REPLACE_uu_new2)) {//打开新的webview
            String newUrl = url.replace(Constant.URL_REPLACE_uu_new1, "").replace(Constant.URL_REPLACE_uu_new2, "");
            String js_1 = (String) SPUtils.getInstance().getString(SpField.SP_JS_1, "");
            String js_2 = (String) SPUtils.getInstance().getString(SpField.SP_JS_2, "");
            //AdActivity.toAdActivity(SvippalyActivity.this, newUrl, mVwebName, js_1, js_2, mUser_agent);
        } else if (url.contains(Constant.URL_REPLACE_uu_down1) || url.contains(Constant.URL_REPLACE_uu_down2)) {
            url = url.replace(Constant.URL_REPLACE_uu_down1, "").replace(Constant.URL_REPLACE_uu_down2, "");

        } else {
            webView.loadUrl(url);
        }
    }

    private WebResourceResponse getWebResourceResponse(String url) {
        if (url != null) {
            if (isShield_Src) {
                if (BaseX5Webview.isHasKeyword(mKeywordArray, url)) {
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


    @Override
    public void receiveEventBus(EventObject eventObject) {
        super.receiveEventBus(eventObject);
        if (isResume) {
            String tag = eventObject.tag;
            if (tag.equals(EventObject.TAG_ANTHOLOGYDIALOG)) {//是选集
                int position = (int) eventObject.object;
                if (mAnthologyEntityList != null) {
                    AnthologyEntity entity = mAnthologyEntityList.get(position);
                    showWaitingDialog();
                    intLine = 2;
                    mHtmlurl = entity.url;
                    mHtmltitle = entity.title;
                    mIsClickAnthology = true;//是选集
                    HttpWorkUtils.getSvipPaly(1, mHtmltitle, mHtmlurl, mVweb, Constant.TAG_SVIP_PLAY_AD_YES);////sVip播放
                }
            } else {
                if (tag.startsWith(HttpWorkUtils.HTTP_TAG_WEB_SVIP_GET_PLAY_URL)
                        && (tag.endsWith(Constant.TAG_SVIP_PLAY_AD_YES) || tag.endsWith(Constant.TAG_SVIP_PLAY_AD_NO))) {
                    dismissWaitingDialog();
                    SvipplayEntity event = (SvipplayEntity) eventObject.object;
                    if (event != null) {
                        String play_type = event.play_type;
                        String svip_ad_open = event.svip_ad_open;
                        if (!TextUtils.isEmpty(play_type) && play_type.equals("1")) {//webview播放
                            mSvipPlayUrl = event.url;
                            LogUtils.d("mSvipPlayUrl--->>" + mSvipPlayUrl);
                            mJs_1 = event.js_1;
                            mJs_2 = event.js_2;
                            mUser_agent = event.user_agent;
                            mDown_url = event.down_url;
                            String shield_src = event.shield_src;
                            String release_src = event.release_src;
                            shieldOrrelease(shield_src, release_src);
                            if (mTvToptitle != null && mHtmltitle != null && mAnthologyEntityList != null) {
                                mTvToptitle.setText(mHtmltitle);
                            }
                            if (!TextUtils.isEmpty(svip_ad_open) && svip_ad_open.equals("1")) {//1 是开启广告
                                if (tag.endsWith(Constant.TAG_SVIP_PLAY_AD_YES)) {
                                    startShowAdDialog();
                                }
                            }
                            mDefaultVideoScreen = event.defaultVideoScreen;
                            loadSvipUrl();
                        } else if (!TextUtils.isEmpty(play_type) && play_type.equals("2")) {//原生播放
                            if (tag.endsWith(Constant.TAG_SVIP_PLAY_AD_YES)) {
                                svip_ad_open = "1";
                            } else {
                                svip_ad_open = "0";
                            }
                            VideoPlayerActivity.startActivity(this, event.play_url, mHtmltitle, Constant.VIDEO_TYPE_HTTP,
                                    mHtmltitle, mHtmlurl, "0", true, svip_ad_open, event, intLine);
                            finish();
                        }
                        if (tag.endsWith(Constant.TAG_SVIP_PLAY_AD_NO)) {
                            ToastUtils.showShort("刷新播放成功, 若还不能播放,请尝试再次“刷新播放”");
                        }
                    }
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mX5WebView != null) {
            mX5WebView.removeAllViews();
            mX5WebView.destroy();
            mX5WebView = null;
            if (mRl != null) {
                mRl.removeAllViews();
            }
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        System.gc();
    }


    /**
     * 进来时原生广告
     */
    private void startShowAdDialog() {
        if (mVideoPlayerAdDialog != null) {
            mVideoPlayerAdDialog.dismissAllowingStateLoss();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().remove(mVideoPlayerAdDialog).commitNowAllowingStateLoss();
            mVideoPlayerAdDialog = null;
        }
        if (mVideoPlayerAdDialog == null) {
            mVideoPlayerAdDialog = new VideoPlayerAdDialog();
        }
        mVideoPlayerAdDialog.show(getSupportFragmentManager(), "startShowAdDialog");
    }

}

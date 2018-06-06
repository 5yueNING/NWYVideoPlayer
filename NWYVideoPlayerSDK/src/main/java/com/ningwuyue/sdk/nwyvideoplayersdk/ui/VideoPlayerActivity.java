package com.ningwuyue.sdk.nwyvideoplayersdk.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.base.BaseActivity;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.Constant;
import com.ningwuyue.sdk.nwyvideoplayersdk.dialog.AdDialog;
import com.ningwuyue.sdk.nwyvideoplayersdk.dialog.AnthologyDialog;
import com.ningwuyue.sdk.nwyvideoplayersdk.dialog.VideoPlayerAdDialog;
import com.ningwuyue.sdk.nwyvideoplayersdk.http.httpwork.HttpWorkUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.AnthologyEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.SvipplayEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.LogUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.ToastUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.widget.MarqueeTextView;
import com.ningwuyue.sdk.nwyvideoplayersdk.widget.SampleVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Random;


public class VideoPlayerActivity extends BaseActivity implements View.OnClickListener {
    private static final String BUNDLE_VWEB_KEY = "bundle_vweb_key";
    private static final String BUNDLE_HTMLURL_KEY = "bundle_htmlurl_key";
    private static final String BUNDLE_HTMLTITLE_KEY = "bundle_htmltitle_key";
    private static final int REQUEST_CODE_PLAY = 44;
    private static final String BUNDLE_KEY_URL = "bundle_key_url";
    private static final String BUNDLE_KEY_TITLE = "bundle_key_title";
    private static final String BUNDLE_KEY_VIDEO_TYPE = "bundle_key_video_type";
    private static final String BUNDLE_SVIPENTITY_KEY = "bundle_svipentity_key";
    private static final String BUNDLE_KEY_IS_SVIP_BOOLEAN = "bundle_key_is_svip_boolean";//是否是svip
    private static final String BUNDLE_KEY_OPEN_AD = "bundle_key_open_ad";//是否打开广告
    private static final String BUNDLE_KEY_INTLINE = "bundle_key_intline";//第几条线路
    private static final long DELAYMILLIS_HIDE = 5000;
    private static final long POSITION_REFRESH_TIME = 500;
    private static final long DELAYMILLIS_LOAD_TIME = 1000;
    private static final int WHAT_PROGRESS_LOADING = 3333;
    private static final int WHAT_OPEN_AD = 4444;


    private volatile boolean isFullScreen = false;

    private String mUrl;
    private String mTitle;
    private String mVideoType;
    private int intLine = 2;

    private boolean isResume = false, mIsShowAdDialog = false, mIsPlayEeorr = false;
    ;
    private boolean isSvipPlayer = true;//是否是svip接口播放

    private String mVweb;
    private String mHtmltitle;
    private String mHtmlurl;//请求的url
    private ArrayList<AnthologyEntity> mAnthologyEntityList;

    private OrientationUtils mOrientationUtils = null;
    private int mLoadingProgerss;//总时长
    private int mCurrentPositionWhenPlaying;
    private VideoPlayerAdDialog mVideoPlayerAdDialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_PROGRESS_LOADING:
                    if (mVideoPlayer != null) {
                        Random random = new Random();
                        int i = random.nextInt(5);
                        mLoadingProgerss += i;
                        random = null;
                        if (mLoadingProgerss < 100) {
                            StringBuffer sb = new StringBuffer();
                            sb.append("缓冲中...").append(mLoadingProgerss).append(" %");
                            if (mVideoPlayer != null) {
                                mVideoPlayer.setTvLoadingText(sb.toString());
                            }
                            sb = null;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(WHAT_PROGRESS_LOADING, DELAYMILLIS_LOAD_TIME);
                            }
                        } else {
                            if (mHandler != null) {
                                mLoadingProgerss = 0;
                                mHandler.removeMessages(WHAT_PROGRESS_LOADING);
                            }
                        }
                    }
                    break;
                case WHAT_OPEN_AD:
                    startShowAdDialog();
                    break;
                default:
                    break;
            }
        }
    };
    private boolean mIsPrepared;
    private MarqueeTextView mTvMarquee;
    private TextView mTvTitle;
    private SampleVideo mVideoPlayer;
    private TextView mTvTopFlushBtn;
    private TextView mTvFeedbackBtn;
    private TextView mTvAnthologyBtn;
    private RelativeLayout mRlTopBar;
    //private TextView mTvVideoName;

    /**
     * @param context
     * @param url       播放路径
     * @param title     电影标题
     * @param videoType 电影类型----网络或者本地
     * @param htmlTitle svip播放时的网页标题
     * @param htmlUrl   svip播放时的网页url
     * @param vweb      svip平台号
     * @param isSvip    是否是svip接口播放
     * @param open_ad   是否打开广告  1打开  0关闭
     * @param entity    svip实体类
     */
    public static void startActivity(Context context, String url, String title,
                                     String videoType, String htmlTitle,
                                     String htmlUrl, String vweb, boolean isSvip,
                                     String open_ad,  SvipplayEntity entity) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_HTMLTITLE_KEY, htmlTitle);
        bundle.putString(BUNDLE_HTMLURL_KEY, htmlUrl);
        bundle.putString(BUNDLE_VWEB_KEY, vweb);
        bundle.putString(BUNDLE_KEY_URL, url);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        bundle.putString(BUNDLE_KEY_VIDEO_TYPE, videoType);
        bundle.putBoolean(BUNDLE_KEY_IS_SVIP_BOOLEAN, isSvip);
        bundle.putString(BUNDLE_KEY_OPEN_AD, open_ad);
        bundle.putParcelable(BUNDLE_SVIPENTITY_KEY, entity);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 这个方法是svipActivity刷新播放调用的
     *
     * @param context
     * @param url
     * @param title
     * @param videoType
     * @param htmlTitle
     * @param htmlUrl
     * @param vweb
     * @param isSvip
     * @param open_ad
     * @param entity
     * @param intLine
     */
    public static void startActivity(Context context, String url, String title,
                                     String videoType, String htmlTitle,
                                     String htmlUrl, String vweb, boolean isSvip,
                                     String open_ad,  SvipplayEntity entity, int intLine) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_HTMLTITLE_KEY, htmlTitle);
        bundle.putString(BUNDLE_HTMLURL_KEY, htmlUrl);
        bundle.putString(BUNDLE_VWEB_KEY, vweb);
        bundle.putString(BUNDLE_KEY_URL, url);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        bundle.putString(BUNDLE_KEY_VIDEO_TYPE, videoType);
        bundle.putBoolean(BUNDLE_KEY_IS_SVIP_BOOLEAN, isSvip);
        bundle.putString(BUNDLE_KEY_OPEN_AD, open_ad);
        bundle.putParcelable(BUNDLE_SVIPENTITY_KEY, entity);
        bundle.putInt(BUNDLE_KEY_INTLINE, intLine);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_topback) {
            finish();
        } else if (id == R.id.tv_top_flush_btn) {
            doHttp(intLine++, mHtmltitle, mHtmlurl, mVweb, Constant.TAG_SVIP_PLAY_AD_NO);
        } else if (id == R.id.tv_anthology_btn) {
            if (mAnthologyEntityList != null && mAnthologyEntityList.size() > 0) {
                AnthologyDialog dialog = new AnthologyDialog();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(AnthologyDialog.BUNDLE_KEY_LIST, mAnthologyEntityList);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "anthologydialog");
            }
        } else if (id == R.id.tv_feedback_btn) {
            // showFeedbackDialog();
        }
    }


    //设置播放源
    private void setPlayerUrl(boolean isAutoPlayer) {
        if (!TextUtils.isEmpty(mVideoType) && mVideoPlayer != null) {
            mIsPrepared = false;

            if (mVideoType.equals(Constant.VIDEO_TYPE_HTTP)) {//网络
                if (!TextUtils.isEmpty(mUrl)) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(WHAT_PROGRESS_LOADING, DELAYMILLIS_LOAD_TIME);
                    }
                    if (mUrl != null && mUrl.contains(".m3u8")) {
                        mVideoPlayer.setUp(mUrl, false, mTitle);
                    } else {
                        mVideoPlayer.setUp(mUrl, false, mTitle);
                    }
                    mVideoPlayer.startPlayLogic();
                }
            } else if (mVideoType.equals(Constant.VIDEO_TYPE_NATIVE)) {//本地
                if (!TextUtils.isEmpty(mUrl)) {
                    mVideoPlayer.setUp(mUrl, false, mTitle);
                    mVideoPlayer.startPlayLogic();
                    if (mTvMarquee != null) {
                        mTvMarquee.setVisibility(View.GONE);
                    }
                }
            }
            LogUtils.d("播放url--" + mVideoType + "---->>" + mUrl);
        }
    }


    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEventBus(EventObject eventObject) {
        if (isResume) {
            String tag = eventObject.tag;
            if (tag.equals(EventObject.TAG_CLICK_COLES_AD)) {//关闭广告
                if (mVideoPlayer != null && mIsPrepared) {
                    if (mVideoPlayer != null) {
                        mVideoPlayer.getStartButton().performClick();
                    }
                    startPlay();
                }
            } else if (tag.equals(EventObject.TAG_AD_CLOSE_RESUME_PLAYER)) {//暂停播放时,弹出的广告关闭,发送信息
                mIsShowAdDialog = false;
                if (mVideoPlayer != null) {
                    mVideoPlayer.getStartButton().performClick();
                }
                startPlay();
            } else if (tag.equals(EventObject.TAG_ANTHOLOGYDIALOG)) {//选集
                int mAnthologyPosition = (int) eventObject.object;
                if (mAnthologyEntityList != null) {
                    try {
                        AnthologyEntity entity = mAnthologyEntityList.get(mAnthologyPosition);
                        intLine = 2;
                        mHtmlurl = entity.url;
                        mHtmltitle = entity.title;
                        doHttp(1, mHtmltitle, mHtmlurl, mVweb, Constant.TAG_SVIP_PLAY_AD_YES);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            } else if (tag.startsWith(HttpWorkUtils.HTTP_TAG_WEB_SVIP_GET_PLAY_URL)
                    && (tag.endsWith(Constant.TAG_SVIP_PLAY_AD_NO) || tag.endsWith(Constant.TAG_SVIP_PLAY_AD_YES))) {
                dismissWaitingDialog();
                SvipplayEntity entity = (SvipplayEntity) eventObject.object;
                if (entity != null) {
                    String play_type = entity.play_type; //LogUtils.d("kuxunyou", "AdvancedPlayActivity----" + event.url);
                    String svip_ad_open = entity.svip_ad_open;
                    if (entity.type.endsWith(Constant.TAG_SVIP_PLAY_AD_NO)) {//刷新的
                        entity.svip_ad_open = "0";
                        ToastUtils.showShort("刷新播放成功, 若还不能播放,请尝试再次“刷新播放”");
                    }

                    if (!TextUtils.isEmpty(play_type) && play_type.equals("1")) {//webview播放
                        SvippalyActivity.startAdvancedplayActivitytoSvipAvtivity(this, entity, "0", mHtmltitle, mHtmlurl, mHtmltitle, intLine);
                        finish();
                    } else if (!TextUtils.isEmpty(play_type) && play_type.equals("2")) {//原生播放
                        mTitle = mHtmltitle;
                        mUrl = entity.play_url;
                        LogUtils.d("kuxunyou", "原生播放svip--->>" + mUrl);
                        if (!TextUtils.isEmpty(svip_ad_open) && svip_ad_open.equals("1")) {//1 是开启广告
                            if (entity.type.endsWith(Constant.TAG_SVIP_PLAY_AD_YES)) {
                                startShowAdDialog();
                            }
                        }
                        if (mVideoPlayer != null) {
                            setPlayerUrl(true);//刷新播放后, 从新请求api,播放新的url
                        }
                    }
                }
            }
        }
    }

    private void doHttp(int intLine, String htmltitle, String htmlurl, String vweb, String tagSvipPlayAdYes) {
        showWaitingDialog();
        HttpWorkUtils.getSvipPaly(intLine, htmltitle, htmlurl, vweb, tagSvipPlayAdYes);////sVip播放
    }


    //// 恢复播放
    private void startPlay() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onVideoResume();
        }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrientationUtils != null) {
            mOrientationUtils.releaseListener();
        }
        GSYVideoManager.clearAllDefaultCache(getApplicationContext());
        GSYVideoPlayer.releaseAllVideos();
        if (mVideoPlayer != null) {
            mVideoPlayer.setStandardVideoAllCallBack(null);
            mVideoPlayer.clearAnimation();
            mVideoPlayer.clearCurrentCache();
            mVideoPlayer.clearDisappearingChildren();
            mVideoPlayer.clearThumbImageView();
            mVideoPlayer.removeAllViews();
            mVideoPlayer = null;
        }
        System.gc();
    }


    /**
     * 接收广告返回的
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PLAY && resultCode == REQUEST_CODE_PLAY) {
            startPlay();//广告回来后
        }
    }

    @Override
    protected int setLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//c常亮
        return R.layout.nwyvideo_activity_video_player;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        getBundleData();
        configView();

        setVisibility();
        initVideoPlayer();
        if (!TextUtils.isEmpty(mTitle)) {
            mTvTitle.setText(mTitle);
        }
        mTvMarquee.setScrollMode(MarqueeTextView.SCROLL_FAST);
    }

    private void configView() {
        TextView tv = findViewById(R.id.tv);
        RelativeLayout mRlTopBack = findViewById(R.id.rl_topback);
        mTvTitle = findViewById(R.id.tv_toptitle);
        mTvTopFlushBtn = findViewById(R.id.tv_top_flush_btn);
        mRlTopBar = findViewById(R.id.rl_top);
        mVideoPlayer = findViewById(R.id.video_player);
        mTvAnthologyBtn = findViewById(R.id.tv_anthology_btn);
        mTvFeedbackBtn = findViewById(R.id.tv_feedback_btn);
        mTvMarquee = findViewById(R.id.tv_marquee);
        RelativeLayout mRlRootView = findViewById(R.id.rl_root);

    }

    /**
     * 获取上一界面传来的数据
     */
    private void getBundleData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SvipplayEntity svipplayEntity = extras.getParcelable(BUNDLE_SVIPENTITY_KEY);
            if (svipplayEntity != null) {
                mAnthologyEntityList = (ArrayList<AnthologyEntity>) svipplayEntity.anthologyEntityList;
                //LogUtils.e("kuxunyou", "shield_src---" + shield_src + "---release_src----" + release_src);
            }
            mUrl = extras.getString(BUNDLE_KEY_URL, "");

            mTitle = extras.getString(BUNDLE_KEY_TITLE, "");
            mVideoType = extras.getString(BUNDLE_KEY_VIDEO_TYPE, "");
            mVweb = extras.getString(BUNDLE_VWEB_KEY, "");
            mHtmltitle = extras.getString(BUNDLE_HTMLTITLE_KEY, "");
            mHtmlurl = extras.getString(BUNDLE_HTMLURL_KEY, "");
            isSvipPlayer = extras.getBoolean(BUNDLE_KEY_IS_SVIP_BOOLEAN, true);
            String svip_ad_open = extras.getString(BUNDLE_KEY_OPEN_AD, "");
            intLine = extras.getInt(BUNDLE_KEY_INTLINE, 2);
            if (!TextUtils.isEmpty(svip_ad_open) && svip_ad_open.equals("1")) {//1 是开启广告
                if (mHandler != null) {//开启广告
                    mHandler.sendEmptyMessageDelayed(WHAT_OPEN_AD, 100);
                }
            }
        }
    }

    /**
     * 初始化当前页面的显示影藏
     */
    private void setVisibility() {
        if (!isSvipPlayer) {
            mTvTopFlushBtn.setVisibility(View.GONE);
            mTvAnthologyBtn.setVisibility(View.GONE);
            mTvFeedbackBtn.setVisibility(View.GONE);
        } else {
            if (mAnthologyEntityList != null && mAnthologyEntityList.size() > 0) {
                mTvAnthologyBtn.setVisibility(View.VISIBLE);
            } else {
                mTvAnthologyBtn.setVisibility(View.GONE);
            }
            mTvFeedbackBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initVideoPlayer() {
        //是否可以滑动调整
        // GSYVideoType.enableMediaCodec();//使能硬解码，播放前设置
        //GSYVideoType.enableMediaCodecTexture();//使能硬解码渲染优化

        mVideoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        mVideoPlayer.getBackButton().setVisibility(View.VISIBLE);

        mVideoPlayer.setIsTouchWiget(true);
        mVideoPlayer.setSeekRatio(20);//滑动快进的比例，默认1。数值越大，滑动的产生的seek越小
        mVideoPlayer.setShowFullAnimation(false);
        mVideoPlayer.setRotateViewAuto(false);
        mVideoPlayer.setDismissControlTime((int) DELAYMILLIS_HIDE);//设置触摸显示控制ui的消失时间，默认2500
        mVideoPlayer.setShowPauseCover(true);//否需要加载显示暂停的cover图片
        mVideoPlayer.setRotateWithSystem(false);
        mVideoPlayer.setNeedShowWifiTip(false);//是否需要显示流量提示,默认true
        mVideoPlayer.setThumbPlay(false);//是否点击封面可以播放
        mIsPrepared = false;

        setPlayerUrl(true);//初始化数据;
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer);
        mVideoPlayer.setVideoAllCallBack(new VideoAllCallBack() {
            @Override////加载成功，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
            public void onPrepared(String url, Object... objects) {
                mIsPrepared = true;

                if (mTvMarquee != null) {
                    mTvMarquee.setVisibility(View.GONE);
                }
                LogUtils.d("加载成功 " + mVideoPlayerAdDialog.isVisible());
                if (mVideoPlayerAdDialog != null && mVideoPlayerAdDialog.isVisible()) {
                    pausePlay();
                } else {
                    if (mIsPlayEeorr) {
                        if (mVideoPlayer != null) {
                            if (mCurrentPositionWhenPlaying > 0) {
                                mVideoPlayer.seekTo(mCurrentPositionWhenPlaying);
                                LogUtils.d("错误-定位到--" + mCurrentPositionWhenPlaying);
                            }
                        }
                    }
                    mIsPlayEeorr = false;
                    if (mHandler != null) {
                        mHandler.removeMessages(WHAT_PROGRESS_LOADING);
                    }
                    if (mVideoPlayer != null) {
                        mVideoPlayer.setTvLoadingText("缓冲中请稍候...");
                    }
                }
            }

            @Override///点击了开始按键播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
            public void onClickStartIcon(String url, Object... objects) {
                LogUtils.e("点击了开始按键播放");
            }

            @Override////点击了错误状态下的开始按键，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
            public void onClickStartError(String url, Object... objects) {
                LogUtils.e("点击了错误状态下的开始按键");
                if (mVideoPlayer != null) {
                    mLoadingProgerss = 50;
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(WHAT_PROGRESS_LOADING);
                    }
                }
            }

            @Override////点击了播放状态下的开始按键--->停止，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
            public void onClickStop(String url, Object... objects) {
                LogUtils.d("点击了播放状态下的开始按键");
                //playerOrPause();
                showAdDialog();
            }

            @Override////点击了全屏播放状态下的开始按键--->停止，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
            public void onClickStopFullscreen(String url, Object... objects) {
                LogUtils.d("点击了全屏播放状态下的开始按键");
            }

            @Override////点击了暂停状态下的开始按键--->播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
            public void onClickResume(String url, Object... objects) {
                LogUtils.d("点击了暂停状态下的开始按键");
            }

            @Override
            public void onClickResumeFullscreen(String url, Object... objects) {
                LogUtils.d("点击了全屏暂停状态下的开始按键");
            }

            @Override
            public void onClickSeekbar(String url, Object... objects) {
                LogUtils.d("点击了空白弹出seekbar");
            }

            @Override
            public void onClickSeekbarFullscreen(String url, Object... objects) {
                LogUtils.d("点击了全屏的seekbar");
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                LogUtils.e("播放完了");
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                LogUtils.e("进去全屏");
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                LogUtils.e("退出全屏");
            }

            @Override
            public void onQuitSmallWidget(String url, Object... objects) {
                LogUtils.e("进入小窗口");
            }

            @Override
            public void onEnterSmallWidget(String url, Object... objects) {
                LogUtils.e("退出小窗口");
            }

            @Override
            public void onTouchScreenSeekVolume(String url, Object... objects) {
                LogUtils.d("点击了空白弹出seekbar");
            }

            @Override
            public void onTouchScreenSeekPosition(String url, Object... objects) {
                LogUtils.d("触摸调整进度");
            }

            @Override
            public void onTouchScreenSeekLight(String url, Object... objects) {
                LogUtils.d("触摸调整亮度");
            }

            @Override
            public void onPlayError(String url, Object... objects) {
                mIsPlayEeorr = true;
                if (mHandler != null) {
                    mHandler.removeMessages(WHAT_PROGRESS_LOADING);
                }
                if (mTvMarquee != null) {
                    mTvMarquee.setVisibility(View.GONE);
                }
                LogUtils.e("播放错误---》" + mCurrentPositionWhenPlaying);
                if (mCurrentPositionWhenPlaying == 0) {
                    ToastUtils.showLong("影片地址错误,无法链接播放资源!!!");
                } else {
                    ToastUtils.showLong("播放出错,请点击继续播放");
                }
            }

            @Override
            public void onClickStartThumb(String url, Object... objects) {
                LogUtils.d("点击了空白区域开始播放");
            }

            @Override
            public void onClickBlank(String url, Object... objects) {
                LogUtils.d("点击了播放中的空白区域");
                setTvFeedbackBtnTvAnthologyBtnVisibility();//点击时显示隐藏--- 选集和反馈
            }

            @Override
            public void onClickBlankFullscreen(String url, Object... objects) {
                LogUtils.d("点击了全屏播放中的空白区域");
            }
        });

        mVideoPlayer.setGSYVideoProgressListener(new GSYVideoProgressListener() {
            @Override
            public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
                mCurrentPositionWhenPlaying = currentPosition;
            }
        });

        //设置返回按键功能
        mVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        mVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrientationUtils != null) {
                    mOrientationUtils.resolveByClick();
                }
                portraitOrLandscape();
            }
        });
    }

    //// 暂停播放
    private void pausePlay() {
        if (mVideoPlayer != null) {
            mVideoPlayer.onVideoPause();
        }
    }

    /**
     * 暂停时显示的原生广告
     */
    private void showAdDialog() {
        AdDialog adDialog = new AdDialog();
        mIsShowAdDialog = true;
        adDialog.show(getSupportFragmentManager(), "showAdDialog");
    }

    //是svip播放时,  设置显示隐藏
    private void setTvFeedbackBtnTvAnthologyBtnVisibility() {
        if (isSvipPlayer) {
            if (mVideoPlayer != null) {
                View startButton = mVideoPlayer.getStartButton();
                if (startButton != null) {
                    int visibility = startButton.getVisibility();
                    if (visibility == View.VISIBLE) {
                        if (mTvFeedbackBtn != null) {
                            mTvFeedbackBtn.setVisibility(View.VISIBLE);
                        }
                        if (mAnthologyEntityList != null && mAnthologyEntityList.size() > 0) {
                            if (mTvAnthologyBtn != null) {
                                mTvAnthologyBtn.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (mTvFeedbackBtn != null) {
                            mTvFeedbackBtn.setVisibility(View.GONE);
                        }

                        if (mAnthologyEntityList != null && mAnthologyEntityList.size() > 0) {
                            if (mTvAnthologyBtn != null) {
                                mTvAnthologyBtn.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (mOrientationUtils != null && mOrientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if (mVideoPlayer != null) {
                mVideoPlayer.getFullscreenButton().performClick();
            }
            return;
        }
        //释放所有
        if (mVideoPlayer != null) {
            mVideoPlayer.setStandardVideoAllCallBack(null);
        }
        GSYVideoPlayer.releaseAllVideos();
        finish();
    }

    /**
     * 竖屏或者横屏
     */
    private void portraitOrLandscape() {
        if (isFullScreen) {
            cancelFullScreen();
            if (mRlTopBar != null) {
                mRlTopBar.setVisibility(View.VISIBLE);
            }
        } else {
            setFullScreen();
            if (mRlTopBar != null) {
                mRlTopBar.setVisibility(View.GONE);
            }
        }
        isFullScreen = !isFullScreen;
    }

    /**
     * 取消全屏
     */
    public void cancelFullScreen() {
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 设置全屏
     */
    public void setFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        if (!mIsShowAdDialog) {
            startPlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
        pausePlay();
    }


}

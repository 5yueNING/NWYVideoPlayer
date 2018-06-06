package com.ningwuyue.sdk.nwyvideoplayersdk.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by shuyu on 2016/12/7.
 * 注意
 * 这个播放器的demo配置切换到全屏播放器
 * 这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
 */

public class SampleVideo extends StandardGSYVideoPlayer {
    private String mUrl = null;
    private TextView mTvLoading;


    /**
     * 1.5.0开始加入，如果需要不同布局区分功能，需要重载
     */
    public SampleVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleVideo(Context context) {
        super(context);
    }

    public SampleVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.nwyvideo_sample_video;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        initView();
        //控制着允许丢帧的范围。可以通过修改 framedrop 的数值来解决不同步的问题，framedrop 是在视频帧处理不过来的时候丢弃一些帧达到同步的效果。
        VideoOptionModel videoOptionModel = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 50);

        VideoOptionModel videoOptionMode2 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        //为什么拖动视屏会弹回来，因为ijk的FFMPEG对关键帧问题。
        VideoOptionModel videoOptionMode6 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        // 视频的话，设置100帧即开始播放
        VideoOptionModel videoOptionMode8 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 100);
        // 设置缓冲区为100KB，目前我看来，多缓冲了4秒
        VideoOptionModel videoOptionMode9 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024);

        VideoOptionModel videoOptionMode3 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
        VideoOptionModel videoOptionMode4 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1);
        VideoOptionModel videoOptionMode5 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max_delay", 0);
        //重连模式，如果中途服务器断开了连接，让它重新连接
        VideoOptionModel videoOptionMode7 = new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);

        List<VideoOptionModel> list = new ArrayList<>();
        list.add(videoOptionModel);
        list.add(videoOptionMode2);
        list.add(videoOptionMode3);
        list.add(videoOptionMode4);
        list.add(videoOptionMode5);
        list.add(videoOptionMode6);
        list.add(videoOptionMode7);
        list.add(videoOptionMode8);
        list.add(videoOptionMode9);

        GSYVideoManager.instance().setOptionModelList(list);
    }

    private void initView() {
        mTvLoading = findViewById(R.id.tv_loading);
    }

    public void setTvLoadingText(String text) {
        if (mTvLoading != null && text != null) {
            mTvLoading.setText(text);
        }
    }
    @Override
    protected void updateStartImage() {
        if (mIfCurrentIsFullscreen) {
            if(mStartButton instanceof ImageView) {
                ImageView imageView = (ImageView) mStartButton;
                if (mCurrentState == CURRENT_STATE_PLAYING) {
                    imageView.setImageResource(R.drawable.video_click_pause_selector);
                } else if (mCurrentState == CURRENT_STATE_ERROR) {
                    imageView.setImageResource(R.drawable.video_click_play_selector);
                } else {
                    imageView.setImageResource(R.drawable.video_click_play_selector);
                }
            }
        } else {
            super.updateStartImage();
        }
    }

    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        SampleVideo sampleVideo = (SampleVideo) super.startWindowFullscreen(context, actionBar, statusBar);
        sampleVideo.mUrl = mUrl;
        sampleVideo.mTvLoading = mTvLoading;
        //sampleVideo.resolveTransform();
        sampleVideo.resolveTypeUI();
        //sampleVideo.resolveRotateUI();
        //这个播放器的demo配置切换到全屏播放器
        //这只是单纯的作为全屏播放显示，如果需要做大小屏幕切换，请记得在这里耶设置上视频全屏的需要的自定义配置
        //比如已旋转角度之类的等等
        //可参考super中的实现
        return sampleVideo;
    }

    /**
     * 推出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    @Override
    protected void resolveNormalVideoShow(View oldF, ViewGroup vp, GSYVideoPlayer gsyVideoPlayer) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer);
        if (gsyVideoPlayer != null) {
            SampleVideo sampleVideo = (SampleVideo) gsyVideoPlayer;
            mUrl = sampleVideo.mUrl;
            sampleVideo.mTvLoading = mTvLoading;
            setUp(mUrl, mCache, mCachePath, mTitle);
            resolveTypeUI();
        }
    }

    /**
     * 处理显示逻辑
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        super.onSurfaceTextureAvailable(surface, width, height);
        resolveRotateUI();
    }

    /**
     * 旋转逻辑
     */
    private void resolveRotateUI() {
        if (!mHadPlay) {
            return;
        }
        mTextureView.setRotation(mRotate);
        mTextureView.requestLayout();
    }

    /**
     * 显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    private void resolveTypeUI() {
        if (!mHadPlay) {
            return;
        }
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        changeTextureViewShowType();
        if (mTextureView != null) {
            mTextureView.requestLayout();
        }
    }

    /**
     * 设置播放URL
     *
     * @param url           播放url
     * @param cacheWithPlay 是否边播边缓存
     * @param title         title
     * @return
     */
    public boolean setUp(String url, boolean cacheWithPlay, String title) {
        this.mUrl = url;
        return super.setUp(url, cacheWithPlay, title);
    }
}

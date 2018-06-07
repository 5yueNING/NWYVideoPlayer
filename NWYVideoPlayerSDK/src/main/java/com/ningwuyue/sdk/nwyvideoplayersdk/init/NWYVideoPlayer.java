package com.ningwuyue.sdk.nwyvideoplayersdk.init;

import android.app.Application;
import android.content.Context;

import com.ningwuyue.sdk.nwyvideoplayersdk.model.SvipplayEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.ui.SvippalyActivity;
import com.ningwuyue.sdk.nwyvideoplayersdk.ui.VideoPlayerActivity;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.other.OkGoUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.other.TBSWebViewUtils;


/**
 * Created by ${武跃} on 2018/6/4.
 * 一句话简介：---
 */

public class NWYVideoPlayer {
    private static NWYVideoPlayer mNWYVideoPlayer = null;
    private Application mApplication = null;


    private NWYVideoPlayer() {
    }

    public static NWYVideoPlayer getInstance() {
        if (mNWYVideoPlayer == null) {
            mNWYVideoPlayer = new NWYVideoPlayer();
        }
        return mNWYVideoPlayer;
    }


    public void init(Application application) {
        mApplication = application;
        getInstance();
        TBSWebViewUtils.init(application);
        OkGoUtils.init(application);
    }

    public Application getApplication() {
        return this.mApplication;
    }

    /**
     * @param context
     * @param event------svip实体类
     * @param vweb-----平台        号
     * @param htmlTitle----网页标题
     * @param htmlUrl------网页url
     * @param vwebName-----平台名
     */
    public void startSvipAvtivity(Context context, SvipplayEntity event, String vweb, String htmlTitle, String htmlUrl,
                     String vwebName){
        SvippalyActivity.startSvipAvtivity(context,event,vweb,htmlTitle,htmlUrl,vwebName);
    }

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
    public static void startVideoPlayerActivity(Context context, String url, String title,
                                     String videoType, String htmlTitle,
                                     String htmlUrl, String vweb, boolean isSvip,
                                     String open_ad,  SvipplayEntity entity) {
        VideoPlayerActivity.startActivity(context,url,title,videoType,htmlTitle,htmlUrl,vweb,isSvip,open_ad,entity);
    }

}
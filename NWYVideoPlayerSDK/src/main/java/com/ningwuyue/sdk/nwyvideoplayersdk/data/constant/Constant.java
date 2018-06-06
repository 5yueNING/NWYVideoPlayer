package com.ningwuyue.sdk.nwyvideoplayersdk.data.constant;

import com.ningwuyue.sdk.nwyvideoplayersdk.init.NWYVideoPlayer;

/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */

public class Constant {
    public static final long HTTP_TIMEOUT_DEFALUT = 30 * 1000;//默认超时时间
    public static final String TAG_HTTP_ERROR = "tag_http_error";
    public static final String TAG_HTTP_HIDE_WAITING_DIALOG ="tag_http_hide_waiting_dialog" ;


    public static final String PATH_CACHE_WEBVIEW = NWYVideoPlayer.getInstance().getApplication().getCacheDir().getAbsolutePath() + "/webcache";
    public static final String DEFAUT_USERAGENT = "Mozilla/5.0 (Linux; U; Android 5.1; zh-cn; OPPO R9tm Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko)Version/4.0 Chrome/37.0.0.0 MQQBrowser/7.6 Mobile Safari/537.36 Ykxia";
    public static final String DEFAUT_SHAREDPREFERENCES_NAME = NWYVideoPlayer.getInstance().getApplication().getPackageName()+"lib_sharedpreferences";

    public static final String URL_REPLACE_uu_ext1 = "uu-ext";//跳出到外部浏览器
    public static final String URL_REPLACE_uu_down1 = "uu-down";//调用内部下载
    public static final String URL_REPLACE_uu_svip2 = "UU-SVIP";//打开svip播放
    public static final String URL_REPLACE_uu_svip1 = "uu-svip";//打开svip播放
    public static final String URL_REPLACE_uu_new1 = "uu-new";//跳到新的webview
    public static final String URL_REPLACE_uu_new2 = "UU-NEW";//跳到新的webview
    public static final String URL_REPLACE_uu_ext2 = "UU-EXT";//跳出到外部浏览器
    public static final String URL_REPLACE_uu_down2 = "UU-DOWN";//调用内部下载

    public static final String VIDEO_TYPE_NATIVE = "video_type_native";//本地播放
    public static final String VIDEO_TYPE_HTTP = "video_type_http";//网络链接
    public static final String FROMTYPE_SVIPACTIVITY = "fromtype_SvipActivity";//从svip界面跳转过来

    public static final String TAG_SHIELD_SRC = "tag_shield_src";//拦截
    public static final String TAG_RELEAS_SRC = "tag_releas_src";//放行

    public static final String TAG_SVIP_PLAY_AD_YES = "tag_svip_play_ad_yes";//有弹窗广告
    public static final String TAG_SVIP_PLAY_AD_NO = "tag_svip_play_ad_no";//没有弹窗广告
}

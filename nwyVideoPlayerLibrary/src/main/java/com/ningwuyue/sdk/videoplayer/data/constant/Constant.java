package com.ningwuyue.sdk.videoplayer.data.constant;

import com.ningwuyue.sdk.videoplayer.init.NWYVideoPlayer;

/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */

public class Constant {
    public final static long HTTP_TIMEOUT_DEFALUT = 30 * 1000;//默认超时时间
    public static final String PATH_CACHE_WEBVIEW = NWYVideoPlayer.getInstance().getApplication().getCacheDir().getAbsolutePath() + "/webcache";

}

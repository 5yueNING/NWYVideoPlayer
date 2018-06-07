package com.ningwuyue.sdk.nwyvideoplayer;

import android.app.Application;

import com.ningwuyue.sdk.nwyvideoplayersdk.init.NWYVideoPlayer;

/**
 * Created by ${武跃} on 2018/6/7.
 * 一句话简介：---
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NWYVideoPlayer.getInstance().init(this);
    }
}

package com.ningwuyue.sdk.nwyvideoplayersdk.init;

import android.app.Application;

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

}
package com.ningwuyue.sdk.videoplayer.util.other;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by ${武跃} on 2018/6/4.
 * 一句话简介：---
 */

public class TBSWebViewUtils {

    public static void init(Application application) {
        if (application != null) {
            initTBSX5(application);
        }
    }


    //初始化腾讯X5内核
    private static void initTBSX5(Application application) {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            @Override
            public void onCoreInitFinished() {
            }

            ////x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            @Override
            public void onViewInitFinished(boolean arg0) {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(application, cb);
    }
}

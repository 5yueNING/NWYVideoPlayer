package com.ningwuyue.sdk.nwyvideoplayersdk.util.other;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.LogUtils;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by ${武跃} on 2018/6/5.
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


    /**
     * 打开外部浏览器
     *
     * @param url
     */
    public static  void openOutBrowser(Activity activity,String url) {
        if (!TextUtils.isEmpty(url)) {
            LogUtils.d("外跳url---" + url);
            try {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri parse = Uri.parse(url);
                if (parse != null) {
                    intent.setData(parse);
                    activity.startActivity(intent);
                }
            } catch (Exception e) {
                if (url.startsWith("alipay")) {
                    // ToastUtils.showShort("您还未安装支付宝");
                } else if (url.startsWith("weixin")) {
                    // ToastUtils.showShort("您还未安装微信");
                } else if (url.contains("taobao")) {
                    // ToastUtils.showShort("您还未安装淘宝");
                } else if (url.contains("mqqapi") || url.contains("mqq")) {
                    // ToastUtils.showShort("您还未安装QQ");
                } else if (url.contains("jdmobile")) {
                    // ToastUtils.showShort("您还未安装京东");
                } else if (url.contains("imeituan")) {
                    // ToastUtils.showShort("您还未安装美团");
                }
            }
        }
    }
}

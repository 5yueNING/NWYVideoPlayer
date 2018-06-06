package com.ningwuyue.sdk.nwyvideoplayersdk.model.event;

/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */

public class EventObject {
    public static final String TAG_CLICK_COLES_AD = "tag_click_coles_ad";//原生播放----关闭广告
    public static final String TAG_SEND_SMS_SUCCESS = "tag_send_sms_success";//验证码成功
    public static final String TAG_ANTHOLOGYDIALOG = "tag_anthology_dialog";//选集
    public static final String TAG_AD_CLOSE_RESUME_PLAYER = "tag_ad_close_resume_player";////暂停播放时,弹出的广告关闭,发送信息


    public boolean isCacheJson = false;
    public String tag = "";
    public Object object;
    public String param1;
    public String param2;
    public int position;

    public EventObject(String tag) {
        this.tag = tag;
    }

    public EventObject(String tag, String param1) {
        this.tag = tag;
        this.param1 = param1;
    }

    public EventObject(String tag, String param1, String param2) {
        this.tag = tag;
        this.param1 = param1;
        this.param2 = param2;
    }

    public EventObject(String tag, String param1, int position) {
        this.tag = tag;
        this.param1 = param1;
        this.position = position;
    }

    public EventObject(String tag, Object object) {
        this.tag = tag;
        this.object = object;
    }

    public EventObject(String tag, Object object, boolean isCacheJson) {
        this.isCacheJson = isCacheJson;
        this.tag = tag;
        this.object = object;
    }
}

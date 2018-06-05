package com.ningwuyue.sdk.videoplayer.model.event;

/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */

public class EventObject {
    public static final String TAG_REGISTER_SUCCESS = "tag_register_success";//注册成功
    public static final String TAG_SMS_CODE_SUCCESS = "tag_sms_code_success";//验证码


    public static final String TAG_EXIT_LOGIN_SUCCESS = "tag_exit_login_success";//退出登录
    public static final String TAG_GOTO_BOOKSELF = "tag_goto_bookself";//去书架
    public static final String TAG_GOTO_BOOKSTORE = "tag_goto_bookstore";//去书城
    public static final String TAG_LONG_CLICK_BOOKSELF = "tag_long_click_bookself";//书架 长按


    public static final String TAG_CLICK_AND_COLLECT = "tag_click_and_collect";//免费领券中-----点击领取
    public static final String TAG_CLICK_USE_CODE = "tag_click_use_code";//卡券列表---使用
    public static final String TAG_CLICK_BIND_PHONE = "tag_click_bind_phone";//验证码
    public static final String TAG_CLICK_UPDATE_PWD = "tag_click_update_pwd";//更改密码


    public static final String TAG_CLICK_BOOKMARK = "tag_click_bookmark";//书签--列表点击
    public static final String TAG_CLICK_POPUP_READING_ADDSHLF = "tag_click_popup_reading_addshlf";//阅读界面---退出弹出加入书架
    public static final String TAG_CLICK_DIALOG_CLEAR_READ_HISTORY_ITEM = "tag_click_dialog_clear_read_history_item";//清除阅读记录
    public static final String TAG_CLICK_DIALOG_CLEAR_READ_HISTORY_ALL = "tag_click_dialog_clear_read_history_all";//清除所有



    public boolean isCacheJson = false;
    public String tag;
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

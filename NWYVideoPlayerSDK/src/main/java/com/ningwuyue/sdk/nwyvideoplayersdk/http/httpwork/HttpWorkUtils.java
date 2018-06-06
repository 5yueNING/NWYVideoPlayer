package com.ningwuyue.sdk.nwyvideoplayersdk.http.httpwork;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.Constant;
import com.ningwuyue.sdk.nwyvideoplayersdk.http.api.Api;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.AnthologyEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.SvipplayEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HttpWorkUtils {
    public static final String HTTP_TAG_WEB_SVIP_GET_PLAY_URL = "http_tag_web_svip_get_play_url";

    public static void cancelHttp(String http_tag) {
        OkGo.getInstance().cancelTag(http_tag);
    }

    public static synchronized void doHttpWork(ArrayMap<String, String> arrayMap, String url, final String httpTag) {
        try {
            if (!TextUtils.isEmpty(url)) {
                OkGo.<String>post(url)
                        .tag(httpTag)
                        .params(arrayMap, true)
                        .execute(new com.lzy.okgo.callback.StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                if (response != null) {
                                    int code = response.code();
                                    if (code == 200) {
                                        httpSuccess(response.body(), httpTag);
                                    } else {
                                        httpError(code, response.getException(), httpTag);//成功,但code不等于200;
                                    }
                                }
                                otherHttpError(httpTag);//成功
                            }

                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);
                                if (response != null) {
                                    httpError(response.code(), response.getException(), httpTag);//失败
                                }
                                otherHttpError(httpTag);//失败
                            }
                        });
            }
        } catch (Exception e) {
            otherHttpError(httpTag);
            LogUtils.e("api请求异常" + e);
        }
    }


    //http请求成功
    private static void httpSuccess(String body, String httpTag) {
        LogUtils.d("api成功-->" + httpTag, body);
        if (!TextUtils.isEmpty(body)) {
            try {
                if (httpTag.startsWith(HTTP_TAG_WEB_SVIP_GET_PLAY_URL)) {
                    analysisSvipPaly(body,httpTag);
                }
            } catch (Exception e) {
                LogUtils.e("json异常" + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    //http请求错误
    private static void httpError(int code, Throwable exception, String httpTag) {
        if (exception != null) {
            LogUtils.e("api失败--" + httpTag + ">>>>  " + exception + ">>>  " + code);
            String error_msg = "";
            if (exception instanceof UnknownHostException) {//
                error_msg = "网络不可用, 请检查网络设置";
            }
            if (!TextUtils.isEmpty(error_msg)) {
                EventBus.getDefault().post(new EventObject(httpTag + Constant.TAG_HTTP_ERROR, error_msg));
            }
        }
    }

    //主要用来隐藏加载框;
    private static void otherHttpError(String httpTag) {
        // LogUtils.d("隐藏对话框  " + httpTag);
        EventBus.getDefault().post(new EventObject(Constant.TAG_HTTP_HIDE_WAITING_DIALOG + httpTag));
    }


    private static void analysisSvipPaly(String response, String httpTag) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                int code = jsonObject.optInt("error_code", -1);
                SvipplayEntity svipplayEntity = null;
                if (code == 0) {
                    JSONObject obj = null;
                    try {
                        obj = jsonObject.optJSONObject("data");
                        String url = obj.optString("url");
                        String js_1 = obj.optString("js_1");
                        String js_2 = obj.optString("js_2");
                        String user_agent = obj.optString("user_agent");
                        String play_type = obj.optString("play_type");
                        String play_url = obj.optString("play_url");
                        String down_url = obj.optString("down_url");
                        String use_sdk = obj.optString("use_sdk");
                        String url_status = obj.optString("url_status");
                        String shield_src = obj.optString("shield_src");
                        String release_src = obj.optString("release_src");
                        String svip_ad_open = obj.optString("svip_ad_open");
                        int full_screen = obj.optInt("full_screen", 1);//// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
                        JSONArray xuanji = jsonObject.optJSONArray("xuanji");
                        //LogUtils.e("选集的----" + xuanji);
                        List<AnthologyEntity> list = new ArrayList<>();
                        if (xuanji != null) {
                            int length = xuanji.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject o = xuanji.optJSONObject(i);
                                if (o != null) {
                                    String number = o.optString("number");
                                    String url1 = o.optString("url");
                                    String title = o.optString("title");
                                    String selected = o.optString("selected");
                                    AnthologyEntity entity;
                                    if (!TextUtils.isEmpty(selected) && selected.equals("1")) {
                                        entity = new AnthologyEntity(url1, number, title, true);
                                    } else {
                                        entity = new AnthologyEntity(url1, number, title, false);
                                    }
                                    list.add(entity);
                                }
                            }
                        }
                        svipplayEntity = new SvipplayEntity(
                                url, js_1, js_2, user_agent, play_type, play_url,
                                down_url, use_sdk, url_status, shield_src, release_src, svip_ad_open, list, httpTag, full_screen);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                EventBus.getDefault().post(new EventObject(httpTag, svipplayEntity));
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //视频播放---报错---sVip播放
    public static void getSvipPaly(int intLine, String htmlTitle, String htmlUrl, String vweb, String type) {
        String line = String.valueOf(intLine);
        String username = "yunliapi";
        String pwd = "yunliapi123PWD";
        String devtype = "1";
        String timestamp = getTimeStamp();
        String token = getToken(username, pwd, devtype, timestamp);
        ArrayMap<String, String> map = new ArrayMap<String, String>();
        map.put("title", htmlTitle);
        map.put("url", htmlUrl);
        map.put("line", line);
        map.put("username", username);
        map.put("token", token);
        map.put("vweb", vweb);
        map.put("devtype", devtype);
        map.put("timestamp", timestamp);
        cancelHttp(HTTP_TAG_WEB_SVIP_GET_PLAY_URL + type);
        doHttpWork(map, Api.WEB_SVIP_GET_PLAY_URL, HTTP_TAG_WEB_SVIP_GET_PLAY_URL + type);
    }

    private static String getToken(String username, String pwd, String devtype, String timestamp) {
        StringBuilder sb = new StringBuilder();
        String toString = sb.append(username).append(pwd).append(devtype).append(timestamp).toString();
        sb = null;
        return md5(toString);
    }

    public static String formatTime(String timestamp) {
        if (!TextUtils.isEmpty(timestamp)) {
            long time = Long.valueOf(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(time * 1000);
            return format;
        } else {
            return "";
        }
    }


    //时间戳
    public static String getTimeStamp() {
        long time = System.currentTimeMillis();
        String timestamp = String.valueOf((time / 1000));
        return timestamp;
    }


    //MD5加密
    private static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}

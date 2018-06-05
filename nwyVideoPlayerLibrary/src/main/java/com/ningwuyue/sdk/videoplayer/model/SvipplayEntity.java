package com.ningwuyue.sdk.videoplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ${武跃} on 2017/7/4.
 * <p>
 * {一句话描述功能:----svip播放返回的 实体类}
 */

public class SvipplayEntity implements Parcelable, Serializable {
    public String url;
    public String js_1;
    public String js_2;
    public String user_agent;
    public String play_type;
    public String play_url;
    public String down_url;
    public String use_sdk;//是否使用sdk  1   使用
    public String url_status;//  返回链接的状态   1 表示svip的url可以用
    public String shield_src;//拦截的
    public String release_src;//放行的
    public String svip_ad_open;//svip播放广告弹窗开关，1：开启，0：关闭
    public List<AnthologyEntity> anthologyEntityList;

    public int defaultVideoScreen=1;// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1//todo  改的
    public String type;//加载的地方----为了本地区分

    public SvipplayEntity(String url, String js_1, String js_2,
                          String user_agent, String play_type,
                          String play_url, String down_url, String use_sdk,
                          String url_status,
                          String shield_src, String release_src, String svip_ad_open,
                          List<AnthologyEntity> anthologyEntityList, String type, int defaultVideoScreen) {
        this.url = url;
        this.js_1 = js_1;
        this.js_2 = js_2;
        this.user_agent = user_agent;
        this.play_type = play_type;
        this.play_url = play_url;
        this.down_url = down_url;
        this.use_sdk = use_sdk;
        this.url_status = url_status;
        this.shield_src = shield_src;
        this.release_src = release_src;
        this.svip_ad_open = svip_ad_open;
        this.anthologyEntityList = anthologyEntityList;
        this.type = type;
        this.defaultVideoScreen=defaultVideoScreen;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.js_1);
        dest.writeString(this.js_2);
        dest.writeString(this.user_agent);
        dest.writeString(this.play_type);
        dest.writeString(this.play_url);
        dest.writeString(this.down_url);
        dest.writeString(this.use_sdk);
        dest.writeString(this.url_status);
        dest.writeString(this.shield_src);
        dest.writeString(this.release_src);
        dest.writeString(this.svip_ad_open);
        dest.writeTypedList(this.anthologyEntityList);
        dest.writeString(this.type);
        dest.writeInt(this.defaultVideoScreen);
    }

    protected SvipplayEntity(Parcel in) {
        this.url = in.readString();
        this.js_1 = in.readString();
        this.js_2 = in.readString();
        this.user_agent = in.readString();
        this.play_type = in.readString();
        this.play_url = in.readString();
        this.down_url = in.readString();
        this.use_sdk = in.readString();
        this.url_status = in.readString();
        this.shield_src = in.readString();
        this.release_src = in.readString();
        this.svip_ad_open = in.readString();
        this.anthologyEntityList = in.createTypedArrayList(AnthologyEntity.CREATOR);
        this.type = in.readString();
        this.defaultVideoScreen=in.readInt();
    }

    public static final Creator<SvipplayEntity> CREATOR = new Creator<SvipplayEntity>() {
        @Override
        public SvipplayEntity createFromParcel(Parcel source) {
            return new SvipplayEntity(source);
        }

        @Override
        public SvipplayEntity[] newArray(int size) {
            return new SvipplayEntity[size];
        }
    };
}

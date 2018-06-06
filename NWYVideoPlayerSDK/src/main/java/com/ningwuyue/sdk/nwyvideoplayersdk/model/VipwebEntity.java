package com.ningwuyue.sdk.nwyvideoplayersdk.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${武跃} on 2018/6/6.
 * 一句话简介：---
 */

public class VipwebEntity implements Parcelable {

    public String vweb;
    public String name;
    public String img_url;
    public String title;
    public String url;
    public String ping;
    public String loadjs;
    public String sort;
    public String shield_src;//拦截的
    public String release_src;//放行的

    public VipwebEntity(String vweb, String name, String img_url, String title, String url, String ping, String loadjs, String sort, String shield_src, String release_src) {
        this.vweb = vweb;
        this.name = name;
        this.img_url = img_url;
        this.title = title;
        this.url = url;
        this.ping = ping;
        this.loadjs = loadjs;
        this.sort = sort;
        this.shield_src = shield_src;
        this.release_src = release_src;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vweb);
        dest.writeString(this.name);
        dest.writeString(this.img_url);
        dest.writeString(this.title);
        dest.writeString(this.url);
        dest.writeString(this.ping);
        dest.writeString(this.loadjs);
        dest.writeString(this.sort);
        dest.writeString(this.shield_src);
        dest.writeString(this.release_src);
    }

    protected VipwebEntity(Parcel in) {
        this.vweb = in.readString();
        this.name = in.readString();
        this.img_url = in.readString();
        this.title = in.readString();
        this.url = in.readString();
        this.ping = in.readString();
        this.loadjs = in.readString();
        this.sort = in.readString();
        this.shield_src = in.readString();
        this.release_src = in.readString();
    }

    public static final Creator<VipwebEntity> CREATOR = new Creator<VipwebEntity>() {
        @Override
        public VipwebEntity createFromParcel(Parcel source) {
            return new VipwebEntity(source);
        }

        @Override
        public VipwebEntity[] newArray(int size) {
            return new VipwebEntity[size];
        }
    };
}

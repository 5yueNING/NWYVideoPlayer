package com.ningwuyue.sdk.videoplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${武跃} on 2017/12/18.
 * 一句话简介：---
 */

public class AnthologyEntity implements Parcelable {
    public String url;
    public String number;
    public String title;
    public boolean isSelected = false;

    public AnthologyEntity(String url, String number, String title, boolean isSelected) {
        this.url = url;
        this.number = number;
        this.title = title;
        this.isSelected = isSelected;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.number);
        dest.writeString(this.title);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected AnthologyEntity(Parcel in) {
        this.url = in.readString();
        this.number = in.readString();
        this.title = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<AnthologyEntity> CREATOR = new Creator<AnthologyEntity>() {
        @Override
        public AnthologyEntity createFromParcel(Parcel source) {
            return new AnthologyEntity(source);
        }

        @Override
        public AnthologyEntity[] newArray(int size) {
            return new AnthologyEntity[size];
        }
    };
}

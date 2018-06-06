package com.ningwuyue.sdk.nwyvideoplayersdk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;


/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */


public class WaitingDialog extends Dialog {
    public WaitingDialog(@NonNull Context context) {
        super(context, R.style.nwyvideo_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nwyvideo__view_progress_global);
        setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        setCancelable(false);
    }
}

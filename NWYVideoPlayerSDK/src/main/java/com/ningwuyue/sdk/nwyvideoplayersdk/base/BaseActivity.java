package com.ningwuyue.sdk.nwyvideoplayersdk.base;

/**
 * Created by ${武跃} on 2018/6/5.
 * 一句话简介：---
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.Constant;
import com.ningwuyue.sdk.nwyvideoplayersdk.dialog.WaitingDialog;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class BaseActivity extends AppCompatActivity {
    private static final int WHAT_DIALOG = 417964;

    protected int statusBarColor = 0;
    protected View statusBarView = null;
    protected boolean isResume = false;
    private WaitingDialog dialog;//进度条

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                if (msg.what == WHAT_DIALOG) {
                    hideWaitingDialog();
                } else {
                    handlerMsg(msg);
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);//注册
        setContentView(setLayoutId());
        if (statusBarColor == 0) {
            statusBarView = StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.nwyvideo_colorPrimaryDark));
        } else if (statusBarColor != -1) {
            statusBarView = StatusBarCompat.compat(this, statusBarColor);
        }
        transparent19and20();
        afterCreate(savedInstanceState);
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void initData() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEventBus(EventObject eventObject) {

    }

    protected abstract int setLayoutId();

    protected void handlerMsg(Message msg) {

    }

    protected abstract void afterCreate(Bundle savedInstanceState);

    protected void transparent19and20() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);//注销
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        dismissWaitingDialog();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }


    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected boolean isVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    // dialog
    public WaitingDialog getWaitingDialog() {
        if (dialog == null) {
            dialog = new WaitingDialog(this);
            dialog.setCancelable(true);
        }
        return dialog;
    }

    public void hideWaitingDialog() {
        dismissWaitingDialog();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void showWaitingDialog() {
        if (!isFinishing()) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler.sendEmptyMessageDelayed(WHAT_DIALOG, Constant.HTTP_TIMEOUT_DEFALUT);
            }
            getWaitingDialog().show();
        }
    }

    public void dismissWaitingDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        if (statusBarView != null) {
            statusBarView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        if (statusBarView != null) {
            statusBarView.setBackgroundColor(statusBarColor);
        }
    }
}

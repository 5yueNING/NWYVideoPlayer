package com.ningwuyue.sdk.nwyvideoplayersdk.util.common;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.init.NWYVideoPlayer;


/**
 * Created by ${武跃} on 2017/6/26.
 * <p>
 * {一句话描述功能:----吐司工具类}
 */
public class ToastUtils {
    private static Toast mToast = null;

    public static void showLong(String text) {
        if (mToast == null) {
            mToast = new Toast(NWYVideoPlayer.getInstance().getApplication());
        }
        View view = LayoutInflater.from(NWYVideoPlayer.getInstance().getApplication()).inflate(R.layout.nwyvideo_view_toast, null, false);
        mToast.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.tv_toast);
        textView.setText(text);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
    }

    public static void showShort(String text) {
        /*if (mToast == null) {
            mToast = Toast.makeText(NWYVideoPlayer.getInstance().getApplication(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();*/

        if (mToast == null) {
            mToast = new Toast(NWYVideoPlayer.getInstance().getApplication());
        }
        View view = LayoutInflater.from(NWYVideoPlayer.getInstance().getApplication()).inflate(R.layout.nwyvideo_view_toast, null, false);
        mToast.setView(view);
        TextView textView = (TextView) view.findViewById(R.id.tv_toast);
        textView.setText(text);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }
}

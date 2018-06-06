package com.ningwuyue.sdk.nwyvideoplayersdk.util.glide;


import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.SizeUtils;

public class ShowImageUtils {
    /**
     * 显示图片Imageview
     *
     * @param context  上下文
     * @param errorimg 错误的资源图片
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageView(Context context, int errorimg, String url, ImageView imgeview) {
        if (!TextUtils.isEmpty(url)) {
            try {
                if (url.endsWith(".gif") || url.endsWith(".GIF")) {
                    Glide.with(context).load(url)// 加载图片
                            .error(errorimg)// 设置错误图片
                            //.placeholder(errorimg)// 设置占位图
                            .crossFade(0)// 设置淡入淡出效果，默认300ms，可以传参
                            .dontAnimate()
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)// 原图
                            .into(new GlideDrawableImageViewTarget(imgeview, 1));
                } else {
                    Glide.with(context).load(url)// 加载图片
                            .error(errorimg)// 设置错误图片
                            //.placeholder(errorimg)// 设置占位图
                            .crossFade(0)// 设置淡入淡出效果，默认300ms，可以传参
                            .dontAnimate()
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                            .into(imgeview);
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }


    /**
     * 显示轮播图
     */
    public static void showADImageView(Context context, String url, ImageView imgeview) {
        if (!TextUtils.isEmpty(url)) {
            // Log.e("kuxunyou", "轮播图--------" + url);
            if (url.endsWith(".gif") || url.endsWith(".GIF")) {
                Glide.with(context).load(url)// 加载图片
                        .error(R.drawable.nwyvideo_defaut_pic)// 设置错误图片
                        .dontAnimate()
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)// 原图
                        .into(new GlideDrawableImageViewTarget(imgeview, 1));
            } else {
                Glide.with(context).load(url)// 加载图片
                        .error(R.drawable.nwyvideo_defaut_pic)// 设置错误图片
                        // .placeholder(R.drawable.ad_v)// 设置占位图
                        .dontAnimate()
                        .priority(Priority.HIGH)
                        .crossFade(0)// 设置淡入淡出效果，默认300ms，可以传参
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                        .into(imgeview);
            }
        }
    }


    /**
     * （8）
     * 显示图片 圆角显示  ImageView
     *
     * @param context  上下文
     * @param errorimg 错误的资源图片
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageViewToCircle(Context context, int errorimg, String url, ImageView imgeview) {
        Glide.with(context).load(url)
                // 加载图片
                .error(errorimg)
                // 设置错误图片
                .crossFade()
                // 设置淡入淡出效果，默认300ms，可以传参
                //.placeholder(errorimg)
                // 设置占位图
                .bitmapTransform(new RoundCornersTransformation(context, SizeUtils.dp2px(10), RoundCornersTransformation.CornerType.TOP))//圆角
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgeview);
    }

    /**
     * （8）
     * 显示图片 圆角显示  ImageView
     *
     * @param context  上下文
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageViewToCircle(Context context, String url, ImageView imgeview) {
        Glide.with(context).load(url)
                // 加载图片
                .error(R.drawable.nwyvideo_defaut_pic)
                // 设置错误图片
                .crossFade()
                // 设置淡入淡出效果，默认300ms，可以传参
                //.placeholder(R.drawable.ad_v)
                // 设置占位图
                .bitmapTransform(new RoundCornersTransformation(context, SizeUtils.dp2px(10), RoundCornersTransformation.CornerType.ALL))//圆角
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgeview);
    }
}
package com.ningwuyue.sdk.nwyvideoplayersdk.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.config.Config;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.constant.SpField;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.LogUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.SPUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.ScreenUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.SizeUtils;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.glide.ShowImageUtils;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;
import com.stx.xhb.xbanner.XBanner;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by ${武跃} on 2018/1/10.
 * 一句话简介：---
 */

public class VideoPlayerAdDialog extends BaseDialogFragment implements NativeAD.NativeAdListener {
    public static final int WHAT_SVIP = 430;
    public int count = 5;
    private NativeADDataRef adItem;
    private NativeAD nativeAD;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_SVIP:
                    if (mTv_message != null) {
                        if (count > 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("视频加载中    ").append(count--).append("  秒");
                            String s = sb.toString();
                            mTv_message.setText(s);
                            sb = null;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(WHAT_SVIP, 1000);
                            }
                        } else if (count == 0) {
                            mTv_message.setText("视频加载成功");
                            if (mHandler != null) {
                                mHandler.removeCallbacksAndMessages(null);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private TextView mTv_message;
    private XBanner mXBanner;

    @Override
    public void onResume() {
        super.onResume();
        if (mXBanner != null) {
            mXBanner.startAutoPlay();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mXBanner != null) {
            mXBanner.stopAutoPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mXBanner != null) {
            mXBanner.stopAutoPlay();
            mXBanner = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.nwyvideo_activity_nativead4;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        confingView(mDialogFragmentView);
        
    }

    private void confingView(View dialogFragmentView) {
        mXBanner = dialogFragmentView.findViewById(R.id.banner_1);
        ImageView iv = dialogFragmentView.findViewById(R.id.iv);
        ImageButton ibtnClose = dialogFragmentView.findViewById(R.id.ibtn_close);
        mTv_message = dialogFragmentView.findViewById(R.id.tv_message);

        ibtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
                EventBus.getDefault().post(new EventObject(EventObject.TAG_CLICK_COLES_AD));
            }
        });

        if (ScreenUtils.isLandscape()) {
            int screenHeight = ScreenUtils.getScreenHeight();
            ConstraintLayout.LayoutParams bannerParams = (ConstraintLayout.LayoutParams) mXBanner.getLayoutParams();
            bannerParams.width = screenHeight * 1 / 2;
            mXBanner.setLayoutParams(bannerParams);
        } else {
            int screenWidth = ScreenUtils.getScreenWidth();
            ConstraintLayout.LayoutParams bannerParams = (ConstraintLayout.LayoutParams) mXBanner.getLayoutParams();
            bannerParams.width = screenWidth * 2 / 3;
            mXBanner.setLayoutParams(bannerParams);
        }

        String ad_close_but = SPUtils.getInstance().getString(SpField.AD_CLOSE_BUT, "1");
        if (mHandler != null) {
            mHandler.sendEmptyMessageAtTime(WHAT_SVIP, 500);//
        }
        NativeAD nativeAD = new NativeAD(mContext, Config.GDT_APPID, Config.GDT_AD_READBOOK_ID, this);
        nativeAD.loadAD(3);
        if (!TextUtils.isEmpty(ad_close_but) && ad_close_but.equals("2")) {
            ViewGroup.LayoutParams layoutParams = ibtnClose.getLayoutParams();
            layoutParams.width = SizeUtils.dp2px(22);
            layoutParams.height = SizeUtils.dp2px(22);
            ibtnClose.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onADLoaded(final List<NativeADDataRef> list) {
        if (list != null) {
            int size = list.size();
            if (size > 0 && mXBanner != null) {
                mXBanner.setData(list, null);
                mXBanner.setData(R.layout.xbanner_item_image, list, null);
                //mXBanner.setPageTransformer(Transformer.Default);
                mXBanner.setmAdapter(new XBanner.XBannerAdapter() {
                    @Override
                    public void loadBanner(XBanner banner, Object model, View view, int position) {
                        if (model != null) {
                            ShowImageUtils.showADImageView(mContext, ((NativeADDataRef) model).getImgUrl(), (ImageView) view);
                        }
                    }
                });

                //设置广告图片点击事件
                mXBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
                    @Override
                    public void onItemClick(XBanner banner, int position) {
                        adItem = list.get(position);
                        if (adItem != null) {
                            //LogUtils.e("onItemClick---" + position + "---" + mImageView);
                            adItem.onExposured(banner); // 需要先调用曝光接口
                            adItem.onClicked(banner); // 点击接口
                        }
                    }
                });

                mXBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (list != null) {
                            NativeADDataRef adItem = list.get(position);
                            if (adItem != null && mXBanner != null) {
                                adItem.onExposured(mXBanner);
                            }
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
    }

    @Override
    public void onNoAD(AdError adError) {

        LogUtils.e(adError.getErrorMsg()+"-------------"+adError.getErrorMsg());

    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {

    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {
        LogUtils.e(adError.getErrorMsg()+"-------------"+adError.getErrorMsg());
    }
}

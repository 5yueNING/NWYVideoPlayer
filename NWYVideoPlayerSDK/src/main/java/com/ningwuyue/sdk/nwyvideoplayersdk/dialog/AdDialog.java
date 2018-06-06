package com.ningwuyue.sdk.nwyvideoplayersdk.dialog;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.data.config.Config;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
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

public class AdDialog extends BaseDialogFragment implements NativeAD.NativeAdListener {
    private XBanner mXBanner;

    @Override
    protected int getLayoutId() {
        return R.layout.nwyvideo_dialog_ad;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        mXBanner = mDialogFragmentView.findViewById(R.id.banner);
        Button mBtnPlay = mDialogFragmentView.findViewById(R.id.btn_play);

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                EventBus.getDefault().post(new EventObject(EventObject.TAG_AD_CLOSE_RESUME_PLAYER));
            }
        });
        NativeAD nativeAD = new NativeAD(mContext, Config.GDT_APPID, Config.GDT_AD_READBOOK_CHAPTER_ID, this);
        nativeAD.loadAD(3);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mXBanner != null) {
            mXBanner.stopAutoPlay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mXBanner != null) {
            mXBanner.startAutoPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mXBanner != null) {
            mXBanner.stopAutoPlay();
            mXBanner = null;
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
                            ShowImageUtils.showImageViewToCircle(mContext, ((NativeADDataRef) model).getImgUrl(), (ImageView) view);
                        }
                    }
                });

                //设置广告图片点击事件
                mXBanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
                    @Override
                    public void onItemClick(XBanner banner, int position) {
                        if (list != null) {
                            NativeADDataRef adItem = list.get(position);
                            if (adItem != null) {
                                adItem.onExposured(banner); // 需要先调用曝光接口
                                adItem.onClicked(banner); // 点击接口
                            }
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

    }

    @Override
    public void onADStatusChanged(NativeADDataRef nativeADDataRef) {

    }

    @Override
    public void onADError(NativeADDataRef nativeADDataRef, AdError adError) {

    }
}

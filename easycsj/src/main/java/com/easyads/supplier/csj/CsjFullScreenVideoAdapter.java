package com.easyads.supplier.csj;

import android.app.Activity;
import android.content.res.Configuration;

import com.easyads.EasyAdsManger;
import com.easyads.core.full.EAFullScreenVideoSetting;
import com.easyads.custom.EAFullScreenCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;

import java.lang.ref.SoftReference;

public class CsjFullScreenVideoAdapter extends EAFullScreenCustomAdapter implements TTAdNative.FullScreenVideoAdListener, TTFullScreenVideoAd.FullScreenVideoAdInteractionListener {
    private EAFullScreenVideoSetting setting;
    private TTFullScreenVideoAd ttFullScreenVideoAd;

    public CsjFullScreenVideoAdapter(SoftReference<Activity> activity, EAFullScreenVideoSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    protected void doLoadAD() {
        CsjUtil.initCsj(this, new CsjUtil.InitListener() {
            @Override
            public void success() {
                //只有在成功初始化以后才能调用load方法，否则穿山甲会抛错导致无法进行广告展示
                startLoadAD();
            }

            @Override
            public void fail(String code, String msg) {
                handleFailed(code, msg);
            }
        });
    }

    private void startLoadAD() {

        // 创建TTAdNative对象,用于调用广告请求接口
        TTAdNative mTTAdNative = CsjUtil.getADManger(this).createAdNative(getActivity().getApplicationContext());

        boolean isPortrait = getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        int orientation;
        if (isPortrait) { // 基于当前屏幕方向来设定广告期望方向
            orientation = TTAdConstant.VERTICAL;
        } else {
            orientation = TTAdConstant.HORIZONTAL;
        }

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sdkSupplier.adspotId)
                //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
                .setExpressViewAcceptedSize(500, 500)
                .setSupportDeepLink(true)
                .setDownloadType(EasyCsjManger.getInstance().csj_downloadType)
                .setOrientation(orientation)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();

//step5:请求广告
        mTTAdNative.loadFullScreenVideoAd(adSlot, this);
    }


    @Override
    protected void doShowAD() {
        ttFullScreenVideoAd.setFullScreenVideoAdInteractionListener(this);
        ttFullScreenVideoAd.showFullScreenVideoAd(getActivity());
    }


    @Override
    public void doDestroy() {

    }


    /**
     * 以下为FullScreenVideoAdListener 回调
     */
    @Override
    public void onError(int i, String s) {
        EALog.high(TAG + "onError ");
        handleFailed(i, s);
    }

    @Override
    public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
        try {
            EALog.high(TAG + "onFullScreenVideoAdLoad ");
            this.ttFullScreenVideoAd = ttFullScreenVideoAd;
            handleSucceed();
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }

    @Override
    public void onFullScreenVideoCached() {
        EALog.high(TAG + "onFullScreenVideoCached ");
    }

    @Override
    public void onFullScreenVideoCached(TTFullScreenVideoAd ttFullScreenVideoAd) {
        try {
            String ad = "";
            if (ttFullScreenVideoAd != null) {
                ad = ttFullScreenVideoAd.toString();
            }
            EALog.high(TAG + "onFullScreenVideoCached( " + ad + ")");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        handleCached();
    }


    /**
     * 以下为setFullScreenVideoAdInteractionListener 广告事件监听
     */

    @Override
    public void onAdShow() {
        EALog.high(TAG + "onFullScreenVideo onAdShow");
        handleExposure();

    }

    @Override
    public void onAdVideoBarClick() {
        EALog.high(TAG + "onFullScreenVideo onAdVideoBarClick");
        handleClick();

    }

    @Override
    public void onAdClose() {
        EALog.high(TAG + "onFullScreenVideo onAdClose");

        if (setting != null)
            setting.adapterClose(sdkSupplier);

    }

    @Override
    public void onVideoComplete() {
        EALog.high(TAG + "onFullScreenVideo onVideoComplete");

        if (setting != null)
            setting.adapterVideoComplete(sdkSupplier);

    }

    @Override
    public void onSkippedVideo() {
        EALog.high(TAG + "onFullScreenVideo onSkippedVideo");

        if (setting != null)
            setting.adapterVideoSkipped(sdkSupplier);

    }
}

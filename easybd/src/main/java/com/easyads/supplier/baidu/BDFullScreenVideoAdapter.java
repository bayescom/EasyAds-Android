package com.easyads.supplier.baidu;

import android.app.Activity;

import com.easyads.core.full.EAFullScreenVideoSetting;
import com.easyads.custom.EAFullScreenCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.baidu.mobads.sdk.api.FullScreenVideoAd;

import java.lang.ref.SoftReference;

public class BDFullScreenVideoAdapter extends EAFullScreenCustomAdapter implements FullScreenVideoAd.FullScreenVideoAdListener {
    private EAFullScreenVideoSetting setting;

    private FullScreenVideoAd mFullScreenVideoAd;

    public BDFullScreenVideoAdapter(SoftReference<Activity> activity, EAFullScreenVideoSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    protected void doLoadAD() {
        BDUtil.initBDAccount(this);

        // 全屏视频产品可以选择是否使用SurfaceView进行渲染视频
        mFullScreenVideoAd = new FullScreenVideoAd(getActivity(), sdkSupplier.adspotId
                , this, EasyBDManager.getInstance().fullScreenUseSurfaceView);
        mFullScreenVideoAd.load();

    }

    @Override
    protected void doShowAD() {
        try {
            boolean isReady = mFullScreenVideoAd != null && mFullScreenVideoAd.isReady();
            EALog.simple(TAG + "-doShowAD- isReady = " + isReady);
            if (mFullScreenVideoAd != null) {
                mFullScreenVideoAd.show();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void doDestroy() {
    }


    /**
     * 广告事件回调
     */

    @Override
    public void onAdShow() {
        EALog.high(TAG + "onAdShow");

        handleExposure();
    }

    @Override
    public void onAdClick() {
        EALog.high(TAG + "onAdClick");

        handleExposure();
    }

    @Override
    public void onAdClose(float playScale) {
        // 用户关闭了广告
        // 说明：关闭按钮在mssp上可以动态配置，媒体通过mssp配置，可以选择广告一开始就展示关闭按钮，还是播放结束展示关闭按钮
        // 建议：收到该回调之后，可以重新load下一条广告,最好限制load次数（4-5次即可）
        // playScale[0.0-1.0],1.0表示播放完成，媒体可以按照自己的设计给予奖励
        EALog.high(TAG + "onAdClose，playScale = " + playScale);


        if (setting != null)
            setting.adapterClose(sdkSupplier);
    }

    @Override
    public void onAdFailed(String s) {
        String msg = "onAdFailed" + s;

        handleFailed(EasyAdError.ERROR_BD_FAILED, msg);
    }

    @Override
    public void onVideoDownloadSuccess() {
        EALog.high(TAG + "onVideoDownloadSuccess");

        handleCached();
    }

    @Override
    public void onVideoDownloadFailed() {
        EALog.e(TAG + "onVideoDownloadFailed");
    }

    @Override
    public void playCompletion() {
        EALog.high(TAG + "playCompletion");

        if (setting != null)
            setting.adapterVideoComplete(sdkSupplier);
    }

    @Override
    public void onAdSkip(float playScale) {
        // 用户跳过了广告
        // playScale[0.0-1.0],1.0表示播放完成，媒体可以按照自己的设计给予奖励
        EALog.high(TAG + "onAdSkip，playScale = " + playScale);
        if (setting != null)
            setting.adapterVideoSkipped(sdkSupplier);
    }

    @Override
    public void onAdLoaded() {
        EALog.high(TAG + "onAdLoaded");
        handleSucceed();
        try {
            if (mFullScreenVideoAd != null) {
                String ecpm = mFullScreenVideoAd.getECPMLevel();
                EALog.high(TAG + "mFullScreenVideoAd ECPMLevel = " + ecpm);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}

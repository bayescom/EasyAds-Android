package com.easyads.supplier.baidu;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.easyads.core.inter.EAInterstitialSetting;
import com.easyads.custom.EAInterstitialCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.baidu.mobads.sdk.api.AdSize;
import com.baidu.mobads.sdk.api.InterstitialAd;
import com.baidu.mobads.sdk.api.InterstitialAdListener;

import java.lang.ref.SoftReference;

public class BDInterstitialAdapter extends EAInterstitialCustomAdapter implements InterstitialAdListener {
    private EAInterstitialSetting setting;
    private InterstitialAd mInterAd;            // 插屏广告实例，支持单例模式
    private AdSize adSize;
    private RelativeLayout videoLayout;
    private boolean isAdForVideo = false;

    public BDInterstitialAdapter(SoftReference<Activity> activity, EAInterstitialSetting baseSetting) {
        super(activity, baseSetting);
        this.setting = baseSetting;
        adSize = EasyBDManager.getInstance().interstitialType;
        videoLayout = EasyBDManager.getInstance().interstitialVideoLayout;
    }

    @Override
    protected void doLoadAD() {
        if (sdkSupplier != null) {
            BDUtil.initBDAccount(this);

            String adPlaceId = sdkSupplier.adspotId;
            if (adSize == null) {
                mInterAd = new InterstitialAd(getActivity(), adPlaceId);
            } else {
                mInterAd = new InterstitialAd(getActivity(), adSize, adPlaceId);
            }
            mInterAd.setListener(this);
        }


        if (adSize != null) {
            isAdForVideo = adSize == AdSize.InterstitialForVideoBeforePlay || adSize == AdSize.InterstitialForVideoPausePlay;
            if (isAdForVideo && videoLayout != null) {
                RelativeLayout.LayoutParams reLayoutParams = (RelativeLayout.LayoutParams) videoLayout.getLayoutParams();
                mInterAd.loadAdForVideoApp(reLayoutParams.width, reLayoutParams.height);
            } else {
                mInterAd.loadAd();
            }
        } else {
            mInterAd.loadAd();
        }
    }

    @Override
    protected void doShowAD() {
        if (mInterAd != null) {
            if (videoLayout != null && isAdForVideo) {
                mInterAd.showAdInParentForVideoApp(  videoLayout);
            } else {
                mInterAd.showAd();
            }
        }
    }


    @Override
    public void doDestroy() {
        if (mInterAd != null) {
            mInterAd.destroy();
        }
    }


    //广告的回调事件
    @Override
    public void onAdReady() {
        EALog.high(TAG + "onAdReady");
        handleSucceed();
    }

    @Override
    public void onAdPresent() {
        EALog.high(TAG + "onAdPresent");
        handleExposure();
    }

    @Override
    public void onAdClick(InterstitialAd interstitialAd) {
        EALog.high(TAG + "onAdClick");
        handleClick();
    }

    @Override
    public void onAdDismissed() {
        EALog.high(TAG + "onAdDismissed");
        if (null != setting) {
            setting.adapterDidClosed(sdkSupplier);
        }
    }

    @Override
    public void onAdFailed(String s) {
        EALog.high(TAG + "onAdFailed ，reason：" + s);
        handleFailed(EasyAdError.ERROR_BD_FAILED, s);
    }


}

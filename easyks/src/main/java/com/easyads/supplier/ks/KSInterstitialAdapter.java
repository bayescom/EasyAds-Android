package com.easyads.supplier.ks;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.easyads.core.inter.EAInterstitialSetting;
import com.easyads.custom.EAInterstitialCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsInterstitialAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;

import java.lang.ref.SoftReference;
import java.util.List;

public class KSInterstitialAdapter extends EAInterstitialCustomAdapter implements KsInterstitialAd.AdInteractionListener {
    private EAInterstitialSetting setting;
    KsInterstitialAd interstitialAD;
    List<KsInterstitialAd> list;

    public KSInterstitialAdapter(SoftReference<Activity> activity, EAInterstitialSetting baseSetting) {
        super(activity, baseSetting);
        this.setting = baseSetting;
    }

    @Override
    protected void doShowAD() {
        interstitialAD.showInterstitialAd(getActivity(), EasyKSManager.getInstance().interstitialVideoConfig);
    }

    @Override
    protected void doLoadAD() {
        //初始化快手SDK
        KSUtil.initAD(this, new KSUtil.InitListener() {
            @Override
            public void success() {
                //只有在成功初始化以后才能调用load方法，否则穿山甲会抛错导致无法进行广告展示
                startLoad();
            }

            @Override
            public void fail(String code, String msg) {
                handleFailed(code, msg);
            }
        });

    }

    private void startLoad() {
            //场景设置
            KsScene scene = new KsScene.Builder(KSUtil.getADID(sdkSupplier)).build();
            KsAdSDK.getLoadManager().loadInterstitialAd(scene,
                    new KsLoadManager.InterstitialAdListener() {
                        @Override
                        public void onError(int code, String msg) {
                            EALog.high(TAG + " onError " + code + msg);

                            handleFailed(code, msg);
                        }

                        @Override
                        public void onRequestResult(int adNumber) {
                            EALog.high(TAG + "onRequestResult，广告填充数量：" + adNumber);

                        }


                        @Override
                        public void onInterstitialAdLoad(@Nullable List<KsInterstitialAd> adList) {
                            EALog.high(TAG + "onInterstitialAdLoad");

                            try {
                                list = adList;
                                if (list == null || list.size() == 0 || list.get(0) == null) {
                                    handleFailed(EasyAdError.ERROR_DATA_NULL, "");
                                } else {
                                    interstitialAD = list.get(0);
                                    //回调监听
                                    if (interstitialAD != null) {
                                        interstitialAD.setAdInteractionListener(KSInterstitialAdapter.this);
                                    }
                                    handleSucceed();
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                                handleFailed(EasyAdError.ERROR_EXCEPTION_LOAD, "");
                            }

                        }
                    });
        }

    @Override
    public void doDestroy() {

    }


    /**
     * 广告事件回调
     */
    @Override
    public void onAdClicked() {
        EALog.high(TAG + " onAdClicked");
        handleClick();
    }

    @Override
    public void onAdShow() {
        EALog.high(TAG + " onAdShow");
        handleExposure();
    }

    @Override
    public void onAdClosed() {
        EALog.high(TAG + " onAdClosed");
        if (setting != null) {
            setting.adapterDidClosed(sdkSupplier);
        }
    }

    @Override
    public void onPageDismiss() {
        EALog.high(TAG + " onPageDismiss");
        if (setting != null) {
            setting.adapterDidClosed(sdkSupplier);
        }
    }

    @Override
    public void onVideoPlayError(int code, int extra) {
        EALog.e(TAG + " onVideoPlayError,code = " + code + ",extra = " + extra);
        try {
            if (setting != null) {
                EasyAdError error = EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_RENDER, "onVideoPlayError");
                setting.adapterDidFailed(error, sdkSupplier);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoPlayEnd() {
        EALog.high(TAG + " onVideoPlayEnd");
    }

    @Override
    public void onVideoPlayStart() {
        EALog.high(TAG + " onVideoPlayStart");
    }

    @Override
    public void onSkippedAd() {
        EALog.high(TAG + " onSkippedAd");
        if (setting != null) {
            setting.adapterDidClosed(sdkSupplier);
        }
    }
}

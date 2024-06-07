package com.easyads.supplier.ks;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.easyads.core.full.EAFullScreenVideoSetting;
import com.easyads.custom.EAFullScreenCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsFullScreenVideoAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;

import java.lang.ref.SoftReference;
import java.util.List;

public class KSFullScreenVideoAdapter extends EAFullScreenCustomAdapter implements KsFullScreenVideoAd.FullScreenVideoAdInteractionListener {

    public EAFullScreenVideoSetting setting;

    private List<KsFullScreenVideoAd> list;
    KsFullScreenVideoAd ad;

    public KSFullScreenVideoAdapter(SoftReference<Activity> activity, EAFullScreenVideoSetting baseSetting) {
        super(activity, baseSetting);
        setting = baseSetting;
    }

    @Override
    protected void doLoadAD() {
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
            KsScene scene = new KsScene.Builder(KSUtil.getADID(sdkSupplier)).build(); // 此为测试posId，请联系快手平台申请正式posId
            KsAdSDK.getLoadManager().loadFullScreenVideoAd(scene, new KsLoadManager.FullScreenVideoAdListener() {
                @Override
                public void onError(int code, String msg) {
                    EALog.high(TAG + " onError " + code + msg);

                    handleFailed(code, msg);
                }

                @Override
                public void onFullScreenVideoResult(@Nullable List<KsFullScreenVideoAd> list) {
                    EALog.high(TAG + "onFullScreenVideoResult  ");

                }

                @Override
                public void onFullScreenVideoAdLoad(@Nullable List<KsFullScreenVideoAd> adList) {
                    EALog.high(TAG + " onFullScreenVideoAdLoad");
                    try {
                        list = adList;
                        if (list == null || list.size() == 0 || list.get(0) == null) {
                            handleFailed(EasyAdError.ERROR_DATA_NULL, "");
                        } else {
                            ad = list.get(0);
                            //回调监听
                            if (ad != null && ad.isAdEnable()) {
                                ad.setFullScreenVideoAdInteractionListener(KSFullScreenVideoAdapter.this);
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
    protected void doShowAD() {
        ad.showFullScreenVideoAd(getActivity(), EasyKSManager.getInstance().fullScreenVideoConfig);
    }

    @Override
    public void doDestroy() {

    }


    //--------广告回调--------
    @Override
    public void onAdClicked() {
        EALog.high(TAG + " onAdClicked");
        handleClick();
    }

    @Override
    public void onPageDismiss() {
        EALog.high(TAG + " onPageDismiss");
        if (setting != null) {
            setting.adapterClose(sdkSupplier);
        }
    }

    @Override
    public void onVideoPlayError(int code, int extra) {
        String msg = " onVideoPlayError,code = " + code + ",extra = " + extra;
        EALog.high(TAG + msg);

        handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_RENDER, msg));
    }

    @Override
    public void onVideoPlayEnd() {
        EALog.high(TAG + " onVideoPlayEnd");
        if (setting != null) {
            setting.adapterVideoComplete(sdkSupplier);
        }
    }

    @Override
    public void onVideoPlayStart() {
        EALog.high(TAG + " onVideoPlayStart");
        handleExposure();
    }

    @Override
    public void onSkippedVideo() {
        EALog.high(TAG + " onSkippedVideo");
        if (setting != null) {
            setting.adapterVideoSkipped(sdkSupplier);
        }
    }

}

package com.easyads.supplier.csj;

import android.app.Activity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.easyads.core.inter.EAInterstitialSetting;
import com.easyads.custom.EAInterstitialCustomAdapter;
import com.easyads.itf.BaseEnsureListener;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.easyads.utils.EAUtil;

import java.lang.ref.SoftReference;

public class CsjInterstitialAdapter extends EAInterstitialCustomAdapter  {
    private EAInterstitialSetting setting;
    //    private TTNativeExpressAd mTTAd;
    private long startTime = 0;

    public TTFullScreenVideoAd newVersionAd;

    public CsjInterstitialAdapter(SoftReference<Activity> activity, EAInterstitialSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    public void doDestroy() {

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


    @Override
    protected void doShowAD() {
        EAUtil.switchMainThread(new BaseEnsureListener() {
            @Override
            public void ensure() {
                try {
                    String nullTip = TAG + "请先加载广告或者广告已经展示过";
                    if (newVersionAd != null) {
                        newVersionAd.showFullScreenVideoAd(getActivity(), TTAdConstant.RitScenes.GAME_GIFT_BONUS, null);
                        newVersionAd = null;
                    } else {
                        EALog.e(nullTip);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_SHOW));
                }
            }
        });
    }


    private void startLoadAD() {
        TTAdNative ttAdNative = CsjUtil.getADManger(this).createAdNative(getActivity());
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sdkSupplier.adspotId)
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(setting.getCsjExpressViewWidth(), setting.getCsjExpressViewHeight())
                .build();
        ttAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                handleFailed(i, s);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                try {
                    EALog.high(TAG + "onFullScreenVideoAdLoad");

                    newVersionAd = ttFullScreenVideoAd;
                    if (newVersionAd == null) {
                        handleFailed(EasyAdError.ERROR_DATA_NULL, "new ints ad null");
                        return;
                    }
                    newVersionAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                        @Override
                        public void onAdShow() {
                            EALog.high(TAG + "newVersionAd onAdShow");
                            handleExposure();
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            EALog.high(TAG + "newVersionAd onAdVideoBarClick");
                            handleClick();
                        }

                        @Override
                        public void onAdClose() {
                            EALog.high(TAG + "newVersionAd onAdClose");

                            if (setting != null)
                                setting.adapterDidClosed(sdkSupplier);
                        }

                        @Override
                        public void onVideoComplete() {
                            EALog.high(TAG + "newVersionAd onVideoComplete");
                        }

                        @Override
                        public void onSkippedVideo() {
                            EALog.high(TAG + "newVersionAd onSkippedVideo");
                        }
                    });
                    handleSucceed();

                } catch (Throwable e) {
                    e.printStackTrace();
                    handleFailed(EasyAdError.ERROR_EXCEPTION_LOAD, "");
                }
            }

            @Override
            public void onFullScreenVideoCached() {
                EALog.high(TAG + "onFullScreenVideoCached");

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

            }
        });
    }

}

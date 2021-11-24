package com.easyads.supplier.csj;

import android.app.Activity;
import android.view.View;

import com.easyads.EasyAdsManger;
import com.easyads.core.inter.EAInterstitialSetting;
import com.easyads.custom.EAInterstitialCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.lang.ref.SoftReference;
import java.util.List;

public class CsjInterstitialAdapter extends EAInterstitialCustomAdapter implements TTAdNative.NativeExpressAdListener {
    private EAInterstitialSetting setting;
    private TTNativeExpressAd mTTAd;
    private long startTime = 0;

    private boolean isNewVersion;//是否为 新模板渲染插屏，穿山甲自3.5.5.0版本后新增
    public TTFullScreenVideoAd newVersionAd;

    public CsjInterstitialAdapter(SoftReference<Activity> activity, EAInterstitialSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    public void doDestroy() {
        if (mTTAd != null) {
            mTTAd.destroy();
        }
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
        if (isNewVersion) {
            newVersionAd.showFullScreenVideoAd(getActivity());
            return;
        }
        mTTAd.showInteractionExpressAd(getActivity());
    }


    @Override
    public void onError(int i, String s) {
        handleFailed(i, s);
    }

    @Override
    public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
        try {
            EALog.high(TAG + "onNativeExpressAdLoad");
            if (ads == null || ads.size() == 0) {
                handleFailed(EasyAdError.ERROR_DATA_NULL, "ads.size() == 0");
                return;
            }
            mTTAd = ads.get(0);
            if (null == mTTAd) {
                handleFailed(EasyAdError.ERROR_DATA_NULL, "null == mTTAd");
                return;
            }
            mTTAd.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {
                @Override
                public void onAdDismiss() {
                    EALog.high(TAG + "onAdDismiss");

                    if (null != setting) {
                        setting.adapterDidClosed(sdkSupplier);
                    }
                }

                @Override
                public void onAdClicked(View view, int type) {
                    EALog.high(TAG + "onAdClicked");

                    handleClick();
                }

                @Override
                public void onAdShow(View view, int type) {
                    EALog.high(TAG + "onAdShow");
                    handleExposure();
                }

                @Override
                public void onRenderFail(View view, String msg, int code) {
                    EALog.high(TAG + "ExpressView render fail:" + (System.currentTimeMillis() - startTime));
                    handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_RENDER_FAILED, TAG + code + "， " + msg));
                }

                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    EALog.high(TAG + "ExpressView render suc:" + (System.currentTimeMillis() - startTime));
                    //返回view的宽高 单位 dp
                }
            });
            startTime = System.currentTimeMillis();
            mTTAd.render();
            handleSucceed();
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }


    private void startLoadAD() {

        //此处使用渠道配置参数versionTag，来确定是否为新插屏广告，以方便动态设置
        if (sdkSupplier.versionTag == 1) {
            isNewVersion = false;
        } else if (sdkSupplier.versionTag == 2) {
            isNewVersion = true;
        } else {//如果未设置versionTag信息，取广告位上的设置
            if (setting != null) {
                isNewVersion = setting.isCsjNew();
            } else {//广告位为配置默认为新版本
                isNewVersion = true;
            }
        }
        EALog.simple(TAG + "当前广告是否为'新插屏'： " + isNewVersion);


        TTAdNative ttAdNative = CsjUtil.getADManger(this).createAdNative(getActivity());
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sdkSupplier.adspotId)
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(setting.getCsjExpressViewWidth(), setting.getCsjExpressViewHeight())
                .setDownloadType(EasyAdsManger.getInstance().csj_downloadType)
                .build();
        if (isNewVersion) {
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
        } else {
            ttAdNative.loadInteractionExpressAd(adSlot, this);
        }
    }


}

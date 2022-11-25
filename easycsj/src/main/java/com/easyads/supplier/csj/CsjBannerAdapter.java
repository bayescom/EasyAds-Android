package com.easyads.supplier.csj;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.easyads.EasyAdsManger;
import com.easyads.core.banner.EABannerSetting;
import com.easyads.custom.EABannerCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.lang.ref.SoftReference;
import java.util.List;

/**
 * 如果网络异常，不会进行刷新行为，且不会回调失败。当网络正常，会继续定时刷新。视为内部闭环了刷新行为，一旦失败就流转下一优先级
 */
public class CsjBannerAdapter extends EABannerCustomAdapter implements TTAdNative.NativeExpressAdListener {
    private EABannerSetting setting;
    private long startTime = 0;
    private TTNativeExpressAd ad;

    public CsjBannerAdapter(SoftReference<Activity> activity, final EABannerSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }


    @Override
    public void onError(int code, String message) {
        EALog.high(TAG + " onError: code = " + code + " msg = " + message);
        handleFailed(code + "", message);
    }

    @Override
    public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
        try {
            EALog.high(TAG + "onNativeExpressAdLoad");
            if (ads == null || ads.size() == 0) {
                handleFailed(EasyAdError.ERROR_DATA_NULL, "广告列表数据为空");
                return;
            }
            ad = ads.get(0);
            // 加载成功的回调，接入方可在此处做广告的展示，请确保您的代码足够健壮，能够处理异常情况；
            if (null == ad) {
                handleFailed(EasyAdError.ERROR_DATA_NULL, "广告数据为空");
                return;
            }
            bindAdListener(ad);

            handleSucceed();
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        try {
            if (null != setting) {
                ad.setSlideIntervalTime(setting.getRefreshInterval() * 1000);
            }
            ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                @Override
                public void onAdClicked(View view, int i) {
                    EALog.high(TAG + "ExpressView onAdClicked , type :" + i);

                    handleClick();
                }

                @Override
                public void onAdShow(View view, int i) {
                    EALog.high(TAG + "ExpressView onAdShow, type :" + i + ",cost time = " + (System.currentTimeMillis() - startTime));

                    handleExposure();
                }

                @Override
                public void onRenderFail(View view, String msg, int code) {
                    EALog.high(TAG + "ExpressView onRenderFail ，cost：" + (System.currentTimeMillis() - startTime));

                    handleFailed(EasyAdError.ERROR_RENDER_FAILED, TAG + code + "， " + msg);
                }

                @Override
                public void onRenderSuccess(View view, float v, float v1) {
                    EALog.high(TAG + "ExpressView onRenderSuccess，cost：" + (System.currentTimeMillis() - startTime));

                    if (null != setting) {
                        ViewGroup adContainer = setting.getContainer();
                        if (adContainer != null) {
                            adContainer.removeAllViews();
                            adContainer.addView(view);
                        }
                    }
                }
            });


            //使用默认模板中默认dislike弹出样式
            ad.setDislikeCallback(getActivity(), new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onShow() {

                }

                @Override
                public void onSelected(int position, String value, boolean enforce) {
                    if (null != setting) {
                        //用户选择不喜欢原因后，移除广告展示
                        ViewGroup adContainer = setting.getContainer();
                        if (adContainer != null) {
                            adContainer.removeAllViews();
                        }

                        setting.adapterDidDislike(sdkSupplier);
                    }
                }

                @Override
                public void onCancel() {
                }

            });
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void doDestroy() {
        try {
            if (ad != null)
                ad.destroy();
        } catch (Throwable e) {
            e.printStackTrace();
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

    private void startLoadAD() {

        TTAdNative ttAdNative =  CsjUtil.getADManger(this).createAdNative(getActivity());
        AdSlot adSlot = new AdSlot.Builder()
                // 必选参数 设置您的CodeId
                .setCodeId(sdkSupplier.adspotId)
                //期望模板广告view的size,单位dp
                .setExpressViewAcceptedSize(setting.getCsjExpressViewAcceptedWidth(), setting.getCsjExpressViewAcceptedHeight())
                // 可选参数 设置是否支持deeplink
                .setSupportDeepLink(true)
                //请求原生广告时候需要设置，参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .build();
        ttAdNative.loadBannerExpressAd(adSlot, this);
    }


    @Override
    protected void doShowAD() {
        startTime = System.currentTimeMillis();
        if (ad != null) {
            ad.render();
        }
    }
}

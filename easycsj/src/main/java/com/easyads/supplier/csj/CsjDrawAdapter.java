package com.easyads.supplier.csj;

import android.app.Activity;
import android.view.View;

import com.easyads.core.draw.EADrawSetting;
import com.easyads.custom.EADrawCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.lang.ref.SoftReference;
import java.util.List;


public class CsjDrawAdapter extends EADrawCustomAdapter implements TTAdNative.NativeExpressAdListener {
    TTNativeExpressAd ad;

    public CsjDrawAdapter(SoftReference<Activity> activity, EADrawSetting setting) {
        super(activity, setting);
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
    public void doDestroy() {

    }


    @Override
    protected void doShowAD() {
        if (addADView(ad.getExpressAdView())) {
            ad.render();
        }
    }


    private void startLoadAD() {

        // 创建TTAdNative对象,用于调用广告请求接口
        TTAdNative mTTAdNative = CsjUtil.getADManger(this).createAdNative(getActivity().getApplicationContext());

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sdkSupplier.adspotId)
                .setSupportDeepLink(true)
                .setExpressViewAcceptedSize(mDrawSetting.getCsjExpressWidth(), mDrawSetting.getCsjExpressHeight()) //期望模板广告view的size,单位dp
                .setAdCount(1) //请求广告数量为1到3条
//                .setAdLoadType(PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build();

        mTTAdNative.loadExpressDrawFeedAd(adSlot, this);
    }

    @Override
    public void onError(int code, String message) {
        EALog.high(TAG + "onError" + code + message);

        handleFailed(code, message);
    }

    @Override
    public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
        try {
            EALog.high(TAG + "onNativeExpressAdLoad, ads = " + ads);

            if (ads == null || ads.isEmpty()) {
                handleFailed(EasyAdError.ERROR_DATA_NULL, "ads empty");
                return;
            }
            ad = ads.get(0);
//            ad.setCanInterruptVideoPlay(false);
            ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                //广告点击的回调
                @Override
                public void onAdClicked(View view, int type) {
                    EALog.high(TAG + "onAdClicked, type = " + type);

                    handleClick();
                }

                //广告展示回调
                @Override
                public void onAdShow(View view, int type) {
                    EALog.high(TAG + "onAdShow, type = " + type);

                    handleExposure();
                }

                //广告渲染失败
                @Override
                public void onRenderFail(View view, String msg, int code) {
                    String log = "onRenderFail : code = " + code + ",msg =" + msg;
                    EALog.high(TAG + "onRenderFail, log = " + log);

                    handleFailed(EasyAdError.ERROR_RENDER_FAILED, log);
                }

                //广告渲染成功
                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    EALog.high(TAG + "onRenderSuccess, width = " + width + ",height = " + height);

                }
            });

            handleSucceed();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}

package com.easyads.supplier.csj;

import android.app.Activity;
import android.view.View;

import com.easyads.EasyAdsManger;
import com.easyads.core.nati.EANativeExpressSetting;
import com.easyads.custom.EANativeExpressCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.lang.ref.SoftReference;
import java.util.List;

public class CsjNativeExpressAdapter extends EANativeExpressCustomAdapter implements TTAdNative.NativeExpressAdListener {

    TTNativeExpressAd ttNativeExpressAd;
    private EANativeExpressSetting setting;


    public CsjNativeExpressAdapter(SoftReference<Activity> activity, EANativeExpressSetting setting) {
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
        TTAdNative ttAdNative = CsjUtil.getADManger(this).createAdNative(getActivity());
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sdkSupplier.adspotId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setDownloadType(EasyCsjManger.getInstance().csj_downloadType)
                .setExpressViewAcceptedSize(setting.getExpressViewWidth(), setting.getExpressViewHeight()) //期望模板广告view的size,单位dp
                .build();
        //加载广告
        ttAdNative.loadNativeExpressAd(adSlot, this);
    }

    @Override
    public void doDestroy() {

    }


    @Override
    public void onError(int i, String s) {
        EALog.high(TAG + "onError");
        handleFailed(i, s);
    }

    @Override
    public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
        try {
            EALog.high(TAG + "onNativeExpressAdLoad");
            if (ads == null || ads.size() == 0) {
                handleFailed(EasyAdError.ERROR_DATA_NULL, "ads empty");
            } else {
                ttNativeExpressAd = ads.get(0);
                if (null == ttNativeExpressAd) {
                    handleFailed(EasyAdError.ERROR_DATA_NULL, "ttNativeExpressAd is null ");
                    return;
                }
                handleSucceed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.ERROR_EXCEPTION_LOAD, "");
        }
    }


    @Override
    protected void doShowAD() {
        addADView(ttNativeExpressAd.getExpressAdView());
        ttNativeExpressAd.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int i) {
                EALog.high(TAG + "onAdClicked");
                setNEView(view);

                handleClick();
            }

            @Override
            public void onAdShow(View view, int i) {
                EALog.high(TAG + "onAdShow");

                setNEView(view);
                handleExposure();
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                EALog.high(TAG + "onRenderFail");
                setNEView(view);
                if (null != setting) {
                    setting.adapterRenderFailed(sdkSupplier);
                }

                handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_RENDER_FAILED, TAG + code + "， " + msg));
                removeADView();
            }

            @Override
            public void onRenderSuccess(View view, float v, float v1) {
                EALog.high(TAG + "onRenderSuccess");
                setNEView(view);
                if (null != setting) {
                    setting.adapterRenderSuccess(sdkSupplier);
                }
            }
        });
        ttNativeExpressAd.setDislikeCallback(getActivity(), new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onShow() {

            }

            @Override
            public void onSelected(int i, String s, boolean enforce) {
                EALog.high(TAG + "DislikeInteractionCallback_onSelected , int i = +" + i + ", String s" + s + ", boolean enforce" + enforce + " ;");
                if (null != setting) {
                    setting.adapterDidClosed(sdkSupplier);
                }
                removeADView();
            }

            @Override
            public void onCancel() {

            }
        });
        ttNativeExpressAd.render();
    }
}

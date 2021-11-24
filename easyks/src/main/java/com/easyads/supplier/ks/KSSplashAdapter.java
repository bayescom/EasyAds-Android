package com.easyads.supplier.ks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.easyads.core.splash.EASplashSetting;
import com.easyads.custom.EASplashCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;
import com.kwad.sdk.api.KsSplashScreenAd;

import java.lang.ref.SoftReference;

public class KSSplashAdapter extends EASplashCustomAdapter implements KsSplashScreenAd.SplashScreenAdInteractionListener {
    private KsSplashScreenAd splashAd;

    public KSSplashAdapter(SoftReference<Activity> softReferenceActivity, EASplashSetting baseSetting) {
        super(softReferenceActivity, baseSetting);
    }


    @Override
    protected void doLoadAD() {

        //初始化快手SDK
        boolean initOK = KSUtil.initAD(this);
        if (initOK) {
            //场景设置
            KsScene scene = new KsScene.Builder(KSUtil.getADID(sdkSupplier)).build(); // 此为测试posId，请联系快手平台申请正式posId
            KsAdSDK.getLoadManager().loadSplashScreenAd(scene, new KsLoadManager.SplashScreenAdListener() {
                @Override
                public void onError(int code, String msg) {
                    EALog.high(TAG + " onError ");

                    handleFailed(code, msg);
                }

                @Override
                public void onRequestResult(int adNumber) {
                    EALog.high(TAG + "onRequestResult，广告填充数量：" + adNumber);
                }

                @Override
                public void onSplashScreenAdLoad(@NonNull KsSplashScreenAd splashScreenAd) {
                    EALog.high(TAG + "onSplashScreenAdLoad");

                    try {
                        if (splashScreenAd == null) {
                            String nMsg = TAG + " KsSplashScreenAd null";
                            handleFailed(EasyAdError.ERROR_DATA_NULL, nMsg);
                            return;
                        }
                        splashAd = splashScreenAd;

                        handleSucceed();

                    } catch (Throwable e) {
                        e.printStackTrace();
                        handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
                    }
                }
            });

        }
    }

    @Override
    protected void doShowAD() {

        if (splashAd != null) {
            //获取SplashView
            View view = splashAd.getView(getActivity(), this);
            //渲染之前判断activity生命周期状态
            if (!EAUtil.isActivityDestroyed(softReferenceActivity)) {
                adContainer.removeAllViews();
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                adContainer.addView(view);

            }
        }

    }

    @Override
    public void doDestroy() {

    }


    //------广告回调事件------

    @Override
    public void onAdClicked() {
        EALog.high(TAG + "onAdClicked");

        handleClick();
    }

    @Override
    public void onAdShowError(int code, String extra) {
        String msg = ",开屏广告显示错误 ,code =" + code + " extra " + extra;
        EALog.high(TAG + "onAdShowError" + msg);

        //异常时不触发显示miniWindow
        splashAd = null;
        //按照渲染异常进行异常回调
        handleFailed(EasyAdError.ERROR_EXCEPTION_RENDER, msg);
    }

    @Override
    public void onAdShowEnd() {
        EALog.high(TAG + "onAdShowEnd");

        if (mSplashSetting != null) {
            mSplashSetting.adapterDidTimeOver(sdkSupplier);
        }
    }

    @Override
    public void onAdShowStart() {
        EALog.high(TAG + "onAdShowStart");

        handleExposure();
    }

    @Override
    public void onSkippedAd() {
        EALog.high(TAG + "onSkippedAd");
        if (mSplashSetting != null) {
            mSplashSetting.adapterDidSkip(sdkSupplier);
        }
    }

    @Override
    public void onDownloadTipsDialogShow() {
        EALog.high(TAG + "onDownloadTipsDialogShow");

    }

    @Override
    public void onDownloadTipsDialogDismiss() {
        EALog.high(TAG + "onDownloadTipsDialogDismiss");

    }

    @Override
    public void onDownloadTipsDialogCancel() {
        EALog.high(TAG + "onDownloadTipsDialogCancel");

    }
}

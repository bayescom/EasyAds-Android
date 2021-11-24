package com.easyads.supplier.baidu;

import android.app.Activity;
import android.os.Handler;

import com.easyads.core.splash.EASplashSetting;
import com.easyads.custom.EASplashCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.baidu.mobads.sdk.api.RequestParameters;
import com.baidu.mobads.sdk.api.SplashAd;
import com.baidu.mobads.sdk.api.SplashInteractionListener;

import java.lang.ref.SoftReference;

public class BDSplashAdapter extends EASplashCustomAdapter implements SplashInteractionListener {
    private SplashAd splashAd;
    private RequestParameters parameters;
    private boolean isCountingEnd = false;//用来判断是否倒计时走到了最后，false 回调dismiss的话代表是跳过，否则倒计时结束


    public BDSplashAdapter(SoftReference<Activity> softReferenceActivity, EASplashSetting setting) {
        super(softReferenceActivity, setting);

        parameters = EasyBDManager.getInstance().splashParameters;
    }

    @Override
    protected void doLoadAD() {
        initSplash();
        splashAd.load();
    }

    private void initSplash() {
        BDUtil.initBDAccount(this);
        splashAd = new SplashAd(getActivity(), sdkSupplier.adspotId, parameters, this);
    }

    @Override
    protected void doShowAD() {
        splashAd.show(adContainer);
    }

    @Override
    public void doDestroy() {
        if (splashAd != null) {
            splashAd.destroy();
            splashAd = null;
        }
    }


    /**
     * 以下为监听的广告事件
     */

    @Override
    public void onLpClosed() {
//落地页关闭回调
        EALog.high(TAG + "onLpClosed");
    }

    @Override
    public void onAdPresent() {
        EALog.high(TAG + "onAdPresent");

        //进行辅助判断倒计时操作的定时任务
        try {
            handleExposure();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isCountingEnd = true;
                }
            }, 4800);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onADLoaded() {
        EALog.high(TAG + "onADLoaded ");

        handleSucceed();
    }

    @Override
    public void onAdClick() {
        EALog.high(TAG + "onAdClick");
        handleClick();
    }

    @Override
    public void onAdCacheSuccess() {
        EALog.high(TAG + "onAdCacheSuccess");

    }

    @Override
    public void onAdCacheFailed() {
        EALog.high(TAG + "onAdCacheFailed");

    }

    @Override
    public void onAdDismissed() {
        EALog.high(TAG + "onAdDismissed");

        if (mSplashSetting != null) {
            if (isCountingEnd) {
                mSplashSetting.adapterDidTimeOver(sdkSupplier);
            } else {
                mSplashSetting.adapterDidSkip(sdkSupplier);
            }
        }
    }

    @Override
    public void onAdFailed(String s) {
        EALog.high(TAG + "onAdFailed reason:" + s);

        handleFailed(EasyAdError.ERROR_BD_FAILED, s);
    }
}

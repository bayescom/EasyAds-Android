package com.easyads.supplier.ylh;

import android.app.Activity;

import com.easyads.core.inter.EAInterstitialSetting;
import com.easyads.custom.EAInterstitialCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.lang.ref.SoftReference;

public class YlhInterstitialAdapter extends EAInterstitialCustomAdapter implements UnifiedInterstitialADListener {
    private EAInterstitialSetting setting;
    private UnifiedInterstitialAD interstitialAD;


    public YlhInterstitialAdapter(SoftReference<Activity> activity, EAInterstitialSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    public void doDestroy() {
        if (null != interstitialAD) {
            interstitialAD.destroy();
        }
    }

    @Override
    protected void doLoadAD() {
        YlhUtil.initAD(this);
        interstitialAD = new UnifiedInterstitialAD(getActivity(), sdkSupplier.adspotId, this);
        interstitialAD.loadAD();
    }

    @Override
    protected void doShowAD() {
        if (null != interstitialAD) {
            interstitialAD.show();
        }
    }


    @Override
    public void onADReceive() {
        EALog.high(TAG + "onADReceive");

        handleSucceed();

        // onADReceive之后才能调用getAdPatternType()
        if (interstitialAD != null && interstitialAD.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            interstitialAD.setMediaListener(setting.getYlhMediaListener());
        }
    }

    @Override
    public void onVideoCached() {
        EALog.high(TAG + "onVideoCached");

    }

    @Override
    public void onNoAD(AdError adError) {
        try {
            int code = -1;
            String msg = "default onNoAD";
            if (adError != null) {
                code = adError.getErrorCode();
                msg = adError.getErrorMsg();
            }
            EALog.high(TAG + "onNoAD " + code + msg);
            EasyAdError error = EasyAdError.parseErr(code, msg);
            handleFailed(error);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onADOpened() {
        EALog.high(TAG + "onADOpened");

    }

    @Override
    public void onADExposure() {
        EALog.high(TAG + "onADExposure");

        handleExposure();
    }

    @Override
    public void onADClicked() {
        EALog.high(TAG + "onADClicked");

        handleClick();
    }

    @Override
    public void onADLeftApplication() {
        EALog.high(TAG + "onADLeftApplication");

    }

    @Override
    public void onADClosed() {
        EALog.high(TAG + "onADClosed");

        if (null != setting) {
            setting.adapterDidClosed(sdkSupplier);
        }
    }

    @Override
    public void onRenderSuccess() {
        EALog.high(TAG + "onRenderSuccess");
    }

    @Override
    public void onRenderFail() {
        EALog.high(TAG + "onRenderFail");
        handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_RENDER_FAILED));
    }
}

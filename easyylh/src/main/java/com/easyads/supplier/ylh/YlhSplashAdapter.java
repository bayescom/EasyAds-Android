package com.easyads.supplier.ylh;

import android.app.Activity;
import android.os.SystemClock;

import com.easyads.EasyAdsManger;
import com.easyads.core.splash.EASplashSetting;
import com.easyads.custom.EASplashCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADZoomOutListener;
import com.qq.e.comm.util.AdError;

import java.lang.ref.SoftReference;

public class YlhSplashAdapter extends EASplashCustomAdapter {

    private long remainTime = 5000;
    private boolean isClicked = false;
    private SplashAD splashAD;

    public YlhSplashAdapter(SoftReference<Activity> activity, EASplashSetting setting) {
        super(activity, setting);
    }

    @Override
    public void doShowAD() {
        splashAD.showAd(adContainer);
    }

    @Override
    public void doLoadAD() {
        initAD();
        splashAD.fetchAdOnly();
    }

    @Override
    public void doDestroy() {
    }


    private void initAD() {
        YlhUtil.initAD(this);
        int timeout = 5000;
        SplashADZoomOutListener listener = new SplashADZoomOutListener() {
            @Override
            public void onZoomOut() {
                EALog.high(TAG + "onZoomOut ");
                zoomOut();
            }

            @Override
            public void onZoomOutPlayFinish() {
                EALog.high(TAG + "onZoomOutPlayFinish ");

            }

            @Override
            public boolean isSupportZoomOut() {
                EALog.high(TAG + "isSupportZoomOut ");
                return true;
            }

            @Override
            public void onADDismissed() {
                EALog.high(TAG + "onADDismissed ");
                if (null != mSplashSetting) {
                    //剩余时长在600ms以上，且未点击才按照跳过
                    if (remainTime >= 600 && !isClicked) {
                        mSplashSetting.adapterDidSkip(sdkSupplier);
                    } else {
                        mSplashSetting.adapterDidTimeOver(sdkSupplier);
                    }

                }
            }

            @Override
            public void onNoAD(AdError adError) {
                int code = -1;
                String msg = "default onNoAD";
                if (adError != null) {
                    code = adError.getErrorCode();
                    msg = adError.getErrorMsg();
                }
                EALog.high(TAG + "onNoAD");

                handleFailed(code, msg);
                preLoad();
            }

            @Override
            public void onADPresent() {
                EALog.high(TAG + "onADPresent ");

            }

            @Override
            public void onADClicked() {
                EALog.high(TAG + "onADClicked ");

                handleClick();
                isClicked = true;
            }

            @Override
            public void onADTick(long l) {
                EALog.high(TAG + "onADTick :" + l);
                remainTime = l;

            }

            @Override
            public void onADExposure() {
                EALog.high(TAG + "onADExposure ");
                handleExposure();
                preLoad();
            }

            @Override
            public void onADLoaded(long expireTimestamp) {
                try {
                    EALog.high(TAG + "onADLoaded expireTimestamp:" + expireTimestamp);

                    handleSucceed();
                    if (splashAD != null)
                        EALog.max(TAG + "getECPMLevel = " + splashAD.getECPMLevel());

                    long rt = SystemClock.elapsedRealtime();
                    long expire = expireTimestamp - rt;
                    EALog.high(TAG + "ad will expired in :" + expire + " ms");

                } catch (Throwable e) {
                    e.printStackTrace();
                    handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
                }

            }
        };

        splashAD = new SplashAD(getActivity(), sdkSupplier.adspotId, listener, timeout);
    }

    private void zoomOut() {
        try {
            SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance();
            zoomOutManager.initSize(getActivity());
            zoomOutManager.setSplashInfo(splashAD, adContainer.getChildAt(0),
                    getActivity().getWindow().getDecorView());
            if (mSplashSetting == null) {
                return;
            }
            if (mSplashSetting.isShowInSingleActivity()) {
                new YlhUtil().zoomOut(getActivity());
            } else {
                EasyAdsManger.getInstance().isSplashSupportZoomOut = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //当广告曝光、广告失败的时候再执行广告预加载，避免影响当前展示
    private void preLoad() {
        try {
            //预加载素材，会有频次限制，目前是交给优量汇自己来控制，不做额外频次控制
            if (splashAD != null)
                splashAD.preLoad();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

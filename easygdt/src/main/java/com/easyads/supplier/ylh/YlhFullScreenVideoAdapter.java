package com.easyads.supplier.ylh;

import android.app.Activity;

import com.easyads.core.full.EAFullScreenVideoSetting;
import com.easyads.custom.EAFullScreenCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;
import com.qq.e.comm.util.AdError;

import java.lang.ref.SoftReference;

public class YlhFullScreenVideoAdapter extends EAFullScreenCustomAdapter implements UnifiedInterstitialADListener {
    private EAFullScreenVideoSetting setting;

    private UnifiedInterstitialAD iad;
    private long videoDuration;
    private long videoStartTime;

    public YlhFullScreenVideoAdapter(SoftReference<Activity> activity, EAFullScreenVideoSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    public void onADReceive() {
        try {
            EALog.high(TAG + "onADReceive");

            handleSucceed();
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }

    //虽然有此回调，但是返回该事件的时机不固定。。。
    @Override
    public void onVideoCached() {
        EALog.high(TAG + "onVideoCached");


        handleCached();
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
            EALog.high(TAG + " onNoAD");
            handleFailed(code, msg);
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

        if (setting != null) {
            long costTime = System.currentTimeMillis() - videoStartTime;
            EALog.high(TAG + "costTime ==   " + costTime + " videoDuration == " + videoDuration);

            if (costTime < videoDuration) {
                EALog.high(TAG + " adapterVideoSkipped");
                setting.adapterVideoSkipped(sdkSupplier);
            }
            EALog.high(TAG + " adapterClose");
            setting.adapterClose(sdkSupplier);
        }
    }

    @Override
    public void onRenderSuccess() {
        EALog.high(TAG + "onRenderSuccess");

    }

    @Override
    public void onRenderFail() {
        EALog.high(TAG + "onRenderFail");
        handleFailed(EasyAdError.ERROR_RENDER_FAILED, "");
    }


    @Override
    protected void doLoadAD() {
        YlhUtil.initAD(this);

        iad = new UnifiedInterstitialAD(getActivity(), sdkSupplier.adspotId, this);
        //用来获取视频时长
        iad.setMediaListener(new UnifiedInterstitialMediaListener() {
            @Override
            public void onVideoInit() {
                EALog.high(TAG + " onVideoInit");

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoInit();
            }

            @Override
            public void onVideoLoading() {
                EALog.high(TAG + " onVideoLoading");

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoLoading();
            }

            @Override
            public void onVideoReady(long l) {
                EALog.high(TAG + " onVideoReady, videoDuration = " + l);
                try {
                    if (setting != null && setting.getYlhMediaListener() != null)
                        setting.getYlhMediaListener().onVideoReady(l);
                    videoStartTime = System.currentTimeMillis();
                    videoDuration = l;

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onVideoStart() {
                EALog.high(TAG + " onVideoStart");

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoStart();
            }

            @Override
            public void onVideoPause() {
                EALog.high(TAG + " onVideoPause");

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoPause();
            }

            @Override
            public void onVideoComplete() {
                EALog.high(TAG + " onVideoComplete");

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoComplete();

                if (null != setting) {
                    setting.adapterVideoComplete(sdkSupplier);
                }

            }

            @Override
            public void onVideoError(AdError adError) {
                EALog.high(TAG + " onVideoError ");
                String msgInf = "";
                if (adError != null) {
                    EALog.high(TAG + " ErrorCode: " + adError.getErrorCode() + ", ErrorMsg: " + adError.getErrorMsg());
                    msgInf = TAG + adError.getErrorCode() + "， " + adError.getErrorMsg();
                }

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoError(adError);

                handleFailed(EasyAdError.ERROR_RENDER_FAILED, msgInf);
            }

            @Override
            public void onVideoPageOpen() {
                EALog.high(TAG + "onVideoPageOpen ");

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoPageOpen();
            }

            @Override
            public void onVideoPageClose() {
                EALog.high(TAG + " onVideoPageClose");

                if (setting != null && setting.getYlhMediaListener() != null)
                    setting.getYlhMediaListener().onVideoPageClose();
            }
        });
        VideoOption videoOption;
        if (setting != null && setting.getYlhVideoOption() != null) {
            videoOption = setting.getYlhVideoOption();
        } else {
            videoOption = new VideoOption.Builder().setAutoPlayMuted(false)
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS)
                    .build();
        }

        iad.setMinVideoDuration(0);
        iad.setMaxVideoDuration(60);
        iad.setVideoOption(videoOption);
        iad.loadFullScreenAD();
    }


    @Override
    public void doDestroy() {

    }

    @Override
    protected void doShowAD() {
        if (iad != null) {
            iad.showFullScreenAD(getActivity());
        }
    }
}

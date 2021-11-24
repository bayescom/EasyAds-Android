package com.easyads.supplier.ks;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.easyads.core.draw.EADrawSetting;
import com.easyads.custom.EADrawCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsDrawAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;

import java.lang.ref.SoftReference;
import java.util.List;

public class KSDrawAdapter extends EADrawCustomAdapter implements KsDrawAd.AdInteractionListener {
    private KsDrawAd drawAD;

    public KSDrawAdapter(SoftReference<Activity> activity, EADrawSetting setting) {
        super(activity, setting);
    }


    @Override
    protected void doLoadAD() {

        if (KSUtil.initAD(this)) {
            //场景设置
            KsScene scene = new KsScene.Builder(KSUtil.getADID(sdkSupplier)).build();
            KsAdSDK.getLoadManager().loadDrawAd(scene, new KsLoadManager.DrawAdListener() {
                @Override
                public void onError(int code, String msg) {
                    EALog.high(TAG + " onError " + code + msg);

                    handleFailed(code, msg);
                }

                @Override
                public void onDrawAdLoad(@Nullable List<KsDrawAd> list) {
                    EALog.high(TAG + "onDrawAdLoad");

                    try {
                        if (list == null || list.size() == 0 || list.get(0) == null) {
                            handleFailed(EasyAdError.ERROR_DATA_NULL, "");
                        } else {
                            drawAD = list.get(0);
                            //回调监听
                            if (drawAD != null) {
                                drawAD.setAdInteractionListener(KSDrawAdapter.this);
                            }
                            handleSucceed();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        handleFailed(EasyAdError.ERROR_EXCEPTION_LOAD, "");
                    }
                }
            });
        }
    }


    @Override
    public void doDestroy() {

    }

    @Override
    protected void doShowAD() {
        View drawVideoView = drawAD.getDrawView(getActivity());
        if (addADView(drawVideoView)) {

        }
    }

    /**
     * ks回调事件
     */

    @Override
    public void onAdClicked() {
        EALog.high(TAG + " onAdClicked");

        handleClick();
    }

    @Override
    public void onAdShow() {
        EALog.high(TAG + " onAdShow");

        handleExposure();
    }

    @Override
    public void onVideoPlayStart() {
        EALog.high(TAG + " onVideoPlayStart");

    }

    @Override
    public void onVideoPlayPause() {
        EALog.high(TAG + " onVideoPlayPause");

    }

    @Override
    public void onVideoPlayResume() {
        EALog.high(TAG + " onVideoPlayResume");

    }

    @Override
    public void onVideoPlayEnd() {
        EALog.high(TAG + " onVideoPlayEnd");

    }

    @Override
    public void onVideoPlayError() {
        EALog.high(TAG + " onVideoPlayError");

    }
}

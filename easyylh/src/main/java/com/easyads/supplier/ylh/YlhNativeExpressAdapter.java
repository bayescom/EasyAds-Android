package com.easyads.supplier.ylh;

import android.app.Activity;
import android.view.View;

import com.easyads.core.nati.EANativeExpressSetting;
import com.easyads.custom.EANativeExpressCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.qq.e.ads.cfg.DownAPPConfirmPolicy;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.lang.ref.SoftReference;
import java.util.List;

import static com.easyads.model.EasyAdError.ERROR_DATA_NULL;

public class YlhNativeExpressAdapter extends EANativeExpressCustomAdapter {
    private EANativeExpressSetting setting;
    NativeExpressADView adView;
    boolean isVideoMute = false;

    public YlhNativeExpressAdapter(SoftReference<Activity> activity, EANativeExpressSetting setting) {
        super(activity, setting);
        try {
            this.setting = setting;
            isVideoMute = setting.isVideoMute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doLoadAD() {
        YlhUtil.initAD(this);

        int width = setting.getExpressViewWidth();
        int height = setting.getExpressViewHeight();

        // 验证高度为0时为自适应
        ADSize adSize = new ADSize(width, height);
        NativeExpressAD nativeExpressAd = new NativeExpressAD(getActivity(), adSize, sdkSupplier.adspotId, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                onADLoadedEV(list);
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                onRenderFailEV(nativeExpressADView);
            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                onRenderSuccessEV(nativeExpressADView);
            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                onADExposureEV(nativeExpressADView);
            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                onADClickedEV(nativeExpressADView);

            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                onADClosedEV(nativeExpressADView);

            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onNoAD(AdError adError) {
                onNoADEV(adError);
            }
        }); // 这里的Context必须为Activity
        VideoOption option = new VideoOption.Builder()
                .setAutoPlayMuted(isVideoMute)
                .build();
        nativeExpressAd.setVideoOption(option);
        nativeExpressAd.setMaxVideoDuration(setting.getYlhMaxVideoDuration());
        nativeExpressAd.setDownAPPConfirmPolicy(DownAPPConfirmPolicy.NOConfirm);
        nativeExpressAd.loadAD(1);

    }


    public void onADLoadedEV(List<NativeExpressADView> list) {
        EALog.high(TAG + "onADLoadedEV");

        boolean isEmpty = list == null || list.isEmpty();
        if (isEmpty) {
            handleFailed(EasyAdError.parseErr(ERROR_DATA_NULL));
            return;
        }

        boolean isAllDataNull = true;
        for (NativeExpressADView nativeExpressADView : list) {
            isAllDataNull = isAllDataNull && nativeExpressADView == null;
        }

        if (isAllDataNull) {
            handleFailed(EasyAdError.parseErr(ERROR_DATA_NULL));
            return;
        }
        adView = list.get(0);
        addADView(adView);

        handleSucceed();
    }


    public void onRenderFailEV(View nativeExpressADView) {
        EALog.high(TAG + "onRenderFailEV");
        setNEView(nativeExpressADView);

        if (setting != null)
            setting.adapterRenderFailed(sdkSupplier);

        handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_RENDER_FAILED));
        removeADView();
    }

    public void onRenderSuccessEV(View nativeExpressADView) {
        EALog.high(TAG + "onRenderSuccessEV");
        setNEView(nativeExpressADView);

        if (setting != null)
            setting.adapterRenderSuccess(sdkSupplier);

    }

    public void onADExposureEV(View nativeExpressADView) {
        EALog.high(TAG + "onADExposureEV");

        setNEView(nativeExpressADView);
        handleExposure();
    }

    public void onADClickedEV(View nativeExpressADView) {
        EALog.high(TAG + "onADClickedEV");
        setNEView(nativeExpressADView);

        handleClick();
    }

    public void onADClosedEV(View nativeExpressADView) {
        EALog.high(TAG + "onADClosedEV");
        setNEView(nativeExpressADView);

        if (setting != null)
            setting.adapterDidClosed(sdkSupplier);

        removeADView();
    }


    public void onNoADEV(AdError adError) {
        EALog.high(TAG + "onNoADEV");
        int code = -1;
        String msg = "default onNoAD";
        if (adError != null) {
            code = adError.getErrorCode();
            msg = adError.getErrorMsg();
        }
        handleFailed(code, msg);
    }

    @Override
    public void doDestroy() {

    }

    @Override
    protected void doShowAD() {
        adView.render();
    }
}

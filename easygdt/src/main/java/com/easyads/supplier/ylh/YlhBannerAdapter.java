package com.easyads.supplier.ylh;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.easyads.core.banner.EABannerSetting;
import com.easyads.custom.EABannerCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;

import java.lang.ref.SoftReference;

public class YlhBannerAdapter extends EABannerCustomAdapter implements UnifiedBannerADListener {
    private EABannerSetting setting;
    private UnifiedBannerView bv;

    public YlhBannerAdapter(SoftReference<Activity> activity, EABannerSetting setting) {
        super(activity, setting);
        this.setting = setting;
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
            EALog.e(" onError: code = " + code + " msg = " + msg);
            EasyAdError easyAdError = EasyAdError.parseErr(code, msg);

            doBannerFailed(easyAdError);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onADReceive() {
        try {
            EALog.high(TAG + "onADReceive");

            if (setting != null) {
                int refreshValue = setting.getRefreshInterval();
                EALog.high("refreshValue == " + refreshValue);

                if (refreshValue > 0) {
                    //当收到广告后，且有设置刷新间隔，代表目前正在刷新中
                    refreshing = true;
                }
            }

            handleSucceed();
        } catch (Throwable e) {
            e.printStackTrace();
            doBannerFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }

    @Override
    public void onADExposure() {
        EALog.high(TAG + "onADExposure");

        handleExposure();
    }

    @Override
    public void onADClosed() {
        EALog.high(TAG + "onADClosed");

        if (null != setting) {
            setting.adapterDidDislike(sdkSupplier);
        }
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
    public void onADOpenOverlay() {
        EALog.high(TAG + "onADOpenOverlay");

    }

    @Override
    public void onADCloseOverlay() {
        EALog.high(TAG + "onADCloseOverlay");

    }

    @Override
    protected void doLoadAD() {
        YlhUtil.initAD(this);
        bv = new UnifiedBannerView(getActivity(), sdkSupplier.adspotId, this);
        if (setting != null) {
            int refreshValue = setting.getRefreshInterval();
            bv.setRefresh(refreshValue);
        }
        /* 发起广告请求，收到广告数据后会展示数据   */
        bv.loadAD();
    }

    @Override
    protected void doShowAD() {
        if (null != setting) {
            ViewGroup adContainer = setting.getContainer();
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.addView(bv, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @Override
    public void doDestroy() {
        if (null != bv) {
            bv.destroy();
        }
    }
}

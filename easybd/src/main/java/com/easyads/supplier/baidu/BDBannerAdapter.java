package com.easyads.supplier.baidu;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.easyads.core.banner.EABannerSetting;
import com.easyads.custom.EABannerCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.baidu.mobads.sdk.api.AdView;
import com.baidu.mobads.sdk.api.AdViewListener;

import org.json.JSONObject;

import java.lang.ref.SoftReference;

/**
 * 横幅会自动刷新，刷新间隔30秒。如果网络异常，下一次加载将回调加载失败，网络恢复，不再执行刷新。故失败逻辑直接进入下一优先级的加载
 */
public class BDBannerAdapter extends EABannerCustomAdapter implements AdViewListener {

    private EABannerSetting setting;
    private AdView adView;

    public BDBannerAdapter(SoftReference<Activity> activity, EABannerSetting baseSetting) {
        super(activity, baseSetting);
        setting = baseSetting;
    }

    @Override
    protected void doLoadAD() {

        if (sdkSupplier != null) {
            BDUtil.initBDAccount(this);
            adView = new AdView(getActivity(), sdkSupplier.adspotId);
            adView.setListener(this);
        }

        //必须要添加布局后，才会返回广告
        if (null != setting) {
            ViewGroup adContainer = setting.getContainer();
            if (adContainer != null) {
                adContainer.removeAllViews();
                int width = adContainer.getWidth();
                EALog.max(TAG + "adContainer width = " + width);
                if (width <= 0) {
                    width = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                adContainer.addView(adView, new RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @Override
    protected void doShowAD() {

    }


    @Override
    protected void doDestroy() {
        try {
            if (adView != null) {
                adView.destroy();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAdReady(AdView adView) {
        EALog.high(TAG + "onAdReady" + adView);

        handleSucceed();
    }

    @Override
    public void onAdShow(JSONObject jsonObject) {
        EALog.high(TAG + "onAdShow " + jsonObject);

        handleExposure();
    }

    @Override
    public void onAdClick(JSONObject jsonObject) {
        EALog.high(TAG + "onAdClick " + jsonObject);

        handleClick();
    }

    @Override
    public void onAdFailed(String s) {
        EALog.e(TAG + "onAdFailed " + s);

        handleFailed(EasyAdError.ERROR_BD_FAILED, s);
    }

    @Override
    public void onAdSwitch() {
        EALog.high(TAG + "onAdSwitch");

    }

    @Override
    public void onAdClose(JSONObject jsonObject) {
        EALog.high(TAG + "onAdClose " + jsonObject);

        if (null != setting) {
            setting.adapterDidDislike(sdkSupplier);
        }
    }
}

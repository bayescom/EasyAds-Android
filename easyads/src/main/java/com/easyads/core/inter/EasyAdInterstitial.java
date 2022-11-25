package com.easyads.core.inter;

import android.app.Activity;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.model.EasyAdType;
import com.easyads.model.SdkSupplier;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;

public class EasyAdInterstitial extends EasyAdBaseAdspot implements EAInterstitialSetting {
    private EAInterstitialListener listener;
    private UnifiedInterstitialMediaListener ylhlistener;//优量汇视频加载情况监听器
    private float csjExpressViewWidth = 300;//穿山甲模板尺寸设置，单位dp
    private float csjExpressViewHeight = 300;
    private boolean isCsjNew = true;//是否为穿山甲"新插屏广告"


    public EasyAdInterstitial(Activity activity, EAInterstitialListener listener) {
        super(activity, listener);
        adType = EasyAdType.INTR;

        this.listener = listener;
    }

    @Override
    public void initSdkSupplier() {
        try {
            initAdapter(EasyAdsConstant.SDK_TAG_CSJ, this);
            initAdapter(EasyAdsConstant.SDK_TAG_YLH, this);
            initAdapter(EasyAdsConstant.SDK_TAG_BAIDU, this);
            initAdapter(EasyAdsConstant.SDK_TAG_KS, this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void adapterDidClosed(SdkSupplier supplier) {
        updateSupplier("adapterDidClosed", supplier);
        if (null != listener) {
            listener.onAdClose();
        }
        destroy();
    }


    //期望模板广告view的size,单位dp；注意：参数请按照平台勾选的比例去进行请求。现有1:1，3:2 ，2:3 三种比例可供选择。
    public void setCsjExpressViewAcceptedSize(float expressViewWidth, float expressViewHeight) {
        this.csjExpressViewWidth = expressViewWidth;
        this.csjExpressViewHeight = expressViewHeight;
    }

    @Deprecated
    public void setCsjNew(boolean csjNew) {
        isCsjNew = csjNew;
    }

    public void setYlhMediaListener(UnifiedInterstitialMediaListener listener) {
        ylhlistener = listener;
    }

    public UnifiedInterstitialMediaListener getYlhMediaListener() {
        return ylhlistener;
    }


    public float getCsjExpressViewWidth() {
        return csjExpressViewWidth;
    }

    public float getCsjExpressViewHeight() {
        return csjExpressViewHeight;
    }


    @Override
    public boolean isCsjNew() {
        return isCsjNew;
    }


}

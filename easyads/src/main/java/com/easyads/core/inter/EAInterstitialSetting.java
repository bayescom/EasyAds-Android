package com.easyads.core.inter;

import com.easyads.itf.BaseAdapterEvent;
import com.easyads.model.SdkSupplier;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;

public interface EAInterstitialSetting extends BaseAdapterEvent {
    void adapterDidClosed(SdkSupplier sdkSupplier);

    float getCsjExpressViewWidth();

    float getCsjExpressViewHeight();

    UnifiedInterstitialMediaListener getYlhMediaListener();

    boolean isCsjNew();

}

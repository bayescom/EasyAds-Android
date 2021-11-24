package com.easyads.core.full;

import com.easyads.itf.BaseAdapterEvent;
import com.easyads.model.SdkSupplier;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;

public interface EAFullScreenVideoSetting extends BaseAdapterEvent {

    void adapterVideoCached(SdkSupplier sdkSupplier);

    void adapterVideoComplete(SdkSupplier sdkSupplier);

    void adapterClose(SdkSupplier sdkSupplier);

    void adapterVideoSkipped(SdkSupplier sdkSupplier);

    UnifiedInterstitialMediaListener getYlhMediaListener();

    VideoOption getYlhVideoOption();

}

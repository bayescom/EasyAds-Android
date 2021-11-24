package com.easyads.core.splash;

import android.view.ViewGroup;

import com.easyads.itf.BaseAdapterEvent;
import com.easyads.model.SdkSupplier;

public interface EASplashSetting extends BaseAdapterEvent {

    int getCsjAcceptedSizeWidth();

    int getCsjAcceptedSizeHeight();

    float getCsjExpressViewWidth();

    float getCsjExpressViewHeight();

    boolean getCsjShowAsExpress();

    void adapterDidSkip(SdkSupplier sdkSupplier);

    void adapterDidTimeOver(SdkSupplier sdkSupplier);

    ViewGroup getAdContainer();

    boolean isShowInSingleActivity();
}

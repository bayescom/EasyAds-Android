package com.easyads.core.nati;

import android.view.View;
import android.view.ViewGroup;

import com.easyads.itf.BaseAdapterEvent;
import com.easyads.model.SdkSupplier;

public interface EANativeExpressSetting extends BaseAdapterEvent {

    int getExpressViewWidth();

    int getExpressViewHeight();

    boolean isVideoMute();

    void adapterRenderFailed(SdkSupplier sdkSupplier);

    void adapterRenderSuccess(SdkSupplier sdkSupplier);

    void adapterDidClosed(SdkSupplier sdkSupplier);

    void setNativeExpressADView(View expressADView);

    int getYlhMaxVideoDuration();

    ViewGroup getAdContainer();
}

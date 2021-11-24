package com.easyads.core.draw;

import android.view.ViewGroup;

import com.easyads.itf.BaseAdapterEvent;

public interface EADrawSetting extends BaseAdapterEvent {
    ViewGroup getContainer();

    int getCsjExpressHeight();

    int getCsjExpressWidth();
}

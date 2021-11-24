package com.easyads.custom;

import android.app.Activity;

import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.core.inter.EAInterstitialSetting;

import java.lang.ref.SoftReference;

public abstract class EAInterstitialCustomAdapter extends EABaseSupplierAdapter {
    EAInterstitialSetting mInterSetting;

    public EAInterstitialCustomAdapter(SoftReference<Activity> activity, EAInterstitialSetting setting) {
        super(activity, setting);
        mInterSetting = setting;
    }

}

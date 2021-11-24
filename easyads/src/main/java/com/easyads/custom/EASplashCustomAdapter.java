package com.easyads.custom;

import android.app.Activity;
import android.view.ViewGroup;

import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.core.splash.EASplashSetting;

import java.lang.ref.SoftReference;

public abstract class EASplashCustomAdapter extends EABaseSupplierAdapter {
    public EASplashSetting mSplashSetting;
    public ViewGroup adContainer;

    public EASplashCustomAdapter(SoftReference<Activity> activity, final EASplashSetting setting) {
        super(activity, setting);
        this.mSplashSetting = setting;
        try {
            if (setting != null) {
                adContainer = setting.getAdContainer();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



}

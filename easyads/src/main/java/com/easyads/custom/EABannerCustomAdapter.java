package com.easyads.custom;

import android.app.Activity;

import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.itf.BaseAdapterEvent;

import java.lang.ref.SoftReference;

public abstract class EABannerCustomAdapter extends EABaseSupplierAdapter {

    public EABannerCustomAdapter(SoftReference<Activity> softReferenceActivity, BaseAdapterEvent baseListener) {
        super(softReferenceActivity, baseListener);
    }
}

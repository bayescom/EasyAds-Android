package com.easyads.custom;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.core.nati.EANativeExpressSetting;
import com.easyads.utils.EALog;

import java.lang.ref.SoftReference;

public abstract class EANativeExpressCustomAdapter extends EABaseSupplierAdapter {
    EANativeExpressSetting mNativeSetting;

    public EANativeExpressCustomAdapter(SoftReference<Activity> activity, EANativeExpressSetting baseSetting) {
        super(activity, baseSetting);
        mNativeSetting = baseSetting;
    }

    public void addADView(View adView) {
        try {
            ViewGroup adContainer = mNativeSetting.getAdContainer();
            if (adContainer == null) {
                EALog.e("adContainer 不存在，请先调用setAdContainer() 方法设置adContainer");
                return;
            }
            if (adContainer.getChildCount() > 0
                    && adContainer.getChildAt(0) == adView) {
                EALog.high("已添加的布局");
                return;
            }
            if (adContainer.getChildCount() > 0) {
                adContainer.removeAllViews();
            }
            if (adView != null) {
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }
                adContainer.setVisibility(View.VISIBLE);
                EALog.max("add adContainer = " + adContainer.toString());
                // 广告可见才会产生曝光，否则将无法产生收益。
                adContainer.addView(adView);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void removeADView() {
        try {
            ViewGroup adContainer = mNativeSetting.getAdContainer();
            if (adContainer == null) {
                EALog.e("adContainer 不存在");
                return;
            }
            EALog.max("remove adContainer = " + adContainer.toString());

            adContainer.removeAllViews();
            setNEView(null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setNEView(View view) {
        try {
            if (view == null) {
                return;
            }
            mNativeSetting.setNativeExpressADView(view);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

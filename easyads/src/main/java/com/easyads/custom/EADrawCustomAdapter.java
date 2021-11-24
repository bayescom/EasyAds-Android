package com.easyads.custom;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.core.draw.EADrawSetting;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;

import java.lang.ref.SoftReference;

public abstract class EADrawCustomAdapter extends EABaseSupplierAdapter {
    protected EADrawSetting mDrawSetting;

    public EADrawCustomAdapter(SoftReference<Activity> activity, EADrawSetting setting) {
        super(activity, setting);
        this.mDrawSetting = setting;
    }


    public boolean addADView(View adView) {
        boolean hasAdded = false;
        try {
            if (mDrawSetting != null) {
                ViewGroup adC = mDrawSetting.getContainer();
                if (adC != null) {
                    adC.removeAllViews();
                    if (adView != null) {
                        ViewParent parent = adView.getParent();
                        if (parent != null) {
                            ((ViewGroup) parent).removeView(adView);
                        }
                    }
                    adC.addView(adView);
                    hasAdded = true;
                } else {
                    EALog.e(TAG + "无法展示广告，原因：未设置广告承载布局，请检查 setAdContainer(ViewGroup adContainer) 方法是否有赋值");
                }
            } else {
                EALog.e(TAG + "无法展示广告，原因：内部处理异常，mDrawSetting 为空");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (!hasAdded) {
            handleFailed(EasyAdError.ERROR_RENDER_FAILED, "添加广告视图操作失败");
        } else {
            EALog.simple(TAG + "ADView has Added");
        }
        return hasAdded;
    }

}

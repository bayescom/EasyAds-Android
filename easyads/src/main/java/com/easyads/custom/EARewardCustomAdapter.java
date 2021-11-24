package com.easyads.custom;

import android.app.Activity;

import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.core.reward.EARewardVideoSetting;

import java.lang.ref.SoftReference;

public abstract class EARewardCustomAdapter extends EABaseSupplierAdapter {
    public EARewardVideoSetting rewardSetting;

    public EARewardCustomAdapter(SoftReference<Activity> softReferenceActivity, EARewardVideoSetting setting) {
        super(softReferenceActivity, setting);
        this.rewardSetting = setting;
    }


    public void handleCached() {
        try {
            if (null != rewardSetting) {
                rewardSetting.adapterVideoCached(sdkSupplier);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

package com.easyads.supplier.ylh;

import android.app.Activity;
import android.os.SystemClock;

import com.easyads.core.reward.EARewardServerCallBackInf;
import com.easyads.core.reward.EARewardVideoSetting;
import com.easyads.custom.EARewardCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.ads.rewardvideo.ServerSideVerificationOptions;
import com.qq.e.comm.util.AdError;

import java.lang.ref.SoftReference;
import java.util.Map;

import static com.easyads.model.EasyAdError.ERROR_EXCEPTION_SHOW;

public class YlhRewardVideoAdapter extends EARewardCustomAdapter implements RewardVideoADListener {

    private EARewardVideoSetting setting;
    public RewardVideoAD rewardVideoAD;

    public YlhRewardVideoAdapter(SoftReference<Activity> activity, EARewardVideoSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    public void onADLoad() {
        EALog.high(TAG + "onADLoad");

        handleSucceed();
    }

    @Override
    public void onVideoCached() {
        EALog.high(TAG + "onVideoCached");

        handleCached();
    }

    @Override
    public void onADShow() {
        EALog.high(TAG + "onADShow");

    }

    @Override
    public void onADExpose() {
        EALog.high(TAG + "onADExpose");

        handleExposure();
    }

    @Override
    public void onReward(Map<String, Object> map) {
        try {
            EALog.high(TAG + "onReward");

            if (setting != null) {
                setting.adapterAdReward(sdkSupplier);

                EARewardServerCallBackInf inf = new EARewardServerCallBackInf();
                inf.ylhRewardMap = map;
                setting.postRewardServerInf(inf, sdkSupplier);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onADClick() {
        EALog.high(TAG + "onADClick");

        handleClick();
    }

    @Override
    public void onVideoComplete() {
        EALog.high(TAG + "onVideoComplete");

        if (setting != null) {
            setting.adapterVideoComplete(sdkSupplier);
        }
    }

    @Override
    public void onADClose() {
        EALog.high(TAG + "onADClose");

        if (setting != null)
            setting.adapterDidClosed(sdkSupplier);
    }

    @Override
    public void onError(AdError adError) {
        try {
            int code = -1;
            String msg = "default onNoAD";
            if (adError != null) {
                code = adError.getErrorCode();
                msg = adError.getErrorMsg();
            }
            EALog.high(TAG + "onError");
            handleFailed(code, msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doLoadAD() {
        YlhUtil.initAD(this);

        boolean vo = false;
        ServerSideVerificationOptions ssv0 = null;
        if (setting != null) {
            vo = setting.isYlhVolumeOn();
            ssv0 = setting.getYlhSSVO();
        }

        rewardVideoAD = new RewardVideoAD(getActivity(), sdkSupplier.adspotId, this, vo);
        if (ssv0 != null) {
            rewardVideoAD.setServerSideVerificationOptions(ssv0);
        }
        rewardVideoAD.loadAD();
    }

    @Override
    protected void doShowAD() {
        if (checkRewardOk()) {
            rewardVideoAD.showAD();
        } else {
            handleFailed(EasyAdError.parseErr(ERROR_EXCEPTION_SHOW, "RewardNotVis"));
        }
    }

    @Override
    public void doDestroy() {

    }


    public boolean checkRewardOk() {
        try {
            return rewardVideoAD.isValid();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }




}

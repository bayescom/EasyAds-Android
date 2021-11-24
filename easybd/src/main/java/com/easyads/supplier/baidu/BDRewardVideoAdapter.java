package com.easyads.supplier.baidu;

import android.app.Activity;

import com.easyads.core.reward.EARewardServerCallBackInf;
import com.easyads.core.reward.EARewardVideoSetting;
import com.easyads.custom.EARewardCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.baidu.mobads.sdk.api.RewardVideoAd;

import java.lang.ref.SoftReference;


public class BDRewardVideoAdapter extends EARewardCustomAdapter implements RewardVideoAd.RewardVideoAdListener {
    private RewardVideoAd mRewardVideoAd;

    public BDRewardVideoAdapter(SoftReference<Activity> activity, EARewardVideoSetting setting) {
        super(activity, setting);
    }

    @Override
    protected void doLoadAD() {
        BDUtil.initBDAccount(this);
        //使用SurfaceView会无法展示视频，第三个字段需要为false代表不使用SurfaceView
        mRewardVideoAd = new RewardVideoAd(getActivity(), sdkSupplier.adspotId, this, EasyBDManager.getInstance().rewardUseSurfaceView);
        mRewardVideoAd.setDownloadAppConfirmPolicy(EasyBDManager.getInstance().rewardDownloadAppConfirmPolicy);

        mRewardVideoAd.load();
    }

    @Override
    protected void doShowAD() {
        if (mRewardVideoAd != null) {
            // 在跳过按钮后展示弹框
            mRewardVideoAd.setShowDialogOnSkip(true);
            // 展示奖励领取倒计时提示
            mRewardVideoAd.setUseRewardCountdown(true);

            mRewardVideoAd.show();
        }
    }

    @Override
    public void doDestroy() {
    }


    //以下为广告回调事件

    @Override
    public void onAdShow() {
        EALog.high(TAG + "onAdShow");
        handleExposure();
    }

    @Override
    public void onAdClick() {
        EALog.high(TAG + "onAdClick");
        handleClick();
    }

    @Override
    public void onAdClose(float v) {
        EALog.high(TAG + "onAdClose " + v);
        if (null != rewardSetting) {
            rewardSetting.adapterDidClosed(sdkSupplier);
        }

    }

    @Override
    public void onAdFailed(String s) {
        EALog.e(TAG + "onAdFailed ，reason ：" + s);
        handleFailed(EasyAdError.ERROR_BD_FAILED, s);
    }

    @Override
    public void onVideoDownloadSuccess() {
        EALog.high(TAG + "onVideoDownloadSuccess");

        handleCached();
    }

    @Override
    public void onVideoDownloadFailed() {
        EALog.e(TAG + "onVideoDownloadFailed");
    }

    @Override
    public void playCompletion() {
        EALog.high(TAG + "playCompletion");
        if (null != rewardSetting) {
            rewardSetting.adapterVideoComplete(sdkSupplier);
        }
    }

    @Override
    public void onAdSkip(float playScale) {
        // 用户点击跳过, 展示尾帧
        // 建议：媒体可以按照自己的设计给予奖励
        EALog.high(TAG + " onSkip: playScale = " + playScale);

        if (null != rewardSetting) {
            rewardSetting.adapterVideoSkipped(sdkSupplier);
        }
    }

    @Override
    public void onRewardVerify(boolean rewardVerify) {
        try {
            EALog.high(TAG + " onRewardVerify : rewardVerify = " + rewardVerify);

            EARewardServerCallBackInf inf = new EARewardServerCallBackInf();
            inf.bdRewardVerify = rewardVerify;
            if (null != rewardSetting) {
                rewardSetting.postRewardServerInf(inf, sdkSupplier);
                if (rewardVerify) {
                    //激励达成回调
                    rewardSetting.adapterAdReward(sdkSupplier);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdLoaded() {
        EALog.high(TAG + "onAdLoaded");

        handleSucceed();
    }

}

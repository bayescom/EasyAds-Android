package com.easyads.supplier.csj;

import android.app.Activity;

import com.easyads.EasyAdsManger;
import com.easyads.core.reward.EARewardServerCallBackInf;
import com.easyads.core.reward.EARewardVideoSetting;
import com.easyads.custom.EARewardCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import java.lang.ref.SoftReference;

public class CsjRewardVideoAdapter extends EARewardCustomAdapter implements TTAdNative.RewardVideoAdListener {

    private EARewardVideoSetting setting;
    private TTRewardVideoAd ttRewardVideoAd;

    public CsjRewardVideoAdapter(SoftReference<Activity> activity, EARewardVideoSetting setting) {
        super(activity, setting);
        this.setting = setting;
    }

    @Override
    public void onError(int i, String s) {
        EALog.high(TAG + "onError，" + i + s);

        handleFailed(EasyAdError.parseErr(i, s));
    }

    @Override
    public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
        try {
            EALog.high(TAG + "onRewardVideoAdLoad");

            this.ttRewardVideoAd = ttRewardVideoAd;

            handleSucceed();
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }

    @Override
    public void onRewardVideoCached() {
        EALog.high(TAG + "onRewardVideoCached");

    }

    @Override
    public void onRewardVideoCached(TTRewardVideoAd ttRewardVideoAd) {
        try {
            String ad = "";
            if (ttRewardVideoAd != null) {
                ad = ttRewardVideoAd.toString();
            }
            EALog.high(TAG + "onRewardVideoCached( " + ad + ")");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        handleCached();
    }

    @Override
    protected void doLoadAD() {
        CsjUtil.initCsj(this, new CsjUtil.InitListener() {
            @Override
            public void success() {
                //只有在成功初始化以后才能调用load方法，否则穿山甲会抛错导致无法进行广告展示
                startLoadAD();
            }

            @Override
            public void fail(String code, String msg) {
                handleFailed(code, msg);
            }
        });
    }

    private void startLoadAD() {

        TTAdNative ttAdNative = CsjUtil.getADManger(this).createAdNative(getActivity());

        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(sdkSupplier.adspotId)
                .setSupportDeepLink(true)
                .setAdCount(1)
                //设置模板属性
                .setExpressViewAcceptedSize(500, 500)
                .setRewardName(setting.getCsjRewardName()) //奖励的名称
                .setRewardAmount(setting.getCsjRewardAmount())   //奖励的数量
                //必传参数，表来标识应用侧唯一用户；若非服务器回调模式或不需sdk透传
                //可设置为空字符串
                .setUserID(setting.getCsjUserId())
                .setOrientation(setting.getOrientation())  //设置期望视频播放的方向，为TTAdConstant.HORIZONTAL或TTAdConstant.VERTICAL
                .setMediaExtra(setting.getCsjMediaExtra()) //用户透传的信息，可不传
                .setDownloadType(EasyAdsManger.getInstance().csj_downloadType)
                .build();
        ttAdNative.loadRewardVideoAd(adSlot, this);
    }


    @Override
    public void doDestroy() {

    }

    @Override
    protected void doShowAD() {
        if (ttRewardVideoAd == null) {
            EALog.e("无广告内容");
            return;
        }
        ttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
            @Override
            public void onAdShow() {
                EALog.high(TAG + "onAdShow");
                handleExposure();
            }

            @Override
            public void onAdVideoBarClick() {
                EALog.high(TAG + "onAdVideoBarClick");
                handleClick();
            }

            @Override
            public void onAdClose() {
                EALog.high(TAG + "onAdClose");

                if (null != setting) {
                    setting.adapterDidClosed(sdkSupplier);
                }
            }

            @Override
            public void onVideoComplete() {
                EALog.high(TAG + "onVideoComplete");

                if (null != setting) {
                    setting.adapterVideoComplete(sdkSupplier);
                }

            }

            @Override
            public void onVideoError() {
                EALog.high(TAG + "onVideoError");

                handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_RENDER, "onVideoError"));
            }

            @Override
            public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int errorCode, String errMsg) {
                try {
                    EALog.high(TAG + "onRewardVerify; rewardVerify = " + rewardVerify + ",rewardAmount = " + rewardAmount + ",rewardName = " + rewardName + " errorCode:" + errorCode + " errMsg:" + errMsg);

                    EARewardServerCallBackInf inf = new EARewardServerCallBackInf();
                    EARewardServerCallBackInf.CsjRewardInf csjRewardInf = new EARewardServerCallBackInf.CsjRewardInf();
                    csjRewardInf.rewardVerify = rewardVerify;
                    csjRewardInf.rewardAmount = rewardAmount;
                    csjRewardInf.rewardName = rewardName;
                    csjRewardInf.errorCode = errorCode;
                    csjRewardInf.errMsg = errMsg;
                    inf.csjInf = csjRewardInf;
                    setting.postRewardServerInf(inf, sdkSupplier);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (rewardVerify) {
                    if (null != setting) {
                        setting.adapterAdReward(sdkSupplier);
                    }
                } else if (errorCode != 0) {//如果有异常信息，是否进行异常回调？
                    EALog.e("onRewardVerify error ，Code = " + errorCode + "  errMsg" + errMsg);
                }
            }

            @Override
            public void onSkippedVideo() {
                EALog.high(TAG + "onSkippedVideo");
                if (null != setting) {
                    setting.adapterVideoSkipped(sdkSupplier);
                }
            }
        });
        ttRewardVideoAd.showRewardVideoAd(getActivity());
    }
}

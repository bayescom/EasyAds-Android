package com.easyads.supplier.ks;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.easyads.core.reward.EARewardVideoSetting;
import com.easyads.custom.EARewardCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsRewardVideoAd;
import com.kwad.sdk.api.KsScene;
import com.kwad.sdk.api.model.KsExtraRewardType;

import java.lang.ref.SoftReference;
import java.util.List;

public class KSRewardVideoAdapter extends EARewardCustomAdapter implements KsRewardVideoAd.RewardAdInteractionListener {
    public EARewardVideoSetting setting;
    KsRewardVideoAd ad;

    public KSRewardVideoAdapter(SoftReference<Activity> activity, EARewardVideoSetting baseSetting) {
        super(activity, baseSetting);
        setting = baseSetting;
    }

    @Override
    protected void doLoadAD() {
        //初始化快手SDK
        boolean initOK = KSUtil.initAD(this);
        if (initOK) {
            KsScene scene = new KsScene.Builder(KSUtil.getADID(sdkSupplier)).build(); // 此为测试posId，请联系快手平台申请正式posId
            KsAdSDK.getLoadManager().loadRewardVideoAd(scene, new KsLoadManager.RewardVideoAdListener() {
                @Override
                public void onError(int code, String msg) {
                    EALog.high(TAG + " onError ");

                    handleFailed(code, msg);
                }

                @Override
                public void onRewardVideoResult(@Nullable List<KsRewardVideoAd> list) {
                    EALog.high(TAG + "onRewardVideoResult  ");

                }
//
//                @Override
//                public void onRequestResult(int adNumber) {
//                    EALog.high(TAG + "onRequestResult，广告填充数量：" + adNumber);
//                }

                @Override
                public void onRewardVideoAdLoad(@Nullable List<KsRewardVideoAd> list) {
                    EALog.high(TAG + " onRewardVideoAdLoad");
                    try {
                        if (list == null || list.size() == 0 || list.get(0) == null) {
                            handleFailed(EasyAdError.ERROR_DATA_NULL, "");
                        } else {
                            ad = list.get(0);
                            if (ad != null) {
                                ad.setRewardAdInteractionListener(KSRewardVideoAdapter.this);
                            }
                            handleSucceed();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        handleFailed(EasyAdError.ERROR_EXCEPTION_LOAD, "");
                    }
                }
            });
        }
    }


    @Override
    public void doDestroy() {
    }


    //--------广告回调--------

    @Override
    public void onAdClicked() {
        EALog.high(TAG + " onAdClicked");

        handleClick();
    }

    @Override
    public void onPageDismiss() {
        EALog.high(TAG + " onPageDismiss");
        if (setting != null) {
            setting.adapterDidClosed(sdkSupplier);
        }
    }

    @Override
    public void onVideoPlayError(int code, int extra) {
        String msg = " onVideoPlayError,code = " + code + ",extra = " + extra;
        EALog.high(TAG + msg);

        handleFailed(EasyAdError.ERROR_EXCEPTION_RENDER, msg);
    }

    @Override
    public void onVideoPlayEnd() {
        EALog.high(TAG + " onVideoPlayEnd");
        if (setting != null) {
            setting.adapterVideoComplete(sdkSupplier);
        }
    }

    @Override
    public void onVideoSkipToEnd(long l) {
        EALog.high(TAG + " onVideoSkipToEnd，l=" + l);
    }

    @Override
    public void onVideoPlayStart() {
        EALog.high(TAG + " onVideoPlayStart");

        handleExposure();
    }

    @Override
    public void onRewardVerify() {
        EALog.high(TAG + " onRewardVerify");
        if (setting != null) {
            setting.adapterAdReward(sdkSupplier);
        }
    }


    /**
     * 视频激励分阶段回调（激励广告新玩法，相关政策请联系商务或技术支持）
     *
     * @param taskType          当前激励视频所属任务类型
     *                          RewardTaskType.LOOK_VIDEO 观看视频类型             属于浅度奖励类型
     *                          RewardTaskType.LOOK_LANDING_PAGE 浏览落地⻚N秒类型  属于深度奖励类型
     *                          RewardTaskType.USE_APP 下载使用App N秒类型          属于深度奖励类型
     * @param currentTaskStatus 当前所完成任务类型，@RewardTaskType中之一
     */
    @Override
    public void onRewardStepVerify(int taskType, int currentTaskStatus) {
        EALog.high(TAG + " onRewardStepVerify , taskType :" + taskType + "，currentTaskStatus = " + currentTaskStatus);

    }

    @Override
    public void onExtraRewardVerify(@KsExtraRewardType int extraRewardType) {
        EALog.high(TAG + " onExtraRewardVerify , extraRewardType :" + extraRewardType);
    }


    //--------广告回调 结束--------

    @Override
    protected void doShowAD() {
        ad.showRewardVideoAd(getActivity(), EasyKSManager.getInstance().rewardVideoConfig);
    }
}

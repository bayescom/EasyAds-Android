package com.easyads.core.reward;

import android.app.Activity;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.model.EasyAdType;
import com.easyads.model.SdkSupplier;
import com.qq.e.ads.rewardvideo.ServerSideVerificationOptions;

public class EasyAdRewardVideo extends EasyAdBaseAdspot implements EARewardVideoSetting {
    private EARewardVideoListener listener;
    private int csjOrientation = ORIENTATION_VERTICAL; //屏幕方向，仅穿山甲支持
    private String csjRewardName = ""; //穿山甲奖励名称
    private String csjUserId = "";//穿山甲用户id
    private int csjRewardAmount = 1;//穿山甲奖励数量
    private String csjMediaExtra = "";//穿山甲媒体补充信息

    private boolean isYlhVO = false; //优量汇是否开启声音
    private ServerSideVerificationOptions ylhSSVO = null;  //优量汇服务端校验配置信息

    public static final int ORIENTATION_VERTICAL = 1;
    public static final int ORIENTATION_HORIZONTAL = 2;


    public EasyAdRewardVideo(Activity activity, EARewardVideoListener listener) {
        super(activity, listener);
        adType = EasyAdType.REWARD;

        this.listener = listener;
    }

    @Override
    public void initSdkSupplier() {
        try {
            initAdapter(EasyAdsConstant.SDK_TAG_CSJ, this);
            initAdapter(EasyAdsConstant.SDK_TAG_YLH, this);
            initAdapter(EasyAdsConstant.SDK_TAG_BAIDU, this);
            initAdapter(EasyAdsConstant.SDK_TAG_KS, this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void adapterVideoCached(SdkSupplier supplier) {
        updateSupplier("adapterVideoCached", supplier);
        if (null != listener) {
            listener.onVideoCached();
        }
    }

    public void adapterVideoComplete(SdkSupplier supplier) {
        updateSupplier("adapterVideoComplete", supplier);
        if (null != listener) {
            listener.onVideoComplete();
        }
    }

    public void adapterDidClosed(SdkSupplier supplier) {
        updateSupplier("adapterDidClosed", supplier);
        if (null != listener) {
            listener.onAdClose();
        }
    }

    public void adapterAdReward(SdkSupplier supplier) {
        updateSupplier("adapterAdReward", supplier);
        if (null != listener) {
            listener.onAdReward();
        }
    }

    public void adapterVideoSkipped(SdkSupplier supplier) {
        updateSupplier("adapterVideoSkipped", supplier);
        if (null != listener) {
            listener.onVideoSkip();
        }
    }

    @Override
    public void postRewardServerInf(EARewardServerCallBackInf inf, SdkSupplier supplier) {
        updateSupplier("postRewardServerInf", supplier);
        if (null != listener) {
            listener.onRewardServerInf(inf);
        }
    }

    public void setCsjRewardName(String rewardName) {
        this.csjRewardName = rewardName;

    }

    public void setCsjUserId(String userId) {
        this.csjUserId = userId;

    }

    public void setCsjRewardAmount(int rewardAmount) {
        this.csjRewardAmount = rewardAmount;

    }

    public void setCsjOrientation(int csjOrientation) {
        this.csjOrientation = csjOrientation;

    }

    public void setCsjMediaExtra(String mediaExtra) {
        this.csjMediaExtra = mediaExtra;
    }


    public void setYlhVolumeOn(boolean vo) {
        isYlhVO = vo;
    }

    public void setYlhSSVO(ServerSideVerificationOptions options){
        ylhSSVO = options;
    }

    @Override
    public boolean isYlhVolumeOn() {
        return isYlhVO;
    }

    public String getCsjMediaExtra() {
        return csjMediaExtra;
    }

    @Override
    public ServerSideVerificationOptions getYlhSSVO() {
        return ylhSSVO;
    }

    public int getCsjOrientation() {
        return csjOrientation;
    }

    public int getCsjRewardAmount() {
        return csjRewardAmount;
    }

    public String getCsjRewardName() {
        return csjRewardName;
    }

    public String getCsjUserId() {
        return csjUserId;
    }

}

package com.easyads.core.reward;

import com.easyads.itf.EABaseADListener;

public interface EARewardVideoListener extends EABaseADListener {

    void onVideoCached();

    void onVideoComplete();

    void onVideoSkip();

    void onAdReward();

    //激励视频返回的服务器回调信息，穿山甲一直支持，优量汇自v4.330.1200 开始支持,百度9.13开始支持
    void onRewardServerInf(EARewardServerCallBackInf inf);
}

package com.easyads.itf;

import com.easyads.model.EasyAdError;

public interface EABaseADListener {
    void onAdSucceed();//成功加载到广告

    void onAdExposure();//广告曝光

    void onAdClicked();//广告点击

    void onAdClose();//广告关闭

    void onAdFailed(EasyAdError easyAdError);//广告失败
}

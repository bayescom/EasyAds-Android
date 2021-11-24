package com.easyads.model;

/**
 * 广告类型
 */
public enum EasyAdType {

    SPLASH("Splash"),
    BANNER("Banner"),
    INTR("Interstitial"),
    NATIV("NativeExpress"),
    REWARD("RewardVideo"),
    DRAW("Draw"),
    FULL("FullScreenVideo");

    EasyAdType(String ni) {
        name = ni;
    }

    public String name;
}

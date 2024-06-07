package com.easyads;

import com.easyads.core.BuildConfig;
import com.easyads.model.EALogLevel;

public class EasyAds {

    //获取聚合SDK版本号
    public static String getVersion() {
        return BuildConfig.VERSION_NAME;
    }

    //设置debug状态，同时可设置log打印等级
    public static void setDebug(boolean isDebug, EALogLevel logLevel) {
        EasyAdsManger.getInstance().debug = isDebug;
        EasyAdsManger.getInstance().logLevel = logLevel;
    }


    //设置开屏v+ 小窗口自动关闭时间，单位毫秒，不设置使用默认各个渠道默认展示逻辑
    public static void setSplashPlusAutoClose(int time) {
        EasyAdsManger.getInstance().splashPlusAutoClose = time;
    }


}

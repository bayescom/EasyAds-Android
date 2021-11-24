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


    //设置开屏v+ 小窗口自动关闭时间，不设置使用默认各个渠道默认展示逻辑
    public static void setSplashPlusAutoClose(int time) {
        EasyAdsManger.getInstance().splashPlusAutoClose = time;
    }

    //穿山甲特殊设置，用于控制开屏广告的点击区域，具体设置值及含义可参考：  https://www.pangle.cn/support/doc/611f0f0c1b039f004611e4da
    public static void setCSJSplashButtonType(int splashButtonType) {
        EasyAdsManger.getInstance().csj_splashButtonType = splashButtonType;
    }

    //穿山甲特殊设置，用于控制下载APP前是否弹出二次确认弹窗(适用所有广告类型)。具体设置值及含义可参考：  https://www.pangle.cn/support/doc/611f0f0c1b039f004611e4da
    public static void setCSJDownloadType(int downloadType) {
        EasyAdsManger.getInstance().csj_downloadType = downloadType;
    }
}

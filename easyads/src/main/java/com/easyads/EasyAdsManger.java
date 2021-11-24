package com.easyads;

import com.easyads.model.EALogLevel;

/**
 * 单例处理一些全局配置信息
 */
public class EasyAdsManger {
    private static EasyAdsManger instance;

    private EasyAdsManger() {
    }

    public static synchronized EasyAdsManger getInstance() {
        if (instance == null) {
            instance = new EasyAdsManger();
        }
        return instance;
    }
    public boolean debug = false;
    public boolean isDev = false;

    //标记各平台是否初始化过
    public boolean hasYLHInit = false;
    public boolean hasCSJInit = false;
    public boolean hasKSInit = false;
    public boolean hasBDInit = false;

    //各平台最近一次初始化的APP_ID
    public String lastYLHAID = "";
    public String lastCSJAID = "";
    public String lastKSAID = "";
    public String lastBDAID = "";


    public String currentSupTag = "";//当前执行的SDK渠道id
    public boolean isSplashSupportZoomOut = false; //是否需要执行v+形式的缩放操作

    public int splashPlusAutoClose = -1;//开屏v+自动关闭时间，单位毫秒，默认-1不执行强制关闭
    public EALogLevel logLevel = EALogLevel.DEFAULT;

    public int csj_splashButtonType = -1;
    public int csj_downloadType = -1;
    public boolean csj_supportMultiProcess = true;
    public int[] csj_directDownloadNetworkType;
    public boolean csj_needPermissionCheck = false;

    public int getSplashPlusAutoClose() {
        try {
            if (splashPlusAutoClose <= 0) {
                return -1;
            }
            //最短3秒
            if (splashPlusAutoClose < 3000) {
                return 3000;
            }
            //最长30秒
            if (splashPlusAutoClose > 30000) {
                return 30000;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return splashPlusAutoClose;
    }

}

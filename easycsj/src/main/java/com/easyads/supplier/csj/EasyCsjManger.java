package com.easyads.supplier.csj;

/**
 * 穿山甲全局属性配置管理类
 */
public class EasyCsjManger {
    private static EasyCsjManger instance;

    private EasyCsjManger() {
    }

    public static synchronized EasyCsjManger getInstance() {
        if (instance == null) {
            instance = new EasyCsjManger();
        }
        return instance;
    }

    //用于控制开屏广告的点击区域，具体设置值及含义可参考：  https://www.pangle.cn/support/doc/611f0f0c1b039f004611e4da
    public int csj_splashButtonType = -1;
    //用于控制下载APP前是否弹出二次确认弹窗(适用所有广告类型)。具体设置值及含义可参考：  https://www.pangle.cn/support/doc/611f0f0c1b039f004611e4da
    public int csj_downloadType = -1;
    //是否支持多进程
    public boolean csj_supportMultiProcess = true;
    //下载类广告允许直接下载的网络状态，如果未设置下载状态集合，默认4g和wifi下可以下载。
    public int[] csj_directDownloadNetworkType;
    //是否允许穿山甲SDK，在必要时进行权限询问，默认不允许
    public boolean csj_askPermission = false;
}

package com.easyads.supplier.baidu;


import android.widget.RelativeLayout;

import com.baidu.mobads.sdk.api.AdSize;
import com.baidu.mobads.sdk.api.RequestParameters;
import com.baidu.mobads.sdk.api.RewardVideoAd;
import com.baidu.mobads.sdk.api.StyleParams;

/**
 * baidu 渠道相关位置的特殊配置项
 */
public class EasyBDManager {
    private static EasyBDManager instance;

    private EasyBDManager() {
    }

    public static synchronized EasyBDManager getInstance() {
        if (instance == null) {
            instance = new EasyBDManager();
        }
        return instance;
    }

    //开屏个性化配置信息
    public RequestParameters splashParameters = null;
    //信息流个性化配置信息
    public RequestParameters nativeExpressParameters = null;
    //信息流布局个性化配置信息
    public StyleParams nativeExpressSmartStyle = null;

    //插屏类型
    public AdSize interstitialType = null;
    //视频贴片类型的插屏布局父布局
    public RelativeLayout interstitialVideoLayout = null;

    //全屏视频是否使用SurfaceView来渲染
    public boolean fullScreenUseSurfaceView = false;
    //激励视频是否使用SurfaceView来渲染
    public boolean rewardUseSurfaceView = false;
    //下载确认弹框设置，默认永不弹框
    public int rewardDownloadAppConfirmPolicy = RewardVideoAd.DOWNLOAD_APP_CONFIRM_NEVER;

}

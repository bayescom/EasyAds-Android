package com.easyads.demo.custom;

import android.app.Activity;
import android.os.Handler;

import com.easyads.core.splash.EASplashSetting;
import com.easyads.custom.EASplashCustomAdapter;
import com.easyads.model.EasyAdError;
import com.miui.zeus.mimo.sdk.MimoSdk;
import com.miui.zeus.mimo.sdk.SplashAd;

import java.lang.ref.SoftReference;

/**
 * 自定义小米开屏渠道
 */
public class XiaoMiSplashAdapter extends EASplashCustomAdapter {
    SplashAd mSplashAd;
    private boolean isCountingEnd = false;//用来判断是否倒计时走到了最后，false 回调dismiss的话代表是跳过，否则倒计时结束

    public XiaoMiSplashAdapter(SoftReference<Activity> activity, EASplashSetting splashSetting) {
        super(activity, splashSetting);
    }


    @Override
    protected void doLoadAD() {
        loadAndShowXM();
    }

    @Override
    public void doDestroy() { //销毁广告
        if (mSplashAd != null) {
            mSplashAd.destroy();
        }
    }

    @Override
    protected void doShowAD() {

    }

    /**
     * 初始化小米SDK，并请求和展示广告
     */
    private void loadAndShowXM() {
        MimoSdk.init(getActivity());
        mSplashAd = new SplashAd();
        //执行广告展示方法，并在对应回调中执行对应生命周期回调
        mSplashAd.loadAndShow(adContainer, getPosID(), new SplashAd.SplashAdListener() {
            @Override
            public void onAdShow() {
                handleExposure();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isCountingEnd = true;
                    }
                }, 4800);
            }

            @Override
            public void onAdClick() {
                handleClick();
            }

            @Override
            public void onAdDismissed() {
                //必要回调：广告关闭事件回调，回调区分用户点击了跳过还是计时结束
                if (mSplashSetting != null) {
                    if (isCountingEnd) {
                        mSplashSetting.adapterDidTimeOver(sdkSupplier);
                    } else {
                        mSplashSetting.adapterDidSkip(sdkSupplier);
                    }
                }
            }

            @Override
            public void onAdLoadFailed(int i, String s) {
                handleFailed(i + "", s);
            }

            @Override
            public void onAdLoaded() {
                handleSucceed();
            }

            @Override
            public void onAdRenderFailed() {
                //渲染失败也要执行失败回调处理
                handleFailed(EasyAdError.ERROR_RENDER_FAILED, "");
            }
        });
    }
}

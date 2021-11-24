package com.easyads.demo.custom;

import android.app.Activity;
import android.app.Application;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.easyads.core.splash.EASplashSetting;
import com.easyads.custom.EASplashCustomAdapter;
import com.easyads.utils.EALog;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.splash.SplashAdDisplayListener;
import com.huawei.hms.ads.splash.SplashView;

import java.lang.ref.SoftReference;

public class HuaWeiSplashAdapter extends EASplashCustomAdapter {
    SplashView splashView;
    private boolean isCountingEnd = false;//用来辅助判断用户行为，用户是点击了跳过还是倒计时结束，false 回调dismiss的话代表是跳过，否则倒计时结束
    String TAG = "[HuaWeiSplashAdapter] ";
    private Application.ActivityLifecycleCallbacks alcb;

    public HuaWeiSplashAdapter(SoftReference<Activity> activity, EASplashSetting splashSetting) {
        super(activity, splashSetting);
        if (alcb != null) {
            //如果已存在，先注销
            activity.get().getApplication().unregisterActivityLifecycleCallbacks(alcb);
        }
        //注册Lifecycle生命周期监控，用于在合适的生命周期处理广告事件
        alcb = new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (getActivity() == activity && splashView != null) {
                    splashView.resumeView();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

                if (getActivity() == activity && splashView != null) {
                    splashView.pauseView();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        };
        activity.get().getApplication().registerActivityLifecycleCallbacks(alcb);

    }


    @Override
    protected void doLoadAD() { //请求广告
        EALog.simple(TAG + "doLoadAD");
        loadAD();
    }

    @Override
    protected void doShowAD() {//展示广告
        adContainer.addView(splashView);
    }

    @Override
    public void doDestroy() {//销毁广告
        EALog.simple(TAG + "doDestroy");
        if (splashView != null) {
            splashView.destroyView();
        }
        if (alcb != null) {
            //先注销监听
            getActivity().getApplication().unregisterActivityLifecycleCallbacks(alcb);
        }
    }

    private void loadAD() {
        int orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        AdParam adParam = new AdParam.Builder().build();
        splashView = new SplashView(getActivity());
        // 一定要调用基类生命相关的handle周期方法
        SplashAdDisplayListener adDisplayListener = new SplashAdDisplayListener() {
            @Override
            public void onAdShowed() {
                handleExposure();

                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isCountingEnd = true;
                        }
                    }, 4800);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAdClick() {
                handleClick();
            }
        };
        splashView.setAdDisplayListener(adDisplayListener);
        splashView.load(getPosID(), orientation, adParam, new SplashView.SplashAdLoadListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                EALog.e(TAG + "广告失败：errorCode = " + errorCode);
                handleFailed(errorCode + "", "");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                EALog.high(TAG + "onAdLoaded ");

                handleSucceed();
            }

            @Override
            public void onAdDismissed() {
                super.onAdDismissed();
                EALog.high(TAG + "onAdDismissed ");


                //广告关闭事件回调，回调区分用户点击了跳过还是倒计时结束
                if (mSplashSetting != null) {
                    if (isCountingEnd) {
                        mSplashSetting.adapterDidTimeOver(sdkSupplier);
                    } else {
                        mSplashSetting.adapterDidSkip(sdkSupplier);
                    }
                }
            }
        });

    }


}

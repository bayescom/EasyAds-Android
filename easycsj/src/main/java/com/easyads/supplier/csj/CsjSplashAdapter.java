package com.easyads.supplier.csj;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.view.View;
import android.view.ViewGroup;

import com.easyads.EasyAdsManger;
import com.easyads.core.splash.EASplashSetting;
import com.easyads.custom.EASplashCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.ISplashClickEyeListener;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import java.lang.ref.SoftReference;

public class CsjSplashAdapter extends EASplashCustomAdapter {
    private TTSplashAd splashAd;

    public CsjSplashAdapter(SoftReference<Activity> activity, EASplashSetting setting) {
        super(activity, setting);
    }

    @Override
    public void doDestroy() {

    }

    @Override
    protected void doShowAD() {
        initSplashClickEyeData();

        if (splashAd != null) {
            //获取SplashView
            View view = splashAd.getSplashView();
            //渲染之前判断activity生命周期状态
            if (!EAUtil.isActivityDestroyed(softReferenceActivity)) {
                adContainer.removeAllViews();
                //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕宽
                adContainer.addView(view);
            }
        }
    }

    @Override
    protected void doLoadAD() {
        CsjUtil.initCsj(this, new CsjUtil.InitListener() {
            @Override
            public void success() {
                //只有在成功初始化以后才能调用load方法，否则穿山甲会抛错导致无法进行广告展示
                startLoadAD();
            }

            @Override
            public void fail(String code, String msg) {
                handleFailed(code, msg);
            }
        });
    }

    private void startLoadAD() {
        final TTAdManager ttAdManager = TTAdSdk.getAdManager();
        if (EasyAdsManger.getInstance().csj_needPermissionCheck) {
            ttAdManager.requestPermissionIfNecessary(getActivity());
        }
        AdSlot adSlot;


        boolean isExpress;

        if (sdkSupplier.versionTag == 1) {
            isExpress = false;
        } else if (sdkSupplier.versionTag == 2) {
            isExpress = true;

        } else {
            if (mSplashSetting != null) {
                isExpress = mSplashSetting.getCsjShowAsExpress();
            } else {
                isExpress = false;
            }
        }
        EALog.simple(TAG + "是否为模板类型：" + isExpress);


        //穿山甲后台暂时不支持开屏模板广告，代码先加上相关判断，不影响现有展示。
        if (isExpress) {
            adSlot = new AdSlot.Builder()
                    .setCodeId(sdkSupplier.adspotId)
                    .setSupportDeepLink(true)
                    .setExpressViewAcceptedSize(mSplashSetting.getCsjExpressViewWidth(), mSplashSetting.getCsjExpressViewHeight())
                    .setImageAcceptedSize(mSplashSetting.getCsjAcceptedSizeWidth(), mSplashSetting.getCsjAcceptedSizeHeight())
                    .setSplashButtonType(EasyAdsManger.getInstance().csj_splashButtonType)
                    .setDownloadType(EasyAdsManger.getInstance().csj_downloadType)
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(sdkSupplier.adspotId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(mSplashSetting.getCsjAcceptedSizeWidth(), mSplashSetting.getCsjAcceptedSizeHeight())
                    .setSplashButtonType(EasyAdsManger.getInstance().csj_splashButtonType)
                    .setDownloadType(EasyAdsManger.getInstance().csj_downloadType)
                    .build();
        }

        TTAdNative ttAdNative = CsjUtil.getADManger(this).createAdNative(getActivity());
        ttAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                EALog.high(TAG + "onError , code = " + code + ", msg = " + message);
                handleFailed(code, message);
            }

            @Override
            @MainThread
            public void onTimeout() {
                String emsg = TAG + "onTimeout";
                EALog.high(emsg);
                EasyAdError error = EasyAdError.parseErr(EasyAdError.ERROR_CSJ_TIMEOUT, emsg);

                handleFailed(error);
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                try {
                    if (ad == null) {
                        String nMsg = TAG + " TTSplashAd null";
                        EasyAdError error = EasyAdError.parseErr(EasyAdError.ERROR_DATA_NULL, nMsg);
                        handleFailed(error);
                        return;
                    }
                    splashAd = ad;
                    EALog.high(TAG + "onAdLoaded");

                    handleSucceed();

                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                    //设置SplashView的交互监听器
                    ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            EALog.high(TAG + "onAdClicked");

                            handleClick();
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            EALog.high(TAG + "onAdShow");

                            handleExposure();
                        }

                        @Override
                        public void onAdSkip() {
                            EALog.high(TAG + "onAdSkip");

                            if (mSplashSetting != null) {
                                mSplashSetting.adapterDidSkip(sdkSupplier);
                            }
                            switchSplashClickShow();

                        }

                        @Override
                        public void onAdTimeOver() {
                            EALog.high(TAG + "onAdTimeOver");
                            if (mSplashSetting != null) {
                                mSplashSetting.adapterDidTimeOver(sdkSupplier);
                            }
                            switchSplashClickShow();

                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                    handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
                }

            }
        }, 5000);
    }

    /**
     * 以下为点睛广告特殊处理
     */

    //是否进行点睛广告的展示
    private void switchSplashClickShow() {
        try {
            if (mSplashSetting == null) {
                return;
            }
            if (mSplashSetting.isShowInSingleActivity()) {
                new CsjUtil().zoomOut(getActivity());
            } else {
                EasyAdsManger.getInstance().isSplashSupportZoomOut = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //初始化点睛广告数据、类
    private void initSplashClickEyeData() {
        try {
            if (splashAd == null) {
                return;
            }
            View splashView = splashAd.getSplashView();
            if (splashView == null) {
                return;
            }
            SplashClickEyeListener mSplashClickEyeListener = new SplashClickEyeListener(getActivity(), splashAd, adContainer);

            splashAd.setSplashClickEyeListener(mSplashClickEyeListener);
            CSJSplashClickEyeManager csjSplashClickEyeManager = CSJSplashClickEyeManager.getInstance();
            csjSplashClickEyeManager.init(getActivity());
            csjSplashClickEyeManager.setSplashInfo(splashAd, splashView, getActivity().getWindow().getDecorView());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public static class SplashClickEyeListener implements ISplashClickEyeListener {
        private SoftReference<Activity> mActivity;
        private TTSplashAd mSplashAd;
        private View mSplashContainer;
        String TAG = "[SplashClickEyeListener] ";

        public SplashClickEyeListener(Activity activity, TTSplashAd splashAd, View splashContainer) {
            try {
                mSplashAd = splashAd;
                mSplashContainer = splashContainer;
                mActivity = new SoftReference<>(activity);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSplashClickEyeAnimationStart() {
            //开始执行开屏点睛动画
            startSplashAnimationStart();
        }

        @Override
        public void onSplashClickEyeAnimationFinish() {
            try {
                //sdk关闭了了点睛悬浮窗
                CSJSplashClickEyeManager splashClickEyeManager = CSJSplashClickEyeManager.getInstance();
                boolean isSupport = splashClickEyeManager.isSupportSplashClickEye();
                if (isSupport) {
                    finishActivity();
                }
                splashClickEyeManager.clearSplashStaticData();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean isSupportSplashClickEye(boolean isSupport) {
            try {
                EALog.high(TAG + " isSupportSplashClickEye = " + isSupport);
                CSJSplashClickEyeManager splashClickEyeManager = CSJSplashClickEyeManager.getInstance();
                //此处可以修改强制支持，测试点睛广告展现效果
//                if (EasyAdsManger.getInstance().isDev) {
//                    EALog.devDebug("csj test SplashClickEye force isSupport = true");
//                    isSupport = true;
//                }
                splashClickEyeManager.setSupportSplashClickEye(isSupport);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return isSupport;
        }

        private void finishActivity() {
            try {
                if (mActivity.get() == null) {
                    return;
                }
                mActivity.get().finish();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        private void startSplashAnimationStart() {
            try {
                if (mActivity.get() == null || mSplashAd == null || mSplashContainer == null) {
                    return;
                }
                CSJSplashClickEyeManager splashClickEyeManager = CSJSplashClickEyeManager.getInstance();
                ViewGroup content = mActivity.get().findViewById(android.R.id.content);
                splashClickEyeManager.startSplashClickEyeAnimation(mSplashContainer, content, content, new CSJSplashClickEyeManager.AnimationCallBack() {
                    @Override
                    public void animationStart(int animationTime) {
                    }

                    @Override
                    public void animationEnd() {
                        if (mSplashAd != null) {
                            mSplashAd.splashClickEyeAnimationFinish();
                        }
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}

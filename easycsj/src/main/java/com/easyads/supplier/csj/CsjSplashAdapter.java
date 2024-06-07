package com.easyads.supplier.csj;

import android.app.Activity;
import android.support.annotation.MainThread;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.CSJAdError;
import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.bytedance.sdk.openadsdk.CSJSplashCloseType;
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
    private CSJSplashAd newSplashAd;

    public CsjSplashAdapter(SoftReference<Activity> activity, EASplashSetting setting) {
        super(activity, setting);
    }

    @Override
    public void doDestroy() {

    }

    @Override
    protected void doShowAD() {
//        initSplashClickEyeData();

        if (newSplashAd != null) {
            //获取SplashView
            View view = newSplashAd.getSplashView();
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

        AdSlot adSlot;
        //穿山甲后台暂时不支持开屏模板广告，代码先加上相关判断，不影响现有展示。
        if (isExpress) {
            adSlot = new AdSlot.Builder()
                    .setCodeId(sdkSupplier.adspotId)
                    .setSupportDeepLink(true)
                    .setExpressViewAcceptedSize(mSplashSetting.getCsjExpressViewWidth(), mSplashSetting.getCsjExpressViewHeight())
                    .setImageAcceptedSize(mSplashSetting.getCsjAcceptedSizeWidth(), mSplashSetting.getCsjAcceptedSizeHeight())
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(sdkSupplier.adspotId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(mSplashSetting.getCsjAcceptedSizeWidth(), mSplashSetting.getCsjAcceptedSizeHeight())
                    .build();
        }

        TTAdNative ttAdNative = CsjUtil.getADManger(this).createAdNative(getActivity());
        ttAdNative.loadSplashAd(adSlot, new TTAdNative.CSJSplashAdListener() {
            @Override
            public void onSplashLoadSuccess(CSJSplashAd splashAd) {
                EALog.high(TAG + "onSplashLoadSuccess");

            }

            @Override
            public void onSplashLoadFail(CSJAdError csjAdError) {
                EALog.high(TAG + "onSplashLoadFail");
                newApiAdFailed(csjAdError, EasyAdError.ERROR_EXCEPTION_LOAD, "onSplashLoadFail");
            }

            @Override
            public void onSplashRenderSuccess(CSJSplashAd csjSplashAd) {
                EALog.high(TAG + "onAdLoaded");


                if (csjSplashAd == null) {
                    String nMsg = TAG + " TTSplashAd null";
                    EasyAdError error = EasyAdError.parseErr(EasyAdError.ERROR_DATA_NULL, nMsg);
                    handleFailed(error);
                    return;
                }
                newSplashAd = csjSplashAd;
                handleSucceed();
                newSplashAd.setSplashAdListener(new CSJSplashAd.SplashAdListener() {

                    @Override
                    public void onSplashAdShow(CSJSplashAd csjSplashAd) {
                        EALog.high(TAG + "onSplashAdShow");

                        handleExposure();
                    }

                    @Override
                    public void onSplashAdClick(CSJSplashAd csjSplashAd) {
                        EALog.high(TAG + "onSplashAdClick");
                        handleClick();
                    }

                    @Override
                    public void onSplashAdClose(CSJSplashAd csjSplashAd, int closeType) {
                        EALog.high(TAG + "onSplashAdClose");
                        if (mSplashSetting != null) {
                            if (closeType == CSJSplashCloseType.CLICK_SKIP) {
                                mSplashSetting.adapterDidSkip(sdkSupplier);
                            } else if (closeType == CSJSplashCloseType.COUNT_DOWN_OVER) {
                                mSplashSetting.adapterDidTimeOver(sdkSupplier);
                            } else {
                                mSplashSetting.adapterDidSkip(sdkSupplier);
                            }
                        }
                    }
                });
                CsjUtil.getCPMInfNew(TAG, newSplashAd);

            }


            @Override
            public void onSplashRenderFail(CSJSplashAd csjSplashAd, CSJAdError csjAdError) {
                EALog.high(TAG + "onSplashRenderFail");

                newApiAdFailed(csjAdError, EasyAdError.ERROR_RENDER_FAILED, "onSplashRenderFail");

            }

        }, 5000);
    }


    private void newApiAdFailed(CSJAdError csjAdError, String errCodeDefault, String errExt) {
        try {
            EasyAdError error;
            if (csjAdError == null) {
                error = EasyAdError.parseErr(errCodeDefault, errExt);
            } else {
                error = EasyAdError.parseErr(csjAdError.getCode(), csjAdError.getMsg());
            }
            handleFailed(error);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//    private void initSplashClickEyeData() {
//        try {
//            if (splashAd == null) {
//                return;
//            }
//            View splashView = splashAd.getSplashView();
//            if (splashView == null) {
//                return;
//            }
//            SplashClickEyeListener mSplashClickEyeListener = new SplashClickEyeListener(getActivity(), splashAd, adContainer);
//
//            splashAd.setSplashClickEyeListener(mSplashClickEyeListener);
//            CSJSplashClickEyeManager csjSplashClickEyeManager = CSJSplashClickEyeManager.getInstance();
//            csjSplashClickEyeManager.init(getActivity());
//            csjSplashClickEyeManager.setSplashInfo(splashAd, splashView, getActivity().getWindow().getDecorView());
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }


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

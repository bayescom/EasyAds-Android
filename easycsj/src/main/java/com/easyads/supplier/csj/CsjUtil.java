package com.easyads.supplier.csj;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.sdk.openadsdk.CSJSplashAd;
import com.easyads.EasyAdsManger;
import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.itf.BaseEnsureListener;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EASplashPlusManager;
import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;
import com.bytedance.sdk.openadsdk.ISplashClickEyeListener;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTSplashAd;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;

public class CsjUtil implements EASplashPlusManager.ZoomCall {

    /**
     * 统一处理权限申请设置
     *
     * @param adapter 渠道基础适配器
     * @return 穿山甲广告manger
     */
    public static TTAdManager getADManger(EABaseSupplierAdapter adapter) {
        TTAdManager ttAdManager = null;
        try {
            ttAdManager = TTAdSdk.getAdManager();
            if (EasyCsjManger.getInstance().csj_askPermission) {
                ttAdManager.requestPermissionIfNecessary(adapter.getActivity());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return ttAdManager;
    }

    /**
     * 穿山甲3450以后版本初始化方法，支持异步初始化以及对应回调方法
     *
     * @param adapter  渠道基础适配器
     * @param listener 初始化回调
     */
    public static void initCsj(final EABaseSupplierAdapter adapter, final InitListener listener) {
        try {
            if (adapter == null) {
                String msg = "[initCsj] initAD failed BaseSupplierAdapter null";
                if (listener != null) {
                    listener.fail(EasyAdError.ERROR_DATA_NULL, msg);
                }
                EALog.e(msg);
                return;
            }

            boolean hasInit = EasyAdsManger.getInstance().hasCSJInit;
            String resultAppID = adapter.getAppID();
            EALog.high("[CsjUtil.initCsj] 穿山甲 appID：" + resultAppID);

            if (TextUtils.isEmpty(resultAppID)) {
                String msg = "[initCsj] initAD failed AppID null";
                if (listener != null) {
                    listener.fail(EasyAdError.ERROR_DATA_NULL, msg);
                }
                EALog.e(msg);
                return;
            }
            String lastAppId = EasyAdsManger.getInstance().lastCSJAID;
            boolean isSame = lastAppId.equals(resultAppID);
            if (hasInit && adapter.canOptInit() && isSame) {
                if (listener != null) {
                    listener.success();
                }
                return;
            }
            EALog.simple("[CsjUtil] 开始初始化SDK");

            boolean supportMP = EasyCsjManger.getInstance().csj_supportMultiProcess;
            int[] directDownloadNetworkType = EasyCsjManger.getInstance().csj_directDownloadNetworkType;

            //如果未设置下载状态集合，默认4g和wifi下可以下载。
            if (directDownloadNetworkType == null || directDownloadNetworkType.length == 0) {
                directDownloadNetworkType = new int[]{TTAdConstant.NETWORK_STATE_4G, TTAdConstant.NETWORK_STATE_WIFI};
            }

            boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
            if (!isMainThread) {
                EALog.high("[CsjUtil.initCsj]需要在主线程中调用穿山甲sdk 初始化方法");
            } else {
                EALog.high("[CsjUtil.initCsj]当前在主线程中调用穿山甲sdk 初始化方法");
            }
            EALog.high("[CsjUtil.initCsj] supportMultiProcess = " + supportMP + " directDownloadNetworkType = " + Arrays.toString(directDownloadNetworkType));

            final TTAdConfig config = new TTAdConfig.Builder()
                    .appId(resultAppID)
                    .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                    .appName("")//
                    .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_LIGHT)
                    .allowShowNotify(true) //是否允许sdk展示通知栏提示
//                    .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                    .debug(EasyAdsManger.getInstance().debug) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                    .directDownloadNetworkType(directDownloadNetworkType) //允许直接下载的网络状态集合
                    .supportMultiProcess(supportMP) //是否支持多进程，true支持
//                    .asyncInit(true) //如果是主线程使用异步
                    .build();

            //主线程和非主线程逻辑分开
            final String finalResultAppID = resultAppID;
            EAUtil.switchMainThread(new BaseEnsureListener() {
                @Override
                public void ensure() {
                    doInit(adapter.getActivity(), config, listener, finalResultAppID);
                }
            });

        } catch (Throwable e) {
            String msg = "穿山甲sdk 初始化异常";
            EALog.e(msg);
            e.printStackTrace();
            if (listener != null) {
                listener.fail(EasyAdError.ERROR_CSJ_INIT_FAILED, msg);
            }
        }
    }

    private static void doInit(Context context, TTAdConfig config, final InitListener listener, final String appID) {
        try {
            TTAdSdk.init(context.getApplicationContext(), config
    //                , new TTAdSdk.InitCallback() {
    //                    @Override
    //                    public void success() {
    //                        EALog.simple("csj init success");
    //
    //                        EAUtil.switchMainThread(new BaseEnsureListener() {
    //                            @Override
    //                            public void ensure() {
    //                                if (listener != null) {
    //                                    listener.success();
    //                                }
    //                            }
    //                        });
    //                        EasyAdsManger.getInstance().lastCSJAID = appID;
    //                        EasyAdsManger.getInstance().hasCSJInit = true;
    //                    }
    //
    //                    @Override
    //                    public void fail(int code, String msg) {
    //                        EALog.e("csj init fail : code = " + code + " msg = " + msg);
    //                        if (listener != null) {
    //                            listener.fail(EasyAdError.ERROR_CSJ_INIT_FAILED, msg);
    //                        }
    //                        EasyAdsManger.getInstance().hasCSJInit = false;
    //                    }
    //                }
                    );

            TTAdSdk.start(new TTAdSdk.Callback() {
                @Override
                public void success() {
                    EALog.simple("csj init success");

                    if (listener != null) {
                        listener.success();
                    }
                    EasyAdsManger.getInstance().lastCSJAID = appID;
                    EasyAdsManger.getInstance().hasCSJInit = true;
                }

                @Override
                public void fail(int code, String msg) {
                    EALog.e("csj init fail : code = " + code + " msg = " + msg);
                    if (listener != null) {
                        listener.fail(code+"", msg);
                    }
                    EasyAdsManger.getInstance().hasCSJInit = false;
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            if (listener != null) {
                listener.fail(EasyAdError.ERROR_CSJ_INIT_FAILED, "csj init exception");
            }
        }
    }



    public static void getCPMInfNew(String TAG, CSJSplashAd newSplashAd) {
        try {
            Map<String, Object> extraInfo = newSplashAd.getMediaExtraInfo();
            //设置cpm
            double cpm = 0;
            if (extraInfo != null) {
                Object price = extraInfo.get("price");
                if (price != null) {
                    if (price instanceof Double) {
                        cpm = (double) price;
                    }
                }
            }
            EALog.devDebug(TAG + " cpm = " + cpm);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * 以下处理点睛广告，到达首页后的展示逻辑
     */

    @Override
    public void zoomOut(Activity activity) {
        EALog.simple("CsjUtil start zoomOut");

        CSJSplashClickEyeManager splashClickEyeManager = CSJSplashClickEyeManager.getInstance();
        boolean isSupportSplashClickEye = splashClickEyeManager.isSupportSplashClickEye();
        if (!isSupportSplashClickEye) {
            EALog.simple("notSupportSplashClickEye");
            return;
        }
        View splashClickEyeView = addSplashClickEyeView(activity);
        if (splashClickEyeView != null) {
            activity.overridePendingTransition(0, 0);
        }
        TTSplashAd splashAd = splashClickEyeManager.getSplashAd();
        HomeSplashClickEyeListener splashClickEyeListener = new HomeSplashClickEyeListener(splashClickEyeView, splashAd);
        if (splashAd != null) {
            splashAd.setSplashClickEyeListener(splashClickEyeListener);
        }
        //根据设定延迟自动关闭小窗口
        EAUtil.autoClose(splashClickEyeView);
    }

    private View addSplashClickEyeView(Activity activity) {
        final CSJSplashClickEyeManager splashClickEyeManager = CSJSplashClickEyeManager.getInstance();
        final TTSplashAd splashAd = splashClickEyeManager.getSplashAd();
        return splashClickEyeManager.startSplashClickEyeAnimationInTwoActivity((ViewGroup) activity.getWindow().getDecorView(),
                (ViewGroup) activity.findViewById(android.R.id.content), new CSJSplashClickEyeManager.AnimationCallBack() {
                    @Override
                    public void animationStart(int animationTime) {
                    }

                    @Override
                    public void animationEnd() {
                        splashAd.splashClickEyeAnimationFinish();
                    }
                });
    }

    static class HomeSplashClickEyeListener implements ISplashClickEyeListener {

        private SoftReference<View> mSplashView;
        private SoftReference<TTSplashAd> mSplashAd;

        public HomeSplashClickEyeListener(View splashView, TTSplashAd splashAd) {
            mSplashView = new SoftReference<>(splashView);
            mSplashAd = new SoftReference<>(splashAd);
        }

        @Override
        public void onSplashClickEyeAnimationStart() {
        }

        @Override
        public void onSplashClickEyeAnimationFinish() {
            //小窗展示五秒后会自动回调此方法，导致页面自动关闭。手动点击窗口上的关闭按钮亦会回调此方法。
            //接收点击关闭按钮的事件将开屏点睛移除。
            EALog.high("[HomeSplashClickEyeListener] onSplashClickEyeAnimationFinish ； close mSplashView");
            if (mSplashView != null && mSplashView.get() != null) {
                mSplashView.get().setVisibility(View.GONE);
                EAUtil.removeFromParent(mSplashView.get());
                mSplashView = null;
                mSplashAd = null;
            }
            CSJSplashClickEyeManager.getInstance().clearSplashStaticData();
        }

        @Override
        public boolean isSupportSplashClickEye(boolean isSupport) {
            return isSupport;
        }
    }

    interface InitListener {
        void success();

        void fail(String code, String msg);
    }
}

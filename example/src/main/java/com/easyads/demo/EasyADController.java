package com.easyads.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.core.banner.EasyAdBanner;
import com.easyads.core.banner.EABannerListener;
import com.easyads.core.draw.EasyAdDraw;
import com.easyads.core.draw.EADrawListener;
import com.easyads.core.full.EasyAdFullScreenVideo;
import com.easyads.core.full.EAFullScreenVideoListener;
import com.easyads.core.inter.EasyAdInterstitial;
import com.easyads.core.inter.EAInterstitialListener;
import com.easyads.core.nati.EasyAdNativeExpress;
import com.easyads.core.nati.EANativeExpressListener;
import com.easyads.core.reward.EasyAdRewardVideo;
import com.easyads.core.reward.EARewardVideoListener;
import com.easyads.core.reward.EARewardServerCallBackInf;
import com.easyads.core.splash.EasyAdSplash;
import com.easyads.core.splash.EASplashListener;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.easyads.utils.ScreenUtil;
import com.easyads.demo.custom.HuaWeiSplashAdapter;
import com.easyads.demo.custom.XiaoMiSplashAdapter;
import com.hjq.toast.ToastUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;

/**
 * 进一步封装了使用 EasyAds 的广告加载逻辑，方便代码中调用，也为了尽量广告处理代码独立。
 * EasyAds不会进行任何的采集和上报处理，如果有统计需求，APP开发者可以在对应的时间回调内进行统一上报处理。
 */
public class EasyADController {
    EasyAdBaseAdspot baseAD;
    Activity mActivity;

    //小米渠道是否需要添加为自定义渠道
    public boolean cusXiaoMi = false;
    //华为渠道是否需要添加为自定义渠道
    public boolean cusHuaWei = false;

    /**
     * 初始化广告处理类
     *
     * @param activity 页面上下文
     */
    public EasyADController(Activity activity) {
        mActivity = activity;
    }


    /**
     * 加载开屏广告，开屏推荐使用加载并展示开屏广告方式，所有的广告均支持请求和展示分离，如有必要，可分别调用加载广告和展示广告，可参考"插屏广告"中的处理示例。
     *
     * @param adContainer    广告承载布局，不可为空
     * @param logoContainer  底部logo布局，可以为空
     * @param singleActivity 是否为单独activity中展示开屏广告
     * @param callBack       跳转回调，在回调中进行跳转主页或其他操作
     */
    public void loadSplash(String jsonFileName, final ViewGroup adContainer, final ViewGroup logoContainer, boolean singleActivity, final SplashCallBack callBack) {
        //必须：设置开屏核心回调事件的监听器。
        EASplashListener listener = new EASplashListener() {

            @Override
            public void onAdClose() {
                if (callBack != null)
                    callBack.jumpMain();

                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");
                if (logoContainer != null)
                    logoContainer.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAdExposure() {
                //设置开屏父布局背景色为白色
                if (adContainer != null)
                    adContainer.setBackgroundColor(Color.WHITE);


                logAndToast(mActivity, "广告展示成功");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


        };
        EasyAdSplash easySplash = new EasyAdSplash(mActivity, adContainer, listener);
        baseAD = easySplash;
        //注意：如果开屏页是fragment或者dialog实现，这里需要置为false。默认为true，代表开屏和首页为两个不同的activity
        easySplash.setShowInSingleActivity(singleActivity);
        if (cusXiaoMi) {
            //注意：此处自定义渠道的tag，一定要和setData()中配置的tag一致。
            easySplash.addCustomSupplier("xm", new XiaoMiSplashAdapter(new SoftReference<>(mActivity), easySplash));
        }
        if (cusHuaWei) {
            easySplash.addCustomSupplier("hw", new HuaWeiSplashAdapter(new SoftReference<>(mActivity), easySplash));
        }
        //必须：设置策略信息
        easySplash.setData(getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easySplash.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }


    /**
     * 开屏跳转回调
     */
    public interface SplashCallBack {
        void jumpMain();
    }

    /**
     * 加载并展示banner广告
     *
     * @param adContainer banner广告的承载布局
     */
    public void loadBanner(String jsonFileName, ViewGroup adContainer) {
        //必须：核心事件监听回调
        EABannerListener listener = new EABannerListener() {
            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展现");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");
            }

        };
        EasyAdBanner easyAdBanner = new EasyAdBanner(mActivity, adContainer, listener);
        baseAD = easyAdBanner;
        //如果集成穿山甲，这里必须配置，建议尺寸要和穿山甲后台中的"代码位尺寸"宽高比例一致，值单位为dp，这里示例使用的广告位宽高比为640：100。
        int adWidth = ScreenUtil.px2dip(mActivity, ScreenUtil.getScreenWidth(mActivity));
        int adHeight = (int) (((double) adWidth / (double) 640) * 100);
        //如果高度传入0代表自适应高度
        easyAdBanner.setCsjExpressSize(adWidth, adHeight);
        //必须：设置策略信息
        easyAdBanner.setData(getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easyAdBanner.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }


    /**
     * 初始话插屏广告。
     * 可以选择性先提前加载，然后在合适的时机再调用展示方法
     * 或者直接调用加载并展示广告
     * <p>
     * 注意！！！：穿山甲默认为"新插屏广告"
     */
    public EasyAdInterstitial initInterstitial(String jsonFileName) {
        //必须：核心事件监听回调
        EAInterstitialListener listener = new EAInterstitialListener() {

            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告就绪");
            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }


            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }


            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }
        };
        //初始化
        EasyAdInterstitial easyInterstitial = new EasyAdInterstitial(mActivity, listener);
        baseAD = easyInterstitial;
        //注意：穿山甲默认为"新插屏广告"，如果要使用旧版请打开这条设置
//        easyInterstitial.setCsjNew(false);
        //必须：设置策略信息
        easyInterstitial.setData(getJson(mActivity, jsonFileName));
        return easyInterstitial;
    }

    /**
     * 加载并展示激励视频广告。
     * 也可以选择性先提前加载，然后在合适的时机再调用展示方法
     */
    public EasyAdRewardVideo initReward(String jsonFileName) {
        //必须：核心事件监听回调
        EARewardVideoListener listener = new EARewardVideoListener() {
            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");
            }


            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


            @Override
            public void onVideoCached() {
                logAndToast(mActivity, "广告缓存成功");
            }

            @Override
            public void onVideoComplete() {
                logAndToast(mActivity, "视频播放完毕");
            }

            @Override
            public void onVideoSkip() {

            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdReward() {
                logAndToast(mActivity, "激励发放");
            }

            @Override
            public void onRewardServerInf(EARewardServerCallBackInf inf) {
                //优量汇和穿山甲支持回调服务端激励验证信息，详见RewardServerCallBackInf中字段信息
                logAndToast(mActivity, "onRewardServerInf" + inf);
            }
        };
        //初始化，注意需要时再初始化，不要复用。
        EasyAdRewardVideo easyRewardVideo = new EasyAdRewardVideo(mActivity, listener);
        baseAD = easyRewardVideo;
        //必须：设置策略信息
        easyRewardVideo.setData(getJson(mActivity, jsonFileName));
        return easyRewardVideo;
    }

    /**
     * 初始化获取展示全屏视频的广告对象。
     * 也可以选择先提前加载，然后在合适的时机再调用展示方法
     */
    public EasyAdFullScreenVideo initFullVideo(String jsonFileName) {

        //推荐：核心事件监听回调
        EAFullScreenVideoListener listener = new EAFullScreenVideoListener() {
            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");

            }

            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onVideoComplete() {
                logAndToast(mActivity, "视频播放结束");
            }

            @Override
            public void onVideoSkipped() {
                logAndToast(mActivity, "跳过视频");
            }

            @Override
            public void onVideoCached() {
                //广告缓存成功，可以在此记录状态，但要注意：不一定所有的广告会返回该回调
                logAndToast(mActivity, "广告缓存成功");
            }

            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }


            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }
        };
        //初始化
        EasyAdFullScreenVideo easyFullScreenVideo = new EasyAdFullScreenVideo(mActivity, listener);
        baseAD = easyFullScreenVideo;
        //必须：设置策略信息
        easyFullScreenVideo.setData(getJson(mActivity, jsonFileName));

        return easyFullScreenVideo;
    }

    public boolean hasNativeShow = false;
    boolean isNativeLoading = false;

    /**
     * 加载并展示原生模板信息流广告
     *
     * @param adContainer 广告的承载布局
     */
    public void loadNativeExpress(String jsonFileName, ViewGroup adContainer) {

        if (hasNativeShow) {//同一位置广告，已展示过不再重复发起请求
            EALog.d("loadNativeExpress hasNativeShow");
            return;
        }

        if (isNativeLoading) {//同一位置广告，正在请求中，不再重复请求
            EALog.d("loadNativeExpress isNativeLoading");
            return;
        }
        isNativeLoading = true;

        if (adContainer.getChildCount() > 0) {
            adContainer.removeAllViews();
        }


        //推荐：核心事件监听回调
        EANativeExpressListener listener = new EANativeExpressListener() {
            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");

            }

            @Override
            public void onAdRenderSuccess() {
                logAndToast(mActivity, "广告渲染成功");

            }


            @Override
            public void onAdClose() {
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdExposure() {
                hasNativeShow = true;
                isNativeLoading = false;
                logAndToast(mActivity, "广告展示");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                isNativeLoading = false;
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }


            @Override
            public void onAdRenderFailed() {
                isNativeLoading = false;
                logAndToast(mActivity, "广告渲染失败");
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }

        };
        //初始化
        final EasyAdNativeExpress easyNativeExpress = new EasyAdNativeExpress(mActivity, listener);
        baseAD = easyNativeExpress;
        easyNativeExpress.setAdContainer(adContainer);
        //必须：设置策略信息
        easyNativeExpress.setData(getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easyNativeExpress.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }

    public EasyAdDraw easyAdDraw;

    public void loadDraw(String jsonFileName, ViewGroup adContainer) {

        EADrawListener listener = new EADrawListener() {
            @Override
            public void onAdSucceed() {
                if (easyAdDraw != null) {
                    easyAdDraw.show();
                }
                logAndToast(mActivity, "广告加载成功");

            }

            @Override
            public void onAdExposure() {
                logAndToast(mActivity, "广告展示");

            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");

            }

            @Override
            public void onAdClose() { //此位置不存在广告关闭动作，当前回调不会触发
                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }
        };
        easyAdDraw = new EasyAdDraw(mActivity, listener);
        baseAD = easyAdDraw;
        easyAdDraw.setAdContainer(adContainer);
        //必须：设置策略信息
        easyAdDraw.setData(getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easyAdDraw.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }


    /**
     * 销毁广告
     */
    public void destroy() {
        if (baseAD != null) {
            baseAD.destroy();
            baseAD = null;
        }
    }

    /**
     * 统一处理打印日志，并且toast提示。
     *
     * @param context 上下文
     * @param msg     需要显示的内容
     */
    public static void logAndToast(Context context, String msg) {
        Log.d("[DemoUtil][logAndToast]", msg);
        ToastUtils.debugShow(msg);
    }

    /**
     * 获取存放在assets下面的广告json文件内容，建议APP端有条件的话，通过后端下发json配置内容
     */
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}

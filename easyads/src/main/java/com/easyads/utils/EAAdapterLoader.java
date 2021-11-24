package com.easyads.utils;

import android.app.Activity;

import com.easyads.EasyAdsConstant;
import com.easyads.core.draw.EADrawSetting;
import com.easyads.core.banner.EABannerSetting;
import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.core.full.EAFullScreenVideoSetting;
import com.easyads.core.inter.EAInterstitialSetting;
import com.easyads.core.nati.EANativeExpressSetting;
import com.easyads.core.reward.EARewardVideoSetting;
import com.easyads.core.splash.EASplashSetting;
import com.easyads.model.EasyAdType;

import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;

/**
 * 通过反射的方式获取对应渠道的各个广告位初始化方法，
 */
public class EAAdapterLoader {


    public static String BASE_ADAPTER_PKG_PATH = "com.easyads.supplier.";

    /**
     * 利用反射获取渠道adapter
     *
     * @param sdkTag         渠道标志
     * @param adType         广告类型
     * @param activity       软引用上下文
     * @param parameterTypes 实体类
     * @return 渠道adapter适配器
     */
    public static EABaseSupplierAdapter getSDKLoader(String sdkTag, EasyAdType adType, SoftReference<Activity> activity, Object parameterTypes) {
        try {
            Class sdkClz = null;
            String namePath = "";
            //根据tag添加对应adapter类信息的路径
            switch (sdkTag) {
                case EasyAdsConstant.SDK_TAG_CSJ:
                    namePath = "csj.Csj";
                    break;
                case EasyAdsConstant.SDK_TAG_YLH:
                    namePath = "ylh.Ylh";
                    break;
                case EasyAdsConstant.SDK_TAG_BAIDU:
                    namePath = "baidu.BD";
                    break;
                case EasyAdsConstant.SDK_TAG_KS:
                    namePath = "ks.KS";
                    break;
            }
            namePath = namePath + adType.name + "Adapter";

            //根据广告类型使用对应的接口参数
            switch (adType) {
                case DRAW:
                    sdkClz = EADrawSetting.class;
                    break;
                case FULL:
                    sdkClz = EAFullScreenVideoSetting.class;
                    break;
                case SPLASH:
                    sdkClz = EASplashSetting.class;
                    break;
                case BANNER:
                    sdkClz = EABannerSetting.class;
                    break;
                case REWARD:
                    sdkClz = EARewardVideoSetting.class;
                    break;
                case INTR:
                    sdkClz = EAInterstitialSetting.class;
                    break;
                case NATIV:
                    sdkClz = EANativeExpressSetting.class;
                    break;
            }

            return getSDKAdapter(namePath, activity, sdkClz, parameterTypes);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 弱引用初始化广告的反射获取方法
     *
     * @param clzName
     * @param activity
     * @param parClz
     * @param parameterTypes
     * @return
     */
    private static EABaseSupplierAdapter getSDKAdapter(String clzName, SoftReference<Activity> activity, Class parClz, Object... parameterTypes) {
        EABaseSupplierAdapter result = null;
        try {
            String fullClzName = BASE_ADAPTER_PKG_PATH + clzName;
            Class clz = Class.forName(fullClzName);
            Object par1;
            if (parameterTypes.length > 0) {
                par1 = parameterTypes[0];
                Constructor cons1 = clz.getConstructor(SoftReference.class, parClz);
                result = (EABaseSupplierAdapter) cons1.newInstance(activity, par1);
                if (result != null)
                    EALog.devDebug("反射获取SDK渠道adapter类，已完成： " + result.toString());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return result;
    }

}

package com.easyads.utils;

import android.app.Activity;

import com.easyads.EasyAdsConstant;
import com.easyads.EasyAdsManger;

public class EASplashPlusManager {

    public static void startZoom(Activity activity) {
        try {
            if (EasyAdsManger.getInstance().isSplashSupportZoomOut) {
                String sdkId = EasyAdsManger.getInstance().currentSupTag;
                ZoomCall call = null;
                switch (sdkId) {
                    case EasyAdsConstant.SDK_TAG_YLH:
                        call = reflectZoomMethod("ylh.YlhUtil");
                        break;
                    case EasyAdsConstant.SDK_TAG_CSJ:
                        call = reflectZoomMethod("csj.CsjUtil");
                        break;
                    case EasyAdsConstant.SDK_TAG_KS:
                        call = reflectZoomMethod("ks.KSUtil");
                        break;
                    case EasyAdsConstant.SDK_TAG_BAIDU:
                        call = reflectZoomMethod("baidu.BDUtil");
                        break;
                }

                if (call != null) {
                    call.zoomOut(activity);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static ZoomCall reflectZoomMethod(String supClzName) {
        ZoomCall zoomCall = null;
        try {
            Class clz = Class.forName(EAAdapterLoader.BASE_ADAPTER_PKG_PATH + supClzName);
            zoomCall = (ZoomCall) clz.newInstance();
            if (zoomCall != null)
                EALog.devDebug("reflectZoomMethod result = " + zoomCall.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return zoomCall;
    }


    public interface ZoomCall {
        void zoomOut(Activity activity);
    }

}

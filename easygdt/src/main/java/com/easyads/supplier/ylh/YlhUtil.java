package com.easyads.supplier.ylh;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.easyads.EasyAdsManger;
import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EASplashPlusManager;
import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.comm.managers.GDTAdSdk;

public class YlhUtil implements EASplashPlusManager.ZoomCall {
    public static String TAG = "[YlhUtil] ";

    public static void initAD(EABaseSupplierAdapter adapter) {
        try {
            if (adapter == null) {
                EALog.e(TAG + " initAD failed BaseSupplierAdapter null");
                return;
            }
            boolean hasInit = EasyAdsManger.getInstance().hasYLHInit;


            String lastAppId = EasyAdsManger.getInstance().lastYLHAID;
            String resultAppID = adapter.getAppID();
            if (TextUtils.isEmpty(resultAppID)) {
                String msg = "[initCsj] initAD failed AppID null";
                adapter.handleFailed(EasyAdError.ERROR_DATA_NULL, msg);
                EALog.e(msg);
                return;
            }
            boolean isSame = lastAppId.equals(resultAppID);
            //只有当允许初始化优化时，且快手已经初始化成功过，并且初始化的id和当前id一致，才可以不再重复初始化。
            if (hasInit && adapter.canOptInit() && isSame) {
                return;
            }
            EALog.simple(TAG + " 开始初始化SDK");

            GDTAdSdk.init(adapter.getActivity().getApplicationContext(), resultAppID);

            EasyAdsManger.getInstance().hasYLHInit = true;
            EasyAdsManger.getInstance().lastYLHAID = resultAppID;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void zoomOut(Activity activity) {
        try {
            EALog.simple(TAG + " start zoomOut");
            final SplashZoomOutManager zoomOutManager = SplashZoomOutManager.getInstance();
            final SplashAD zoomAd = zoomOutManager.getSplashAD();
            final ViewGroup zoomOutView = zoomOutManager.startZoomOut((ViewGroup) activity.getWindow().getDecorView(),
                    (ViewGroup) activity.findViewById(android.R.id.content), new SplashZoomOutManager.AnimationCallBack() {

                        @Override
                        public void animationStart(int animationTime) {

                        }

                        @Override
                        public void animationEnd() {
                            zoomAd.zoomOutAnimationFinish();
                        }
                    });

            if (zoomOutView != null) {
                activity.overridePendingTransition(0, 0);
            }
            EAUtil.autoClose(zoomOutView);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}

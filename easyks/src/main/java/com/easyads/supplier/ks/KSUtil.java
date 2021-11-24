package com.easyads.supplier.ks;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.easyads.EasyAdsManger;
import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.model.SdkSupplier;
import com.easyads.utils.EASplashPlusManager;
import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.SdkConfig;

public class KSUtil implements EASplashPlusManager.ZoomCall {

    public static boolean initAD(EABaseSupplierAdapter adapter) {
        boolean hasInit = doInitAD(adapter);
        try {
            //如果未初始化成功，需要进行异常回调，否则无法进入下一步流程
            if (!hasInit) {
                EasyAdError error = EasyAdError.parseErr(EasyAdError.ERROR_KS_INIT);
                adapter.handleFailed(error);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return hasInit;
    }

    public static boolean doInitAD(EABaseSupplierAdapter adapter) {
        boolean initSuccess = false;
        try {
            if (adapter == null) {
                EALog.e("initAD failed BaseSupplierAdapter null");
                return false;
            }

            String resultAppID = adapter.getAppID();
            if (TextUtils.isEmpty(resultAppID)) {
                String msg = "[initCsj] initAD failed AppID null";
                adapter.handleFailed(EasyAdError.ERROR_DATA_NULL, msg);
                EALog.e(msg);
                return false;
            }

            boolean hasInit = EasyAdsManger.getInstance().hasKSInit;


            String lastAppId = EasyAdsManger.getInstance().lastKSAID;
            boolean isSame = lastAppId.equals(resultAppID);
            //只有当允许初始化优化时，且快手已经初始化成功过，并且初始化的id和当前id一致，才可以不再重复初始化。
            if (hasInit && isSame && adapter.canOptInit()) {
                return true;
            }
            EALog.simple("[KSUtil] 开始初始化SDK");


            Context context = adapter.getActivity();

            String currentProcessName = EAUtil.getCurrentProcessName(context);
            if (currentProcessName.equals(context.getPackageName())) {
                EALog.high("[KSUtil.doInitAD] init appId = " + resultAppID);
                SdkConfig.Builder builder = new SdkConfig.Builder();
                builder.appId(resultAppID)// aapId，请联系快手平台申请正式AppId，必填
                        .showNotification(true) // 是否展示下载通知栏
                        .debug(EasyAdsManger.getInstance().debug);

//                    builder.appName(appName);// appName，请填写您应用的名称，非必填
//                    builder.appKey(appKey);// 直播sdk安全验证，接入直播模块必填
//                    builder.appWebKey(appWebKey);// 直播sdk安全验证，接入直播模块必填
//

                // 建议只在需要的进
                // 程初始化SDK即可，如主进程
                KsAdSDK.init(context, builder.build());
                EasyAdsManger.getInstance().lastKSAID = resultAppID;
                EasyAdsManger.getInstance().hasKSInit = true;
                initSuccess = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return initSuccess;
    }


    public static long getADID(SdkSupplier supplier) {
        long id = -1;
        try {
            id = Long.parseLong(supplier.adspotId);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return id;
    }


    @Override
    public void zoomOut(Activity activity) {

    }
}

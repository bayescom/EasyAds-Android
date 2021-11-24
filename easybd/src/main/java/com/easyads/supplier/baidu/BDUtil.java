package com.easyads.supplier.baidu;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.easyads.EasyAdsManger;
import com.easyads.core.EABaseSupplierAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EASplashPlusManager;
import com.easyads.utils.EALog;
import com.baidu.mobads.sdk.api.BDAdConfig;
import com.baidu.mobads.sdk.api.BDDialogParams;
import com.baidu.mobads.sdk.api.MobadsPermissionSettings;

public class BDUtil implements EASplashPlusManager.ZoomCall {


    public static void initBDAccount(EABaseSupplierAdapter adapter) {
        try {
            if (adapter == null) {
                EALog.e("[BDUtil] initAD failed BaseSupplierAdapter null");
                return;
            }


            boolean hasInit = EasyAdsManger.getInstance().hasBDInit;
            String lastAppId = EasyAdsManger.getInstance().lastBDAID;

            String resultAppID = adapter.getAppID();


            if (TextUtils.isEmpty(resultAppID)) {
                String msg = "[initCsj] initAD failed AppID null";
                adapter.handleFailed(EasyAdError.ERROR_DATA_NULL, msg);
                EALog.e(msg);
                return;
            }

            EALog.high("[BDUtil.initBDAccount] 百度 appID：" + resultAppID);

            boolean isSame = lastAppId.equals(resultAppID);
            //只有当允许初始化优化时，且快手已经初始化成功过，并行初始化的id和当前id一致，才可以不再重复初始化。
            if (hasInit && adapter.canOptInit() && isSame) {
                return;
            }
            EALog.simple("[BDUtil] 开始初始化SDK");


            Activity activity = adapter.getActivity();

            if (getProcessName(activity).startsWith(activity.getPackageName())) {
                // 初始化信息，初始化一次即可，（此处用startsWith()，可包括激励/全屏视频的进程）
                // https、视频缓存空间有特殊需求可动态配置，一般取默认值即可，无需设置
                BDAdConfig bdAdConfig = new BDAdConfig.Builder()
                        // 1、设置app名称，可选
//                        .setAppName("网盟demo")
                        // 2、应用在mssp平台申请到的appsid，和包名一一对应，此处设置等同于在AndroidManifest.xml里面设置
                        .setAppsid(resultAppID)
                        // 3、设置下载弹窗的类型和按钮动效样式，可选
                        .setDialogParams(new BDDialogParams.Builder()
                                .setDlDialogType(BDDialogParams.TYPE_BOTTOM_POPUP)
                                .setDlDialogAnimStyle(BDDialogParams.ANIM_STYLE_NONE)
                                .build())
                        .setHttps(false)//如果设置为true，那么banner广告将会无法展示，todo 这是SDK的bug，需要观察后续版本是否解决了
                        .build(activity);
                bdAdConfig.init();
                // 设置SDK可以使用的权限，包含：设备信息、定位、存储、APP LIST
                // 注意：建议授权SDK读取设备信息，SDK会在应用获得系统权限后自行获取IMEI等设备信息
                // 授权SDK获取设备信息会有助于提升ECPM
                MobadsPermissionSettings.setPermissionReadDeviceID(true);
                MobadsPermissionSettings.setPermissionLocation(true);
                MobadsPermissionSettings.setPermissionStorage(true);
                MobadsPermissionSettings.setPermissionAppList(true);
            }

            EasyAdsManger.getInstance().hasBDInit = true;
            EasyAdsManger.getInstance().lastBDAID = resultAppID;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static String getProcessName(Context context) {
        if (context == null) return null;
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.pid == android.os.Process.myPid()) {
                    return processInfo.processName;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void zoomOut(Activity activity) {
        EALog.e("[BDUtil] not support zoomOut");
    }
}

package com.easyads.demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.easyads.EasyAds;
import com.easyads.model.EALogLevel;
import com.easyads.demo.utils.Constant;
import com.hjq.toast.ToastUtils;
import com.huawei.hms.ads.HwAds;

public class MyApplication extends Application {

    static MyApplication instance;

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        boolean hasPri = getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE).getBoolean(Constant.SP_AGREE_PRIVACY, false);
        //建议当用户同意了隐私政策以后才调用SDK初始化
        if (hasPri) {
            initSDK();
        }
    }

    public void initSDK() {
        //设置debug模式，日志可分等级打印，默认只打印简单的事件信息
        EasyAds.setDebug(BuildConfig.DEBUG, EALogLevel.DEFAULT);

        //自定义渠道-华为广告的初始化，如果不需要自定义可忽略此处
        HwAds.init(this);

        // 初始化 Toast 框架
        ToastUtils.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);        // 调用MutiDex
    }
}

package com.easyads.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.easyads.EasyAdsConstant;
import com.easyads.EasyAdsManger;
import com.easyads.core.splash.EASplashListener;
import com.easyads.core.splash.EASplashLifeCallback;
import com.easyads.itf.AdCoreAction;
import com.easyads.itf.EABaseADListener;
import com.easyads.itf.BaseAdapterEvent;
import com.easyads.itf.BaseEnsureListener;
import com.easyads.model.EasyAdType;
import com.easyads.model.EasyAdError;
import com.easyads.model.SdkRule;
import com.easyads.model.SdkSupplier;
import com.easyads.model.StrategyModel;
import com.easyads.utils.EAAdapterLoader;
import com.easyads.utils.EAUtil;
import com.easyads.utils.BigDecimalUtil;
import com.easyads.utils.EALog;

import java.lang.ref.SoftReference;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 广告处理基类，统一处理SDK渠道adapter加载、选择，adapter中基础回调事件统一处理。
 */
public abstract class EasyAdBaseAdspot implements AdCoreAction, BaseAdapterEvent {
    private String BTAG = "[" + this.getClass().getSimpleName() + "] ";
    private EABaseADListener baseADListener;
    private boolean isLoadOnly = false; //是否为仅加载广告模
    private boolean fromActivityDestroy = false;
    private String reqId = ""; //请求唯一id
    private StrategyModel mStrategyModel; //策略执行对象
    private Application.ActivityLifecycleCallbacks alcb;

    protected SoftReference<Activity> mSoftActivity;//软引用上下文
    //需要执行的广告SDK列表
    protected ArrayList<SdkSupplier> suppliers;
    //当前选中的广告SDK
    protected SdkSupplier currentSdkSupplier;
    //当前执行的广告SDK渠道
    protected SdkSupplier callBackRunningSupplier;
    //异常记录
    protected EasyAdError easyAdError;
    //各SDK渠道适配adapter集合，初始化时传入，后续渠道选择时从map中选取，
    protected HashMap<String, EABaseSupplierAdapter> supplierAdapters = new HashMap<>();
    protected String currentSdkTag = "";//当前正在执行SDK的tag
    protected EASplashLifeCallback splashLifeCallback;//适用于开屏的生命周期回调
    protected EasyAdType adType;//广告类型

    public EasyAdBaseAdspot(Activity activity, EABaseADListener listener) {
        try {
            this.mSoftActivity = new SoftReference<>(activity);
            this.baseADListener = listener;
            //初始化渠道列表
            initSupplierAdapterList();
            if (alcb != null) {
                //如果已存在，先注销
                activity.getApplication().unregisterActivityLifecycleCallbacks(alcb);
            }
            //注册Lifecycle生命周期监控，用于在合适的生命周期处理广告事件
            alcb = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {

                }

                @Override
                public void onActivityResumed(@NonNull Activity mActivity) {
                    if (splashLifeCallback != null && mActivity == getActivity()) {
                        splashLifeCallback.onResume();
                    }
                }

                @Override
                public void onActivityPaused(@NonNull Activity mActivity) {
                    if (splashLifeCallback != null && mActivity == getActivity()) {
                        splashLifeCallback.onPause();
                    }
                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity mActivity) {
                    if (getActivity() == mActivity) {
                        fromActivityDestroy = true;
                        destroy();
                    }
                }
            };
            activity.getApplication().registerActivityLifecycleCallbacks(alcb);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    void initSupplierAdapterList() {
        try {
            if (supplierAdapters == null) {
                supplierAdapters = new HashMap<>();
            } else {
                supplierAdapters.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 必须！！设置策略信息，注意json格式一定要正确，否则有可能解析策略失败导致无广告。
     *
     * @param strategyJson 策略信息json串，一定要传入指定要求的格式，具体请阅读文档对应内容了解。
     */
    public void setData(String strategyJson) {
        try {
            //解析json并转换为StrategyModel对象
            mStrategyModel = StrategyModel.covert(strategyJson);

            ArrayList<SdkRule> rules = mStrategyModel.rules;
            int ruleSize = rules.size();
            if (ruleSize <= 0) {//代表无策略规则信息，不加载广告

            } else if (ruleSize == 1) {//只有一组策略规则信息，全量加载
                suppliers = rules.get(0).sortedSuppliers;
                EALog.simple(BTAG + "策略组唯一，全量执行: " + rules.get(0).tag);
            } else { //> 1，一组策略规则信息，按照设定的percent值进行比例切分，按比例随机选择规则组。
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
//                以10000为基数，可以随机到0.01%精度
                int maxNum = 10000;
                //获取0-999随机数
                int randomPos = random.nextInt(maxNum);
                for (int i = 0; i < ruleSize; i++) {
                    SdkRule rule = rules.get(i);
                    //分母为各个setting内容的percent之和
                    double perResult = BigDecimalUtil.div(rule.percent, mStrategyModel.sumPercent, 4);
                    double gapNum = BigDecimalUtil.mul(perResult, maxNum);
                    if (i == 0) {
                        rule.lowLimit = 0;
                    } else {
                        rule.lowLimit = rules.get(i - 1).highLimit + 1;
                    }

                    if (i == ruleSize - 1) {
                        rule.highLimit = maxNum - 1;
                    } else {
                        rule.highLimit = rule.lowLimit + (int) gapNum - 1;
                    }
                    EALog.devDebug(BTAG + " randomPos =  " + randomPos + ", gapNum = " + gapNum + " ；rule.lowLimit = " + rule.lowLimit + ", rule.highLimit = " + rule.highLimit);

                    //命中当前rule，锁定对应排序后的渠道信息为当前执行的渠道信息
                    if (randomPos < rule.highLimit && randomPos > rule.lowLimit) {
                        suppliers = rule.sortedSuppliers;
                        EALog.simple(BTAG + "根据已配置流量切分比例，策略组随机选择，此次命中: " + rule.tag + "，流量占比：" + BigDecimalUtil.mul(perResult, 100) + "%");

                        for (SdkSupplier supplier : suppliers) {
                            EALog.devDebug(BTAG + "SDK渠道信息： " + supplier.toString());
                        }
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void loadOnly() {
        isLoadOnly = true;
        startLoad();
    }

    @Override
    public void loadAndShow() {
        isLoadOnly = false;
        startLoad();
    }

    /**
     * 用来统一调用adapter中的show方法
     */
    @Override
    public void show() {
        try {
            if (TextUtils.isEmpty(currentSdkTag)) {
                EALog.e("未选中任何SDK");
                return;
            }
            if (supplierAdapters == null || supplierAdapters.size() == 0) {
                EALog.e("无可用渠道");
                return;
            }
            EABaseSupplierAdapter adapter = supplierAdapters.get(currentSdkTag);
            if (adapter == null) {
                EALog.e("未找到当前渠道下adapter，渠道id：" + currentSdkTag);
                return;
            }
            if (adapter.isDestroy) {
                EALog.e("广告已销毁，无法展示，请重新初始化");
                return;
            }
            adapter.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始加载
     */
    protected void startLoad() {
        try {
            //添加各广告位初始化支持的adapter
            initSdkSupplier();
            //选择执行adapter
            dispatchSuppliers();
            //生成当前请求的唯一id
            reqId = EAUtil.getUUID();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    //获取此次广告的唯一请求id，值为本地生成的uuid
    public String getReqId() {
        return reqId;
    }

    /**
     * BaseAdapterEventListener中方法，在此统一进行回调处理 ----start
     * 不同广告位可能会有其他的回调listener事件，此处为共有的广告成功、展示、点击等处理
     */

    @Override
    public void adapterDidFailed(EasyAdError easyAdError, SdkSupplier supplier) {
        try {
            this.easyAdError = easyAdError;
            updateSupplier("adapterDidFailed", supplier);
            selectSdkSupplier();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void adapterDidSucceed(SdkSupplier supplier) {
        updateSupplier("adapterDidSucceed", supplier);

        if (null != baseADListener) {
            baseADListener.onAdSucceed();
        }
    }

    @Override
    public void adapterDidExposure(SdkSupplier supplier) {
        updateSupplier("adapterDidExposure", supplier);

        if (null != baseADListener) {
            baseADListener.onAdExposure();
        }
    }

    @Override
    public void adapterDidClicked(SdkSupplier supplier) {
        updateSupplier("adapterDidClicked", supplier);

        if (null != baseADListener) {
            baseADListener.onAdClicked();
        }
    }


    @Override
    public SdkSupplier getSupplierInf() {
        return callBackRunningSupplier;
    }

    /**
     * BaseAdapterEventListener中方法，在此统一进行回调处理 ----end
     */

    protected void updateSupplier(String func, SdkSupplier sdkSupplier) {
        try {
            String extMsg = "";
            if (sdkSupplier != null) {
                callBackRunningSupplier = sdkSupplier;
                extMsg = ", sdkSupplier = " + sdkSupplier.toString();
            }
            EALog.high(BTAG + "_" + func + "_" + extMsg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加自定义的渠道adapter
     *
     * @param sdkTag  自定义的渠道tag
     * @param adapter 自定义的渠道adapter
     */
    public void addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) {
        try {
            if (supplierAdapters == null) {
                supplierAdapters = new HashMap<>();
            }
            EABaseSupplierAdapter adapter1 = supplierAdapters.get(sdkTag);
            if (adapter1 == null) {
                supplierAdapters.put(sdkTag, adapter);
            } else {
                EALog.simple("该sdkTag：" + sdkTag + "下已存在渠道adapter，无法重复添加");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 分发获取到的策略信息
     */
    private void dispatchSuppliers() {
        //未设置策略信息，直接抛错
        if (mStrategyModel == null) {
            if (easyAdError == null) {
                easyAdError = EasyAdError.parseErr(EasyAdError.ERROR_NONE_STRATEGY);
            }
            onTotalFailed();
            return;
        }

        //确保在主线程执行策略选择逻辑
        EAUtil.switchMainThread(new BaseEnsureListener() {
            @Override
            public void ensure() {
                receivedSuppliers();
            }
        });
    }


    /**
     * 获取activity上下文实例，注意默认为软引用的Activity
     *
     * @return activity上下文
     */
    protected Activity getActivity() {
        try {
            if (mSoftActivity != null) {
                return mSoftActivity.get();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    private void receivedSuppliers() {
        try {
            //对当前渠道组进行排序
            Collections.sort(suppliers);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        selectSdkSupplier();
    }


    /**
     * 统一处理渠道选择流程
     * 选择SDK渠道逻辑，如果渠道为空，执行策略失败方法，不为空则开始渠道选中后的广告执行逻辑
     */
    void selectSdkSupplier() {
        try {
            EALog.simple(BTAG + "策略调度执行中");

            if (suppliers == null || suppliers.size() == 0) {
                EALog.e(BTAG + "渠道信息为空");
                if (easyAdError == null) {
                    EALog.simple("None SDK: sdk suppliers is empty, callback failed");
                    easyAdError = EasyAdError.parseErr(EasyAdError.ERROR_NONE_SDK);
                }
                onTotalFailed();
                return;
            }
            currentSdkSupplier = suppliers.get(0);
            if (EAUtil.isActivityDestroyed(getActivity())) {
                try {
                    EALog.e(BTAG + "当前activity已被销毁，不再请求广告");
                    //activity被销毁也算是异常，广告流程的结束，需要回调失败，并上报整体失败时间
                    easyAdError = EasyAdError.parseErr(EasyAdError.ERROR_NO_ACTIVITY);
                    onTotalFailed();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return;
            }


            //串行加载广告
            runOrderSuppliers();


        } catch (Throwable e) {
            e.printStackTrace();
            easyAdError = EasyAdError.parseErr(EasyAdError.ERROR_SUPPLIER_SELECT);
            onTotalFailed();
        }
    }

    /**
     * 整体失败回调，包括任意场景下的失败，可以在这里统一进行失败信息上报或者延迟上报的处理
     */
    protected void onTotalFailed() {
        try {
            //回调开发者错误信息
            if (baseADListener != null) {

                baseADListener.onAdFailed(easyAdError);
                if (baseADListener instanceof EASplashListener) {
                    final EASplashListener fListener = (EASplashListener) baseADListener;
                    EAUtil.switchMainThread(new BaseEnsureListener() {
                        @Override
                        public void ensure() {
                            fListener.onAdClose();
                        }
                    });
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 串行统一处理任务，当前是串行渠道直接loadAd，不是再去选择
     */
    private void runOrderSuppliers() {
        callSDKSelected();
        if (suppliers != null && suppliers.size() > 0 && currentSdkSupplier != null) {
            String tag = currentSdkSupplier.tag;
            try {
                //移除当前正在执行的渠道
                suppliers.remove(0);

                //查找对应渠道的处理adapter
                EABaseSupplierAdapter adapter = supplierAdapters.get(tag);
                if (adapter != null) {
                    adapter.setSDKSupplier(currentSdkSupplier);
                    //区分加载模式
                    if (isLoadOnly) {
                        adapter.loadOnly();
                    } else {
                        adapter.loadAndShow();
                    }
                } else {
                    EALog.e(EasyAdsConstant.NOT_SUPPORT_SUPPLIER_TIPS);
                    //无对应渠道处理adapter，需要重新调度策略
                    selectSdkSupplier();
                }
            } catch (Throwable e) {
                e.printStackTrace();
                catchFailed();
            }
        }
    }

    private void callSDKSelected() {
        try {
            currentSdkTag = currentSdkSupplier.tag;
            EasyAdsManger.getInstance().currentSupTag = currentSdkTag;
            EasyAdsManger.getInstance().isSplashSupportZoomOut = false;//初始化开屏v+支持状态
            EALog.simple(BTAG + "即将执行SDK :" + currentSdkTag);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //异常时的
    private void catchFailed() {
        try {
            this.easyAdError = EasyAdError.parseErr(EasyAdError.ERROR_LOAD_SDK, "");
            selectSdkSupplier();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁非正在进行的其他渠道，一般是在广告成功展示以后调用。
     *
     * @param supplier 正在进行的渠道
     */
    protected void destroyOtherSupplier(SdkSupplier supplier) {
        try {
            //遍历调用销毁
            if (supplierAdapters != null && supplierAdapters.size() > 0 && supplier != null) {
                for (String key : supplierAdapters.keySet()) {
                    String sTag = supplier.tag;
                    //如果是非当前渠道，进行销毁操作
                    if (sTag != null && !sTag.equals(key)) {
                        EABaseSupplierAdapter bad = supplierAdapters.get(key);
                        //只销毁优先级在当前渠道之上的渠道
                        if (bad != null && bad.sdkSupplier != null && bad.sdkSupplier.priority < supplier.priority) {
                            bad.destroy();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            EALog.e(BTAG + "destroyOtherSupplier catch Throwable");
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            //遍历调用销毁
            if (supplierAdapters != null && supplierAdapters.size() > 0) {
                for (String key : supplierAdapters.keySet()) {
                    // 进行销毁操作
                    EABaseSupplierAdapter bad = supplierAdapters.get(key);
                    if (bad != null) {
                        bad.destroy();
                    }
                }
            }
            if (getActivity() != null && fromActivityDestroy) {
                getActivity().getApplication().unregisterActivityLifecycleCallbacks(alcb);
            }
        } catch (Throwable e) {
            EALog.e(BTAG + " do destroy catch Throwable");
            e.printStackTrace();
        }
    }

    //反射调用各个渠道banner adapter初始化方法
    protected void initAdapter(String sdkTag, Object adObject) {
        try {
            EABaseSupplierAdapter bdAdapter = EAAdapterLoader.getSDKLoader(sdkTag, adType, mSoftActivity, adObject);
            if (bdAdapter != null && supplierAdapters != null) {
                supplierAdapters.put(sdkTag, bdAdapter);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //在此方法里添加需要支持的adapter
    protected abstract void initSdkSupplier();

}

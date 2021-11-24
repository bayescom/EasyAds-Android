package com.easyads.core;

import android.app.Activity;

import com.easyads.EasyAdsConstant;
import com.easyads.itf.AdCoreAction;
import com.easyads.itf.BaseAdapterEvent;
import com.easyads.itf.BaseEnsureListener;
import com.easyads.model.EasyAdError;
import com.easyads.model.SdkSupplier;
import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;

import java.lang.ref.SoftReference;

import static com.easyads.model.EasyAdError.ERROR_EXCEPTION_LOAD;
import static com.easyads.model.EasyAdError.ERROR_EXCEPTION_SHOW;

public abstract class EABaseSupplierAdapter implements AdCoreAction {
    public String TAG = "[" + this.getClass().getSimpleName() + "] ";
    private BaseAdapterEvent baseListener; // 广告基础回调事件
    private boolean hasFailed = false; //广告失败标记
    private int lastFailedPri = -1;//上次广告失败对应优先级，主要是处理同时配置了两个一样SDK渠道时，防止失败回调事件异常
    private int adNum = 0;//记录广告数量，每load一次+1.每失败一次-1.如果此值为负，那么可能为回调了多次的失败回调，需抛弃处理此次回调。
    private boolean isLoadOnly = false; //标记是否为仅load广告

    protected SoftReference<Activity> softReferenceActivity; //软引用上下文，避免强引用导致内存泄漏
    protected SdkSupplier sdkSupplier; //当前adapter正在执行的渠道配置信息

    protected boolean isDestroy = false;//销毁标记
    protected boolean refreshing = false;//banner位置的特殊标记，标记广告正在进行自动刷新


    /**
     * 通用初始化方法
     *
     * @param softReferenceActivity 上下文
     * @param baseListener          基类adapter回调
     */
    public EABaseSupplierAdapter(SoftReference<Activity> softReferenceActivity, final BaseAdapterEvent baseListener) {
        this.softReferenceActivity = softReferenceActivity;
        this.baseListener = baseListener;
    }


    /**
     * 设置当前渠道处理需要的渠道数据
     *
     * @param sdkSupplier 渠道信息model
     */
    public void setSDKSupplier(SdkSupplier sdkSupplier) {
        try {
            this.sdkSupplier = sdkSupplier;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一获取activity方法
     *
     * @return 页面上下文
     */
    public Activity getActivity() {
        if (softReferenceActivity != null) {
            return softReferenceActivity.get();
        }
        return null;
    }

    /**
     * 基础的核心方法，加载、展示、销毁
     */

    @Override
    public void loadOnly() {
        isLoadOnly = true;
        startAdapterADLoad();
    }

    @Override
    public void show() {
        try {
            //当使用分离的加载和展示调用时，只要广告是未成功状态，均不执行show操作
            if (isLoadOnly && sdkSupplier.adStatus != EasyAdsConstant.AD_STATUS_LOAD_SUCCESS) {
                sdkSupplier.adStatus = EasyAdsConstant.AD_STATUS_NEED_SHOW;
                EALog.simple(TAG + "广告尚未获取到，暂无法执行展示，请耐心等待，SDK会在收到广告后，立即执行广告展示。若广告一直未展示，请检查广告失败回调和对应log信息，了解展示失败原因");
                return;
            }
            //保证在主线程调用show
            EAUtil.switchMainThread(new BaseEnsureListener() {
                @Override
                public void ensure() {
                    doShowAD();
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(ERROR_EXCEPTION_SHOW, "BaseSupplierAdapter show Throwable"));
        }
    }

    @Override
    public void loadAndShow() {
        isLoadOnly = false;
        startAdapterADLoad();
    }


    @Override
    public void destroy() {
        try {
            isDestroy = true;
            doDestroy();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 抽象基础方法，使子类仅关注对应的广告处理逻辑
     */

    //抽象方法，此方法内应执行广告加载方法
    protected abstract void doLoadAD();

    //抽象方法，此方法内应执行广告展示方法（如有）
    protected abstract void doShowAD();

    //抽象方法，此方法内应执行广告销毁操作（如有）
    protected abstract void doDestroy();


    //开始调用加载广告方法
    public void startAdapterADLoad() {
        try {
            if (sdkSupplier != null) {
                String msg = TAG + "开始请求广告 ";
                EALog.simple(msg);
            }
            ++adNum;
            //保证在主线程调用load
            EAUtil.switchMainThread(new BaseEnsureListener() {
                @Override
                public void ensure() {
                    doLoadAD();
                }
            });
            sdkSupplier.adStatus = EasyAdsConstant.AD_STATUS_LOADING;
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(ERROR_EXCEPTION_LOAD, "BaseSupplierAdapter load Throwable"));
        }
    }


    /**
     * banner 特有的失败处理，主要是自动刷新部分的处理比较特殊
     *
     * @param easyAdError
     */
    public void doBannerFailed(EasyAdError easyAdError) {
        try {
            if (isBannerFailed()) {
                handleFailed(easyAdError);
                //广告失败并进行销毁
                doDestroy();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * banner 广告是否算失败，因为有刷新的逻辑，如果刷新中失败，是可以按照不失败继续执行刷新的
     */
    private boolean isBannerFailed() {
        String tag = "【isBannerFailed-check】";
        //如果未在展示中，失败了需要进行销毁，否则会在后台自动进行请求
        boolean isRunning = true;
        if (sdkSupplier != null) {
            int pri = sdkSupplier.priority;
            if (baseListener != null && baseListener.getSupplierInf() != null) {
                int curPri = baseListener.getSupplierInf().priority;
                EALog.high(tag + "curPri = " + curPri + " pri = " + pri);
                isRunning = curPri == pri;
            }
        }
        EALog.high(tag + "refreshing = " + refreshing + " isRunning = " + isRunning);

        if (refreshing && isRunning) {
            EALog.high(tag + "等待刷新中，即使失败也不进行销毁操作");
            return false;
        }
        EALog.simple(tag + " 广告失败，进行销毁操作");
        return true;
    }


    /**
     * 是否优化初始化流程，默认为true代表自动优化初始化方法，多次相同appID的初始化不重复执行
     *
     * @return 优化
     */
    public boolean canOptInit() {
        return true;
    }

    /**
     * 获取策略中配置的媒体id。
     */
    public String getAppID() {
        String appID = "";
        if (sdkSupplier != null) {
            appID = sdkSupplier.appId;
        }
        return appID;
    }

    /**
     * 获取策略中配置的广告位id。
     */
    public String getPosID() {
        String posID = "";
        if (sdkSupplier != null) {
            posID = sdkSupplier.adspotId;
        }
        return posID;
    }

    /**
     * --------- 以下是公共处理核心回调事件方法  ----------
     */

    public void handleClick() {
        try {
            EALog.simple(TAG + "广告点击");
            EAUtil.switchMainThread(new BaseEnsureListener() {
                @Override
                public void ensure() {
                    if (baseListener == null) {
                        EALog.e(TAG + "baseListener is null");
                        return;
                    }
                    //这里集中处理回调事件
                    baseListener.adapterDidClicked(sdkSupplier);

                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void handleExposure() {
        try {
            EALog.simple(TAG + "广告曝光成功");
            EAUtil.switchMainThread(new BaseEnsureListener() {
                @Override
                public void ensure() {
                    if (baseListener == null) {
                        EALog.e(TAG + "baseListener is null");
                        return;
                    }
                    //这里集中处理回调事件
                    baseListener.adapterDidExposure(sdkSupplier);

                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void handleSucceed() {
        try {
            EALog.simple(TAG + "广告获取成功");
            sdkSupplier.adStatus = EasyAdsConstant.AD_STATUS_LOAD_SUCCESS;
            EAUtil.switchMainThread(new BaseEnsureListener() {
                @Override
                public void ensure() {
                    if (baseListener == null) {
                        EALog.e(TAG + "baseListener is null");
                        return;
                    }
                    //这里集中处理回调事件
                    baseListener.adapterDidSucceed(sdkSupplier);

                    if (!isLoadOnly) {
                        show();
                    } else {
                        //代表调用过show方法，此时就要自动去执行展示广告流程
                        if (sdkSupplier.adStatus == EasyAdsConstant.AD_STATUS_NEED_SHOW) {
                            show();
                        }
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }

    public void handleFailed(int errCode, String errMsg) {
        handleFailed(errCode + "", errMsg);
    }

    public void handleFailed(String errCode, String errMsg) {
        handleFailed(EasyAdError.parseErr(errCode, errMsg));
    }

    //统一处理，告诉基础类失败了，需要走下一优先级的渠道
    public void handleFailed(EasyAdError error) {
        try {
            if (error != null) {
                EALog.simple(TAG + "广告获取失败 , 失败信息：" + error.toString());
            }

            //避免重复执行失败任务
            checkFailed();

            sdkSupplier.adStatus = EasyAdsConstant.AD_STATUS_LOAD_FAILED;
            if (baseListener == null) {
                EALog.e(TAG + "baseListener is null");
                return;
            }
            baseListener.adapterDidFailed(error, sdkSupplier);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查失败唯一性，避免重复回调失败
     *
     * @throws Exception
     */
    private void checkFailed() throws Exception {
        String exInf = "  -- ad failed check --  ,  already failed , skip callback onFailed";

        --adNum;
        EALog.max(TAG + "[checkFailed] adNum = " + adNum);
        if (sdkSupplier != null) {
            //避免重复执行失败任务
            if (hasFailed && lastFailedPri == sdkSupplier.priority && adNum < 0) {
                EALog.high(TAG + exInf);
                throw new Exception(exInf);
            }
            hasFailed = true;
            lastFailedPri = sdkSupplier.priority;
        }
    }
}

package com.easyads.core.splash;

import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.itf.BaseEnsureListener;
import com.easyads.model.EasyAdType;
import com.easyads.model.SdkSupplier;
import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;
import com.easyads.utils.ScreenUtil;


public class EasyAdSplash extends EasyAdBaseAdspot implements EASplashSetting {
    private EASplashListener listener;
    private ViewGroup adContainer;
    //穿山甲尺寸，单位px
    private int csjAcceptedSizeWidth = 1080;
    private int csjAcceptedSizeHeight = 1920;
    //穿山甲模板宽高，单位dp
    private float csjExpressViewWidth = 360;
    private float csjExpressViewHeight = 640;
    private String failed_waring = "当前渠道（%1$s）已失败，等待后续渠道结果";

    private boolean csjShowAsExpress = false; //设置穿山甲广告是否以个性化模板形式加载广告
    private boolean canJump = false; //内部控制变量，是否可以进行广告跳转
    private boolean showInSingleActivity = true;//APP实现是否为单独activity中展示开屏广告

    public boolean isADSkip = false;//用来判断用户是否选择了跳过广告


    public EasyAdSplash(final Activity activity, final ViewGroup adContainer, EASplashListener listener) {
        super(activity, listener);
        this.adContainer = adContainer;
        this.listener = listener;
        try {
            //赋值ad类型
            adType = EasyAdType.SPLASH;
            splashLifeCallback = new EASplashLifeCallback() {
                @Override
                public void onResume() {
                    if (canJump) {
                        doJump();
                    }
                    canJump = true;
                }

                @Override
                public void onPause() {
                    canJump = TextUtils.equals(currentSdkTag, EasyAdsConstant.SDK_TAG_CSJ);
                }
            };


            adContainer.post(new Runnable() {
                @Override
                public void run() {
                    csjAcceptedSizeWidth = adContainer.getWidth();
                    csjAcceptedSizeHeight = adContainer.getHeight();

                    csjExpressViewWidth = ScreenUtil.px2dip(activity, adContainer.getWidth());
                    csjExpressViewHeight = ScreenUtil.px2dip(activity, adContainer.getHeight());
                    EALog.devDebug("set expressViewWidth as adContainer Width= " + csjExpressViewWidth + " Height= " + csjExpressViewHeight);

                }
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initSdkSupplier() {
        try {
            initAdapter(EasyAdsConstant.SDK_TAG_CSJ, this);
            initAdapter(EasyAdsConstant.SDK_TAG_YLH, this);
            initAdapter(EasyAdsConstant.SDK_TAG_BAIDU, this);
            initAdapter(EasyAdsConstant.SDK_TAG_KS, this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startLoad() {
        canJump = false;
        super.startLoad();
    }


    /**
     * 统一处理跳转逻辑
     */
    private void doJump() {
        try {
            BaseEnsureListener ensureListener = new BaseEnsureListener() {
                @Override
                public void ensure() {
                    EALog.high("[EasyAdSplash] canJump = " + canJump);
                    if (canJump) {
                        if (listener != null)
                            listener.onAdClose();
                    } else {
                        canJump = true;
                    }
                }
            };
            EAUtil.switchMainThread(ensureListener);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void adapterDidExposure(SdkSupplier supplier) {
        super.adapterDidExposure(supplier);
        //防止无法跳转
        canJump = true;
    }

    public void adapterDidSkip(SdkSupplier supplier) {
        updateSupplier("adapterDidSkip", supplier);

        if (supplier != null && supplier.adStatus == EasyAdsConstant.AD_STATUS_LOAD_FAILED) {
            EALog.w(String.format(failed_waring,supplier.tag));
            return;
        }
        isADSkip = true;
        doJump();
    }

    public void adapterDidTimeOver(SdkSupplier supplier) {
        updateSupplier("adapterDidTimeOver", supplier);

        if (supplier != null && supplier.adStatus == EasyAdsConstant.AD_STATUS_LOAD_FAILED) {
            EALog.w(String.format(failed_waring,supplier.tag));
            return;
        }
        isADSkip = false;
        doJump();
    }


    /**
     * 开屏位置公共属性的获取和赋值
     */
    public boolean isShowInSingleActivity() {
        return showInSingleActivity;
    }

    public void setShowInSingleActivity(boolean single) {
        showInSingleActivity = single;
        canJump = single;
    }

    /**
     * 设置穿山甲是否为模板开屏类型
     *
     * @param isExpress 模板标志，true为模板
     */
    public void setCsjShowAsExpress(boolean isExpress) {
        csjShowAsExpress = isExpress;
    }

    public boolean getCsjShowAsExpress() {
        return csjShowAsExpress;
    }

    public int getCsjAcceptedSizeWidth() {
        return csjAcceptedSizeWidth;
    }

    public int getCsjAcceptedSizeHeight() {
        return csjAcceptedSizeHeight;
    }

    public float getCsjExpressViewWidth() {
        return csjExpressViewWidth;
    }

    public float getCsjExpressViewHeight() {
        return csjExpressViewHeight;
    }

    @Override
    public ViewGroup getAdContainer() {
        return adContainer;
    }

}

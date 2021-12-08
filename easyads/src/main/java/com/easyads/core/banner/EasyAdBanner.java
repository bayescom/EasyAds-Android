package com.easyads.core.banner;

import android.app.Activity;
import android.view.ViewGroup;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.model.EasyAdType;
import com.easyads.model.SdkSupplier;
import com.easyads.utils.ScreenUtil;


public class EasyAdBanner extends EasyAdBaseAdspot implements EABannerSetting {
    private ViewGroup adContainer; //banner 父布局
    private int refreshInterval = 0; //定时刷新时间，单位为秒，仅优量汇、穿山甲生效
    private EABannerListener listener;//广告事件回调

    public int csjExpressViewAcceptedWidth = 360; //穿山甲模板尺寸宽度，单位dp，一定要和穿山甲后台配置的尺寸匹配
    public int csjExpressViewAcceptedHeight = 0;

    /**
     * 构造方法
     *
     * @param activity    上下文
     * @param adContainer 广告父布局
     * @param listener    广告事件回调
     */
    public EasyAdBanner(Activity activity, final ViewGroup adContainer, EABannerListener listener) {
        super(activity, listener);
        try {
            adType = EasyAdType.BANNER;//赋值广告类型
            this.adContainer = adContainer;
            this.listener = listener;

            //默认设置为全屏宽度
            csjExpressViewAcceptedWidth = ScreenUtil.px2dip(activity, ScreenUtil.getScreenWidth(activity));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void initSdkSupplier() {
        try {
            initAdapter(EasyAdsConstant.SDK_TAG_CSJ, this);
            initAdapter(EasyAdsConstant.SDK_TAG_YLH, this);
            initAdapter(EasyAdsConstant.SDK_TAG_BAIDU, this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public void adapterDidExposure(SdkSupplier supplier) {
        super.adapterDidExposure(supplier);

        //一旦有渠道展示成功了 则将其他渠道进行销毁操作，避免定时器触发问题
        if (currentSdkSupplier != null) {
            destroyOtherSupplier(currentSdkSupplier);
        }
    }


    public void adapterDidDislike(SdkSupplier supplier) {
        updateSupplier("adapterDidDislike", supplier);
        if (null != listener) {
            listener.onAdClose();
        }
    }

    public EasyAdBanner setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
        return this;
    }

    public void setCsjExpressSize(int width, int height) {
        csjExpressViewAcceptedWidth = width;
        csjExpressViewAcceptedHeight = height;
    }

    public ViewGroup getContainer() {
        return adContainer;
    }

    public int getRefreshInterval() {
        return this.refreshInterval;
    }

    public int getCsjExpressViewAcceptedWidth() {
        return this.csjExpressViewAcceptedWidth;
    }

    public int getCsjExpressViewAcceptedHeight() {
        return this.csjExpressViewAcceptedHeight;
    }
}

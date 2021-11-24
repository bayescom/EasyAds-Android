package com.easyads.core.nati;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.model.EasyAdType;
import com.easyads.model.SdkSupplier;
import com.easyads.utils.EALog;
import com.easyads.utils.ScreenUtil;

public class EasyAdNativeExpress extends EasyAdBaseAdspot implements EANativeExpressSetting {
    private EANativeExpressListener listener;
    private int expressViewWidth = 600;//模板尺寸设置，单位dp
    private int expressViewHeight = 0;//模板尺寸设置，单位dp，高度为0 代表自适应高度
    private boolean videoMute = true; //视频静音，仅优量汇生效，穿山甲在后台中配置
    private int ylhMaxVideoDuration = 60;//优量汇最大视频时长，单位秒
    private ViewGroup adContainer; //广告承载布局
    private View expressADView; //广告的实际展示view信息，数据来源于广告SDK回调


    public EasyAdNativeExpress(Activity activity, EANativeExpressListener listener) {
        super(activity, listener);
        adType = EasyAdType.NATIV;

        this.listener = listener;
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


    public void adapterDidClosed(SdkSupplier supplier) {
        updateSupplier("adapterDidClosed", supplier);
        if (null != listener) {
            listener.onAdClose();
        }
    }

    public void adapterRenderFailed(SdkSupplier supplier) {
        updateSupplier("adapterRenderFailed", supplier);
        if (null != listener) {
            listener.onAdRenderFailed();
        }
    }

    public void adapterRenderSuccess(SdkSupplier supplier) {
        updateSupplier("adapterRenderSuccess", supplier);
        if (null != listener) {
            listener.onAdRenderSuccess();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            if (adContainer != null) {
                adContainer.removeAllViews();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 个性化配置类参数设置
     */


    //   设置用来展示广告的父布局
    public void setAdContainer(ViewGroup container) {
        adContainer = container;

        try {
            adContainer.post(new Runnable() {
                @Override
                public void run() {
                    expressViewWidth = ScreenUtil.px2dip(getActivity(), adContainer.getWidth());
                    expressViewHeight = ScreenUtil.px2dip(getActivity(), adContainer.getHeight());
                    EALog.devDebug("set expressViewWidth as adContainer Width= " + expressViewWidth + " Height= " + expressViewHeight);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public EasyAdNativeExpress setExpressViewAcceptedSize(int width, int height) {
        this.expressViewWidth = width;
        this.expressViewHeight = height;
        return this;
    }


    // 设置视频类广告是否默认静音，默认true
    public EasyAdNativeExpress setVideoMute(boolean mute) {
        this.videoMute = mute;
        return this;
    }

    public EasyAdNativeExpress setYlhMaxVideoDuration(int ylhMaxVideoDuration) {
        this.ylhMaxVideoDuration = ylhMaxVideoDuration;
        return this;
    }

    @Override
    public ViewGroup getAdContainer() {
        return adContainer;
    }

    @Override
    public void setNativeExpressADView(View expressADView) {
        this.expressADView = expressADView;
    }


    public int getExpressViewWidth() {
        return expressViewWidth;
    }

    public int getExpressViewHeight() {
        return expressViewHeight;
    }


    @Override
    public boolean isVideoMute() {
        return videoMute;
    }

    public int getYlhMaxVideoDuration() {
        return ylhMaxVideoDuration;
    }


    public View getExpressADView() {
        return expressADView;
    }
}

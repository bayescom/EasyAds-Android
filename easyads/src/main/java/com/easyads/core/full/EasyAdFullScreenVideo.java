package com.easyads.core.full;

import android.app.Activity;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.model.EasyAdType;
import com.easyads.model.SdkSupplier;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.interstitial2.UnifiedInterstitialMediaListener;

public class EasyAdFullScreenVideo extends EasyAdBaseAdspot implements EAFullScreenVideoSetting {
    private EAFullScreenVideoListener listener;//广告事件回调
    private UnifiedInterstitialMediaListener ylhMediaListener;//优量汇视频加载情况监听器
    private VideoOption ylhVideoOption;//优量汇视频广告设置项

    public EasyAdFullScreenVideo(Activity activity, EAFullScreenVideoListener listener) {
        super(activity, listener);
        adType = EasyAdType.FULL;//赋值广告类型

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


    @Override
    public UnifiedInterstitialMediaListener getYlhMediaListener() {
        return ylhMediaListener;
    }

    @Override
    public VideoOption getYlhVideoOption() {
        return ylhVideoOption;
    }

    public void setYlhMediaListener(UnifiedInterstitialMediaListener mediaListener) {
        this.ylhMediaListener = mediaListener;
    }

    public void setYlhVideoOption(VideoOption videoOption) {
        this.ylhVideoOption = videoOption;
    }

    public void adapterVideoCached(SdkSupplier supplier) {
        updateSupplier("adapterVideoCached", supplier);
        if (null != listener) {
            listener.onVideoCached();
        }
    }

    public void adapterVideoComplete(SdkSupplier supplier) {
        updateSupplier("adapterVideoComplete", supplier);
        if (null != listener) {
            listener.onVideoComplete();
        }
    }

    public void adapterClose(SdkSupplier supplier) {
        updateSupplier("adapterClose", supplier);
        if (null != listener) {
            listener.onAdClose();
        }
    }

    public void adapterVideoSkipped(SdkSupplier supplier) {
        updateSupplier("adapterVideoSkipped", supplier);
        if (null != listener) {
            listener.onVideoSkipped();
        }
    }


}

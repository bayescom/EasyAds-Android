package com.easyads.core.draw;

import android.app.Activity;
import android.view.ViewGroup;

import com.easyads.EasyAdsConstant;
import com.easyads.core.EasyAdBaseAdspot;
import com.easyads.model.EasyAdType;
import com.easyads.utils.EALog;
import com.easyads.utils.ScreenUtil;

public class EasyAdDraw extends EasyAdBaseAdspot implements EADrawSetting {

    EADrawListener listener;//广告事件回调
    ViewGroup adContainer;//广告父布局
    private int csjExpressWidth;//穿山甲模板尺寸宽度，单位dp
    private int csjExpressHeight;

    public EasyAdDraw(Activity activity, EADrawListener listener) {
        super(activity, listener);
        try {
            //赋值广告类型
            adType = EasyAdType.DRAW;

            this.listener = listener;
            //默认赋值为屏幕宽高
            csjExpressWidth = ScreenUtil.px2dip(activity, ScreenUtil.getScreenWidth(activity));
            csjExpressHeight = ScreenUtil.px2dip(activity, ScreenUtil.getScreenHeight(activity));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void initSdkSupplier() {
        try {
            initAdapter(EasyAdsConstant.SDK_TAG_CSJ, this);
            initAdapter(EasyAdsConstant.SDK_TAG_KS, this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 以下为setting回调项处理
     */

    public void setAdContainer(final ViewGroup adContainer) {
        try {
            this.adContainer = adContainer;
            //将模板尺寸宽高，自动赋值为父布局的宽高属性
            adContainer.post(new Runnable() {
                @Override
                public void run() {
                    csjExpressWidth = ScreenUtil.px2dip(getActivity(), adContainer.getWidth());
                    csjExpressHeight = ScreenUtil.px2dip(getActivity(), adContainer.getHeight());
                    EALog.devDebug("set csjExpressView as adContainer Width= " + csjExpressWidth + " Height= " + csjExpressHeight);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public ViewGroup getContainer() {
        return adContainer;
    }


    @Override
    public int getCsjExpressHeight() {
        return csjExpressHeight;
    }

    @Override
    public int getCsjExpressWidth() {
        return csjExpressWidth;
    }

}

package com.easyads.itf;

import com.easyads.model.EasyAdError;
import com.easyads.model.SdkSupplier;

/**
 * adapter 处理基类，所有的广告adapter必须实现的方法
 */
public interface BaseAdapterEvent {
    void adapterDidSucceed(SdkSupplier supplier);

    void adapterDidExposure(SdkSupplier supplier);

    void adapterDidClicked(SdkSupplier supplier);

    void adapterDidFailed(EasyAdError easyAdError, SdkSupplier sdkSupplier);//聚合失败回调,回传sdkSupplier信息

    SdkSupplier getSupplierInf(); //获取当前的渠道策略信息
}

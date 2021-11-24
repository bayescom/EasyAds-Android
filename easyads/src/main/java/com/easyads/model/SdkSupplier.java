package com.easyads.model;

import com.easyads.EasyAdsConstant;

import java.io.Serializable;

public class SdkSupplier implements Comparable<SdkSupplier>, Serializable {
    public String adspotId; //SDK广告位id
    public String appId; //SDK应用id
    public String tag = ""; //SDK标记

    public int index = -1;//坐标标记
    public int versionTag = -1;//版本标记，预留字段，主要为了区分相同广告类型的不同版本

    public String ruleTag = "";//当前渠道所在的策略组标记
    public int priority = 1; //优先级，根据策略设定，进行优先级概念的定义,值越小代表优先级越靠前

    public int adStatus = EasyAdsConstant.AD_STATUS_DEFAULT; //广告加载状态标记

    @Override
    public int compareTo(SdkSupplier o) {
        if (this.priority < o.priority) {
            return -1;
        } else if (this.priority == o.priority) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public String toString() {
        return "SdkSupplier{" +
                "ruleTag='" + ruleTag + '\'' +
                ", tag='" + tag + '\'' +
                ", priority=" + priority +
                ", adspotid='" + adspotId + '\'' +
                ", appid='" + appId + '\'' +
                ", versionTag=" + versionTag +
                ", adStatus=" + adStatus +
                '}';
    }
}

package com.easyads.model;

import com.easyads.EasyAdsConstant;

import java.io.Serializable;
import java.util.ArrayList;

//SDK的加载规则
public class SdkRule implements Serializable {

    public String tag = ""; // 策略组标记
    public ArrayList<Integer> sort = new ArrayList<>();//广告加载index排序组，优先级由高到低依次排序
    public double percent = EasyAdsConstant.DEFAULT_PERCENT;//使用当前规则的概率，建议使用百分值

    public int lowLimit = 0;//下限，用来计算是否命中当前策略组
    public int highLimit = 0;//上限，用来计算是否命中当前策略组

    public ArrayList<SdkSupplier> sortedSuppliers = new ArrayList<>(); //排序后的可执行的渠道列表

}

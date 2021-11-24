package com.easyads;

public class EasyAdsConstant {
    public static final String NOT_SUPPORT_SUPPLIER_TIPS = "未支持的SDK渠道，跳过该渠道加载。请先检查是否引入了该渠道处理依赖，如已引入，检查下发渠道信息，如未在渠道已支持列表中，请请查看文档使用自定义渠道来完成广告加载";

    /**
     * SDK的tag标记
     */
    //腾讯优量汇（原优量汇）
    public static final String SDK_TAG_YLH = "ylh";
    //头条穿山甲
    public static final String SDK_TAG_CSJ = "csj";
    //百度百青藤
    public static final String SDK_TAG_BAIDU = "bd";
    //快手
    public static final String SDK_TAG_KS = "ks";


    /**
     * 策略分发占比默认值，默认100 代表100%执行
     */
    public static final double DEFAULT_PERCENT = 0d;


    /**
     * 广告状态记录，主要是用户调用广告展示时，分析广告状态，来执行不同操作
     */
    //默认状态
    public static final int AD_STATUS_DEFAULT = -1;
    //广告请求中
    public static final int AD_STATUS_LOADING = 0;
    //广告请求成功了
    public static final int AD_STATUS_LOAD_SUCCESS = 1;
    //广告失败了
    public static final int AD_STATUS_LOAD_FAILED = 2;
    //广告需要被展示
    public static final int AD_STATUS_NEED_SHOW = 3;
}

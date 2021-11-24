package com.easyads.model;

import android.support.annotation.NonNull;

public class EasyAdError {

    public static final String ERROR_DEFAULT = "99";

    public static final String ERROR_DATA_NULL = "9901"; //广告返回的数据为空
    public static final String ERROR_EXCEPTION_LOAD = "9902";
    public static final String ERROR_EXCEPTION_SHOW = "9903";
    public static final String ERROR_EXCEPTION_RENDER = "9904";//
    public static final String ERROR_NONE_SDK = "9905";//未配置SDK渠道
    public static final String ERROR_SUPPLIER_SELECT = "9906"; //执行SDK渠道选择是发生异常
    public static final String ERROR_NONE_STRATEGY = "9907";
    public static final String ERROR_LOAD_SDK = "9908";
    public static final String ERROR_CSJ_SKIP = "9909";
    public static final String ERROR_CSJ_TIMEOUT = "9910";
    public static final String ERROR_BD_FAILED = "9911";
    public static final String ERROR_PARAM_FORMAT = "9912"; //参数异常
    public static final String ERROR_NO_ACTIVITY = "9913"; //无activity异常
    public static final String ERROR_KS_INIT = "9914"; //快手初始化异常
    public static final String ERROR_RENDER_FAILED = "9915"; //渲染失败异常
    public static final String ERROR_CSJ_INIT_FAILED = "9916"; //穿山甲初始化失败异常


    public EasyAdError(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String code;
    public String msg;


    public static EasyAdError parseErr(int code) {
        return parseErr(code + "");
    }

    public static EasyAdError parseErr(String code) {
        return parseErr(code, "");
    }

    public static EasyAdError parseErr(int code, String extMsg) {
        return parseErr(code + "", extMsg);
    }

    public static EasyAdError parseErr(String code, String extMsg) {
        EasyAdError error;
        switch (code) {
            case ERROR_DATA_NULL:
                error = new EasyAdError(code, "data null ;" + extMsg);
                break;
            case ERROR_EXCEPTION_LOAD:
                error = new EasyAdError(code, "exception when load ;view System.err log for more " + extMsg);
                break;
            case ERROR_EXCEPTION_SHOW:
                error = new EasyAdError(code, "exception when show ;view System.err log for more  " + extMsg);
                break;
            case ERROR_EXCEPTION_RENDER:
                error = new EasyAdError(code, "exception when render ;view System.err log for more  " + extMsg);
                break;
            case ERROR_NONE_STRATEGY:
                error = new EasyAdError(code, "None Strategy Info: please check setData() function.  " + extMsg);
                break;
            case ERROR_NONE_SDK:
                error = new EasyAdError(code, "none sdk to show ;" + extMsg);
                break;
            case ERROR_SUPPLIER_SELECT:
                error = new EasyAdError(code, "策略调度异常，selectSdkSupplier Throwable ;" + extMsg);
                break;
            case ERROR_LOAD_SDK:
                error = new EasyAdError(code, "sdk 启动异常  " + extMsg);
                break;
            case ERROR_CSJ_SKIP:
                error = new EasyAdError(code, "穿山甲SDK加载超时，不再加载后续SDK渠道  " + extMsg);
                break;
            case ERROR_CSJ_TIMEOUT:
                error = new EasyAdError(code, "穿山甲SDK加载超时  " + extMsg);
                break;
            case ERROR_BD_FAILED:
                error = new EasyAdError(code, "百度SDK加载失败  " + extMsg);
                break;
            case ERROR_PARAM_FORMAT:
                error = new EasyAdError(code, "快手SDK加载失败，广告位id类型转换异常  " + extMsg);
                break;
            case ERROR_NO_ACTIVITY:
                error = new EasyAdError(code, "当前activity已被销毁，不再请求广告  " + extMsg);
                break;
            case ERROR_KS_INIT:
                error = new EasyAdError(code, "快手SDK初始化失败  " + extMsg);
                break;
            case ERROR_RENDER_FAILED:
                error = new EasyAdError(code, "广告渲染失败  " + extMsg);
                break;
            case ERROR_CSJ_INIT_FAILED:
                error = new EasyAdError(code, "穿山甲SDK初始化失败  " + extMsg);
                break;

            default:
                //优量汇的详细异常码
                String extCode = "";
                try {
                    if ("6000".equals(code) && !extMsg.isEmpty()) {
                        int startIndex = extMsg.lastIndexOf("详细码") + 4;
                        extCode = extMsg.substring(startIndex, startIndex + 6);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                error = new EasyAdError(ERROR_DEFAULT + "_" + code + extCode, "err from sdk callback : [" + code + "] " + extMsg);
        }


        return error;
    }

    @NonNull
    @Override
    public String toString() {
        return "[EasyAdError] errorCode = " + code + " ; errorMsg = " + msg;
    }
}

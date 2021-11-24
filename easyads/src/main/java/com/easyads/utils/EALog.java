package com.easyads.utils;

import android.util.Log;

import com.easyads.EasyAdsManger;
import com.easyads.model.EALogLevel;

import java.io.PrintWriter;
import java.io.StringWriter;

public class EALog {
    private static String LOG = "EasyAds-log";

    public static void d(String s) {
        if (isDebug()) {
            Log.d(LOG, s);
        }
    }

    //打印核心信息
    public static void simple(String s) {
        if (isDebug() && showSimpleLog()) {
            Log.d(LOG, "" + s);
        }
    }

    //打印调试用信息
    public static void high(String s) {
        if (isDebug() && showHighLog()) {
            Log.d(LOG, "[H] " + s);
        }
    }

    //打印全部可用信息
    public static void max(String s) {
        if (isDebug() && showAllLog()) {
            Log.d(LOG, "[A] " + s);
        }
    }

    public static void devDebug(String devText) {
        if (EasyAdsManger.getInstance().isDev && EasyAdsManger.getInstance().debug) {
            Log.d(LOG, "[dev] " + devText);
        }
    }

    public static void devDebugAuto(String devText, String debugText) {
        try {
            if (isDebug()) {
                String lt;
                if (EasyAdsManger.getInstance().isDev) {
                    lt = "[dev] " + devText + debugText;
                } else {
                    lt = debugText;
                }
                Log.d(LOG, lt);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static void w(String s) {
        if (isDebug()) {
            Log.w(LOG, s);
        }
    }

    public static void e(String s) {
        if (isDebug()) {
            Log.e(LOG, s);
        }
    }

    public static boolean isDebug() {
        return EasyAdsManger.getInstance().debug;
    }

    //是否可以显示基础等级log
    public static boolean showSimpleLog() {
        return EasyAdsManger.getInstance().logLevel.level >= EALogLevel.DEFAULT.level;
    }

    //是否可以显示高级log信息
    public static boolean showHighLog() {
        return EasyAdsManger.getInstance().logLevel.level >= EALogLevel.HIGH.level;
    }

    //是否可以显示全部log
    public static boolean showAllLog() {
        return EasyAdsManger.getInstance().logLevel.level >= EALogLevel.MAX.level;
    }

    //打印错误信息
    public static String getThrowableLog(Throwable paramThrowable) {
        StringWriter localStringWriter = null;
        PrintWriter localPrintWriter = null;
        try {
            localStringWriter = new StringWriter();
            localPrintWriter = new PrintWriter(localStringWriter);
            paramThrowable.printStackTrace(localPrintWriter);
            for (Throwable localThrowable = paramThrowable.getCause(); localThrowable != null; localThrowable = localThrowable.getCause())
                localThrowable.printStackTrace(localPrintWriter);
            String str = localStringWriter.toString();
            localPrintWriter.close();
            return str;
        } catch (Throwable e2) {
            return "";
        } finally {
            try {
                if (localStringWriter != null)
                    localStringWriter.close();
                if (localPrintWriter != null)
                    localPrintWriter.close();
            } catch (Throwable ignored) {
            }
        }
    }
}

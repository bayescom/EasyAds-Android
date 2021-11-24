package com.easyads.utils;

import android.app.Activity;
import android.content.Context;

public class ScreenUtil {
    public ScreenUtil() {

    }

    public static int dip2px(Activity activity, float dpValue) {
        return dip2pxC(activity, dpValue);
    }

    public static int dip2pxC(Context context, float dpValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int px2dip(Activity activity, float pxValue) {
        return px2dipC(activity, pxValue);
    }

    public static int px2dipC(Context context, float pxValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getScreenWidth(Activity activity) {
        return getScreenWidthC(activity);
    }

    public static int getScreenWidthC(Context context) {
        try {
            return context.getResources()
                    .getDisplayMetrics().widthPixels;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getScreenHeight(Activity activity) {
        return getScreenHeightC(activity);
    }

    public static int getScreenHeightC(Context context) {
        try {
            return context.getResources()
                    .getDisplayMetrics().heightPixels;
        } catch (Throwable e) {
            e.printStackTrace();
            return -1;
        }
    }
}

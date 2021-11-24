package com.easyads.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.easyads.EasyAdsManger;
import com.easyads.itf.BaseEnsureListener;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.UUID;

public class EAUtil {
    public static String getUUID() {
        try {
            return UUID.randomUUID().toString().replace("-", "");
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 判断页面是否被销毁了
     *
     * @param activity
     * @return
     */
    public static boolean isActivityDestroyed(Activity activity) {
        try {
            if (activity == null) {
                return true;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return activity.isDestroyed() || activity.isFinishing();
            } else {
                return activity.isFinishing();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isActivityDestroyed(SoftReference<Activity> activity) {
        try {
            if (activity == null || activity.get() == null) {
                return true;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                return activity.get().isDestroyed() || activity.get().isFinishing();
            } else {
                return activity.get().isFinishing();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void switchMainThread(final BaseEnsureListener ensureListener) {
        try {
            boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
            if (isMainThread) {
                ensureListener.ensure();
            } else {
                //如果是非主线程，需要强制切换到主线程来进行初始化
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EALog.high("[switchMainThread] force to main thread");
                            ensureListener.ensure();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void switchMainThreadDelay(final BaseEnsureListener ensureListener, long delayTime) {
        try {
            EALog.high("[switchMainThreadDelay] " + delayTime + "ms later force to main thread");

            //如果是非主线程，需要强制切换到主线程来进行初始化
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        ensureListener.ensure();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }, delayTime);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void removeFromParent(View view) {
        try {
            if (view != null) {
                ViewParent vp = view.getParent();
                if (vp instanceof ViewGroup) {
                    ((ViewGroup) vp).removeView(view);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 标记开发者身份
     *
     * @return 是否为开发者
     */
    public static boolean isDev() {
        return EasyAdsManger.getInstance().isDev && EasyAdsManger.getInstance().debug;
    }

    public static void autoClose(final View view) {
        try {
            //不执行自动关闭
            int closeDur = EasyAdsManger.getInstance().getSplashPlusAutoClose();
            if (closeDur < 0) {
                return;
            }
            //延迟给定时间，执行移除布局操作
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    EAUtil.removeFromParent(view);
                }
            }, closeDur);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public static boolean isMainProcess(Context context) {
        boolean result;
        try {
            result = context.getPackageName().equals(getCurrentProcessName(context));
        } catch (Throwable e) {
            e.printStackTrace();
            result = false;
        }
        return result;

    }

    public static String getCurrentProcessName(Context context) {
        try {
            int pid = Process.myPid();
            String currentProcessName = "";
            ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
                if (pid == processInfo.pid) {
                    currentProcessName = processInfo.processName;
                }
            }
            return currentProcessName;
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        }
    }

}

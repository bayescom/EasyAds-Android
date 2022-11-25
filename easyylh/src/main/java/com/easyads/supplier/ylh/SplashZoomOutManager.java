package com.easyads.supplier.ylh;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.easyads.utils.EAUtil;
import com.easyads.utils.EALog;
import com.easyads.utils.ScreenUtil;
import com.easyads.utils.EASplashZoomOutLayout;
import com.qq.e.ads.splash.SplashAD;


public class SplashZoomOutManager {
    private static final String TAG = "[SplashZoomOutManager] ";
    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private int zoomOutWidth;//悬浮窗的宽度
    private int zoomOutHeight;//悬浮窗的高度
    private int zoomOutMargin;//悬浮窗最小离屏幕边缘的距离
    private int zoomOutAbove;//悬浮窗默认距离屏幕底端的高度
    private int zoomOutPos;//悬浮窗默认位于屏幕左面或右面
    private int zoomOutAnimationTime;//悬浮窗缩放动画的，单位ms

    private SplashAD splashAD;
    private View splashView;

    private int originSplashWidth;
    private int originSplashHeight;
    private int[] originSplashPos = new int[2];
    private int decorViewWidth;
    private int decorViewHeight;

    public interface AnimationCallBack {
        void animationStart(int animationTime);

        void animationEnd();
    }

    private static final class Holder {
        private static SplashZoomOutManager instance = new SplashZoomOutManager();
    }

    public static SplashZoomOutManager getInstance() {
        return Holder.instance;
    }

    private SplashZoomOutManager() {

    }


    public void initSize(Context context) {
        try {
            int deviceWidth = Math.min(ScreenUtil.getScreenHeightC(context), ScreenUtil.getScreenWidthC(context));
            EALog.high(TAG + "deviceWidth = " + deviceWidth + "， context = " + context);

            zoomOutWidth = Math.round(deviceWidth * 0.3f);//屏幕宽度的30%，之前使用PxUtils.dpToPx(context, 90);
            zoomOutHeight = Math.round(zoomOutWidth * 16 / 9);//根据宽度计算高度，之前使用PxUtils.dpToPx(context, 160);

            zoomOutMargin = ScreenUtil.dip2pxC(context, 6);
            zoomOutAbove = ScreenUtil.dip2pxC(context, 100);
            zoomOutPos = RIGHT;
            zoomOutAnimationTime = 300;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    /**
     * 用于开屏v+在两个activity之间传递数据
     *
     * @param splashAD   开屏对应的广告数据
     * @param splashView 开屏对应显示view，外部提供开屏container的子view
     * @param decorView  因为在另一个单独的activity启动时获取不到view尺寸，在这里获取下decorView的尺寸，在展示悬挂的
     *                   activity使用该尺寸布局
     */
    public void setSplashInfo(SplashAD splashAD, View splashView, View decorView) {
        this.splashAD = splashAD;
        this.splashView = splashView;
        splashView.getLocationOnScreen(originSplashPos);
        originSplashWidth = splashView.getWidth();
        originSplashHeight = splashView.getHeight();
        decorViewWidth = decorView.getWidth();
        decorViewHeight = decorView.getHeight();
    }

    public void clearStaticData() {
        splashAD = null;
        splashView = null;
    }

    public SplashAD getSplashAD() {
        return splashAD;
    }

    /**
     * 开屏采用单独的activity时候，悬浮窗显示在另外一个activity使用该函数进行动画
     * 调用前要先调用setSplashInfo设置数据，该函数会使用setSplashInfo设置的数据，并会清除对设置数据的引用
     *
     * @param animationContainer 一般是decorView
     * @param zoomOutContainer   最终浮窗所在的父布局
     * @param callBack           动画完成的回调
     */
    public ViewGroup startZoomOut(final ViewGroup animationContainer,
                                  final ViewGroup zoomOutContainer,
                                  final AnimationCallBack callBack) {
        try {
            EALog.high(TAG + "zoomOut startZoomOut activity");
            if (animationContainer == null || zoomOutContainer == null) {
                EALog.e(TAG + "zoomOut animationContainer or zoomOutContainer is null");
                return null;
            }
            if (splashAD == null || splashView == null) {
                EALog.e(TAG + "zoomOut splashAD or splashView is null");
                return null;
            }
            //先把view按照原来的尺寸显示出来
            int[] animationContainerPos = new int[2];
            animationContainer.getLocationOnScreen(animationContainerPos);
            int x = originSplashPos[0] - animationContainerPos[0];
            int y = originSplashPos[1] - animationContainerPos[1];

            EAUtil.removeFromParent(splashView);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(originSplashWidth,
                    originSplashHeight);
            animationContainer.addView(splashView, layoutParams);
            EALog.max(TAG + "splashView x = " + x + "，y = " + y);
            splashView.setX(x);
            splashView.setY(y);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return startZoomOut(splashView, animationContainer, zoomOutContainer, callBack);
    }

    /**
     * 开屏显示和悬浮窗显示在同一个activity中
     * 使用该函数会清除setSplashInfo设置的数据
     * * 动画步骤：
     * 1、把需要动画的view从父布局中移除出来，目的是在动画时可以隐藏其他开屏的view
     * 2、把splash对应的view加到动画的view里开始动画，因为动画窗口可能比较最终的布局要大
     * 3、在动画结束把splash view加到zoomOutContainer里
     *
     * @param splash             开屏对应的view;
     * @param animationContainer 开屏动画所在的layout
     * @param zoomOutContainer   动画结束时，最终悬浮窗所在的父布局
     * @param callBack           动画结束时的回调，splashAdView无法感知动画的执行时间，需要使用该函数通知动画结束了
     */
    public ViewGroup startZoomOut(final View splash, final ViewGroup animationContainer,
                                  final ViewGroup zoomOutContainer,
                                  final AnimationCallBack callBack) {
        ViewGroup zoomOutView = null;
        try {
            clearStaticData();//单例清除下引用的view和ad数据，免得内存泄漏
            if (splash == null || zoomOutContainer == null) {
                return null;
            }
            final Context context = zoomOutContainer.getContext();
            final int[] splashScreenPos = new int[2];
            splash.getLocationOnScreen(splashScreenPos);

            int fromWidth = splash.getWidth();
            int fromHeight = splash.getHeight();
            int animationContainerWidth = animationContainer.getWidth();
            int animationContainerHeight = animationContainer.getHeight();

            if (animationContainerWidth == 0) {
                animationContainerWidth = decorViewWidth;
            }
            if (animationContainerHeight == 0) {
                animationContainerHeight = decorViewHeight;
            }
            EALog.high(TAG + "zoomOut width:" + zoomOutWidth + " height:" + zoomOutHeight);

            float xScaleRatio = (float) zoomOutWidth / fromWidth;
            float yScaleRation = (float) zoomOutHeight / fromHeight;
            final float animationDistX = zoomOutPos == LEFT ? zoomOutMargin :
                    animationContainerWidth - zoomOutMargin - zoomOutWidth;
            final float animationDistY = animationContainerHeight - zoomOutAbove - zoomOutHeight;  //最终位于container的y坐标

            EALog.max(TAG + "zoomOut animationContainerWidth:" + animationContainerWidth + " " +
                    "animationContainerHeight:" + animationContainerHeight);
            EALog.max(TAG + "zoomOut splashScreenX:" + splashScreenPos[0] + " splashScreenY:" + splashScreenPos[1]);
            EALog.high(TAG + "zoomOut splashWidth:" + fromWidth + " splashHeight:" + fromHeight);
            EALog.max(TAG + "zoomOut animationDistX:" + animationDistX + " animationDistY:" + animationDistY);

            EAUtil.removeFromParent(splash);
            FrameLayout.LayoutParams animationParams = new FrameLayout.LayoutParams(fromWidth, fromHeight);
            animationContainer.addView(splash, animationParams);

            zoomOutView = new EASplashZoomOutLayout(context, zoomOutMargin);

            splash.setPivotX(0);
            splash.setPivotY(0);
            final ViewGroup finalZoomOutView = zoomOutView;
            splash.animate()
                    .scaleX(xScaleRatio)
                    .scaleY(yScaleRation)
                    .x(animationDistX)
                    .y(animationDistY)
                    .setInterpolator(new OvershootInterpolator(0))
                    .setDuration(zoomOutAnimationTime)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            EALog.high(TAG + "zoomOut onAnimationStart");
                            if (callBack != null) {
                                callBack.animationStart(zoomOutAnimationTime);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            EALog.high(TAG + "zoomOut onAnimationEnd");
                            EAUtil.removeFromParent(splash);
                            splash.setScaleX(1);
                            splash.setScaleY(1);
                            splash.setX(0);
                            splash.setY(0);
                            int[] zoomOutContainerScreenPos = new int[2];
                            zoomOutContainer.getLocationOnScreen(zoomOutContainerScreenPos);
                            float distX = animationDistX - zoomOutContainerScreenPos[0] + splashScreenPos[0];
                            float distY = animationDistY - zoomOutContainerScreenPos[1] + splashScreenPos[1];
                            EALog.max(TAG + "zoomOut distX:" + distX + " distY:" + distY);
                            EALog.max(TAG + "zoomOut containerScreenX:" + zoomOutContainerScreenPos[0] + " " +
                                    "containerScreenY:" + zoomOutContainerScreenPos[1]);
                            finalZoomOutView.addView(splash, FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT);
                            FrameLayout.LayoutParams zoomOutParams = new FrameLayout.LayoutParams(zoomOutWidth,
                                    zoomOutHeight);
                            zoomOutContainer.addView(finalZoomOutView, zoomOutParams);
                            finalZoomOutView.setTranslationX(distX);
                            finalZoomOutView.setTranslationY(distY);
                            if (callBack != null) {
                                callBack.animationEnd();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return zoomOutView;
    }

}

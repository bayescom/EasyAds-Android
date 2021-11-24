package com.easyads.supplier.csj;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;

import com.easyads.utils.EALog;
import com.easyads.utils.ScreenUtil;
import com.easyads.utils.EASplashZoomOutLayout;
import com.bytedance.sdk.openadsdk.TTSplashAd;

/**
 * 点睛广告展示处理逻辑
 */
public class CSJSplashClickEyeManager {
    private static final String TAG = "CSJSplashClickEyeManager";
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private int mClickEyeViewWidth;//悬浮窗的宽度
    private int mClickEyeViewHeight;//悬浮窗的高度
    private int mClickEyeViewMargin;//悬浮窗最小离屏幕边缘的距离
    private int mClickEyeViewMarginBottom;//悬浮窗默认距离屏幕底端的高度
    private int mClickEyeViewPos;//悬浮窗默认位于屏幕左面或右面
    private int mClickEyeViewAnimationTime;//悬浮窗缩放动画的，单位ms
//    private int zoomOutMargin;//悬浮窗最小离屏幕边缘的距离

    private TTSplashAd mSplashAd;
    private View mSplashShowView;
    private int[] mOriginSplashPos = new int[2];
    private int mDecorViewWidth;
    private int mDecorViewHeight;
    private volatile static CSJSplashClickEyeManager mInstance;
    private boolean mIsSupportSplashClickEye = false;

    public interface AnimationCallBack {
        void animationStart(int animationTime);

        void animationEnd();
    }

    /**
     * 单例获取SplashClickEyeManager对象
     *
     * @return
     */
    public static CSJSplashClickEyeManager getInstance() {
        if (mInstance == null) {
            synchronized (CSJSplashClickEyeManager.class) {
                if (mInstance == null) {
                    mInstance = new CSJSplashClickEyeManager();
                }
            }
        }
        return mInstance;
    }

    private CSJSplashClickEyeManager() {
        mClickEyeViewPos = RIGHT;
        mClickEyeViewAnimationTime = 300;
    }

    public void init(Context context) {
        initClickEyeViewData(context);

        mClickEyeViewMargin = ScreenUtil.dip2pxC(context, 16);
        mClickEyeViewMarginBottom = ScreenUtil.dip2pxC(context, 100);
    }

    private void initClickEyeViewData(Context context) {
        if (context == null) {
            return;
        }
        if (mSplashAd != null && mSplashAd.getSplashClickEyeSizeToDp() != null) {
            //使用推荐的点睛宽高
            mClickEyeViewWidth = ScreenUtil.dip2pxC(context, mSplashAd.getSplashClickEyeSizeToDp()[0]);
            mClickEyeViewHeight = ScreenUtil.dip2pxC(context, mSplashAd.getSplashClickEyeSizeToDp()[1]);
        } else {
            int deviceWidth = Math.min(ScreenUtil.getScreenHeightC(context), ScreenUtil.getScreenWidthC(context));
            //默认的点睛宽高
            mClickEyeViewWidth = Math.round(deviceWidth * 0.3f);//屏幕宽度的30%，之前使用PxUtils.dpToPx(context, 90);
            mClickEyeViewHeight = Math.round(mClickEyeViewWidth * 16 / 9);//根据宽度计算高度，之前使用PxUtils.dpToPx(context, 160);
        }
    }

    public void setSplashInfo(TTSplashAd splashAd, View splashView, View decorView) {
        this.mSplashAd = splashAd;
        this.mSplashShowView = splashView;
        splashView.getLocationOnScreen(mOriginSplashPos);
        mDecorViewWidth = decorView.getWidth();
        mDecorViewHeight = decorView.getHeight();
        initClickEyeViewData(splashView.getContext());
    }

    public void clearSplashStaticData() {
        mSplashAd = null;
        mSplashShowView = null;
    }

    public TTSplashAd getSplashAd() {
        return mSplashAd;
    }

    public ViewGroup startSplashClickEyeAnimationInTwoActivity(final ViewGroup decorView,
                                                               final ViewGroup splashViewContainer,
                                                               final AnimationCallBack callBack) {
        EALog.high(TAG + "startSplashClickEyeAnimationInTwoActivity");

        if (decorView == null || splashViewContainer == null) {
            EALog.e(TAG + " decorView == null || splashViewContainer == null");
            return null;
        }
        if (mSplashAd == null || mSplashShowView == null) {
            EALog.e(TAG + "mSplashAd == null || mSplashShowView == null");
            return null;
        }
        return startSplashClickEyeAnimation(mSplashShowView, decorView, splashViewContainer, callBack);
    }

    //开屏点睛动画
    public ViewGroup startSplashClickEyeAnimation(final View splash, final ViewGroup decorView,
                                                  final ViewGroup splashViewContainer,
                                                  final AnimationCallBack callBack) {
        if (splash == null || splashViewContainer == null) {
            EALog.e(TAG + "splash == null || splashViewContainer == null");
            return null;
        }
        final int[] splashScreenPos = new int[2];
        splash.getLocationOnScreen(splashScreenPos);
        final Context context = splashViewContainer.getContext();
        int splashViewWidth = splash.getWidth();
        int splashViewHeight = splash.getHeight();
        int animationContainerWidth = decorView.getWidth();
        int animationContainerHeight = decorView.getHeight();

        if (animationContainerWidth == 0) {
            animationContainerWidth = mDecorViewWidth;
        }
        if (animationContainerHeight == 0) {
            animationContainerHeight = mDecorViewHeight;
        }
        float xScaleRatio = (float) mClickEyeViewWidth / splashViewWidth;
        float yScaleRation = (float) mClickEyeViewHeight / splashViewHeight;
        final float animationDistX = mClickEyeViewPos == LEFT ? mClickEyeViewMargin :
                animationContainerWidth - mClickEyeViewMargin - mClickEyeViewWidth;
        final float animationDistY = animationContainerHeight - mClickEyeViewMarginBottom - mClickEyeViewHeight;  //最终位于container的y坐标
        removeFromParent(splash);
        FrameLayout.LayoutParams animationParams = new FrameLayout.LayoutParams(splashViewWidth, splashViewHeight);
        decorView.addView(splash, animationParams);
        final EASplashZoomOutLayout splashViewLayout = new EASplashZoomOutLayout(context,mClickEyeViewMargin);
        splash.setPivotX(0);
        splash.setPivotY(0);
        splash.animate()
                .scaleX(xScaleRatio)
                .scaleY(yScaleRation)
                .x(animationDistX)
                .y(animationDistY)
                .setInterpolator(new OvershootInterpolator(0))
                .setDuration(mClickEyeViewAnimationTime)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (callBack != null) {
                            callBack.animationStart(mClickEyeViewAnimationTime);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        removeFromParent(splash);
                        splash.setScaleX(1);
                        splash.setScaleY(1);
                        splash.setX(0);
                        splash.setY(0);
                        int[] clickEyeContainerScreenPos = new int[2];
                        splashViewContainer.getLocationOnScreen(clickEyeContainerScreenPos);
                        float distX = animationDistX - clickEyeContainerScreenPos[0] + splashScreenPos[0];
                        float distY = animationDistY - clickEyeContainerScreenPos[1] + splashScreenPos[1];

                        splashViewLayout.addView(splash, FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);
                        FrameLayout.LayoutParams clickEyeParams = new FrameLayout.LayoutParams(mClickEyeViewWidth,
                                mClickEyeViewHeight);
                        splashViewContainer.addView(splashViewLayout, clickEyeParams);
                        splashViewLayout.setTranslationX(distX);
                        splashViewLayout.setTranslationY(distY);
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
        return splashViewLayout;
    }

    public static void removeFromParent(View view) {
        if (view != null) {
            ViewParent vp = view.getParent();
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).removeView(view);
            }
        }
    }

    public boolean isSupportSplashClickEye() {
        return mIsSupportSplashClickEye;
    }

    public void setSupportSplashClickEye(boolean isSupportSplashClickEye) {
        this.mIsSupportSplashClickEye = isSupportSplashClickEye;
    }
}

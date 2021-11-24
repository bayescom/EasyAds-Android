package com.easyads.utils;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class EASplashZoomOutLayout extends FrameLayout {
    private float dX, dY;
    private int margin;
    private int maxX;
    private int maxY;

    private float moveAccumulateX, moveAccumulateY;
    private final int touchSlop;//拖动和点击的触发阈值，采用系统的参数，超过该值认为是拖动，低于认为是点击

    public EASplashZoomOutLayout(Context context, int m) {
        super(context);
        //设置悬浮窗的圆角
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(10);
        this.setBackgroundDrawable(gd);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setClipToOutline(true);
        }
        this.margin = m;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) getParent();
                if (parent == null) {
                    return;
                }
                int parentWidth = parent.getWidth();
                int parentHeight = parent.getHeight();
                maxY = parentHeight - EASplashZoomOutLayout.this.getHeight() - margin;
                maxX = parentWidth - EASplashZoomOutLayout.this.getWidth() - margin;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                moveAccumulateX = 0;
                moveAccumulateY = 0;
                break;

            case MotionEvent.ACTION_MOVE:
                float newX = event.getRawX() + dX;
                float newY = event.getRawY() + dY;

                //这里采用累积，防止转一圈回到起点的情况也触发点击
                moveAccumulateX += Math.abs(newX - getX());
                moveAccumulateY += Math.abs(newY - getY());
                //限制浮窗不会超出父布局
                newX = newX < margin ? margin : newX > maxX ? maxX : newX;
                newY = newY < margin ? margin : newY > maxY ? maxY : newY;
                animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(0)
                        .start();
                break;
            case MotionEvent.ACTION_UP:
                //拖动吸附，放开手时自动吸附到左右两边
                float animationX;
                float upX = event.getRawX() + dX;
                if (upX * 2 > maxX) {
                    animationX = maxX;
                } else {
                    animationX = margin;
                }
                animate()
                        .x(animationX)
                        .setDuration(0)
                        .start();
                //如果拖动超过一定距离拦截发向子view的点击事件
                if (moveAccumulateX > touchSlop || moveAccumulateY > touchSlop) {
                    return true;
                }
                //Log.d("fgt","x:"+getX()+" y:"+getY()+ " height:"+parentHeight+" width:"+parentWidth);
            default:
        }
        return super.onInterceptTouchEvent(event);
    }
}

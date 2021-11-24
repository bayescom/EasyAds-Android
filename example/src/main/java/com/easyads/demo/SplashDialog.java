package com.easyads.demo;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SplashDialog extends Dialog {
    LinearLayout logo;
    FrameLayout adContainer;


    public SplashDialog(@NonNull final Activity context) {
        super(context);
        setContentView(R.layout.activity_splash_custom_logo);
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        adContainer = findViewById(R.id.splash_container);

        logo = findViewById(R.id.ll_logo);

        new EasyADController(context).loadSplash("splash_config.json", adContainer, logo, false, new EasyADController.SplashCallBack() {
            @Override
            public void jumpMain() {
                dismiss();
            }
        });
    }
}

package com.easyads.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.easyads.utils.EASplashPlusManager;

public class SplashToMainActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_to_main);

        //如果需要支持穿山甲、优量汇的特殊开屏（v+、点睛）展示模式，这里需要进行调用适配，否则无法生效。
        EASplashPlusManager.startZoom(this);
    }
}

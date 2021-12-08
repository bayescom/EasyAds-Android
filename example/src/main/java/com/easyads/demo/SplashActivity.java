package com.easyads.demo;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.easyads.demo.utils.Constant;


public class SplashActivity extends BaseActivity {
    LinearLayout logo;
    FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏设置
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_splash_custom_logo);
        adContainer = findViewById(R.id.splash_container);

        logo = findViewById(R.id.ll_logo);

        //初始化广告处理封装类
        EasyADController ad = new EasyADController(this);
        String jsonName = "splash_config.json";

        //自定义渠道部分设置，不需要的可以不添加
        ad.cusHuaWei = getIntent().getBooleanExtra(Constant.CUS_HW, false);
        ad.cusXiaoMi = getIntent().getBooleanExtra(Constant.CUS_XM, false);
        if (ad.cusHuaWei || ad.cusXiaoMi) { //使用包含自定义渠道的配置
            jsonName = "splash_cus_config.json";
        }
        /**
         * 加载广告
         */
        ad.loadSplash(jsonName, adContainer, logo, true, new EasyADController.SplashCallBack() {
            @Override
            public void jumpMain() {
                goToMainActivity();
            }
        });
    }


    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        startActivity(new Intent(this, SplashToMainActivity.class));
        this.finish();
    }

}

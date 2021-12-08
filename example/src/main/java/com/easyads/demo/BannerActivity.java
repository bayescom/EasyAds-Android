package com.easyads.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class BannerActivity extends BaseActivity {
    RelativeLayout rl;
    EasyADController ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
        rl = findViewById(R.id.banner_layout);

        //初始化广告处理封装类
        ad = new EasyADController(this);
        //加载banner
        ad.loadBanner("banner_config.json", rl);
    }

    public void loadBanner(View view) {
        if (ad != null) {
            ad.destroy();
            ad.loadBanner("banner_config.json", rl);
        }
    }
}

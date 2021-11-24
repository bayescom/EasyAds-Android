package com.easyads.demo.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.easyads.demo.R;
import com.easyads.demo.SplashActivity;
import com.easyads.demo.utils.Constant;

public class CustomActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
    }

    public void cusHW(View view) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(Constant.CUS_HW,true);
        startActivity(intent);
    }

    public void cusXM(View view) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(Constant.CUS_XM,true);
        startActivity(intent);
    }
}

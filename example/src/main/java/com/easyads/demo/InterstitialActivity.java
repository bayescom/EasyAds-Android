package com.easyads.demo;

import android.os.Bundle;
import android.view.View;

import com.easyads.core.inter.EasyAdInterstitial;

public class InterstitialActivity extends BaseActivity {
    EasyAdInterstitial interstitialAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

    }

    public void loadAd(View view) {
        interstitialAD = new EasyADController(this).initInterstitial("interstitial_config.json");
        interstitialAD.loadOnly();
    }

    public void showAd(View view) {
        if (interstitialAD != null) {
            interstitialAD.show();
        } else {
            EasyADController.logAndToast(this, "需要先调用loadOnly()");
        }
    }

    public void loadAndShowAd(View view) {
        new EasyADController(this).initInterstitial("interstitial_config.json").loadAndShow();
    }
}

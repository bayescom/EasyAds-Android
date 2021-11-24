package com.easyads.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.easyads.core.full.EasyAdFullScreenVideo;

public class FullScreenVideoActivity extends BaseActivity {
    EasyAdFullScreenVideo fullScreenVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_video);
    }


    public void loadFull(View view) {
        fullScreenVideo = new EasyADController(this).initFullVideo("full_config.json");
        fullScreenVideo.loadOnly();

    }

    public void showFull(View view) {
        if (fullScreenVideo != null) {
            fullScreenVideo.show();
        } else {
            EasyADController.logAndToast(this, "需要先调用loadOnly()");
        }
    }

    public void loadAndShowFull(View view) {
        new EasyADController(this).initFullVideo("full_config.json").loadAndShow();
    }
}

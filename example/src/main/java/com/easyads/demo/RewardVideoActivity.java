package com.easyads.demo;

import android.os.Bundle;
import android.view.View;

import com.easyads.core.reward.EasyAdRewardVideo;

public class RewardVideoActivity extends BaseActivity {
    EasyAdRewardVideo rewardVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);

    }

    public void onLoad(View view) {
        rewardVideo = new EasyADController(this).initReward("reward_config.json");
        rewardVideo.loadOnly();

    }

    public void onShow(View view) {
        if (rewardVideo != null) {
            rewardVideo.show();
        } else {
            EasyADController.logAndToast(this, "需要先调用loadOnly()");
        }
    }

    public void loadAndShow(View view) {
        new EasyADController(this).initReward("reward_config.json").loadAndShow();
    }
}

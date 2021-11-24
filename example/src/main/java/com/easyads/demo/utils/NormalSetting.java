package com.easyads.demo.utils;

public class NormalSetting {

    static NormalSetting instance;

    public static NormalSetting getInstance() {
        if (instance == null) {
            instance = new NormalSetting();
        }
        return instance;
    }

    public boolean showLogcat = true;
}

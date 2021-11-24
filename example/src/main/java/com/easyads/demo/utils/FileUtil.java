package com.easyads.demo.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FileUtil {

    //获取存放在assets下面的广告json文件内容
    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

}

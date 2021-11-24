package com.easyads.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class NativeExpressActivity extends BaseActivity {
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_express);
        container = findViewById(R.id.native_express_container);

        new EasyADController(this).loadNativeExpress("native_config.json",container);
    }

    public void loadNEAD(View view) {
        new EasyADController(this).loadNativeExpress("native_config.json",container);
    }
}

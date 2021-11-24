package com.easyads.demo.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.easyads.demo.R;
import com.github.pedrovgs.lynx.LynxView;

public class DialogLogcat extends Dialog {
    LynxView lynxView;

    public DialogLogcat(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_logcat);
        lynxView = findViewById(R.id.lv_log);
        TextView clear = findViewById(R.id.clear);
        TextView close = findViewById(R.id.close);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除之前的记录
                lynxView.clear();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    @Override
    public void show() {
        super.show();

        try {
            //宽度全屏
            Window window = getWindow();
            if (window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                Point point = new Point();
                window.getWindowManager().getDefaultDisplay().getSize(point);
                lp.gravity = Gravity.BOTTOM;
                lp.width = point.x;
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

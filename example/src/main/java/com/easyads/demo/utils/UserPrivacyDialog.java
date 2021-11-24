package com.easyads.demo.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.easyads.demo.MyApplication;
import com.easyads.demo.R;

public class UserPrivacyDialog extends Dialog {

    public BaseCallBack callBack;

    public UserPrivacyDialog(@NonNull final Context context) {
        super(context);
        setContentView(R.layout.dialog_user_privacy);

        TextView cont = findViewById(R.id.tv_dup_content);
        cont.setText("此弹框为模拟APP隐私政策流程弹框，APP开发者在集成SDK时，建议参考demo中初始化相关流程代码，不要过早初始化SDK，当用户'同意'该隐私政策后再执行一系列SDK的初始化或权限申请，以防止合规检测不通过。");

        TextView y = findViewById(R.id.tv_dup_yes);
        TextView n = findViewById(R.id.tv_dup_no);

        y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences(Constant.SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(Constant.SP_AGREE_PRIVACY, true).commit();
                /** 注意！！！：一定要在用户同意APP隐私政策要求后再调用此初始化方法。*/

                if (callBack != null) {
                    callBack.call();
                }
                MyApplication.getInstance().initSDK();
                dismiss();
            }
        });

        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                System.exit(0);
            }
        });

        setCanceledOnTouchOutside(false);
        setCancelable(false);

    }
}

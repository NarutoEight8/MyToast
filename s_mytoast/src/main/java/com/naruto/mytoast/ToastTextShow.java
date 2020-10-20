package com.naruto.mytoast;

import android.app.Activity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastTextShow {
    public static void show(final String info,final Activity globalActivity){
        globalActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast mToast = Toast.makeText(globalActivity, info, Toast.LENGTH_LONG);
                LinearLayout layout = (LinearLayout) mToast.getView();
                TextView tv = (TextView) layout.getChildAt(0);
                tv.setTextSize(25);
                mToast.setGravity(Gravity.CENTER, 0, 0);
                mToast.show();
            }
        });
    }
}

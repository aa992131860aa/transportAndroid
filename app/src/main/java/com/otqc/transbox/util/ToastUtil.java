package com.otqc.transbox.util;

import android.content.Context;
import android.widget.Toast;

import com.otqc.transbox.App;

public final class ToastUtil {
    private static Toast toast;

    private ToastUtil() {
    }

    /**
     * 可以连续弹吐司，不用等上个吐司消失
     */
    public static void showToast(String text) {
        if (toast == null) {

            toast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }
    /**
     * 可以连续弹吐司，不用等上个吐司消失
     */
    public static void showToast(String text, Context pContext) {
        if (toast == null) {

            toast = Toast.makeText(pContext, text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }
}

package org.redsha.transbox.util;

import android.widget.Toast;

import org.redsha.transbox.App;

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

}

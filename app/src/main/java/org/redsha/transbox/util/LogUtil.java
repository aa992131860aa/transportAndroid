package org.redsha.transbox.util;

import android.util.Log;

/**
 * Log统一管理类
 */
public final class LogUtil {
    private static boolean isDebug = true;//开发完毕将isDebug置为false

    private LogUtil() {
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void i(Object object, String msg) {
        if (isDebug) {
            Log.i(object.getClass().getSimpleName(), msg);
        }
    }

    public static void e(Object object, String msg) {
        if (isDebug) {
            Log.e(object.getClass().getSimpleName(), msg);
        }
    }

}

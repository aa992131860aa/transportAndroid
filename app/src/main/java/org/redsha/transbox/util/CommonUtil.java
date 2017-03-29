package org.redsha.transbox.util;

import android.content.Context;
import android.telephony.TelephonyManager;

import org.redsha.transbox.App;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CommonUtil {
    private CommonUtil() {
    }

    public static String getTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = df.format(date);
        return dateStr;
    }

    /*
    * 将时间转换为时间戳
    */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static void runOnUIThread(Runnable runnable) {
        App.getHandler().post(runnable);
    }

    public static String getIMEI() {
        return ((TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n 随机数个数
     */
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }

}

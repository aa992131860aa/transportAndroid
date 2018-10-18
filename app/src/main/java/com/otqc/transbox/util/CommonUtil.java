package com.otqc.transbox.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;

import com.otqc.transbox.App;
import com.otqc.transbox.json.TransferJson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CommonUtil {
    private static String TAG = "CommonUtil";

    private CommonUtil() {
    }

    /**
     * 开启关闭飞行模式
     *
     * @param context
     * @param enabling true开启     false关闭
     */
    public static void openAirplaneModeOn(Context context, boolean enabling) {

        Settings.Global.putInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, enabling ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enabling);
        context.sendBroadcast(intent);

    }

    /**
     * 修改系统时间
     */
    public static void modifySystemTime(long time) {
        SystemClock.setCurrentTimeMillis(time);
    }


    public static String getTime(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = df.format(date);
        return dateStr;
    }

    public static String getTimeMm(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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

    /**
     * 转换时间(MM-dd HH:mm)格式
     *
     * @param s
     */
    /*
     * 将时间戳转换为时间
     */
    public static String dateToMD(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        res = sdf.format(date);
        return res;
    }

    public static void runOnUIThread(Runnable runnable) {
        App.getHandler().post(runnable);
    }

//    public static String getIMEI() {
//
//
//
//        return ((TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//    }

    public static String getUniqueId() {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits
        return m_szDevIDShort;
    }

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     *
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n   随机数个数
     */
    public static int[] randomCommon(int min, int max, int n) {
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }

    public static String getTrueTimeMM() {
        String deviceDateStr = "";
        try {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //设备的当前时间
            deviceDateStr = sdf.format(new Date());
//            long deviceDate = sdf.parse(deviceDateStr).getTime();
//
//            //设备的默认启动时间
//            //+ 10 * 60 * 1000
//            long defaultDate = sdf.parse("2010-01-01 08:00:00").getTime();
//            //如果设备时间已经纠正成功
//
//            if (deviceDate > CONSTS.SERVER_TIME - 10 * 60 * 1000) {
//                return deviceDateStr;
//
//            } else {
//                long nowDateLong = CONSTS.SERVER_TIME + (deviceDate - defaultDate) ;
//                String nowDateStr = sdf.format(new Date(nowDateLong));
//                return nowDateStr;
//
//             }
        } catch (Exception e) {

        }
        return deviceDateStr;
    }
    public static long getTrueTimeLong() {
        long deviceDate = 0;
        try {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //设备的当前时间

             deviceDate = new Date().getTime();

            //设备的默认启动时间
            //+ 10 * 60 * 1000
//            long defaultDate = sdf.parse("2010-01-01 08:00:00").getTime();
//            //如果设备时间已经纠正成功
//
//            if (deviceDate > CONSTS.SERVER_TIME - 10 * 60 * 1000) {
//                return deviceDate;
//
//            } else {
//                long nowDateLong = CONSTS.SERVER_TIME + (deviceDate - defaultDate) ;
//
//                return nowDateLong;
//
//            }
        } catch (Exception e) {
            //return deviceDate;
        }
        return deviceDate;

    }

    public static String getTrueTime() {
        String deviceDateStr = "";
        try {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //设备的当前时间
            deviceDateStr = sdf.format(new Date());
//            long deviceDate = sdf.parse(deviceDateStr).getTime();
//
//            //设备的默认启动时间
//            //+ 10 * 60 * 1000
//            long defaultDate = sdf.parse("2010-01-01 08:00:00").getTime();
//            //如果设备时间已经纠正成功
//
//            if (deviceDate > CONSTS.SERVER_TIME - 10 * 60 * 1000) {
//                return deviceDateStr;
//
//            } else {
//                long nowDateLong = CONSTS.SERVER_TIME + (deviceDate - defaultDate) ;
//                String nowDateStr = sdf.format(new Date(nowDateLong));
//                return nowDateStr;
//
//            }
        } catch (Exception e) {

        }
        return deviceDateStr;
    }

    /**
     * 判断网络情况
     *
     * @param context 上下文
     * @return false 表示没有网络 true 表示有网络
     */
    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void saveNoStart(Context pContext, TransferJson.ObjBean pTransfer) {

        PrefUtils.putBoolean("isSave", true, pContext);
        //器官段号
        //mData.getOrgan().setSegNumber(PrefUtils.getString("segNumber", "", mActivity));
        PrefUtils.putString("segNumber", pTransfer.getOrganSeg(), pContext);

        PrefUtils.putString("modifyOrganSeg", pTransfer.getModifyOrganSeg(), pContext);

        //获取器官时间
        PrefUtils.putString("organAt", pTransfer.getGetTime(), pContext);

        //器官类型
        PrefUtils.putString("organType", pTransfer.getOrgan(), pContext);

        //器官数量

        PrefUtils.putString("organCount", pTransfer.getOrganNum(), pContext);
        //血型

        PrefUtils.putString("bloodType", pTransfer.getBlood(), pContext);
        //血液样本数量

        PrefUtils.putString("bloodSampleCount", pTransfer.getBloodNum(), pContext);
        //组织样本类型

        PrefUtils.putString("organizationSampleType", pTransfer.getSampleOrgan(), pContext);
        PrefUtils.putString("organizationSampleCount", pTransfer.getSampleOrganNum(), pContext);

        PrefUtils.putString("fromCity", pTransfer.getFromCity(), pContext);


        PrefUtils.putString("personName", pTransfer.getTrueName(), pContext);
        PrefUtils.putString("personPhone", pTransfer.getPhone(), pContext);


        //获取组织

        PrefUtils.putString("opoName", pTransfer.getOpoName(), pContext);
        //联系人

        PrefUtils.putString("contactPerson", pTransfer.getOpoContactName(), pContext);

        //联系人电话

        PrefUtils.putString("contactPhone", pTransfer.getOpoContactPhone(), pContext);

        PrefUtils.putString("departmentName", pTransfer.getContactName(), pContext);
        PrefUtils.putString("departmentPhone", pTransfer.getContactPhone(), pContext);


        PrefUtils.putString("tracfficType", pTransfer.getTracfficType(), pContext);

        //航班/车次

        PrefUtils.putString("tracfficNumber", pTransfer.getTracfficNumber(), pContext);

        PrefUtils.putString("openPs1", pTransfer.getOpenPsd(), pContext);

        PrefUtils.putString("openPs2", pTransfer.getOpenPsd(), pContext);

        PrefUtils.putString("phones", pTransfer.getPhones(), pContext);
        //其他信息
//        PrefUtils.getString("transferPersonId", "", pContext);
//        PrefUtils.getString("personDataType", "", pContext);
//        PrefUtils.getString("opoId", "", pContext);
//        PrefUtils.getString("opoDataType", "", pContext);

        //Log.e("transfer", pTransfer.toString() + ",,,,,,,," + pTransfer.getPhones());
    }


}

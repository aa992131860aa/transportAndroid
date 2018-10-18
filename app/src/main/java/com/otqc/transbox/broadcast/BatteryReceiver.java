package com.otqc.transbox.broadcast;

/**
 * Created by 99213 on 2017/7/28.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 9  * 监听获取手机系统剩余电量
 * 10  * Created by Lx on 2016/9/17.
 * 11
 */
public class BatteryReceiver extends BroadcastReceiver {


    public BatteryReceiver() {

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        int current = intent.getExtras().getInt("level");// 获得当前电量
        int total = intent.getExtras().getInt("scale");// 获得总电量
        int percent = current * 100 / total;
    }
}
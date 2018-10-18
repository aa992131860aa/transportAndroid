package com.otqc.transbox.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.amap.api.fence.GeoFenceClient;

import com.otqc.transbox.R;

/**
 * Created by 99213 on 2017/4/30.
 */

public class AutoCreateServer extends Service {
    //定义接收广播的action字符串
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
    private GeoFenceClient mGeoFenceClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 构造一个前台服务
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Notification notification = builder
                .setContentTitle("Transbox")
                .setContentText("后台监控中...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(102, notification);// 开始前台服务
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 设置监控
     * 1:
     */
    @Override
    public void onDestroy() {
        //会清除所有围栏
        // mGeoFenceClient.removeGeoFence();
        super.onDestroy();
    }




}

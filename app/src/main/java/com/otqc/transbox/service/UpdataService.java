package com.otqc.transbox.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.db.TransRecordItemDb;
import com.otqc.transbox.http.HttpHelper;
import com.otqc.transbox.http.HttpObserver;
import com.otqc.transbox.http.request.TransRecordItemRequest;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.util.A;
import com.otqc.transbox.util.Const;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class UpdataService extends Service {
    private static String TAG = "UpdataService";
    private boolean pushthread = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(TAG, "onStartCommand");

        // 构造一个前台服务
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Notification notification = builder
                .setContentTitle("Transbox")
                .setContentText("数据上传中...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(102, notification);// 开始前台服务

        if (intent.getStringExtra("flags").equals("3")) {
            //判断当系统版本大于20，即超过Android5.0时，我们采用线程循环的方式请求。
            //当小于5.0时的系统则采用定时唤醒服务的方式执行循环
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion > 20) {
                getPushThread();
            } else {
                //writeData();
            }
        }


//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
        LogUtil.e("DataService", TAG + " 销毁了..");
        if (A.isReadyUp) {
            LogUtil.e("DataService", TAG + " 重建了..");
            UpdataService.getConnet(this);
        }
    }

    //循环请求的线程
    public void getPushThread() {
        pushthread = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pushthread) {
                    try {
                        Thread.sleep(Const.UPLOAD_TIME);
                        writeData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //启动服务和定时器
    public static void getConnet(Context mContext) {
        try {
            Intent intent = new Intent(mContext, UpdataService.class);
            intent.putExtra("flags", "3");
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion > 20) {
                //一般的启动服务的方式
                mContext.startService(intent);
            } else {
                //定时唤醒服务的启动方式
                PendingIntent pIntent = PendingIntent.getService(mContext, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) mContext
                        .getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(), Const.UPLOAD_TIME, pIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //停止由AlarmManager启动的循环
    public static void stop(Context mContext) {
        Intent intent = new Intent(mContext, UpdataService.class);
        PendingIntent pIntent = PendingIntent.getService(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    private void writeData() {

        String tid = PrefUtils.getString("tid", "", App.getContext());
        final Realm realm = RealmUtil.getInstance().getRealm();
        RealmResults<TransRecordItemDb> query = realm.where(TransRecordItemDb.class)
                .equalTo("transfer_id", tid)
                .equalTo("isUp", 1)
                .findAll();

        if (query.size() > 0) {
            // 拼接数组
            TransRecordItemRequest[] request = new TransRecordItemRequest[query.size()];
            for (int i = 0; i < query.size(); i++) {
                TransRecordItemRequest record = new TransRecordItemRequest();
                if (query.get(i) != null) {
                    record.setTransferRecordid(query.get(i).getTransferRecordid());

                    record.setTemperature(query.get(i).getTemperature());
                    record.setAvgTemperature(query.get(i).getAvgTemperature());
                    record.setPower(query.get(i).getPower());
                    record.setExpendPower(query.get(i).getExpendPower());
                    record.setHumidity(query.get(i).getHumidity());

                    record.setDuration(query.get(i).getDuration());
                    record.setCurrentCity(query.get(i).getCurrentCity());
                    record.setLongitude(query.get(i).getLongitude());
                    record.setLatitude(query.get(i).getLatitude());
                    record.setDistance(query.get(i).getDistance());

                    record.setTransfer_id(query.get(i).getTransfer_id());
                    record.setRecordAt(query.get(i).getRecordAt());

                    record.setType(query.get(i).getType());
                    record.setRemark(query.get(i).getRemark());

                    request[i] = record;
                }
            }
            // 参数
            TransferRecordRequest rd = new TransferRecordRequest();
            rd.setRecords(request);

            new HttpHelper().record(rd).subscribe(new HttpObserver<List<String>>() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onSuccess(List<String> model) {

                    if (model != null && model.size() > 0) {
                        for (int i = 0; i < model.size(); i++) {
                            String recordid = model.get(i);
                            final RealmResults<TransRecordItemDb> modifyResult = realm.where(TransRecordItemDb.class)
                                    .equalTo("transferRecordid", recordid)
                                    .findAll();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    TransRecordItemDb info = modifyResult.get(0);
                                    info.setUp(2);
                                }
                            });
                        }
                        realm.close();
                    }
                }
            });
        }

    }

}

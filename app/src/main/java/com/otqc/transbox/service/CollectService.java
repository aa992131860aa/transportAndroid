package com.otqc.transbox.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.otqc.transbox.service.event.BoxStateEvent;
import com.otqc.transbox.service.event.MainEvent;
import com.otqc.transbox.service.event.MapEvent;
import com.otqc.transbox.service.event.OpenBoxEvent;

import com.otqc.transbox.service.event.BoxStateEvent;
import com.otqc.transbox.service.event.MainEvent;
import com.otqc.transbox.service.event.MapEvent;
import com.otqc.transbox.service.event.OpenBoxEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import com.otqc.transbox.service.event.BoxStateEvent;
import com.otqc.transbox.service.event.MainEvent;
import com.otqc.transbox.service.event.MapEvent;
import com.otqc.transbox.service.event.OpenBoxEvent;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.db.ChartTransRecordItemDb;
import com.otqc.transbox.db.TransRecordItemDb;
import com.otqc.transbox.engine.SerialDataUtils;
import com.otqc.transbox.service.event.BoxStateEvent;
import com.otqc.transbox.service.event.MainEvent;
import com.otqc.transbox.service.event.MapEvent;
import com.otqc.transbox.service.event.OpenBoxEvent;
import com.otqc.transbox.util.A;
import com.otqc.transbox.util.CRC16M;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;
import com.otqc.transbox.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android_serialport_api.SerialPort;
import io.realm.Realm;
import io.realm.RealmResults;

public class CollectService extends Service {
    private static final String TAG = "CollectService";
    private static final String TAGG = "CollectServiceRecord";
    private boolean pushthread = false;
    private MainEvent mMainEvent;
    private NumberFormat mNf;   // 温度保留两位小数
    private NumberFormat mNfHumidity;   // 湿度不保留小数

    private static long mTimer = 3000;  // 设置串口采样频率

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        LogUtil.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.e(TAG, "onStartCommand");

        if (batteryReceiver == null) {
            batteryReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int level = intent.getIntExtra("level", 0);
                    mPower = level + "%";
                    if (mPower.equals("100%")) {
                        mExpendPower = "0%";
                    } else {
                        mExpendPower = (100 - level) + "%";
                    }
                    sendMainEvent(2, mPower);
                }
            };

            registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        // 构造一个前台服务
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Notification notification = builder
                .setContentTitle("Transbox")
                .setContentText("传感器数据获取中...")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(101, notification);// 开始前台服务

        if (mNf == null) {
            mNf = NumberFormat.getNumberInstance();
            mNf.setMaximumFractionDigits(1);
        }
        if (mNfHumidity == null) {
            mNfHumidity = NumberFormat.getNumberInstance();
            mNfHumidity.setMaximumFractionDigits(0);
        }
        if (mMainEvent == null) {
            mMainEvent = new MainEvent();
        }

        OpenSerial();

        if (A.isSerialPort) { //串口打开后，再send温度
            send("30 03 00 01 00 02 91 EA");    // 第一次从串口获取温度
        }

        if (intent.getStringExtra("flags").equals("3")) {
            //判断当系统版本大于20，即超过Android5.0时，我们采用线程循环的方式请求。
            //当小于5.0时的系统则采用定时唤醒服务的方式执行循环
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion > 20) {
                getPushThread();
            } else {
                writeData();    // 读取温湿度
                getCrashData(); // 读取震动信号
            }
        }


//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销电池广播
        unregisterReceiver(batteryReceiver);
        EventBus.getDefault().unregister(this);

        /**
         * 释放串口
         */
        if (mSerialPort != null) {
            mSerialPort.close();
        }
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mOutputStream != null) {
            try {
                mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stopForeground(true);

        LogUtil.e("DataService", TAG + " 销毁了..");
        if (A.isReadyUp) {
            LogUtil.e("DataService", TAG + " 重建了..");
            CollectService.getConnet(this);
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
                        Thread.sleep(mTimer);
                        writeData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 30s 获取一次所有数据
     */
    private String mTemperature;    // 当前温度
    private String mAvgTemperature;    // 当前平均温度        --------
    private String mPower;  // 剩余电量
    private String mExpendPower; // 消耗电量
    private String mHumidity;   // 适度

    private String mDuration;   // 持续时间（当前记录时间 - 开始时间）
    private String mCurrentCity;    // 当前城市
    private String mLongitude;      // 经度
    private String mLatitude;       // 纬度
    private String mDistance;       // 剩余距离             ------

    private String mRecordAt;

    // 标记异常状态
    private int mTempError = 0;  // 温度异常，0，异常 1
    private int mHumidityError = 0;  // 湿度异常，0，异常 2
    private int mCollisionError = 0;  // 碰撞异常，0，异常 4
    private int mOpenBoxError = 0;  // 是否开箱，默认0，异常 8

    private static int currentRecordNum = 0; //当前记录的个数

    private void writeData() {
        if (A.mCollectState == 1) {
            // 位置相关
            mCurrentCity = MapEvent.city;
            mLongitude = MapEvent.lont;
            mLatitude = MapEvent.lati;
            mDistance = MapEvent.Distance;

            // 计算记录时间、持续时间、计算平均温度
            mRecordAt = CommonUtil.getTime(new Date()); // 当条记录时间

            final String tid = PrefUtils.getString("tid", "", App.getContext());
            Realm realm = RealmUtil.getInstance().getRealm();
            RealmResults<TransRecordItemDb> queryResult = realm.where(TransRecordItemDb.class).
                    equalTo("transfer_id", tid).findAll();

            if (queryResult.size() > 0) {
                String firstAtTime = queryResult.get(0).getRecordAt();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date start = null;
                try {
                    start = sdf.parse(firstAtTime);
                    Date end = sdf.parse(mRecordAt);
                    long l = (end.getTime() - start.getTime()) / (1000 * 60);
                    mDuration = l + "分钟";
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                double sum = 0;
                int tempCount = 0;
                for (int i = 0; i < queryResult.size(); i++) {
                    if (!TextUtils.isEmpty(queryResult.get(i).getTemperature())) {
                        sum += Double.parseDouble(queryResult.get(i).getTemperature());
                        ++tempCount;    //值不为空做为size，不取集合长度。
                    }
                }

                // 有效长度大于0，再计算
                if (tempCount > 0) {
                    mAvgTemperature = mNf.format(sum / tempCount);
                }

            } else {
                /**
                 * 第一条数据
                 */
                mDuration = "0分钟";
                mAvgTemperature = mTemperature;
            }


            /**
             * 记录数据
             */

            if (mCollisionError != 0 || mOpenBoxError != 0) {
                LogUtil.e(TAGG, "直接记录()");

                // 直接记录
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        TransRecordItemDb obj = realm.createObject(TransRecordItemDb.class, UUID.randomUUID() + "");
                        obj.setRecordAt(mRecordAt);
                        obj.setTransfer_id(tid);
                        int errCode = mTempError + mHumidityError + mCollisionError + mOpenBoxError;
                        obj.setType(errCode);
                        obj.setRemark("upload");

                        obj.setTemperature(mTemperature);
                        obj.setAvgTemperature(mAvgTemperature);
                        obj.setPower(mPower);
                        obj.setExpendPower(mExpendPower);
                        obj.setHumidity(mHumidity);

                        obj.setDuration(mDuration);
                        obj.setCurrentCity(mCurrentCity);
                        obj.setLongitude(mLongitude);
                        obj.setLatitude(mLatitude);
                        obj.setDistance(mDistance);

                        obj.setUp(1);

                        EventBus.getDefault().post(obj);
                    }
                });
            } else {

                if (currentRecordNum != 9) {
                    LogUtil.e(TAGG, "不是第10次，通知刷新即可");

                    currentRecordNum++;
                    // 不是第10次，通知刷新即可
                    TransRecordItemDb obj = new TransRecordItemDb();
                    obj.setRecordAt(mRecordAt);
                    obj.setTransfer_id(tid);
                    int errCode = mTempError + mHumidityError + mCollisionError + mOpenBoxError;
                    obj.setType(errCode);
                    obj.setRemark("upload");

                    obj.setTemperature(mTemperature);
                    obj.setAvgTemperature(mAvgTemperature);
                    obj.setPower(mPower);
                    obj.setExpendPower(mExpendPower);
                    obj.setHumidity(mHumidity);

                    obj.setDuration(mDuration);
                    obj.setCurrentCity(mCurrentCity);
                    obj.setLongitude(mLongitude);
                    obj.setLatitude(mLatitude);
                    obj.setDistance(mDistance);

                    obj.setUp(1);

                    EventBus.getDefault().post(obj);


                    return;
                }


                LogUtil.e(TAGG, "第10次记录本条数据");

                // 第10次记录本条数据
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        TransRecordItemDb obj = realm.createObject(TransRecordItemDb.class, UUID.randomUUID() + "");
                        obj.setRecordAt(mRecordAt);
                        obj.setTransfer_id(tid);
                        int errCode = mTempError + mHumidityError + mCollisionError + mOpenBoxError;
                        obj.setType(errCode);
                        obj.setRemark("upload");

                        obj.setTemperature(mTemperature);
                        obj.setAvgTemperature(mAvgTemperature);
                        obj.setPower(mPower);
                        obj.setExpendPower(mExpendPower);
                        obj.setHumidity(mHumidity);

                        obj.setDuration(mDuration);
                        obj.setCurrentCity(mCurrentCity);
                        obj.setLongitude(mLongitude);
                        obj.setLatitude(mLatitude);
                        obj.setDistance(mDistance);

                        obj.setUp(1);

                        EventBus.getDefault().post(obj);


                        // chart / map 30秒刷一次，需要的通知和其它时间不同
                        ChartTransRecordItemDb chartData = new ChartTransRecordItemDb();
                        chartData.setTemperature(mTemperature);
                        chartData.setLongitude(mLongitude);
                        chartData.setLatitude(mLatitude);
                        chartData.setTransfer_id(tid);
                        EventBus.getDefault().post(chartData);


                        currentRecordNum = 0;   //初始化记录个数
                    }
                });


            }


            realm.close();
            // 初始化异常状态
            initCrashStatus();
        }

    }

    private void initCrashStatus() {
        mTempError = 0;  // 温度异常，0，异常 1
        mHumidityError = 0;  // 湿度异常，0，异常 2
        mCollisionError = 0;  // 碰撞异常，0，异常 4
        mOpenBoxError = 0;  // 是否开箱，默认0，异常 8
    }

    //启动服务和定时器
    public static void getConnet(Context mContext) {

        try {
            Intent intent = new Intent(mContext, CollectService.class);
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
                        System.currentTimeMillis(), mTimer, pIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //停止由AlarmManager启动的循环
    public static void stop(Context mContext) {
        Intent intent = new Intent(mContext, CollectService.class);
        PendingIntent pIntent = PendingIntent.getService(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    //--------------------------------- serial Port ---------------------------------
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String sPort = "/dev/ttyMT1";
    private int iBaudRate = 9600;
    private String receiveString;

    /**
     * 打开串口
     */
    private void OpenSerial() {
        //获取串口实例
        try {
            if (mSerialPort == null) {
                mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
            }
            if (mOutputStream == null) {
                mOutputStream = mSerialPort.getOutputStream();
            }
            if (mInputStream == null) {
                mInputStream = mSerialPort.getInputStream();
            }
            if (mSerialPort != null) {
                A.isSerialPort = true;  // 串口是否打开
                LogUtil.e(TAG, "串口已打开..");
            }
            if (mReadThread == null) {
                mReadThread = new ReadThread();
                mReadThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            ToastUtil.showToast("串口打开失败");
        }

    }

    /**
     * 读串口线程
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (mInputStream != null) {
                    byte[] buffer = new byte[1024];
                    int size = 0;
                    try {
                        size = mInputStream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (size > 0) {
                        byte[] buffer2 = new byte[size];
                        for (int i = 0; i < size; i++) {
                            buffer2[i] = buffer[i];
                        }
                        receiveString = SerialDataUtils.ByteArrToHex(buffer2).trim();
                        processData(receiveString.replace(" ", ""));

                        boolean isPass = CRC16M.checkBuf(buffer2);
                        if (!isPass) {
                            LogUtil.e("serialError", "校验失败：" + receiveString.replace(" ", ""));
                            send("30 03 00 01 00 02 91 EA");
                        }

                    }
                    try {
                        //延时50ms
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
    }

    /**
     * 发串口数据
     */
    public void send(final String string) {
        try {
            //去掉空格
            String s = string;
            s = s.replace(" ", "");
            byte[] bytes = SerialDataUtils.HexToByteArr(s);

            LogUtil.e("serial", "发送串口数据：" + s);

            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理 串口 返回的数据
     */
    private void processData(String s) {
        if (!TextUtils.isEmpty(s)) {
            /**
             * 读取温度、湿度
             */
            if (s.length() == 18 && s.startsWith("300304")) {
                mTemperature = mNf.format((SerialDataUtils.HexToInt(s.substring(6, 10))) * 0.01);
                if (mTemperature.equals("-0.00") || mTemperature.equals("0.00")) {
                    mTemperature = "0";
                }
                mHumidity = mNfHumidity.format((SerialDataUtils.HexToInt(s.substring(10, 14))) * 0.01);
                if (mHumidity.equals("-0")) {
                    mHumidity = "0";
                }
                if (!TextUtils.isEmpty(mTemperature)) {
                    sendMainEvent(0, "正常"); // 之前是打开串口算正常，改为温度获取到算正常

                    double temp = Double.parseDouble(mTemperature);
                    if (temp > 4 || temp < 1) { // 1-4度（含）为正常值
                        mTempError = 1;
                    }
                }
                if (!TextUtils.isEmpty(mHumidity)) {
                    double hum = Double.parseDouble(mHumidity);
                    if (hum > 95) { // <=95%正常
                        mHumidityError = 2;
                    }
                }

                sendMainEvent(1, mTemperature + "℃");

                LogUtil.e("serial", "温湿度：" + mTemperature + " / " + mHumidity);
            }

            /**
             * 读取震动
             */
            if (s.length() == 14 && s.startsWith("300302")) {

                String crash = s.substring(6, 10);
                if (crash.equals("0001")) {
                    mCollisionError = 4;
                }

                LogUtil.e("serial", "震动：" + crash);
            }

            /**
             * 读取开箱指令
             */
            if (s.length() == 16 && s.startsWith("30100005")) {

                String open = s.substring(8, 12);
                if (open.equals("0001")) {
                    switch (isOrderState) {
                        case 1: // 调开指令 就是开，关则是关
                            mOpenBoxError = 8;
                            BoxStateEvent boxStateEvent = new BoxStateEvent();
                            boxStateEvent.setState(true);
                            boxStateEvent.setShowDlg(true);
                            EventBus.getDefault().post(boxStateEvent);

                            LogUtil.e("serial", "调用 开箱，串口成功返回");
                            break;
                        case 2:
                            mOpenBoxError = 0;
//                            ToastUtil.showToast("门锁已关闭!");
                            BoxStateEvent bse = new BoxStateEvent();
                            bse.setState(false);
                            bse.setShowDlg(false);
                            EventBus.getDefault().post(bse);

                            LogUtil.e("serial", "调用 关箱，串口成功返回");
                            break;
                    }
                }
            }
        }

    }

    /**
     * 获取 电量
     */
    private BroadcastReceiver batteryReceiver;

    /**
     * 通知MainActivity界面更新
     */
    private void sendMainEvent(int i, String s) {
        switch (i) {
            case 0:
                mMainEvent.setSerialStatus(s);
                EventBus.getDefault().post(mMainEvent);
                break;
            case 1:
                mMainEvent.setTemperature(s);
                EventBus.getDefault().post(mMainEvent);
                break;
            case 2:
                mMainEvent.setPower(s);
                EventBus.getDefault().post(mMainEvent);
                break;
        }
    }


    private int isOrderState;   // 标注是 开 还是 关指令

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OpenBoxEvent event) {


    }

    /**
     * 单独获取震动信号
     */
    private void getCrashData() {
        send("30 03 00 07 00 01 31 EA");
    }

}













package com.otqc.transbox.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.google.gson.Gson;
import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayActivity;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDbNew3;
import com.otqc.transbox.engine.SerialDataUtils;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.Datas;
import com.otqc.transbox.json.DepartmentsJson;
import com.otqc.transbox.json.HospitalJson;
import com.otqc.transbox.json.LatiLongJson;
import com.otqc.transbox.json.OpoInfoContact;
import com.otqc.transbox.json.OpoInfoJson;
import com.otqc.transbox.json.PhotoJson;
import com.otqc.transbox.json.RecordStringJson;
import com.otqc.transbox.json.RepeatJson;
import com.otqc.transbox.json.TransferJson;
import com.otqc.transbox.service.event.MainEvent;
import com.otqc.transbox.test.DeviceManagerBC;
import com.otqc.transbox.util.A;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.LocationUtils;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SerialUtil;
import com.otqc.transbox.util.ToastUtil;

import org.litepal.crud.DataSupport;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android_serialport_api.SerialPort;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommServer extends Service {
    private static final String TAG = "CommServer";

    private boolean pushThread = true;
    private MainEvent mMainEvent;
    private NumberFormat mNf;   // 温度保留两位小数
    private NumberFormat mNfHumidity;   // 湿度不保留小数

    private static long mTimer = 3000;  // 设置串口采样频

    public static SerialPort mSerialPort;
    public static OutputStream mOutputStream;
    public static InputStream mInputStream;
    private ReadThreadComm mReadThreadComm;
    private HandleThreadComm mHandlerThreadComm;
    private SendThreadComm mSendThreadComm;

    private String sPort = "/dev/ttyMT1";
    private int iBaudRate = 9600;
    private String receiveString;
    private String receiveTotal = "";

    //电量
    //BroadcastReceiver mBatteryReceiver;
    private String mPower;
    private String mPowerNo = "80";
    private int mPowerNum = 80;
    private String mExpendPower;
    //    private String mExpendPowerNo;
    private static Context mContext;
    //定位
    public static AMapLocationClientOption mLocationOption = null;
    public static AMapLocationClient mlocationClient = null;
    private static LocationManager mLocationManager;
    private static final int CAMERA_CODE_PERMISSION = 0x1112;
    private boolean temperatureException = false;
    private boolean openException = false;
    private boolean collisionException = false;
    //数据库
    Realm realm = null;

    //发生温度异常的时间
    private long lastTime;
    private double mVoltage;

    //private boolean isSendSms20 = false;

    //获取最低的温度，判断是否开始装箱
    private double mTemperature = 20.0;
    private double mDistance;
    private String endLocation;
    private String mDepartmentName;
    private String mDepartmentPhone;
    private String mOpoName;
    private String mOpoPhone;
    private String mHospitalName;
    private String mDeviceId;
    private double mAutoDistance;

    double temp_a = 3.18019913764E-001;
    double temp_b = 4.08167586043E-001;
    double temp_c = -1.07538760216E-001;
    double temp_d = 1.68099068713E-002;
    double temp_e = -4.97781693029E-004;

    //串口接受的值
    private String mPowerReceive = "";
    private String mCollisionReceive = "";
    private String mTemperatureReceive = "";
    private String mHumidityReceive = "";
    private String mOpenReceive = "";

    private double temperature = CONSTS.INIT_SERIAL;
    private double tureTemperture = CONSTS.INIT_SERIAL;
    private double humidity = CONSTS.INIT_SERIAL;
    private double collision = CONSTS.INIT_SERIAL;

    //记录1碰撞  0不碰撞
    private double collisionRecord = CONSTS.INIT_SERIAL;
    //记录1开箱  0开箱
    private double openRecord = 0;
    private double mItemDistance;
    private String mItemCity;
    private boolean isPlaneMode = false;
    private boolean isStopTransfer = true;
    private boolean isTransferStart = true;
    private int isTransferIndex = -1;

    /**
     * 把转运详细的记录保存在内存里,上传数据的时候清理一次
     */
    private ArrayList<TransRecord> mTransRecordList = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0) {
                long time = (long) msg.obj;
                ToastUtil.showToast("自动转运超过10度,即将停止,倒计时" + (CONSTS.END_TIME / 4 - time) / 60 + "分.");
            } else if (msg.what == 1) {
                ToastUtil.showToast("通知手机界面刷新" + CONSTS.UPLOAD_NUM);
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

    }

    private double makeTemperature(double pTemperature) {
        double tempCompensate = 0;
        if ((pTemperature < 16) && (pTemperature > -4)) {
            tempCompensate = temp_a + temp_b * Math.pow(pTemperature, 1) + temp_c * Math.pow(pTemperature, 2) + temp_d * Math.pow(pTemperature, 3)
                    + temp_e * Math.pow(pTemperature, 4);
        } else {
            tempCompensate = pTemperature;
        }
        return tempCompensate;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.root_view);
        // 构造一个前台服务
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Notification notification = builder
//                .setContentTitle("Transbox")
//                .setContentText("传感器数据获取中..")
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(101, notification);// 开始前台服务
        //remoteViews.setOnClickPendingIntent(R.id.ll_root,new PendingIntent(this,""));


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


        if (mSerialPort == null) {
            try {
                mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
            } catch (IOException e) {
                e.printStackTrace();

            }

        }

        if (mInputStream == null) {
            mInputStream = mSerialPort.getInputStream();

        }

        if (mOutputStream == null) {
            mOutputStream = mSerialPort.getOutputStream();

        }


        if (mSerialPort != null) {
            A.isSerialPort = true;  // 串口是否打开

        }


        if (mReadThreadComm == null) {
            mReadThreadComm = new ReadThreadComm();
            mReadThreadComm.start();

            mHandlerThreadComm = new HandleThreadComm();
            mHandlerThreadComm.start();

            mSendThreadComm = new SendThreadComm();
            mSendThreadComm.start();
        }
        mHospitalName = PrefUtils.getString("hospitalName", "", mContext);
        mDeviceId = PrefUtils.getString("deviceId", "", mContext);


        return START_STICKY;
    }

    /**
     * 发送串口消息
     */
    class SendThreadComm extends Thread {

        @Override
        public void run() {
            super.run();
            while (true) {

                try {


                    SerialUtil.power();
                    //开启一键转运
                    boolean isTransfer = PrefUtils.getBoolean("isTransfer", false, mContext);
                    if (isTransfer) {
                        isTransferIndex = 0;
                        SerialUtil.transferIsStart(isTransfer);
                    } else if (isTransferIndex != -1) {
                        isTransferIndex = -1;
                        SerialUtil.transferIsStart(isTransfer);
                    }
                    String pwd = PrefUtils.getString("pwd", "", mContext);
                    if (!"".equals(pwd)) {
                        boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mContext);
                        boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mContext);
                        //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, true);
                    }
                    boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mContext);
                    boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mContext);

                    SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                    Log.e("MainActivity", "isTemperature:" + isTemperature + ",isPlaneShow:" + isPlaneShow);
                } catch (Exception e) {
                    Log.e("MainActivity", "Exception:" + e.getMessage());
                }


                //开锁
                if (!"".equals(mOpenReceive)) {

                }
                //电量
                if (!"".equals(mPowerReceive)) {
                    String[] receiveTotals = mPowerReceive.trim().split(" ");
                    //判断正负 00为正   01为负

                    mPowerNum = SerialDataUtils.HexToInt(receiveTotals[6]);
                    mVoltage = (SerialDataUtils.HexToInt(receiveTotals[4] + receiveTotals[5]) / 100.0);
                    if (mPowerNum >= 100) {
                        mPowerNum = 100;
                    }
                    CONSTS.POWER = mPowerNum;
                    mPower = mPowerNum + "%";
                    mExpendPower = (100 - mPowerNum) + "";
                    mPowerNo = mPowerNum + "";
                }

                //碰撞
                if (!"".equals(mCollisionReceive)) {

                    String[] receiveTotals = mCollisionReceive.trim().split(" ");
                    collision = 0.0;

                    //判断正负 00为正   01为负
                    if ("00".equals(receiveTotals[4])) {
                        collision = SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;

                    } else if ("01".equals(receiveTotals[4])) {
                        collision = SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;
                    }
                    //Log.e("resultReceive", "collision" + (collision + CONSTS.COLLISION) + "," + getCollision());


                }

                //温度
                if (!"".equals(mTemperatureReceive)) {


                    String[] receiveTotals = mTemperatureReceive.trim().split(" ");
                    temperature = 0.0;
                    //判断正负 00为正   01为负
                    if ("00".equals(receiveTotals[4])) {
                        temperature = SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;

                    } else if ("01".equals(receiveTotals[4])) {
                        temperature = -SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;
                    }

                    tureTemperture = temperature;
                    DecimalFormat df = new DecimalFormat("######0.0");

                    temperature = Double.parseDouble(df.format(makeTemperature(temperature)));

                    if (temperature > 40) {
                        temperature = 0.11;
                    }
                    if (temperature < -10) {
                        temperature = 0.11;
                    }


                }

                //湿度
                if (!"".equals(mHumidityReceive)) {
                    String[] receiveTotals = mHumidityReceive.trim().split(" ");
                    humidity = 0.0;
                    //判断正负 00为正   01为负
                    if ("00".equals(receiveTotals[4])) {
                        humidity = SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;

                    } else if ("01".equals(receiveTotals[4])) {
                        humidity = -SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;
                    }

                }

                Intent intent;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Log.e(TAG, "11time:" + (CONSTS.IS_START) + "," + temperature);
                //判断是否自动开始 //&& CONSTS.SCREEN_LIGHT
                if (temperature != CONSTS.INIT_SERIAL) {
                    if (temperature >= 0 && temperature <= 10) {
                        //sendMainEvent(0, "正常");
                        intent = new Intent(CONSTS.MAIN_ACTION);
                        intent.putExtra("temperature", temperature + "");
                        intent.putExtra("status", "正常");
                        intent.putExtra("power", mPower);


                    } else {

                        //发出警报
                        //sendMainEvent(0, "温度异常");
                        intent = new Intent(CONSTS.MAIN_ACTION);
                        intent.putExtra("temperature", temperature + "");
                        intent.putExtra("status", "温度异常");
                        intent.putExtra("power", mPower);
                        try {
                            //在转运中,温度异常
                            if (CONSTS.IS_START == 1) {
                                if (lastTime == 0) {

                                    lastTime = sdf.parse(CommonUtil.getTrueTime()).getTime();

                                }
                                long nowDate = sdf.parse(CommonUtil.getTrueTime()).getTime();
                                //Log.e(TAG, "1time:" + (nowDate - lastTime) + "," + 60 * 1000 + "," + CONSTS.EXCEPTION_TIME);

                                if (nowDate - lastTime >= CONSTS.EXCEPTION_TIME) {

                                    temperatureException = true;
                                    CONSTS.UPLOAD_NUM = CONSTS.UPLOAD_NUM_VALUE;
                                    lastTime = 0;
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    mContext.sendBroadcast(intent);

                }

                Log.e(TAG, "commserver:" + temperature + "," + CONSTS.SCREEN_LIGHT + "," + CONSTS.IS_START);
                if (CONSTS.IS_START == 1) {

                    double duration = 0;
                    if (MainActivity.mObjBean != null) {
                        SimpleDateFormat sdfSort = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {

                            duration = (new Date().getTime() - sdfSort.parse(MainActivity.mObjBean.getGetTime()).getTime()) / 1000;
                        } catch (ParseException e) {
                            //Log.e(TAG, "error5:" + e.getMessage());
                            e.printStackTrace();
                        }
                    }


                    if (temperature != CONSTS.INIT_SERIAL && CONSTS.SCREEN_LIGHT) {
                        intent = new Intent(CONSTS.ON_WAY_TRANS);


                        intent.putExtra("temperature", temperature + "");
                        intent.putExtra("humidity", humidity + "");
                        intent.putExtra("collision", (int) collision);

                        intent.putExtra("power", mPower);
                        intent.putExtra("expendPower", mExpendPower);
                        intent.putExtra("duration", ((int) duration / 60) + "");
                        intent.putExtra("distance", mItemDistance + "");
                        intent.putExtra("city", mItemCity);


                        mContext.sendBroadcast(intent);
                    }
                }
                try {
                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class HandleThreadComm extends Thread {

        @Override
        public void run() {
            super.run();
            while (true) {


                //温度
                if (!"".equals(mTemperatureReceive)) {


                    String[] receiveTotals = mTemperatureReceive.trim().split(" ");
                    temperature = 0.0;
                    //判断正负 00为正   01为负
                    if ("00".equals(receiveTotals[4])) {
                        temperature = SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;

                    } else if ("01".equals(receiveTotals[4])) {
                        temperature = -SerialDataUtils.HexToInt(receiveTotals[5] + receiveTotals[6]) / 100.0;
                    }

                    tureTemperture = temperature;
                    DecimalFormat df = new DecimalFormat("######0.0");

                    temperature = Double.parseDouble(df.format(makeTemperature(temperature)));

                    if (temperature > 40) {
                        temperature = 0.11;
                    }
                    if (temperature < -10) {
                        temperature = 0.11;
                    }
                    /**
                     * 位置是否变化
                     * 位置是否在终点
                     * 同一位置是否已经超过2小时
                     * 自动停止转运
                     */
//                    Boolean isClose = PrefUtils.getBoolean("isClose", true, mContext);
//                    if (temperature > 10 && CONSTS.SERVER_TIME != 0 && CONSTS.IS_START == 1 && isClose) {
//
//
//                        List<TransRecord> query = DataSupport.where("transfer_id = ? order by id desc limit 30", CONSTS.TRANSFER_ID).find(TransRecord.class);
//                        //Log.e("collision time:", "query:" + query.size() + "," +LocationUtils.getMoveDistanceRecord(query));
//                        if ("".equals(CONSTS.END_FLAG_OVER) || LocationUtils.getMoveDistanceRecord(query) > 0) {
//                            CONSTS.END_FLAG_OVER = CommonUtil.getTrueTime();
//                        }
//
//                        String nowTime = CommonUtil.getTrueTime();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        try {
//                            long time = sdf.parse(nowTime).getTime() / 1000 - sdf.parse(CONSTS.END_FLAG_OVER).getTime() / 1000;
//                            Log.e("collision time:", "结束" + (time));
//                            if (time < 0) {
//                                CONSTS.END_FLAG_OVER = CommonUtil.getTrueTime();
//                            }
//                            Message message = new Message();
//                            message.what = 0;
//                            message.obj = time;
//                            mHandler.sendMessage(message);
//                            if (time > (CONSTS.END_TIME / 4) && time < 100000) {
//                                //ToastUtil.showToast("即将自动停止" + time);
//                                if (MainActivity.mObjBean != null) {
//                                    stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
//                                    sendListTransferSms("18398850872", MainActivity.mObjBean.getBoxNo() + "的温度已经高于10度,自动转运停止");
//                                }
//
//
//                            }
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//
//                        }
//
//                    } else {
//                        CONSTS.END_FLAG_OVER = "";
//                    }


                }


                //密码是否开锁
                if (MainActivity.mObjBean != null) {
                    boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mContext);
                    boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mContext);

                    if (!"".equals(MainActivity.mObjBean.getOpenPsd()) && MainActivity.mObjBean.getOpenPsd() != null) {


                        //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, true);
                    } else {
                        //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                        PrefUtils.putString("pwd", "", mContext);
                    }
                }

                /**
                 * 处理收集的数据
                 */
                if (CONSTS.SERVER_TIME != 0L) {

                    if (temperature != CONSTS.INIT_SERIAL && collision != CONSTS.INIT_SERIAL && humidity != CONSTS.INIT_SERIAL) {
                        if (CONSTS.IS_START == 1) {

                            //碰撞异常
                            if (collision + CONSTS.COLLISION > getCollision()) {
                                collisionException = true;
                                CONSTS.UPLOAD_NUM = CONSTS.UPLOAD_NUM_VALUE;
                                mContext.sendBroadcast(new Intent(CONSTS.EXCEPTION));
                                collisionRecord = 1;

                            } else {
                                collisionException = false;
                                collisionRecord = 0;
                            }

                            int openDB = getOpen();
                            //Log.e("resultReceive", "open" + CONSTS.OPEN + "," + getOpen());

                            //开箱异常
                            if (CONSTS.IS_OPEN) {

                                openException = true;
                                CONSTS.IS_OPEN = false;
                                CONSTS.UPLOAD_NUM = CONSTS.UPLOAD_NUM_VALUE;
                                mContext.sendBroadcast(new Intent(CONSTS.EXCEPTION));
                                openRecord = 1;
                            } else {
                                openException = false;
                                openRecord = 0;
                            }


                            dealData(tureTemperture, collision, temperature, humidity, "start");

                        } else {
                            dealData(tureTemperture, collision, temperature, humidity, "noStart");
                        }
                    }
                }


                if (CONSTS.IS_START == 1) {


                    if ((temperatureException && CONSTS.IS_START == 1) || (openException && CONSTS.IS_START == 1) || (collisionException && CONSTS.IS_START == 1) || CONSTS.COUNT == 0 || CONSTS.POWER == 15) {
                        //服务器发送异常(开箱,碰撞,温度)
                        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);

                        params.addBodyParameter("action", "recordException");
                        params.addBodyParameter("temperatureException", temperatureException + "");
                        params.addBodyParameter("temperature", temperature + "");
                        params.addBodyParameter("openException", openException + "");
                        params.addBodyParameter("open", getOpen() + "");
                        params.addBodyParameter("collisionException", collisionException + "");
                        params.addBodyParameter("collision", (getCollision() + ""));
                        params.addBodyParameter("transferId", CONSTS.TRANSFER_ID);
                        params.addBodyParameter("organSeg", MainActivity.mObjBean.getOrganSeg());
                        params.addBodyParameter("modifyOrganSeg", MainActivity.mObjBean.getModifyOrganSeg());

                        if (CONSTS.COUNT == 0) {
                            params.addBodyParameter("powerException", "true");
                            params.addBodyParameter("power", mPowerNum + "");
                            params.addBodyParameter("powerType", "start");

                            CONSTS.COUNT = -1;
                        }
                        if (mPowerNum == 15 && CONSTS.COUNT != 15) {
                            CONSTS.COUNT = 15;
                            params.addBodyParameter("powerException", "true");
                            params.addBodyParameter("power", mPowerNum + "");
                            params.addBodyParameter("powerType", "exception");
                        }

                        x.http().get(params, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                temperatureException = false;
                                collisionException = false;
                                openException = false;
                                //lastTime = 0;
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {

                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });

                    }
                }

                try {
                    Thread.sleep(CONSTS.SERIAL_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class ReadThreadComm extends Thread {


        @Override
        public void run() {
            super.run();

            while (pushThread) {
                //一秒执行一次

                try {
                    byte[] buffer = new byte[1024];
                    int size = 0;


                    size = mInputStream.read(buffer);


                    if (size > 0) {
                        byte[] buffer2 = new byte[size];
                        for (int i = 0; i < size; i++) {
                            buffer2[i] = buffer[i];
                        }

                        receiveString = SerialDataUtils.ByteArrToHex(buffer2).trim();

                        if (receiveString.length() == 29) {
                            receiveTotal += receiveString;
                        } else {
                            receiveTotal += " " + receiveString;
                        }

                        if (receiveTotal.trim().contains("7B 30 11 06 00 00 64 F8 8B 7D")) {
                            Log.e("resultReceive:", "开锁:" + receiveTotal);
                        } else if (receiveTotal.trim().contains("7B 30 30 03 00 00 64 44 40 7D")) {
                            Log.e("resultReceive:", "开启GPS状态:" + receiveTotal);
                        } else if (receiveTotal.trim().contains("7B 30 30 03 00 00 00 45 AB 7D")) {
                            Log.e("resultReceive:", "关闭GPS状态:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 11 0D") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "清空碰撞次数:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 11 0F") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "加密:" + receiveTotal);
                        } else if (receiveTotal.trim().contains("7B 30 30 05 00 00 00 45 23 7D")
                                || receiveTotal.trim().contains("30 30 05 00 00 00 45 23")) {
                            Log.e("resultReceive:", "关闭屏幕:" + receiveTotal);
                        } else if (receiveTotal.trim().contains("7B 30 30 05 00 00 64 44 C8 7D") || receiveTotal.trim().contains("30 30 05 00 00 64 44 C8")) {
                            Log.e("resultReceive:", "打开屏幕:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 21 0E") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "发送电量信息:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 21 08") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "碰撞:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 21 01") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "温度:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 21 02") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "湿度:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 11 11") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "一键转运设置:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 30 10 00 00 00") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "停止一键转运:" + receiveTotal);
                        } else if (receiveTotal.trim().startsWith("7B 30 30 10 00 00 64") && receiveTotal.trim().length() == 29) {
                            Log.e("resultReceive:", "开启一键转运:" + receiveTotal);


                        } else {
                            Log.e("resultReceive:", "未知的东西出现了:" + receiveTotal);
                        }

                        //接受一键转运设置 receiveTotal.trim().startsWith("7B 30 11 11") && receiveTotal.trim().length() == 29 ||
                        if (receiveTotal.trim().contains("7B 30 11 11 00 00")) {
                            CONSTS.IS_TRANSFER = false;
                            //Log.e("resultReceive:", "一键转运设置成功:" + receiveTotal);
                        }

                        //接受一键转运开始 receiveTotal.trim().contains("7B 30 30 10 00 00 64")
                        if (receiveTotal.trim().contains("7B 30 30 10 00 00 64")) {
                            //Log.e("resultReceive:", "开启一键转运:" + receiveTotal);


                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                            int temp = 0;
                            while (sdf.format(new Date()).equals("2010")) {
                                temp++;
                                if (temp > 100) {
                                    break;
                                }
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

//                            Intent i = new Intent(mContext, OnWayActivity.class);
//                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(i);

                            Intent i = new Intent(CONSTS.MAIN);
                            i.putExtra("startOnWay", "startOnWay");
                            mContext.sendBroadcast(i);


                            //获取了网络
                            if (isTransferStart) {

                                isTransferStart = false;
                                loadHospitalAddress();

                            }


                        }
                        //接受一键转运停止
                        if (receiveTotal.trim().contains("7B 30 30 10 00 00 00") && receiveTotal.trim().length() == 29) {
                            //Log.e("resultReceive:", "停止一键转运:" + receiveTotal);

                            isTransferStart = true;
                            if (MainActivity.mObjBean != null) {
                                Log.e("resultReceive:", "stop come in:");
                                stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
                            } else {
                                Log.e("resultReceive:", "stop come out:");
                                SerialUtil.transfer(false);

//                                Intent intent = new Intent(mContext, MainActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                mContext.startActivity(intent);
                                Intent intent = new Intent(CONSTS.ON_WAY_ACTION);
                                intent.putExtra("stopTransfer", "stopTransfer");
                                mContext.sendBroadcast(intent);

                            }
                            SerialUtil.transferSite(false);
                        }

                        //开锁
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().contains("30 11 06 00 00 64")) {

                            CONSTS.TRANSFER_OPEN = true;


                            if (CONSTS.IS_START == 1) {
                                Intent intent = new Intent(CONSTS.ON_WAY_ACTION);
                                intent.putExtra("open", "open");
                                sendBroadcast(intent);
                            } else {
                                Intent intent = new Intent(CONSTS.MAIN_ACTION);
                                intent.putExtra("open", "open");
                                sendBroadcast(intent);
                            }

                            if (isPlaneMode) {
                                SerialUtil.closeScreen();
                                onScreenOff();
                                acquireWakeLock();
                            }

                        }
                        //开启GPS状态
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().contains("7B 30 30 03 00 00 64 44 40 7D")) {
                            CONSTS.TRANSFER_OPEN = true;
                            SerialUtil.openGPS();
                            isPlaneMode = true;
                        }

                        //关闭GPS状态
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().contains("7B 30 30 03 00 00 00 45 AB 7D")) {
                            CONSTS.TRANSFER_OPEN = true;
                            SerialUtil.closeGPS();
                            isPlaneMode = false;

                        }

                        //清空碰撞次数
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().startsWith("7B 30 11 0D") && receiveTotal.trim().length() == 29) {
                            CONSTS.TRANSFER_OPEN = true;
                        }

                        //加密
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().startsWith("7B 30 11 0F") && receiveTotal.trim().length() == 29) {
                            CONSTS.TRANSFER_OPEN = true;
                        }

                        //关闭屏幕
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().contains("30 30 05 00 00 00 45 23") || receiveTotal.trim().contains("7B 30 30 05 00 00 00 45 23 7D")) {

                            if (CONSTS.SERVER_TIME != 0) {
                                onScreenOff();
                                acquireWakeLock();
                            }


                        }

                        //打开屏幕
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().contains("30 30 05 00 00 64 44 C8") || receiveTotal.trim().contains("7B 30 30 05 00 00 64 44 C8 7D")) {

                            SerialUtil.openScreen();

                            Intent i = new Intent(mContext, ScreenService.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startService(i);
                            releaseWakeLock();

                        }


                        //发送电量信息
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().startsWith("7B 30 21 0E") && receiveTotal.trim().length() == 29) {

                            CONSTS.TRANSFER_OPEN = true;
                            mPowerReceive = receiveTotal;
                            SerialUtil.collision();

                        }


                        //获取碰撞次数
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().startsWith("7B 30 21 08") && receiveTotal.trim().length() == 29) {

                            CONSTS.TRANSFER_OPEN = true;
                            mCollisionReceive = receiveTotal;
                            SerialUtil.temperature();

                        }


                        //获取温度
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().startsWith("7B 30 21 01") && receiveTotal.trim().length() == 29) {

                            CONSTS.TRANSFER_OPEN = true;
                            mTemperatureReceive = receiveTotal;
                            SerialUtil.humidity();

                        }


                        //获取湿度
                        // receiveTotal = SerialUtil.substringSerial(receiveTotal);
                        if (receiveTotal.trim().startsWith("7B 30 21 02") && receiveTotal.trim().length() == 29) {

                            CONSTS.TRANSFER_OPEN = true;
                            mHumidityReceive = receiveTotal;

                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                if (receiveTotal.length() >= 29) {
                    receiveTotal = "";
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    /**
     * 处理收集到的数据
     */
    private void dealData(final double tureTemperature, final double collision, double temperature, final double humidity, final String pType) {

        double longitude = CONSTS.LONGITUDE;
        double latitude = CONSTS.LATITUDE;
        String city = CONSTS.CITY;

//        PrefUtils.putString("collision", collision + "", mContext);
//        PrefUtils.putString("temperature", temperature + "", mContext);
//        PrefUtils.putString("humidity", humidity + "", mContext);
//        PrefUtils.putString("power", mPowerNum + "", mContext);

        CONSTS.UPLOAD_NUM++;
        CONSTS.INSERT_NUM++;

        TransRecord itemDb = new TransRecord();
        TransRecordItemDbNew3 itemDbNew3 = new TransRecordItemDbNew3();
        itemDb.setUp(1); //未上传
        itemDbNew3.setUp(1); //未上传
        itemDb.setNum(CONSTS.INSERT_NUM);
        itemDbNew3.setNum(CONSTS.INSERT_NUM);

        itemDb.setTemperature(temperature + "");
        itemDbNew3.setTemperature(temperature + "");

        itemDb.setHumidity(humidity + "");
        itemDbNew3.setHumidity(humidity + "");

        itemDb.setCollision(collisionRecord);
        itemDbNew3.setCollision(collisionRecord);
        collisionRecord = 0;


        itemDb.setLongitude(longitude + "");
        itemDbNew3.setLongitude(longitude + "");

        itemDb.setLatitude(latitude + "");
        itemDbNew3.setLatitude(latitude + "");

        itemDb.setCurrentCity(city);
        itemDbNew3.setCurrentCity(city);

        itemDb.setPower(mPowerNo);
        itemDbNew3.setPower(mPowerNo);

        itemDb.setExpendPower(mExpendPower + "");
        itemDbNew3.setExpendPower(mExpendPower + "");

        itemDb.setTrueTemperature(tureTemperature + "");
        itemDbNew3.setTrueTemperature(tureTemperature + "");

        itemDb.setVoltage(mVoltage + "");
        itemDbNew3.setVoltage(mVoltage + "");

        itemDb.setOther("暂无其他");
        itemDbNew3.setOther("暂无其他");

//            itemDb.setExpendPower(CONSTS.LOCATION_TYPE);
//            itemDbNew3.setExpendPower(CONSTS.LOCATION_TYPE);

        if ("noStart".equals(pType)) {
            itemDb.setTransfer_id("1");
            itemDbNew3.setTransfer_id("1");
            CONSTS.TRANSFER_ID = "1";
        } else {
            itemDb.setTransfer_id(CONSTS.TRANSFER_ID);
            itemDbNew3.setTransfer_id(CONSTS.TRANSFER_ID);
        }
        Log.e("comeonbaby", CONSTS.UPLOAD_NUM + "");


        itemDb.setRecordAt(CommonUtil.getTrueTime());
        itemDbNew3.setRecordAt(CommonUtil.getTrueTime());


        //Log.e(TAG, ":time:" + itemDb.getRecordAt());
        String deviceId = PrefUtils.getString("deviceId", "", mContext);
        itemDb.setRemark(deviceId);
        itemDbNew3.setRemark(deviceId);


        //打开次数 结束归零
        itemDb.setOpen(openRecord);
        itemDbNew3.setOpen(openRecord);
        openRecord = 0;

        double duration = 0;
        if (MainActivity.mObjBean != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {

                duration = (new Date().getTime() - sdf.parse(MainActivity.mObjBean.getGetTime()).getTime()) / 1000;
            } catch (ParseException e) {
                //Log.e(TAG, "error5:" + e.getMessage());
                e.printStackTrace();
            }
        }
        //持续时间


        itemDb.setDuration(duration);
        itemDbNew3.setDuration(duration);

        //持续距离 结束后 LAST_LONGITUDE LAST_LATITUDE 为 "" DISTANCE 为0
        if (!"".equals(latitude) && !"".equals(longitude) && MainActivity.mObjBean != null) {
            try {
                CONSTS.DISTANCE = LocationUtils.getDistance(latitude, longitude,
                        Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong())) / 1000;

            } catch (Exception e) {
                //Log.e(TAG, "error6:" + e.getMessage());
            }
            itemDb.setDistance(CONSTS.DISTANCE);
            itemDbNew3.setDistance(CONSTS.DISTANCE);

        } else {
            itemDb.setDistance(0);
            itemDbNew3.setDistance(0);

        }


        //itemDb.save();
        mTransRecordList.add(itemDb);

        mItemDistance = itemDb.getDistance();
        mItemCity = itemDb.getCurrentCity();
        Intent mapIntent = new Intent(CONSTS.MAIN_MAP);
        mapIntent.putExtra("distance", itemDb.getDistance() + "");
        mapIntent.putExtra("ll", longitude + "," + latitude);
        mapIntent.putExtra("collision", itemDb.getCollision() + "");
        mContext.sendBroadcast(mapIntent);

        CONSTS.TRANS_DETAIL = itemDbNew3;

        Log.e(TAG, "UPLOAD_NUM:" + CONSTS.UPLOAD_NUM + "," + CONSTS.UPLOAD_NUM_VALUE);
        if (CONSTS.UPLOAD_NUM >= CONSTS.UPLOAD_NUM_VALUE) {

            //保存数据到数据库
            for (int i = mTransRecordList.size() - 1; i >= 0; i--) {
                if (i == 0) {
                    PrefUtils.putString("temperature", mTransRecordList.get(i).getTemperature(), mContext);
                    PrefUtils.putString("power", mTransRecordList.get(i).getPower(), mContext);
                    PrefUtils.putString("humidity", mTransRecordList.get(i).getHumidity(), mContext);

                }
                mTransRecordList.get(i).save();
                mTransRecordList.remove(i);

            }


            //Log.e(TAG,"CONSTS.UPLOAD_NUM1:"+CONSTS.UPLOAD_NUM);
            List<TransRecord> query = DataSupport.where("transfer_id = ? AND isUp = 1", CONSTS.TRANSFER_ID).find(TransRecord.class);

            List<Object> query100 = new ArrayList<>();
            Log.e(TAG, "UPLOAD_NUM:" + query.size() + "," + CONSTS.UPLOAD_NUM + "," + pType);

            int n = 0;
            for (int i = 0; i < query.size(); i++) {

                query100.add(query.get(i).toString());
                //Log.e(TAG,"query100:"+query.get(i).toString());
                n++;
                if (i == query.size() - 1) {
                    //Log.e("comeonbaby:", "N1," + query.size());
                    n = 0;
                    RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
                    params.addBodyParameter("records", query100 + "");
                    params.addBodyParameter("organSeg", MainActivity.mObjBean == null ? "" : MainActivity.mObjBean.getOrganSeg());
                    params.addBodyParameter("type", pType);
                    query100 = new ArrayList<>();
                    params.addBodyParameter("action", "records");
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // ToastUtil.showToast(result+",1");
                            Log.e(TAG, "N1" + result);
                            //ToastUtil.showToast(result + "N1");
                            RecordStringJson recordStringJson = new Gson().fromJson(result, RecordStringJson.class);
                            if (recordStringJson != null && recordStringJson.getResult() == CONSTS.SEND_OK) {
                                if (recordStringJson.getInfo() != null) {
                                    MainActivity.mObjBean = recordStringJson.getInfo();

                                }
                                //ToastUtil.showToast(recordStringJson.getInfo()+":info",mContext);
                                //Log.e(TAG, "info1:" + recordStringJson.getInfo());
                                List<Integer> lists = recordStringJson.getObj();
                                //realm = RealmUtil.getInstance().getRealm();
                                resultProcess(lists, pType, recordStringJson.getMsg());

                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            //ToastUtil.showToast(ex.getMessage() + "1");
                            //Log.e("comeonbaby", "message:" + ex.getLocalizedMessage() + "," + ex.getMessage());
                            //Log.e("comeonbaby:", "exN1:" + ex.getMessage() + "," + ex.getLocalizedMessage() + "," + (MainActivity.mObjBean == null ? "" : MainActivity.mObjBean.getOrganSeg()));
                            //ToastUtil.showToast(ex.getMessage() + "N1");
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });


                } else if (n == 50) {
                    // Log.e("comeonbaby:", "N2," + query.size() + "," + pType);
                    n = 0;
                    RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
                    params.addBodyParameter("records", query100 + "");
                    params.addBodyParameter("action", "records");
                    params.addBodyParameter("organSeg", MainActivity.mObjBean == null ? "" : MainActivity.mObjBean.getOrganSeg());
                    params.addBodyParameter("type", pType);
                    query100 = new ArrayList<>();
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            //ToastUtil.showToast(result + "N2");
                            Log.e(TAG, "N2" + result);
                            //  ToastUtil.showToast(result);
                            RecordStringJson recordStringJson = new Gson().fromJson(result, RecordStringJson.class);
                            if (recordStringJson != null && recordStringJson.getResult() == CONSTS.SEND_OK) {
                                if (recordStringJson.getInfo() != null) {
                                    MainActivity.mObjBean = recordStringJson.getInfo();
                                }
                                //Log.e(TAG, "info2:" + recordStringJson.getInfo());
                                List<Integer> lists = recordStringJson.getObj();
                                //realm = RealmUtil.getInstance().getRealm();
                                resultProcess(lists, pType, recordStringJson.getMsg());
                                // realm.close();
                            }
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            //Log.e("comeonbaby：", "N2+ ex:" + ex.getMessage());
                            //ToastUtil.showToast(ex.getMessage() + "N2");
                            // Log.e(TAG, "message:" + ex.getLocalizedMessage() + "," + ex.getMessage());
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }
            }


            //Log.e(TAG, "dataSize:" + query.size() + ",id:" + query100);


        }


    }

    private void resultProcess(List<Integer> pLists, String pType, String pMsg) {
        //ToastUtil.showToast("开始上次", mContext);
        String startLat = "0.0";
        String startLong = "0.0";
        String endLat = "0.0";
        String endLong = "0.0";
        for (int m = 0; m < pLists.size(); m++) {

            TransRecord transRecord = DataSupport.find(TransRecord.class, pLists.get(m));
            Log.e(TAG, "pLists:" + pLists.get(m) + "," + transRecord);
            if (transRecord == null) {
                continue;
            }
            transRecord.setIsUp(2);
            if (mTemperature > Double.parseDouble(transRecord.getTemperature())) ;
            {
                mTemperature = Double.parseDouble(transRecord.getTemperature());
            }
            if (m == 0) {
                startLat = transRecord.getLatitude();
                startLong = transRecord.getLongitude();
            }

            if (m == pLists.size() - 1) {
                endLat = transRecord.getLatitude();
                endLong = transRecord.getLongitude();
            }


            transRecord.save();
        }
        mDistance = LocationUtils.getDistance(Double.parseDouble(endLat), Double.parseDouble(endLong), Double.parseDouble(startLat), Double.parseDouble(startLong)) / 1000;
        //Log.e("comeonbaby:", mTemperature + "," + mDistance + ",pType:" + pType + "," + endLat + "," + startLat);
        //CONSTS.AUTO_CONDITION = "temperature:" + mTemperature + ",distance:" + mDistance + ",type:" + pType + ",start:" + startLat + "," + startLong + ",end:" + endLat + "," + endLong + "size:" + pLists.size();
        //ToastUtil.showToast(CONSTS.AUTO_CONDITION);
        shutDownTransfer(pType, pMsg);
    }

    private void shutDownTransfer(String pType, String pStatus) {

        String isPauseStr = pStatus.split("=")[3];
//        if(Boolean.parseBoolean(isPauseStr)){
//            mContext.startActivity(new Intent(mContext,MainActivity.class));
//            ((Activity)mContext).finish();
//        }

        //更新手机app状态
        if (CONSTS.UPLOAD_NUM >= CONSTS.FIRST_STAND) {
            //Log.e(TAG,"CONSTS.UPLOAD_NUM2:"+CONSTS.UPLOAD_NUM);
            noticeTransfer(MainActivity.mObjBean.getOrganSeg());
        }

//        if ("noStart".equals(pType)) {
//            //自动转运  温度小于10度，距离大于0.25公里
//
//            //CONSTS.UPLOAD_NUM = 0;
//            //return;
//        }

        String stop = pStatus.split("=")[0];
        String timeStop = pStatus.split("=")[1];
        String siteStr = pStatus.split("=")[2];
        //暂停


        if ("true".equals(stop)) {
            //发送电量信息
            sendPowerException();

            CONSTS.IS_START = 2;
            CONSTS.TRANSFER_ID = "";
            CONSTS.OPEN = 0;
            CONSTS.COLLISION = 0;
            DataSupport.deleteAll(TransRecord.class);

            PrefUtils.putString("pwd", "", mContext);
            //转运已经停止
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            try {
                Thread.sleep(100);
                SerialUtil.power();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // getActivity().finish();

        }
        if ("true".equals(timeStop)) {
            stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
            sendListTransferSms("18398850872", MainActivity.mObjBean.getBoxNo() + "的箱号已经时间超过,自动转运停止");
        }

        double longitude = CONSTS.LONGITUDE;
        double latitude = CONSTS.LATITUDE;
        //double distanceStart = LocationUtils.getDistance(Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong()), Double.parseDouble(MainActivity.mObjBean.getStartLati()), Double.parseDouble(MainActivity.mObjBean.getStartLong())) / 1000;
        double distance = 0;
        if (MainActivity.mObjBean != null) {
            distance = LocationUtils.getDistance(Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong()), latitude, longitude) / 1000;
        }

//        if (!"".equals(latitude) && !"".equals(longitude) && MainActivity.mObjBean != null && distanceStart > CONSTS.END_DISTANCE) {
//
//            if (distance < CONSTS.END_DISTANCE) {
//
//                stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
//            }
//
//        }

        if (latitude != 0 && longitude != 0 && MainActivity.mObjBean != null) {
            //double distance = LocationUtils.getDistance(Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong()), Double.parseDouble(latitude), Double.parseDouble(longitude)) / 1000;

            //pDistance = LocationUtils.getDistance(Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong()), Double.parseDouble(latitude), Double.parseDouble(longitude)) / 1000;
            //20公里发送短信
            boolean isSendSms20 = PrefUtils.getBoolean("isSendSms20", false, mContext);
            if (distance <= CONSTS.END_DISTANCE_20 && !isSendSms20) {
                PrefUtils.putBoolean("isSendSms20", true, mContext);
                getGroupPhones20(MainActivity.mObjBean.getOrganSeg());
            }

        }

        CONSTS.UPLOAD_NUM = 0;
        //处理固定参数和关机问题
        dealSite(siteStr);
    }

    private void dealSite(String siteStr) {

        Log.e(TAG, "site:" + siteStr);
        String sites[] = siteStr.split(",");
        // 变量的赋值变化 todo
        // 距离目的地多远停止转运(km)
        double END_DISTANCE = 0.2;
        CONSTS.END_DISTANCE = Double.parseDouble(sites[0]);
        // 20公里发送短信(km)
        double END_DISTANCE_20 = 20;
        CONSTS.END_DISTANCE_20 = Double.parseDouble(sites[1]);
        // 停止转运后,多少时间关机 60*60s
        long END_TIME = 60 * 60;
        CONSTS.END_TIME = Long.parseLong(sites[2]);
        // 温度的异常时间
        int EXCEPTION_TIME = 1000 * 60 * 20;
        CONSTS.EXCEPTION_TIME = Integer.parseInt(sites[3]);
        // 串口发送的间隔时间 ms
        int SERIAL_PERIOD = 500;
        CONSTS.SERIAL_PERIOD = Integer.parseInt(sites[4]);
        // 上传的值
        int UPLOAD_NUM_VALUE = 10;
        CONSTS.UPLOAD_NUM_VALUE = Integer.parseInt(sites[5]);
        //fantasy
        CONSTS.UPLOAD_NUM_VALUE = 2;
        // 串口循环的时间
        int SERIAL_NUM = 1;
        CONSTS.SERIAL_NUM = Integer.parseInt(sites[6]);

        // 30s 30000ms 间隔时间
        long SERIAL_TIME = 30000;
        CONSTS.SERIAL_TIME = Long.parseLong(sites[7]);
        // 每页的页数
        int PAGE_SIZE = 20;
        CONSTS.PAGE_SIZE = Integer.parseInt(sites[8]);
        // 电量
        int POWER = 13;
        //CONSTS.POWER = Integer.parseInt(sites[9]);

        //自动开始的时间
        long START_TIME = 60 * 15;
        CONSTS.START_TIME = Long.parseLong(sites[10]);

        boolean isStart = true;  //11
        isStart = Boolean.parseBoolean(sites[11]);
        boolean isStopRepeat = true;  //12
        isStopRepeat = Boolean.parseBoolean(sites[12]);
        boolean isClose = true; //13
        isClose = Boolean.parseBoolean(sites[13]);
        boolean isPlaneShow = true;//14
        isPlaneShow = Boolean.parseBoolean(sites[14]);
        boolean isTemperature = true;//15
        isTemperature = Boolean.parseBoolean(sites[15]);
        boolean isOpen = true;//16
        isOpen = Boolean.parseBoolean(sites[16]);
        boolean isHour24 = true;//17
        isHour24 = Boolean.parseBoolean(sites[17]);
        //设备编号
        String device = "";//18
        device = sites[18];
        //是否设置自动类型
        boolean isSite = false;//19
        isSite = Boolean.parseBoolean(sites[19]);
        String stopDevice = sites[20];

        boolean isTransfer = Boolean.parseBoolean(sites[21]);

        final String deviceId = PrefUtils.getString("deviceId", "", mContext);

        //修改单个的配置
        if (!"".equals(device) && device.contains(deviceId)) {
            PrefUtils.putBoolean("isStart", isStart, mContext);
            PrefUtils.putBoolean("isStop", isStopRepeat, mContext);
            PrefUtils.putBoolean("isClose", isClose, mContext);
            PrefUtils.putBoolean("isPlaneShow", isPlaneShow, mContext);
            PrefUtils.putBoolean("isTemperature", isTemperature, mContext);
            PrefUtils.putBoolean("isOpen", isOpen, mContext);
            PrefUtils.putBoolean("isHour24", isHour24, mContext);
            PrefUtils.putBoolean("isTransfer", isTransfer, mContext);
        }
        //修改所有的配置
        if (isSite) {
            PrefUtils.putBoolean("isStart", isStart, mContext);
            PrefUtils.putBoolean("isStop", isStopRepeat, mContext);
            PrefUtils.putBoolean("isClose", isClose, mContext);
            PrefUtils.putBoolean("isPlaneShow", isPlaneShow, mContext);
            PrefUtils.putBoolean("isTemperature", isTemperature, mContext);
            PrefUtils.putBoolean("isOpen", isOpen, mContext);
            PrefUtils.putBoolean("isHour24", isHour24, mContext);
            PrefUtils.putBoolean("isTransfer", isTransfer, mContext);
        }

        Log.e(TAG, deviceId+":siteStr1:" + stopDevice);
        //超过24小时自动关机
        if (!"".equals(stopDevice) && stopDevice.contains(deviceId)) {
            CONSTS.STOP_DEVICES = stopDevice;
            RequestParams params = new RequestParams(URL.TRANSFER);
            params.addBodyParameter("action", "closePower");
            params.addBodyParameter("deviceId", deviceId);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.e(TAG, "siteStr2:" + result);
                    PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                    if (photoJson.getResult() == CONSTS.SEND_OK) {
                        SerialUtil.powerOff();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.e(TAG, "siteStr3:" + ex.getMessage());
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });

        }


    }

    /**
     * 根据医院名称获取地址 上海市
     */
    private void loadHospitalAddress() {

        RequestParams params = new RequestParams(URL.USERS);
        params.addBodyParameter("action", "getHospitalAddress");
        params.addBodyParameter("hospitalName", mHospitalName);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ToastUtil.showToast("开始一键转运", mContext);
                HospitalJson hospitalJson = new Gson().fromJson(result, HospitalJson.class);
                if (hospitalJson != null && hospitalJson.getResult() == CONSTS.SEND_OK) {
                    String toHospitalAddress = hospitalJson.getObj() == null ? null : hospitalJson.getObj().getAddress();

                    loadEndLocation(toHospitalAddress);

                } else {
                    loadEndLocation(PrefUtils.getString("toHospitalAddress", "", mContext));
                }
                //Log.e(TAG, "loadHospitalAddress:" + result + "," + mHospitalName);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Log.e(TAG, "loadHospitalAddress:" + ex.getMessage());
                isTransferStart = true;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取结束的经纬度
     */
    private void loadEndLocation(final String pEndLocation) {
        String url = URL.GAO_DE_LOCATION_URL + pEndLocation;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isTransferStart = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // LogUtil.e(TAG, "response:" + response.body().string());
                LatiLongJson latiLongJson = new Gson().fromJson(response.body().string(), LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        endLocation = latiLongJson.getGeocodes()[0].getLocation();
                        getDepartments();
                    } else {
                        isTransferStart = true;
                        endLocation = PrefUtils.getString("endLocation", "", mContext);
                    }
                } else {
                    isTransferStart = true;
                    endLocation = PrefUtils.getString("endLocation", "", mContext);
                }
            }
        });


    }

    /**
     * 获取科室协调员
     */
    private void getDepartments() {
        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "getDepartments");
        params.addBodyParameter("deviceId", mDeviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DepartmentsJson departmentsJson = new Gson().fromJson(result, DepartmentsJson.class);
                if (departmentsJson != null && departmentsJson.getResult() == CONSTS.SEND_OK) {
                    List<DepartmentsJson.ObjBean> mDepartments = departmentsJson.getObj();
                    if (mDepartments.size() > 0) {
                        mDepartmentName = mDepartments.get(0).getName();
                        mDepartmentPhone = mDepartments.get(0).getPhone();
                        getOneOpo();
                    } else {
                        isTransferStart = true;
                        mDepartmentName = PrefUtils.getString("departmentName", "", mContext);
                        mDepartmentPhone = PrefUtils.getString("departmentPhone", "", mContext);
                    }

                } else {
                    isTransferStart = true;
                    mDepartmentName = PrefUtils.getString("departmentName", "", mContext);
                    mDepartmentPhone = PrefUtils.getString("departmentPhone", "", mContext);
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isTransferStart = true;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取OPO人员
     */
    private void getOneOpo() {
        RequestParams params = new RequestParams(URL.OPO);
        params.addBodyParameter("action", "opo");
        params.addBodyParameter("hospital", mHospitalName);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                OpoInfoJson photoJson = new Gson().fromJson(result, OpoInfoJson.class);
                String modifyOrganSeg = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String organSeg = "";
                try {
                    organSeg = "AP" + sdf.format(sdfAll.parse(CommonUtil.getTrueTime()));

                    if (organSeg.contains("P") && organSeg.length() > 14) {
                        modifyOrganSeg = organSeg.substring(6, 14);
                    } else {
                        modifyOrganSeg = organSeg;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();

                }

                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    List<OpoInfoContact> mOpoInfoContact = photoJson.getObj().getOpoInfoContacts();
                    if (mOpoInfoContact.size() > 0) {
                        mOpoName = mOpoInfoContact.get(0).getContactName();
                        mOpoPhone = mOpoInfoContact.get(0).getContactPhone();

                        isRepeatOrganSeg(organSeg, modifyOrganSeg);

                    } else {
                        mOpoName = PrefUtils.getString("opoName", "", mContext);
                        mOpoPhone = PrefUtils.getString("opoPhone", "", mContext);

                        isRepeatOrganSeg(organSeg, modifyOrganSeg);
                    }
                } else {
                    mOpoName = PrefUtils.getString("opoName", "", mContext);
                    mOpoPhone = PrefUtils.getString("opoPhone", "", mContext);

                    isRepeatOrganSeg(organSeg, modifyOrganSeg);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isTransferStart = true;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void isRepeatOrganSeg(final String organSeg, final String modifyOrganSeg) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        String boxNo = PrefUtils.getString("boxNo", "", mContext);
        params.addBodyParameter("action", "organRepeatType");
        params.addBodyParameter("modifyOrganSeg", modifyOrganSeg);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("boxNo", boxNo);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                RepeatJson photoJson = new Gson().fromJson(result, RepeatJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    String type = photoJson.getObj().getType();

                    autoTransfer(organSeg, photoJson.getObj().getModifyOrganSeg(), type);

                } else {
                    isTransferStart = true;
                    ToastUtil.showToast("器官段号重复,请重新填写");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isTransferStart = true;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 自动转运
     */
    private void autoTransfer(final String organSeg, final String modifyOrganSeg, String type) {


        double longitude = CONSTS.LONGITUDE;
        double latitude = CONSTS.LATITUDE;
        final String city = CONSTS.CITY;


        if (endLocation != null && endLocation.contains(",")) {

            mAutoDistance = LocationUtils.getDistance(latitude, longitude, Double.parseDouble(endLocation.split(",")[1]), Double.parseDouble(endLocation.split(",")[0])) / 1000;
        }

        String groupName = "转运中-" + city + "-器官";

        String usersIds = "";

        usersIds += mDepartmentPhone + ",";
        usersIds += mOpoPhone;


        final String phonesStart = usersIds;
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "create");
        params.addBodyParameter("phone", "");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("modifyOrganSeg", modifyOrganSeg);
        params.addBodyParameter("organ", type);
        params.addBodyParameter("organNum", "1");
        params.addBodyParameter("blood", "A型");
        params.addBodyParameter("bloodNum", "1");
        params.addBodyParameter("sampleOrgan", "脾脏");
        params.addBodyParameter("sampleOrganNum", "1");

        params.addBodyParameter("opoContactName", mOpoName);
        params.addBodyParameter("opoContactPhone", mOpoPhone);
        params.addBodyParameter("contactName", mDepartmentName);
        params.addBodyParameter("contactPhone", mDepartmentPhone);


        params.addBodyParameter("fromCity", city);


        params.addBodyParameter("getTime", CommonUtil.getTrueTimeMM());
        params.addBodyParameter("openPsd", "");
        params.addBodyParameter("opoName", mHospitalName + "OPO");
        params.addBodyParameter("toHospName", mHospitalName);
        params.addBodyParameter("trueName", "");
        params.addBodyParameter("tracfficType", "救护车");
        params.addBodyParameter("tracfficNumber", "");
        params.addBodyParameter("distance", mAutoDistance + "");
        params.addBodyParameter("groupName", groupName);

        params.addBodyParameter("usersIds", usersIds);
        params.addBodyParameter("toHosp", city.split("市")[0]);
        String boxNo = PrefUtils.getString("boxNo", "", App.getContext());
        LogUtil.e(TAG, "boxNo:" + boxNo);
        params.addBodyParameter("boxNo", boxNo);

        params.addBodyParameter("isStart", "1");
        params.addBodyParameter("autoTransfer", "1");


        if (endLocation != null && endLocation.contains(",")) {

            params.addBodyParameter("startLong", longitude + "");
            params.addBodyParameter("startLati", latitude + "");
            params.addBodyParameter("endLong", endLocation.split(",")[0]);
            params.addBodyParameter("endLati", endLocation.split(",")[1]);

        } else {
            params.addBodyParameter("startLong", "0");
            params.addBodyParameter("startLati", "0");
            params.addBodyParameter("endLong", "0");
            params.addBodyParameter("endLati", "0");
        }

        if (!isTransferStart && CONSTS.IS_START != 0 && CONSTS.IS_START != 1) {
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    SerialUtil.transfer(true);
                    //开始短信模板要区分自动和手动的，手动的用现在的模板。自动的用新模板，要提示“检测到从xxx出发的疑似转运，已自动开启监控，请至APP或后台补全信息或删除。”

                    Datas photoJson = new Gson().fromJson(result, Datas.class);
                    //Log.e(TAG, "result:" + result);
                    if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                        String boxNo = PrefUtils.getString("boxNo", "", App.getContext());
                        startTransfer(organSeg);
                        //String content = "检测到从" + city + "出发的疑似转运,箱号为" + boxNo + "，已开启一键监控，请至APP或后台补全信息或删除。";
                        //String content = "从" + city + "出发的转运,箱号为" + boxNo + "，已开启一键监控，请至APP或后台补全信息或删除。";
                        //sendListTransferSms(phonesStart, content);
                        noticeTransfer(organSeg);
                        isTransferStart = true;
                        CONSTS.UPLOAD_NUM = 10;

                    } else if (photoJson != null && photoJson.getResult() == CONSTS.SEND_FAIL) {

                        isTransferStart = true;
                        ToastUtil.showToast("器官段号重复");


                    } else if (photoJson != null && photoJson.getResult() == CONSTS.BAD_PARAM) {
                        isTransferStart = true;
                        //ToastUtil.showToast("箱子已被使用");

                        LogUtil.e(TAG, "error:" + photoJson.getMsg());
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    LogUtil.e(TAG, "ex:" + ex.getMessage());
                    isTransferStart = true;
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }

    }

    private void sendListTransferSms(String phones, String content) {

        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendListTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void startTransfer(String pOrganSeg) {

        noticeTransfer(pOrganSeg);

//        String content = "器官段号：" + organSeg + "，" + from + "的" + organ + "转运已经开始。";
//
//        if (!isSave) {
//            sendTransferSms(phones, content);
//        }

        sendGroupMessage(pOrganSeg);

        //跳转到转运界面

        getTransferInfo();


    }

    /**
     * 获取转运信息
     */
    private void getTransferInfo() {

        final String deviceId = PrefUtils.getString("deviceId", "", App.getContext());
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferByDeviceId");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


                TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);
                //ToastUtil.showToast(re,mContext);

                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {


                    final TransferJson.ObjBean objBean = transferJson.getObj();


                    if (objBean.getIsStart().equals("1")) {


                        CONSTS.TRANSFER_ID = objBean.getTransferid();

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mContext);
                                boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mContext);

                                if (!"".equals(objBean.getOpenPsd()) && objBean.getOpenPsd() != null) {
                                    PrefUtils.putString("pwd", objBean.getOpenPsd(), mContext);

                                    //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, true);
                                } else {
                                    //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                                    PrefUtils.putString("pwd", "", mContext);
                                }


                            }
                        }.start();


                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        MainActivity.mObjBean = objBean;

                        CONSTS.IS_START = 1;
                        PrefUtils.putString("organSeg", objBean.getOrganSeg(), mContext);
                        CONSTS.TRANS_START = new Date().getTime();

                        CONSTS.OPEN = 0;

                        CONSTS.COLLISION = 0;
                        CONSTS.DISTANCE = 0;
                        CONSTS.DURATION_OLD = 0;

                        CONSTS.COUNT = 0;


                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                        if (!cn.getClassName().contains("OnWayActivity")) {
                            //Log.e(TAG, "OPEN:" + getOpen() + ",getDuration:" + getDuration() + ",getCollision:" + getCollsion());
                            Intent intent = new Intent(mContext, OnWayActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);

                        }

                    } else if (objBean.getIsStart().equals("0")) {

                        Intent intent = new Intent(mContext, MainActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("noStart", "noStart");
                        mContext.startActivity(intent);
                    } else {
                        CONSTS.IS_START = 2;

                    }

                } else {
                    PrefUtils.putString("pwd", "", mContext);
                    CONSTS.IS_START = 2;


                    boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mContext);
                    boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mContext);
                    SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                    PrefUtils.putString("pwd", "", mContext);

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                CONSTS.IS_START = 2;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    private void sendGroupMessage(String pOrganSeg) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessage");
        params.addBodyParameter("phone", mDepartmentPhone);
        params.addBodyParameter("organSeg", pOrganSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void sendPowerException() {
        //服务器发送异常(开箱,碰撞,温度)
        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
        params.addBodyParameter("action", "recordException");
        params.addBodyParameter("transferId", CONSTS.TRANSFER_ID);
        params.addBodyParameter("organSeg", MainActivity.mObjBean.getOrganSeg());
        params.addBodyParameter("modifyOrganSeg", MainActivity.mObjBean.getModifyOrganSeg());
        params.addBodyParameter("powerException", "true");
        params.addBodyParameter("power", CONSTS.POWER + "");
        params.addBodyParameter("powerType", "end");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void stopTransfer(final String organSeg, String boxNo) {

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "shutDownTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("boxNo", boxNo);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Datas photoJson = new Gson().fromJson(result, Datas.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ToastUtil.showToast("转运已结束");

                    PrefUtils.putString("pwd", "", mContext);
                    //通知转运监控
                    noticeTransfer(organSeg);
                    //发送短信
                    getGroupPhones(organSeg);

                    CONSTS.OPEN = 0;
                    CONSTS.COLLISION = 0;
                    CONSTS.DISTANCE = 0;

                    CONSTS.TRANS_DETAIL = new TransRecordItemDbNew3();

                    CONSTS.IS_START = 2;
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    mContext.startActivity(intent);

                    Intent intent = new Intent(CONSTS.ON_WAY_ACTION);
                    intent.putExtra("stopTransfer", "stopTransfer");
                    mContext.sendBroadcast(intent);


                    try {
                        Thread.sleep(100);
                        SerialUtil.power();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    CONSTS.END_FLAG_AUTO = "";
                    CONSTS.END_FLAG = "";
                    SerialUtil.transfer(false);
                    MainActivity.mObjBean = null;
                    ((Activity) mContext).finish();
                } else {
                    ToastUtil.showToast("停止转运失败");

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void sendTransferSms(String phones, String content) {
        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getGroupPhones(final String organSeg) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupInfoOrganSeg");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Datas photoJson = new Gson().fromJson(result, Datas.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    String phones = photoJson.getMsg();
                    String organSegTemp = "";

                    if (!TextUtils.isEmpty(MainActivity.mObjBean.getModifyOrganSeg())) {
                        organSegTemp = MainActivity.mObjBean.getModifyOrganSeg();
                    } else {
                        organSegTemp = organSeg;
                    }
                    //请至APP或后台查看，下载或补全信息。
                    String content = "器官段号：" + organSegTemp + "，" + MainActivity.mObjBean.getFromCity() + "的" + MainActivity.mObjBean.getOrgan() + "转运已结束，当前设备电量为" + CONSTS.POWER + "%。";
                    sendTransferSms(phones, content);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getGroupPhones20(final String organSeg) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupInfoOrganSeg");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Datas photoJson = new Gson().fromJson(result, Datas.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    String phones = photoJson.getMsg();
                    String organSegTemp = "";

                    if (!TextUtils.isEmpty(MainActivity.mObjBean.getModifyOrganSeg())) {
                        organSegTemp = MainActivity.mObjBean.getModifyOrganSeg();
                    } else {
                        organSegTemp = organSeg;
                    }

                    String content = "器官段号：" + organSegTemp + "，" + MainActivity.mObjBean.getFromCity() + "的" + MainActivity.mObjBean.getOrgan() + "转运不足20公里，请做好准备。";
                    sendTransferSms(phones, content);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销电池广播


        //unregisterReceiver(mBatteryReceiver);

        stopForeground(true);

        LogUtil.e("DataService", TAG + " 销毁了..");
        if (A.isReadyUp) {
            LogUtil.e("DataService", TAG + " 重建了..");
            // CollectService.getConnet(this);
        }
//        if (realm != null) {
//            realm.close();
//        }

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
            //Log.e(TAG, "error7:" + e.getMessage());
            e.printStackTrace();
        }

    }


    //关屏组件

    private ComponentName mAdminName;

    final static int ENABLE_ADMIN = 1;


    //点击按钮关闭屏幕

    public void onScreenOff() {


        // 申请权限

        mAdminName = new ComponentName(this, DeviceManagerBC.class);

        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);//在设备上执行管理政策
        if (!mDPM.isAdminActive(mAdminName)) {//如果未激活
            LogUtil.e(TAG, "打开手机设备管理器");
            showAdminManagement();//打开手机设备管理器

        }

        if (mDPM.isAdminActive(mAdminName)) {

            mDPM.lockNow();//执行锁屏


        } else {
            LogUtil.e("screenlock", "Unable to lock the phone D:");
        }

    }

    private void showAdminManagement() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);//打开手机设备管理器的intent
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"One key lock screen need to active");
        startActivity(intent);

    }

    PowerManager.WakeLock wakeLock;

    /**
     * 关闭屏幕
     */
    private void acquireWakeLock() {
        if (wakeLock == null) {
            //Log.e(TAG, "Acquiring wake lock");
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");

            wakeLock.acquire();
            CONSTS.SCREEN_LIGHT = false;

            PowerManager powerManager = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);
            boolean ifOpen = powerManager.isScreenOn();
            //Log.e(TAG,"ifOpen:"+ifOpen);
            if (!ifOpen) {
                SerialUtil.closeScreen();
            }


        }
    }

    /**
     * 开启屏幕
     */
    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            CONSTS.SCREEN_LIGHT = true;
        }
    }


//    public static void location() {
//        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
//        mlocationClient = new AMapLocationClient(mContext);
//        //初始化定位参数
//        mLocationOption = new AMapLocationClientOption();
//        //设置定位监听
//        mlocationClient.setLocationListener(new AMapLocationListener() {
//            @Override
//            public void onLocationChanged(AMapLocation amapLocation) {
//                if (amapLocation != null) {
//                    if (amapLocation.getErrorCode() == 0) {
//                        //定位成功回调信息，设置相关消息
//                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                        CONSTS.LATITUDE = amapLocation.getLatitude() + "";//获取纬度
//                        CONSTS.LONGITUDE = amapLocation.getLongitude() + "";//获取经度
//
//                        CONSTS.CITY = amapLocation.getCity();
//
//                        //判断是否达到目的地
//                        double pDistance = LocationUtils.getDistance(Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong()), amapLocation.getLatitude(), amapLocation.getLongitude()) / 1000;
//                        if (pDistance < CONSTS.END_DISTANCE) {
//                            //ToastUtil.showToast("tingzi"+pDistance);
//                            //停止转运
//
//                            Intent intent = new Intent(CONSTS.ON_WAY_TRANS);
//                            intent.putExtra("stopTransfer", "stop");
//                            mContext.sendBroadcast(intent);
//
//
//                        }
//                    } else {
//                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                        //Log.e("AmapError", "location Error, ErrCode:"
////                        +amapLocation.getErrorCode() + ", errInfo:"
////                                + amapLocation.getErrorInfo());
//                    }
//                }
//            }
//        });
//        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(60000);
//        //设置定位参数
//        mlocationClient.setLocationOption(mLocationOption);
//        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//        // 在定位结束后，在合适的生命周期调用onDestroy()方法
//        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//        //启动定位
//        mlocationClient.startLocation();
//    }

    public int getOpen() {
        if ("".equals(CONSTS.TRANSFER_ID)) {
            return -1;
        }
        return DataSupport.where("transfer_id=? and open = 1", CONSTS.TRANSFER_ID).count(TransRecord.class);

    }

    public int getCollision() {
        if ("".equals(CONSTS.TRANSFER_ID)) {
            return 0;
        }
        return DataSupport.where("transfer_id=? and collision=1", CONSTS.TRANSFER_ID).count(TransRecord.class);

    }
}



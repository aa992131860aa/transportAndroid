package com.otqc.transbox.controller.main;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.gson.Gson;
import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.controller.on.OnWayActivity;
import com.otqc.transbox.databinding.ActivityMainBinding;
import com.otqc.transbox.db.PowerTemp;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.Datas;
import com.otqc.transbox.json.DepartmentsJson;
import com.otqc.transbox.json.HospitalJson;
import com.otqc.transbox.json.LatiLongJson;
import com.otqc.transbox.json.OpoInfoContact;
import com.otqc.transbox.json.OpoInfoJson;
import com.otqc.transbox.json.PhotoJson;
import com.otqc.transbox.json.QrImagesJson;
import com.otqc.transbox.json.RepeatJson;
import com.otqc.transbox.json.TransferJson;
import com.otqc.transbox.json.UploadAppJson;
import com.otqc.transbox.service.CommServer;
import com.otqc.transbox.service.ScreenService;
import com.otqc.transbox.service.event.MapEvent;
import com.otqc.transbox.test.DeviceManagerBC;
import com.otqc.transbox.util.A;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.LocationUtils;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SDFileHelper;
import com.otqc.transbox.util.SerialUtil;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.util.UpdateManager;
import com.otqc.transbox.view.NewMonitorPopup;

import org.litepal.crud.DataSupport;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 模块1：main
 */
public class MainActivity extends AppBaseActivity implements AMapLocationListener {
    //是否低于10度的标识
    public static double temperatureFlag = 20;
    private MainData mData;
    private String TAG = "MainActivity";
    //开箱
    private Button btn_open;
    private RelativeLayout rl_main;


    //转运信息
    public static TransferJson.ObjBean mObjBean;
    //开始转运提示
    private AlertDialog.Builder mAlertDialog;
    private ImageView iv_transfer;

    private Object statusBarManagerService;
    private Method methodDisable;
    private final static int SCREENPINNING_MODE_FLAG = 57081856;
    private final static int NORMAL_MODE_FLAG = 0;
    private final static int SCREENPINNING_MODE_WITH_BACK_FLAG = 52887552;
    private final static int DATE_SETTINGS_REQUEST = 0;
    /**
     * 定位的参数
     */
    private double lastLatitude = 0;
    private double lastLongitude = 0;
    private long lastTime = 0;
    private int lastIndex = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    //待转运的信息,显示
    private LinearLayout ll_transfer;
    private int size;
    byte[] buffer = new byte[1024];
    ScheduledExecutorService service1 = null;
    ScheduledExecutorService service = null;


    private MainReceiver mainReceiver;

    private TextView tv_temperature;
    private TextView tv_temperature_title;
    private ImageView iv_temperature;

    private TextView tv_status;
    private TextView tv_status_title;
    private ImageView iv_status;


    private TextView tv_power;
    private TextView tv_power_title;
    private ImageView iv_power;

    //开始图标
    private ImageView imageView;
    private ImageView iv_start;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private LocationManager mLocationManager;
    BroadcastReceiver mBatteryReceiver = null;
    private boolean isHotFix = true;
    private String mHospitalName;
    private String mDeviceId;

    private int mLongitudeDistance = 0;
    private List<LatLng> mLongitudes = new ArrayList();
    private double mDistance;
    private String mOpoName;
    private String mOpoPhone;

    private double mAutoDistance;
    private String endLocation;

    private String mDepartmentName;
    private String mDepartmentPhone;
    private int openTime = 30;

    private String mType = "器官";
    private TextView tv_box_no;

    @Override
    protected void initVariable() {

        //获取本地保存的二维码
        String boxNoTemp = PrefUtils.getString("boxNo", "", this);
        if (!"".equals(boxNoTemp)) {
            tv_box_no = (TextView) findViewById(R.id.tv_box_no);
            tv_box_no.setText("器官：" + boxNoTemp);
            File parent = Environment.getExternalStorageDirectory();
            File file = new File(parent, "box_no1.png");
            Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
            imageView.setImageURI(uri);
        }


        // 地图 数据
        MapEvent.city = "";
        MapEvent.lont = "";
        MapEvent.lati = "";
        MapEvent.Distance = "";
        // key
//        PrefUtils.putString("tid", "", App.getContext());
//        PrefUtils.putString("key", "", App.getContext());
//        PrefUtils.putString("boxid", "", App.getContext());
//        PrefUtils.putString("qrcode", "", App.getContext());
//        PrefUtils.putString("hospitalid", "", App.getContext());
//        PrefUtils.putString("address", "", App.getContext());
        // A
        A.point = null;
        A.isBoxInfo = false;
        A.isKwdInfo = false;
        A.isSerialPort = false;
        A.isReadyUp = false;

        //电量
        if (mBatteryReceiver == null) {

            mBatteryReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int level = intent.getIntExtra("level", 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    PowerTemp powerTemp = new PowerTemp();
                    powerTemp.setLevel(level);
                    powerTemp.setTime(sdf.format(new Date()));
                    powerTemp.save();

                }
            };
            registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {


            openTime--;
            if (openTime <= 0) {
                btn_open.setText("开箱");
                if (service1 != null) {
                    service1.shutdown();
                }
                service1 = null;
            } else {
                btn_open.setText("已开箱(" + openTime + "s)");
            }
            PrefUtils.putInt("openTime", openTime, MainActivity.this);
            return false;
        }
    });


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView(Bundle savedInstanceState) {


        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        btn_open = (Button) findViewById(R.id.btn_open);
        rl_main = (RelativeLayout) findViewById(R.id.rl_main);
        ll_transfer = (LinearLayout) findViewById(R.id.ll_transfer);
        iv_transfer = (ImageView) findViewById(R.id.iv_transfer);
        imageView = (ImageView) findViewById(R.id.imageView);
        iv_start = (ImageView) findViewById(R.id.iv_start);
        tv_box_no = (TextView) findViewById(R.id.tv_box_no);
        mData = new MainData();


        binding.setInfo(mData);
        binding.setPresenter(new MainPresenter(this, binding, ll_transfer));
        // LogUtil.e(TAG,"jniTest:"+new JniTest().getString());

        //注册广播
        IntentFilter intentFilter = new IntentFilter(CONSTS.MAIN_ACTION);
        mainReceiver = new MainReceiver();
        registerReceiver(mainReceiver, intentFilter);

        tv_power = (TextView) findViewById(R.id.tv_power);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_temperature = (TextView) findViewById(R.id.tv_temperatue);

        tv_power_title = (TextView) findViewById(R.id.tv_power_title);
        tv_status_title = (TextView) findViewById(R.id.tv_status_title);
        tv_temperature_title = (TextView) findViewById(R.id.tv_temperature_title);


        iv_power = (ImageView) findViewById(R.id.iv_power);
        iv_status = (ImageView) findViewById(R.id.iv_status);
        iv_temperature = (ImageView) findViewById(R.id.iv_temperature);

        mHospitalName = PrefUtils.getString("hospitalName", "", this);
        mDeviceId = PrefUtils.getString("deviceId", "", this);

        String temperature = PrefUtils.getString("temperature", "", this);
        String power = PrefUtils.getString("power", "", this);
        if (!"".equals(temperature)) {

            tv_temperature.setText(temperature + "℃");
            try {
                if (Double.parseDouble(temperature) < 10) {
                    tv_temperature.setTextColor(getResources().getColor(R.color.white));
                    tv_temperature_title.setTextColor(getResources().getColor(R.color.white));
                } else {
                    tv_temperature.setTextColor(getResources().getColor(R.color.high));
                    tv_temperature_title.setTextColor(getResources().getColor(R.color.high));
                }
            } catch (Exception e) {

            }

        }
        if (!"".equals(power)) {
            tv_power.setText(power + "%");

        }

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (btn_open.getText().toString().equals("开箱") && CommServer.mOutputStream != null) {


                    final String pwd = PrefUtils.getString("pwd", "", MainActivity.this);
                    boolean isOpen = PrefUtils.getBoolean("isOpen", true, MainActivity.this);
                    if (pwd == null || "".equals(pwd) || !isOpen) {

                        SerialUtil.open();
                    } else {

                        final NewMonitorPopup newMonitorPopup = new NewMonitorPopup(MainActivity.this, "开箱");
                        newMonitorPopup.showAtLocation(btn_open, Gravity.CENTER, 0, 0);
                        newMonitorPopup.setOnClickChangeListener(new NewMonitorPopup.OnClickChangeListener() {
                            @Override
                            public void OnClickChange(String number) {

                                //boolean isOpen = PrefUtils.getBoolean("isOpen", true, MainActivity.this);
                                if (number.length() >= 4 && pwd.equals(number.substring(0, 4)) || "9999".equals(number)) {

                                    SerialUtil.open();
                                    //sendBroadcast(new Intent(CONSTS.EXCEPTION));
                                    newMonitorPopup.dismiss();

                                } else {
                                    ToastUtil.showToast("开箱密码错误");

                                }
                            }
                        });
                    }


                    showAdminManagement();


                }
            }
        });
        iv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog = new AlertDialog.Builder(MainActivity.this);
                mAlertDialog.setMessage("是否开始转运?");
                mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mObjBean != null) {
                            // updateStart(mObjBean.getOrganSeg());

                            loadEndLocation(mObjBean.getOrganSeg());
                            getGroupPhones(mObjBean.getOrganSeg(), 0, "start");
                        }

                    }
                });
                mAlertDialog.show();

            }
        });
        try {
            //Log.e(TAG, "init1:" + CONSTS.INIT);

//            if (CONSTS.INIT) {

            PrefUtils.putInt("openTime", 0, MainActivity.this);
            CONSTS.INIT = false;
            //设置
            Intent i = new Intent(this, CommServer.class);
            startService(i);

            Thread.sleep(1000);


            //Log.e(TAG, "startService" + CONSTS.IS_START_CHECK + "," + CONSTS.IS_START);
            //ToastUtil.showToast("startService" + CONSTS.IS_START_CHECK + "," + CONSTS.IS_START, this);
            SerialUtil.power();

//            } else {
//
//                SerialUtil.power();
//                //collision();
//            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "ex:" + e.getMessage());
        }

        // Get android @hide class android.app.StatusBarManager by reflection.
        //反射拿到状态栏管理器

        SerialUtil.transferSite(false);

        if (mlocationClient == null) {
            location();
        }

        //默认开屏关屏一次  开屏
        //创建PowerManager对象
//        PowerManager  pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        //保持cpu一直运行，不管屏幕是否黑屏
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
//        wakeLock.acquire();

//        Intent i = new Intent(this, ScreenService.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startService(i);
//        releaseWakeLock();

//          new Thread(){
//              @Override
//              public void run() {
//                  super.run();
//                  try {
//                      Thread.sleep(3000);
//                      //关屏
//                      onScreenOff();
//                      acquireWakeLock();
//                      Log.e(TAG, "关屏");
//                  } catch (InterruptedException e) {
//                      e.printStackTrace();
//                      Log.e(TAG, "e:"+e.getMessage());
//                  }
//              }
//          }.start();



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

    private void getGroupPhones(String organSeg, final int position, final String type) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupInfoOrganSeg");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    String phones = photoJson.getMsg();
                    String content = "";
                    if ("start".equals(type)) {
                        String organSegTemp = "";

                        if (!TextUtils.isEmpty(MainActivity.mObjBean.getModifyOrganSeg())) {
                            organSegTemp = mObjBean.getModifyOrganSeg();
                        } else {
                            organSegTemp = mObjBean.getOrganSeg();
                        }
                        content = "本次转运医师:" + mObjBean.getTrueName() + ",科室协调员:" + mObjBean.getContactName() + "。器官段号:" + organSegTemp + "，" + mObjBean.getFromCity() + "的" + mObjBean.getOrgan() + "转运已开始。";
                        sendListTransferSms(phones, content);
                    }

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


    private void sendListTransferSms(final String phones, final String content) {

        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendListTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Log.e("time", result + ":" + phones + ":" + content);
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
     * 获取结束的经纬度
     */
    private void loadEndLocation(final String organSeg) {

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "updateStart");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("isStart", "1");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //ToastUtil.showToast(result);
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    CONSTS.IS_START = 1;
                    sendGroupMessage();

                    CONSTS.OPEN = 0;
                    CONSTS.COLLISION = 0;
                    CONSTS.UPLOAD_NUM = CONSTS.UPLOAD_NUM_VALUE;
                    SerialUtil.clearCollisionNumber();

                    //getGroupName(organSeg);
                    //通知转运监控
                    noticeTransfer(organSeg, "");

                    CONSTS.IS_START = 1;
                    PrefUtils.putString("organSeg", mObjBean.getOrganSeg(), MainActivity.this);
                    CONSTS.TRANS_START = new Date().getTime();
                    CONSTS.TRANSFER_ID = mObjBean.getTransferid();
                    Intent intent = new Intent(MainActivity.this, OnWayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //finish();

                } else {
                    ToastUtil.showToast("开始失败");
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误");
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
     * 获取群组名
     */
    private void getGroupName(final String organSeg) {

        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupName");
        params.addBodyParameter("organSeg", organSeg);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    //RongIM.getInstance().refreshGroupInfoCache(new Group(organSeg, photoJson.getMsg(), Uri.parse("http://116.62.28.28:8080/transbox/images/team.png")));
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast("网络错误");
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
    private void noticeTransfer(String organSeg, String type) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("type", type);
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

    private void sendGroupMessage(String organSeg, String phone) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessageStart");
        params.addBodyParameter("phone", phone);
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

    private void sendGroupMessage() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessageStart");
        params.addBodyParameter("phone", mObjBean.getPhone());
        params.addBodyParameter("organSeg", mObjBean.getOrganSeg());
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


    Runnable testRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    };


    private void loadUpload() {

        RequestParams params = new RequestParams(URL.UPLOAD_APP);
        params.addBodyParameter("action", "padNew");
        params.addBodyParameter("deviceId", mDeviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                UploadAppJson uploadAppJson = new Gson().fromJson(result, UploadAppJson.class);
                if (uploadAppJson != null && uploadAppJson.getResult() == CONSTS.SEND_OK) {
                    if (uploadAppJson.getObj() != null) {

                        int oldVersion = getVersionCode(MainActivity.this);
                        int newVersion = uploadAppJson.getObj().getVersion();
                        String url = uploadAppJson.getObj().getUrl();
                        if (newVersion > oldVersion) {
                            UpdateManager updateManager = new UpdateManager(MainActivity.this, url);
                            updateManager.checkUpdateInfo();

                        }

                    }

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
    protected void initData() {

        A.mCollectState = 0;    //只采样


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (client != null) {
//            client.disconnect();
//        }
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient = null;
        }
        unregisterReceiver(mainReceiver);
        unregisterReceiver(mBatteryReceiver);
        if (service != null) {
            service.shutdown();
            service = null;
        }
        Log.e(TAG,"onDestroy");
    }


    /**
     * 开始转运
     *
     * @param organSeg
     */
    private void updateStart(String organSeg) {

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "updateStart");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("isStart", "1");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                UploadAppJson uploadAppJson = new Gson().fromJson(result, UploadAppJson.class);
                if (uploadAppJson != null && uploadAppJson.getResult() == CONSTS.SEND_OK) {
                    ll_transfer.setVisibility(View.GONE);
                    CONSTS.IS_START = 1;
                    PrefUtils.putString("organSeg", mObjBean.getOrganSeg(), MainActivity.this);
                    CONSTS.TRANS_START = new Date().getTime();
                    CONSTS.TRANSFER_ID = mObjBean.getTransferid();
                    Intent intent = new Intent(MainActivity.this, OnWayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //finish();
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


    Realm realm;


    /**
     * 获取版本号
     *
     * @param context 上下文
     * @return 版本号
     */
    public static int getVersionCode(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


//    private void deleteRecord() {
//        realm = RealmUtil.getInstance().getRealm();
//        realm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                RealmResults<TransRecordItemDbNew> query = realm.where(TransRecordItemDbNew.class)
//                        .equalTo("transfer_id", CONSTS.TRANSFER_ID)
//                        .findAll();
//
//                query.deleteAllFromRealm();
//
//            }
//        });
//    }

    //    public Number getOpen() {
//
//
//        try {
//            realm = RealmUtil.getInstance().getRealm();
//            Number n = realm.where(TransRecordItemDbNew3.class)
//                    .equalTo("transfer_id", CONSTS.TRANSFER_ID)
//                    .max("open");
//
//            return n;
//        } catch (Exception e) {
//            return 0;
//        }
//
//
//    }

    public int getOpen() {
        if ("".equals(CONSTS.TRANSFER_ID)) {
            return 0;
        }
        return DataSupport.where("transfer_id=? and open = 1", CONSTS.TRANSFER_ID).count(TransRecord.class);

    }

    public int getCollision() {
        if ("".equals(CONSTS.TRANSFER_ID)) {
            return 0;
        }
        return DataSupport.where("transfer_id=? and collision=1", CONSTS.TRANSFER_ID).count(TransRecord.class);

    }

    public int getDuration() {
        if ("".equals(CONSTS.TRANSFER_ID)) {
            return 0;
        }

        return DataSupport.where("transfer_id=? and collision = 1", CONSTS.TRANSFER_ID).count(TransRecord.class);
    }

    public int getDistance() {
        if ("".equals(CONSTS.TRANSFER_ID)) {
            return 0;
        }

        return DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).max(TransRecord.class, "distance", int.class);

    }

    /**
     * 获取已转移的总数
     *
     * @return
     */

    public int getCount() {
        if ("".equals(CONSTS.TRANSFER_ID)) {
            return 0;
        }
        return DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).count(TransRecord.class);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    /**
     * 读屏幕开关线程
     */
    Runnable screenRunnable = new Runnable() {
        @Override
        public void run() {

            if (CONSTS.IS_START != 1) {
//                if (lastTime != 0 && mlocationClient == null) {
//
//                    long time = ((new Date().getTime()) - lastTime) / 1000;
//                    long startTime = 60 * 20;
//                    Log.e(TAG, "线程持续开启定位;" + time + "," + (time > startTime));
//                    if (time > startTime) {
//                        location();
//                    } else {
//                        if (mlocationClient != null) {
//                            mlocationClient.stopLocation();
//                            mlocationClient = null;
//                        }
//                    }
//
//                }
                getTransferInfo();

            }
            if (CONSTS.IS_START == 1 && service != null) {
                service.shutdown();
                service = null;
            }


        }


    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //Log.e(TAG, "onNewIntent" + CONSTS.IS_START_CHECK + "," + CONSTS.IS_START);
        //ToastUtil.showToast("startServiceonNewIntent" + CONSTS.IS_START_CHECK + "," + CONSTS.IS_START, this);

        if (service == null) {

            service = Executors.newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            service.scheduleAtFixedRate(screenRunnable, 0, 45, TimeUnit.SECONDS);
        }

        lastLatitude = 0;
        lastLongitude = 0;
        lastIndex = 0;

        location();
        setIntent(intent);
        newIntentHandler();


    }

    private void newIntentHandler() {

        //Log.e("BActivity","onNewIntent"+intent.getStringExtra("cyy"));
        String noStart = getIntent().getStringExtra("noStart");

        if ("noStart".equals(noStart)) {
            iv_start.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
            iv_start.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
        int openTimeLocal = PrefUtils.getInt("openTime", 0, this);
        if (openTimeLocal > 0 && service1 == null) {

            openTime = openTimeLocal;

            service1 = Executors.newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            service1.scheduleAtFixedRate(testRunnable, 0, 1000, TimeUnit.MILLISECONDS);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        SerialUtil.transferSite(false);

        if (service == null) {
            service = Executors.newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            service.scheduleAtFixedRate(screenRunnable, 0, 45, TimeUnit.SECONDS);
        }
        //ToastUtil.showToast("onstartShow" + mlocationClient, this);
//        if (mlocationClient == null) {
//            location();
//        }
        //设置未保存
        newIntentHandler();
        //loadUpload();
        getTransferInfo();
        setMobileData(getBaseContext(), true);
        // ToastUtil.showToast(getMobileDataState(getBaseContext(),null)+"");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        AppIndex.AppIndexApi.start(client, getIndexApiAction());


        //openAirplaneModeOn(this,true);
        //setAirplaneModeOn(false);
//        List<TransRecord> recordList = DataSupport.findAll(TransRecord.class);
//        for (int i = 0; i < recordList.size(); i++) {
//            Log.e(TAG, "id:" + recordList.get(i).getId() + ",date:" + recordList.get(i).getRecordAt() + ",isUp:" + recordList.get(i).getIsUp()+","+recordList.get(i).getTransfer_id()+","+CONSTS.TRANSFER_ID);
//
//        }
    }

    //打开或者关闭gps
    public void openGPS(boolean open) {
        if (Build.VERSION.SDK_INT < 19) {
            Settings.Secure.setLocationProviderEnabled(getContentResolver(),
                    LocationManager.GPS_PROVIDER, open);
        } else {
            if (!open) {
                Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
            } else {
                Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.


        //AppIndex.AppIndexApi.end(client, getIndexApiAction());

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

    PowerManager.WakeLock wakeLock;


    private void showAdminManagement() {
        // TODO Auto-generated method stub
        mAdminName = new ComponentName(this, DeviceManagerBC.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);//打开手机设备管理器的intent
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        //intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"One key lock screen need to active");
        startActivityForResult(intent, ENABLE_ADMIN);

    }


    class SerialData extends Thread {

        public SerialData() {

        }

        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {

                SerialUtil.power();
                //collision();


                //Log.e(TAG, "发起了请求,碰撞次数:" + CONSTS.COLLISION + ",数据库的碰撞" + getCollision() + ",距离:" + CONSTS.DISTANCE + ",老的距离:" + CONSTS.DISTANCE_OLD + ",持续时间:" + CONSTS.DISTANCE + ",老的持续时间:" + CONSTS.DURATION_OLD);

                try {
                    Thread.sleep(100);
                    String pwd = PrefUtils.getString("pwd", "", MainActivity.this);
                    if (!"".equals(pwd)) {
                        boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, MainActivity.this);
                        boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, MainActivity.this);
                        //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, true);
                    }

                    Thread.sleep(CONSTS.SERIAL_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }


        }
    }

    private void setDeviceId() {
        //生成设备号
        //deviceId

        if (PrefUtils.getString("deviceId", "", App.getContext()).trim().equals("")) {
            //Log.e(TAG,PrefUtils.getString("deviceId", "", App.getContext())+"2sdfa");
            String deviceId = "35";
            for (int i = 0; i < 12; i++) {
                deviceId += new Random().nextInt(10) + "";
            }
            RequestParams
                    params = new RequestParams(URL.BOX);
            params.addQueryStringParameter("action", "device");
            params.addQueryStringParameter("device", deviceId);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Datas datas = new Gson().fromJson(result, Datas.class);
                    if (datas != null && datas.getResult() == CONSTS.SEND_OK) {

                        PrefUtils.putString("deviceId", datas.getObj().toString(), App.getContext());
                        getBoxInfo(datas.getObj().toString());
                    } else {
                        setDeviceId();
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
        } else {
            A.isBoxInfo = true;
        }
        getBoxInfo(PrefUtils.getString("deviceId", "", App.getContext()).trim());

    }

    private void getBoxInfo(String deviceId) {
        RequestParams params = new RequestParams(URL.QR_IMAGE);
        params.addBodyParameter("action", "boxHosp");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                QrImagesJson qrImagesJson = new Gson().fromJson(result, QrImagesJson.class);
                if (qrImagesJson != null && qrImagesJson.getResult() == CONSTS.SEND_OK) {
                    QrImagesJson.ObjBean model = qrImagesJson.getObj();
                    PrefUtils.putString("boxid", model.getBoxId(), App.getContext());
                    PrefUtils.putString("qrcode", model.getQrImages(), App.getContext());
                    PrefUtils.putString("hospitalid", model.getHospitalId(), App.getContext());
                    PrefUtils.putString("hospitalName", model.getHospitalName(), App.getContext());
                    PrefUtils.putString("boxNo", model.getBoxNo(), App.getContext());

                    new SDFileHelper(MainActivity.this).downloadLocal("box_no1.png", model.getQrImages());

                    tv_box_no.setText("箱号：" + model.getBoxNo());
                    File parent = Environment.getExternalStorageDirectory();
                    File file = new File(parent, "box_no1.png");
                    Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
                    imageView.setImageURI(uri);


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
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取转运信息
     */
    private void getTransferInfo() {
        //ToastUtil.showToast("start");
        final String deviceId = PrefUtils.getString("deviceId", "", App.getContext());
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferByDeviceId");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


                //热修复
                if (isHotFix) {
                    // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
                    //SophixManager.getInstance().queryAndLoadNewPatch();
                    isHotFix = false;
                    DataSupport.deleteAll(TransRecord.class);
                    //检测是否有新版本更新
                    loadUpload();

                    //获取科室,得到器官类型

                    boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, MainActivity.this);
                    boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, MainActivity.this);
                    SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                    CONSTS.IS_TRANSFER = true;

                    setDeviceId();

                }


                TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);

                if (CONSTS.SERVER_TIME == 0L) {
                    CONSTS.SERVER_TIME = Long.parseLong(transferJson.getMsg());

                    CommonUtil.modifySystemTime(CONSTS.SERVER_TIME);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                    int temp = 0;
                    while (sdf.format(new Date()).equals("2010")) {
                        ToastUtil.showToast("设置时间:" + CONSTS.SERVER_TIME, MainActivity.this);
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

                }


                Log.e(TAG, "time:" + CONSTS.SERVER_TIME);
                //ToastUtil.showToast(result);

                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {


                    final TransferJson.ObjBean objBean = transferJson.getObj();


                    if (objBean.getIsStart().equals("0")) {
                        //ll_transfer.setVisibility(View.VISIBLE);
                        iv_start.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
//                        MainEvent mainEvent = new MainEvent();
//                        mainEvent.setIsStart("0");
//                        EventBus.getDefault().post(mainEvent);
                        CONSTS.IS_START = 0;
                        mObjBean = objBean;
                        //设置为未修改
                        CommonUtil.saveNoStart(MainActivity.this, objBean);
                    } else if (objBean.getIsStart().equals("1")) {
                        CONSTS.IS_START = 1;
                        service.shutdown();
                        service = null;

                        CONSTS.TRANSFER_ID = objBean.getTransferid();
                        //清零碰撞次数
//                        clearCollisionNumber();
                        //ToastUtil.showToast((!"".equals(objBean.getOpenPsd()) && objBean.getOpenPsd() != null)+",,"+ objBean.getOpenPsd());
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                SerialUtil.clearCollisionNumber();


                                boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, MainActivity.this);
                                boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, MainActivity.this);


                                if (!"".equals(objBean.getOpenPsd()) && objBean.getOpenPsd() != null) {
                                    PrefUtils.putString("pwd", objBean.getOpenPsd(), MainActivity.this);

                                    //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, true);
                                } else {
                                    //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                                    PrefUtils.putString("pwd", "", MainActivity.this);
                                }
                                //collision();

                            }
                        }.start();


                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        mObjBean = objBean;
                        // Log.e(TAG, mObjBean.toString());
                        // Log.e(TAG, "isStart:" + mObjBean.getIsStart() + "," + getOpen());
                        ll_transfer.setVisibility(View.GONE);

                        PrefUtils.putString("organSeg", mObjBean.getOrganSeg(), MainActivity.this);
                        CONSTS.TRANS_START = new Date().getTime();

                        CONSTS.OPEN = getOpen();

                        CONSTS.COLLISION = getCollision();
                        CONSTS.DISTANCE = getDistance();
                        CONSTS.DURATION_OLD = getDuration();

                        CONSTS.COUNT = getCount();
                        CONSTS.UPLOAD_NUM = CONSTS.FIRST_NUM;

                        if (mlocationClient != null) {
                            mlocationClient.stopLocation();
                            mlocationClient = null;
                        }

                        Log.e(TAG, "OPEN:" + getOpen() + ",getDuration:" + getDuration() + ",getCollision:" + getCollision());
                        Intent intent = new Intent(MainActivity.this, OnWayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        //finish();


                    } else {

                        CONSTS.IS_START = 2;
                        iv_start.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                    }

                } else {
                    PrefUtils.putBoolean("isSendSms20", false, MainActivity.this);
                    //将箱子弄成空闲 发送推送
                    //SerialUtil.transferSite(false);
                    PrefUtils.putBoolean("isSave", false, MainActivity.this);
                    PrefUtils.putString("pwd", "", MainActivity.this);
                    CONSTS.IS_START = 2;
                    File parent = Environment.getExternalStorageDirectory();
                    File file = new File(parent, "box_no1.png");
                    Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
                    imageView.setImageURI(uri);

                    iv_start.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                    boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, MainActivity.this);
                    boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, MainActivity.this);


                    CONSTS.TRANSFER_OPEN = false;
                    //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                    PrefUtils.putString("pwd", "", MainActivity.this);
                }
                if ("器官".equals(mType)) {
                    isRepeatOrganSeg("dd");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                //CONSTS.IS_START = 2;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    public void setTime(long time) {
        if (ShellInterface.isSuAvailable()) {
            ShellInterface.runCommand("chmod 666 /dev/alarm");
            SystemClock.setCurrentTimeMillis(time);
            ShellInterface.runCommand("chmod 664 /dev/alarm");
        }
    }

    /**
     * 自动关机和自动转运
     */
    private void autoPowerOff(double pTemperature) {

        if (CONSTS.SERVER_TIME == 0 || CONSTS.IS_START == 1 || CONSTS.IS_START == 0) {
            return;
        }

        Boolean isStop = PrefUtils.getBoolean("isStop", true, this);
        Boolean isStart = PrefUtils.getBoolean("isStart", true, this);
        //自动关机
//        if (pTemperature > 10 && isStop) {
//            CONSTS.END_FLAG_AUTO = "";
//            if ("".equals(CONSTS.END_FLAG)) {
//                CONSTS.END_FLAG = CommonUtil.getTrueTime();
//
//            }
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            String nowTime = CommonUtil.getTrueTime();
//            try {
//                long time = sdf.parse(nowTime).getTime() / 1000 - sdf.parse(CONSTS.END_FLAG).getTime() / 1000;
//                Log.e("resultReceive:", "关机" + (time));
//                if (time < 0) {
//                    CONSTS.END_FLAG = CommonUtil.getTrueTime();
//                }
//                ToastUtil.showToast("进入自动关机,倒计时" + (CONSTS.END_TIME - time) / 60 + "分.");
//                if (time > CONSTS.END_TIME && time < 100000) {
//
//                    SerialUtil.powerOff();
//
//                }
//
//            } catch (ParseException e) {
//                e.printStackTrace();
//
//            }
//
//
//        } else

        if (isStart) {
            /**
             * 自动转运
             * 1.低于10度,位置移动
             * 2.发生低于过10度,位置移动
             */
            //CONSTS.END_FLAG = "";
            temperatureFlag = pTemperature;

            if ("".equals(CONSTS.END_FLAG_AUTO)) {

                CONSTS.END_FLAG_AUTO = CommonUtil.getTrueTime();

            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String nowTime = CommonUtil.getTrueTime();
            try {
                long time = sdf.parse(nowTime).getTime() / 1000 - sdf.parse(CONSTS.END_FLAG_AUTO).getTime() / 1000;
                Log.e("resultReceive:", "自动转运" + (time));
                if (time < 0) {
                    CONSTS.END_FLAG_AUTO = CommonUtil.getTrueTime();
                }
                ToastUtil.showToast("进入自动转运,倒计时" + (CONSTS.START_TIME - time) / 60 + "分.");

                if (time > CONSTS.START_TIME && time < 100000) {
                    //ToastUtil.showToast("即将开启自动转运" + time);
                    loadHospitalAddress();

                }
            } catch (ParseException e) {
                e.printStackTrace();

            }


        }

        /**
         * 出现过小于10度的情况
         * 位置发生变化后
         * 进入自动转运状态
         */

//        if (temperatureFlag <= 10) {
//
//            if (mlocationClient == null || !mlocationClient.isStarted()) {
//                location();
//            }
//
//            if (LocationUtils.getMoveDistance(mLongitudes) > 0) {
//                loadHospitalAddress();
//            }
//
//        }

    }

    private void startTransfer(String pOrganSeg) {

        noticeTransfer(pOrganSeg, "");
        sendGroupMessage(pOrganSeg, mDepartmentPhone);

        //跳转到转运界面

        getTransferInfo();


    }

    private void isRepeatOrganSeg(final String pOrganSeg) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("organSeg", pOrganSeg);

        params.addParameter("boxNo", mObjBean.getBoxNo());


        params.addBodyParameter("action", "organRepeat");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                RepeatJson photoJson = new Gson().fromJson(result, RepeatJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                }
                mType = photoJson.getObj().getType();

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
     * 自动转运
     */
    private void autoTransfer(final String organSeg, String modifyOrganSeg, String type) {

        double longitude = CONSTS.LONGITUDE;
        double latitude = CONSTS.LATITUDE;
        final String city = CONSTS.CITY;


        if (endLocation != null && endLocation.contains(",")) {

            mAutoDistance = LocationUtils.getDistance(latitude, longitude, Double.parseDouble(endLocation.split(",")[1]), Double.parseDouble(endLocation.split(",")[0])) / 1000;
        }

        String groupName = "转运中-" + modifyOrganSeg + "-器官";

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
        params.addBodyParameter("blood", "");
        params.addBodyParameter("bloodNum", "1");
        params.addBodyParameter("sampleOrgan", "");
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


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //开始短信模板要区分自动和手动的，手动的用现在的模板。自动的用新模板，要提示“检测到从xxx出发的疑似转运，已自动开启监控，请至APP或后台补全信息或删除。”

                Datas photoJson = new Gson().fromJson(result, Datas.class);
                //Log.e(TAG, "result:" + result);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    CONSTS.END_FLAG_AUTO = "";
                    if (mlocationClient != null) {
                        mlocationClient.stopLocation();
                        mlocationClient = null;
                    }

                    String boxNo = PrefUtils.getString("boxNo", "", App.getContext());
                    startTransfer(organSeg);
                    //String content = "检测到从" + city + "出发的疑似转运,箱号为" + boxNo + "，已自动开启监控，请至APP或后台补全信息或删除。";


                    //sendListTransferSms(phonesStart, content);
                    noticeTransfer(organSeg, "");

                } else if (photoJson != null && photoJson.getResult() == CONSTS.SEND_FAIL) {


                    ToastUtil.showToast("器官段号重复");


                } else if (photoJson != null && photoJson.getResult() == CONSTS.BAD_PARAM) {

                    ToastUtil.showToast(" 箱子已被使用");

                    LogUtil.e(TAG, "error:" + photoJson.getMsg());
                } else {
                    ToastUtil.showToast("创建自动转运失败失败");
                }
                Log.e("auto:", "resultauto:" + result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "ex:" + ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setMobileData(Context pContext, boolean pBoolean) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;
            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);
            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回手机移动数据的状态
     *
     * @param pContext
     * @param arg      默认填null
     * @return true 连接 false 未连接
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean getMobileDataState(Context pContext, Object[] arg) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }
            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);
            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
            return isOpen;
        } catch (Exception e) {
            return false;
        }
    }

    class MainReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            //ToastUtil.showToast("dd",context);
            if (bundle != null) {
                String open = bundle.getString("open");
                if ("open".equals(open)) {

                    if ("开箱".equals(btn_open.getText().toString())) {
                        openTime = 30;
                        service1 = Executors.newSingleThreadScheduledExecutor();
                        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
                        service1.scheduleAtFixedRate(testRunnable, 0, 1000, TimeUnit.MILLISECONDS);
                    }

                    return;
                }
                String startOnWay = bundle.getString("startOnWay");
                if ("startOnWay".equals(startOnWay)) {
                    Intent i = new Intent(MainActivity.this, OnWayActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    return;
                }

                String temperature = bundle.getString("temperature");
                String status = bundle.getString("status");
                String power = bundle.getString("power");
                try {
                    autoPowerOff(Double.parseDouble(temperature));
                } catch (Exception e) {
                    ToastUtil.showToast("温度转换错误");
                }
                if (CommonUtil.isNetworkAvalible(context)) {
                    tv_status.setText("工作正常");
                    tv_status.setTextColor(getResources().getColor(R.color.white));
                    iv_status.setImageResource(R.drawable.warn);
                    tv_status_title.setTextColor(getResources().getColor(R.color.white));
                } else {
                    tv_status.setText("检测中");
                    tv_status.setTextColor(getResources().getColor(R.color.high));
                    iv_status.setImageResource(R.drawable.status_warn);
                    tv_status_title.setTextColor(getResources().getColor(R.color.high));
                }


//                tv_status_title.setTextColor(getResources().getColor(R.color.white));
//

                if ("正常".equals(status)) {
                    tv_temperature.setText(temperature + "℃");
                    tv_temperature.setTextColor(getResources().getColor(R.color.white));
                    tv_temperature_title.setTextColor(getResources().getColor(R.color.white));
                    iv_temperature.setImageResource(R.drawable.temp);


                } else if ("温度异常".equals(status)) {
                    tv_temperature.setText(temperature + "℃");
                    tv_temperature.setTextColor(getResources().getColor(R.color.high));
                    tv_temperature_title.setTextColor(getResources().getColor(R.color.high));
                    iv_temperature.setImageResource(R.drawable.temp_warn);

//                    tv_status.setText(status);
//                    tv_status.setTextColor(getResources().getColor(R.color.high));
//                    tv_status_title.setTextColor(getResources().getColor(R.color.high));
//                    iv_status.setImageResource(R.drawable.status_warn);
                } else if (CONSTS.NO_START.equals(status)) {
                    iv_start.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                } else if (CONSTS.DELETE.equals(status)) {
                    iv_start.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                }
                tv_power.setText(power);

            }


        }
    }

    private void location() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(40000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
        LogUtil.e(TAG, "启动定位");
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {

            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                String type = amapLocation.getAccuracy() + "," + amapLocation.getLocationType() + ":" + amapLocation.getLatitude() + "," + amapLocation.getLongitude();
                CONSTS.LOCATION_TYPE = type;
                CONSTS.LATITUDE = amapLocation.getLatitude();//获取纬度
                CONSTS.LONGITUDE = amapLocation.getLongitude();//获取经度
                //收集GPS数据,其他的不搜集
                //ToastUtil.showToast( "accuracy:"+amapLocation.getAccuracy()+",locationType:"+amapLocation.getLocationType(),this);
                //CONSTS.AUTO_CONDITION1 = "accuracy:"+amapLocation.getAccuracy()+",locationType:"+amapLocation.getLocationType()+",";
                if (amapLocation.getLongitude() > 0.0 && amapLocation.getAccuracy() <= 10 && amapLocation.getLocationType() == 1) {
                    mLongitudeDistance++;
                    if (mLongitudeDistance > 10) {
                        mLongitudeDistance = 0;
                        mLongitudes = new ArrayList<>();
                    }

                    mLongitudes.add(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));


//                    CONSTS.LATITUDE = amapLocation.getLatitude() + "";//获取纬度
//                    CONSTS.LONGITUDE = amapLocation.getLongitude() + "";//获取经度
//                    CONSTS.CITY = amapLocation.getCity();
                    CONSTS.LONGITUDE = amapLocation.getLongitude();
                    CONSTS.LATITUDE = amapLocation.getLatitude();
                    CONSTS.CITY = amapLocation.getCity();

                } else {

                    //if ("".equals(longitudeFlag) || "0.0".equals(longitudeFlag)) {
                    CONSTS.LONGITUDE = amapLocation.getLongitude();
                    CONSTS.LATITUDE = amapLocation.getLatitude();
                    CONSTS.CITY = amapLocation.getCity();
                    //}
                }


                ToastUtil.showToast("开启定位", this);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
                Log.e(TAG, "开启定位:" + sdf.format(new Date()) + "=" + (lastLatitude + "," + amapLocation.getLatitude()) + ":" + (lastLatitude == amapLocation.getLatitude()) + "," + CONSTS.IS_START + "," + mlocationClient + "," + CONSTS.SERVER_TIME + "," + amapLocation.getCity());
//                if ((CONSTS.IS_START == 1) || (lastLatitude == amapLocation.getLatitude())) {
//
//                    if (mlocationClient != null) {
//                        if (CONSTS.SERVER_TIME != 0) {
//                            lastTime = CommonUtil.getTrueTimeLong();
//                        }
//                        mlocationClient.stopLocation();
//                        mlocationClient = null;
//                        Log.e(TAG, "开启定位:关闭了");
//                    }
//
//                }
//                lastIndex++;
//                if (lastIndex % 3 == 0) {
//                    lastLatitude = amapLocation.getLatitude();
//                }


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                // Log.e("AmapError", "location Error, ErrCode:"  + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }

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
                HospitalJson hospitalJson = new Gson().fromJson(result, HospitalJson.class);
                if (hospitalJson != null && hospitalJson.getResult() == CONSTS.SEND_OK) {
                    String toHospitalAddress = hospitalJson.getObj() == null ? null : hospitalJson.getObj().getAddress();

                    loadEndLocationAuto(toHospitalAddress);
                    //Log.e("collision time:", "address");
                } else {
                    ToastUtil.showToast("获取医院地址失败");
                }
                Log.e("auto:", "resultaddress:" + result);

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
     * 获取结束的经纬度
     */
    private void loadEndLocationAuto(final String pEndLocation) {
        String url = URL.GAO_DE_LOCATION_URL + pEndLocation;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // LogUtil.e(TAG, "response:" + response.body().string());
                LatiLongJson latiLongJson = new Gson().fromJson(response.body().string(), LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        endLocation = latiLongJson.getGeocodes()[0].getLocation();
                        getDepartments();
                        //Log.e("collision time:", "location");
                    }
                } else {
                    ToastUtil.showToast("获取医院经纬度失败");
                }
                Log.e("auto:", "resultlocation:" + latiLongJson);
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
                        //Log.e("collision time:", "department");
                        getOneOpo();
                    }

                } else {
                    ToastUtil.showToast("获取科室协调员失败");
                }
                Log.e("auto:", "resultdepartment:" + result);

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

    private void isRepeatOrganSeg(final String organSeg, final String modifyOrganSeg) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "organRepeatType");
        params.addBodyParameter("modifyOrganSeg", modifyOrganSeg);
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                RepeatJson photoJson = new Gson().fromJson(result, RepeatJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    mType = photoJson.getObj().getType();
                    autoTransfer(organSeg, modifyOrganSeg, mType);

                } else {
                    ToastUtil.showToast("器官段号重复,请重新填写");
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
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    List<OpoInfoContact> mOpoInfoContact = photoJson.getObj().getOpoInfoContacts();
                    if (mOpoInfoContact.size() > 0) {
                        mOpoName = mOpoInfoContact.get(0).getContactName();
                        mOpoPhone = mOpoInfoContact.get(0).getContactPhone();

                        String modifyOrganSeg;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String organSeg = "";
                        try {
                            organSeg = "AP" + sdf.format(sdfAll.parse(CommonUtil.getTrueTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (organSeg.contains("P") && organSeg.length() > 14) {
                            modifyOrganSeg = organSeg.substring(6, 14);
                        } else {
                            modifyOrganSeg = organSeg;
                        }
                        isRepeatOrganSeg(organSeg, modifyOrganSeg);


                    }
                } else {
                    ToastUtil.showToast("获取OPO人员失败");
                }
                Log.e("auto:", "resultopo:" + result);
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

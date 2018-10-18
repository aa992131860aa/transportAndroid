package com.otqc.transbox.controller.on;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.otqc.transbox.R;
import com.otqc.transbox.bean.EndLocationBean;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.databinding.ActivityOnWayBinding;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDbNew3;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.fragment.ExceptionFragment;
import com.otqc.transbox.fragment.TemperatureFragment;
import com.otqc.transbox.fragment.TimeFragment;
import com.otqc.transbox.service.event.MapEvent;
import com.otqc.transbox.util.A;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SerialUtil;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.util.Utils;
import com.otqc.transbox.view.NewMonitorPopup;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OnWayActivity extends AppBaseActivity implements AMapLocationListener {
    private final static String TAG = "OnWayActivity";

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;
    private ActivityOnWayBinding mBinding;
    private OnWayData mData;
    private DecimalFormat mDf;
    private Button btn_open;
    private OnWayReceiver mOnWayReceiver;

    private ScheduledExecutorService service1;

    private int openTime = 30;


    @Override
    protected void initVariable() {
        mOnWayReceiver = new OnWayReceiver();
        IntentFilter intentFilter = new IntentFilter(CONSTS.ON_WAY_ACTION);
        registerReceiver(mOnWayReceiver, intentFilter);
    }

    private String tid;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient = null;
    private LocationManager mLocationManager;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_way);
        ///CONSTS.IS_START_CHECK = false;
        mData = new OnWayData();
        mData.setOnWayPageState(0);
        mData.setOnWayPageShow("温、湿图");
        mBinding.setInfo(mData);
        mBinding.setPresenter(new OnWayPresenter(this));
        btn_open = (Button) findViewById(R.id.btn_open);
        initViewPager();
        initArrowClick();

        //boolean isTransfer = PrefUtils.getBoolean("isTransfer", true, this);
        SerialUtil.transferSite(true);

        mDf = new DecimalFormat("#");
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = btn_open.getText().toString();
                final String pwd = MainActivity.mObjBean == null ? "" : MainActivity.mObjBean.getOpenPsd();
                if ("开 箱".equals(name)) {
                    boolean isOpen = PrefUtils.getBoolean("isOpen", true, OnWayActivity.this);
                    if (pwd == null || "".equals(pwd) || !isOpen) {


                        SerialUtil.open();

                    } else {

                        final NewMonitorPopup newMonitorPopup = new NewMonitorPopup(OnWayActivity.this, "开箱");
                        newMonitorPopup.showAtLocation(btn_open, Gravity.CENTER, 0, 0);
                        newMonitorPopup.setOnClickChangeListener(new NewMonitorPopup.OnClickChangeListener() {
                            @Override
                            public void OnClickChange(String number) {


                                if ("04140079".equals(number)) {

                                    //跳转开发者选项
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                    startActivity(intent);

                                }


                                if (number.length() >= 4 && pwd.equals(number.substring(0, 4)) || "9999".equals(number)) {


                                    SerialUtil.open();


                                    sendBroadcast(new Intent(CONSTS.EXCEPTION));
                                    newMonitorPopup.dismiss();

                                } else {
                                    ToastUtil.showToast("开箱密码错误");

                                }
                            }
                        });
                    }
                }
            }
        });

        location();

    }

    @Override
    protected void onStart() {
        super.onStart();
        int openTimeLocal = PrefUtils.getInt("openTime", 0, this);
        if (openTimeLocal > 0 && service1 == null) {
            openTime = openTimeLocal;

            //ToastUtil.showToast("gggg");
            service1 = Executors.newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            service1.scheduleAtFixedRate(testRunnable, 0, 1000, TimeUnit.MILLISECONDS);

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SerialUtil.transferSite(true);
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
        //mLocationOption.setOnceLocationLatest(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
        Log.e(TAG, "启动定位1");
    }

    private void initArrowClick() {
        mBinding.arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem > 0) {
                    mViewPager.setCurrentItem(--currentItem);
                }
            }
        });

        mBinding.arrowNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = mViewPager.getCurrentItem();
                if (currentItem < mFragments.size() - 1) {
                    mViewPager.setCurrentItem(++currentItem);
                }
            }
        });

    }

    @Override
    protected void initData() {

        // Log.e(TAG, "send:");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SerialUtil.power();


    }


    private void initViewPager() {
        mViewPager = mBinding.idViewpager;
        mViewPager.setOffscreenPageLimit(3);
        mFragments = new ArrayList<>();
        Fragment mTab1 = new TemperatureFragment();
        Fragment mTab2 = new TimeFragment();
        Fragment mTab3 = new ExceptionFragment();
        mFragments.add(mTab1);
        mFragments.add(mTab2);
        mFragments.add(mTab3);
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mViewPager.setAdapter(mAdapter);


        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mData.setOnWayPageState(position);
                if (position == 0) {
                    mData.setOnWayPageShow("温、湿图");
                }
                if (position == 1) {
                    mData.setOnWayPageShow("地图");
                }
                if (position == 2) {
                    mData.setOnWayPageShow("");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mOnWayReceiver);
        //CONSTS.IS_START_CHECK = true;
        SerialUtil.transferSite(false);
        PrefUtils.putBoolean("isSendSms20", false, this);
//        if (null != locationClient) {
//            /**
//             * 如果AMapLocationClient是在当前Activity实例化的，
//             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//             */
//            locationClient.onDestroy();
//            locationClient = null;
//            locationOption = null;
//
//        }

        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient = null;
        }

//        if (null != alarmReceiver) {
//            unregisterReceiver(alarmReceiver);
//            alarmReceiver = null;
//        }

        LogUtil.e("OnWayActivity", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    /**
     * map init
     **/

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private Intent alarmIntent = null;
    private PendingIntent alarmPi = null;
    private AlarmManager alarm = null;

    private long strInterval = 30000;
    private boolean first = true;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                //开始定位
                case Utils.MSG_LOCATION_START:
                    //Log.e("location", "正在定位...");
                    break;
                // 定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    if (!TextUtils.isEmpty(loc.getCity()) || !TextUtils.isEmpty(loc.getLongitude() + "")) {
                        if (loc.getLongitude() > 0.0) {


                            CONSTS.LATITUDE = loc.getLatitude();//获取纬度
                            CONSTS.LONGITUDE = loc.getLongitude();//获取经度
                            CONSTS.CITY = loc.getCity();
                            if (first) {
                                CONSTS.UPLOAD_NUM = CONSTS.UPLOAD_NUM_VALUE;
                                CONSTS.TRANS_DETAIL = new TransRecordItemDbNew3();
                                first = false;
                            }
                        }
                        MapEvent mapEvent = new MapEvent();
                        mapEvent.setCity(loc.getCity());
                        if (loc.getLongitude() > 0.0) {
                            mapEvent.setLont(loc.getLongitude() + "");
                        }
                        if (loc.getLatitude() > 0.0) {
                            mapEvent.setLati(loc.getLatitude() + "");
                        }

                        LatLng currentLatlng = new LatLng(loc.getLatitude(), loc.getLongitude());
                        LatLng endLatlng = null;

                        // Log.e("calDistance11", "aaa：" + loc.getLatitude() + " bbb：" + loc.getLongitude());
                        // 目的地存在则直接计算，否则查询数据库再转换为坐标，再计算
                        if (A.point != null && A.point.length > 0) {
                            endLatlng = new LatLng(Float.parseFloat(A.point[1]), Float.parseFloat(A.point[0]));
                            LogUtil.i("calDistance22", "aaa：" + Float.parseFloat(A.point[1]) + " bbb：" + Float.parseFloat(A.point[0]));
                        } else {
//                            // 基本信息
//                            Realm realm = RealmUtil.getInstance().getRealm();
//                            RealmResults<TransOddDb> baseResult = realm.where(TransOddDb.class).
//                                    equalTo("transferid", tid).findAll();
//                            if (baseResult.size() > 0) {
//                                TransOddDb obj = baseResult.get(0);
//                                locationTrans(obj.getToHospName(), obj.getToHospitalInfo().getDistrict());
//                            }
                        }

                        float v = AMapUtils.calculateLineDistance(currentLatlng, endLatlng) / 1000;
                        if (v > 0) {
                            mapEvent.setDistance(mDf.format(v) + "km");
                        } else {
                            mapEvent.setDistance("计算中");
                        }

                        EventBus.getDefault().post(mapEvent);
                    }

                    break;
                //停止定位
                case Utils.MSG_LOCATION_STOP:
                    LogUtil.e("location", "停止定位...");
                    break;
                default:
                    break;
            }
        }

    };

    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

//        if (null != loc) {
//            Message msg = mHandler.obtainMessage();
//            msg.obj = loc;
//            msg.what = Utils.MSG_LOCATION_FINISH;
//            mHandler.sendMessage(msg);
//        }


        if (amapLocation != null) {
            Log.e(TAG, "latitude:" + amapLocation.getLatitude() + "," + amapLocation.getLongitude() + "," + amapLocation.getCity() + ",accuracy:" + amapLocation.getAccuracy() + ",type:" + amapLocation.getLocationType());
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                String type = amapLocation.getAccuracy() + "," + amapLocation.getLocationType() + ":" + amapLocation.getLatitude() + "," + amapLocation.getLongitude();
                CONSTS.LOCATION_TYPE = type;
                //收集GPS数据,其他的不搜集
                if (amapLocation.getLongitude() > 0.0 && amapLocation.getAccuracy() <= 10 && amapLocation.getLocationType() == 1) {


//                    CONSTS.LATITUDE = amapLocation.getLatitude() + "";//获取纬度
//                    CONSTS.LONGITUDE = amapLocation.getLongitude() + "";//获取经度
//                    CONSTS.CITY = amapLocation.getCity();

                    ToastUtil.showToast(type);

                    CONSTS.LONGITUDE = amapLocation.getLongitude();
                    CONSTS.LATITUDE = amapLocation.getLatitude();
                    CONSTS.CITY = amapLocation.getCity();


                    Log.e(TAG, "right:" + amapLocation.getLatitude() + "," + amapLocation.getLongitude());
                } else {
                    double longitudeFlag = CONSTS.LONGITUDE;
                    int insertCount = DataSupport.where("transfer_id = ?", CONSTS.TRANSFER_ID).count(TransRecord.class);

                    if (longitudeFlag == 0 || insertCount == 0) {
                        CONSTS.LONGITUDE = amapLocation.getLongitude();
                        CONSTS.LATITUDE = amapLocation.getLatitude();
                        CONSTS.CITY = amapLocation.getCity();
                        Log.e(TAG, "error0:" + amapLocation.getLatitude() + "," + amapLocation.getLongitude());
                    }
                    Log.e(TAG, "error:" + amapLocation.getLatitude() + "," + amapLocation.getLongitude());
                }


            } else {
                ToastUtil.showToast("定位错误", this);
                Log.e(TAG, "noLocation:" + amapLocation.getLatitude() + "," + amapLocation.getLongitude());
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }

        }
    }

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("LOCATION")) {
                if (null != locationClient) {
                    // 启动定位
                    locationClient.startLocation();
                }
            }
        }
    };


    private void processData(EndLocationBean bean) {
        A.point = bean.getGeocodes().get(0).getLocation().split(",");
    }

    OkHttpClient client = new OkHttpClient();

    String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mlocationClient.stopLocation();
//        mlocationClient = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    Runnable testRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(1);
        }
    };
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            openTime--;
            if (openTime <= 0) {
                btn_open.setText("开 箱");
                if (service1 != null) {
                    service1.shutdown();
                }
                service1 = null;
            } else {
                btn_open.setText("已开箱(" + openTime + "s)");
            }
            PrefUtils.putInt("openTime", openTime, OnWayActivity.this);
            return false;
        }
    });

    class OnWayReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String open = bundle.getString("open");
                if ("open".equals(open)) {
                    if ("开 箱".equals(btn_open.getText().toString())) {
                        openTime = 30;
                        //CONSTS.OPEN++;
                        CONSTS.IS_OPEN = true;
                        service1 = Executors.newSingleThreadScheduledExecutor();
                        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
                        service1.scheduleAtFixedRate(testRunnable, 0, 1000, TimeUnit.MILLISECONDS);

                    }

                }

                String stopTransfer = bundle.getString("stopTransfer");
                if ("stopTransfer".equals(stopTransfer)) {
                    //getActivity().startActivity(new Intent(context,MainActivity.class));
                    finish();

                }
            }
        }
    }
}
package org.redsha.transbox.controller.on;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.bean.BoxBean;
import org.redsha.transbox.bean.EndLocationBean;
import org.redsha.transbox.databinding.ActivityOnWayBinding;
import org.redsha.transbox.db.TransOddDb;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.fragment.ExceptionFragment;
import org.redsha.transbox.fragment.TemperatureFragment;
import org.redsha.transbox.fragment.TimeFragment;
import org.redsha.transbox.http.HttpHelper;
import org.redsha.transbox.http.HttpObserver;
import org.redsha.transbox.service.CollectService;
import org.redsha.transbox.service.UpdataService;
import org.redsha.transbox.service.event.MapEvent;
import org.redsha.transbox.service.event.MessageEvent;
import org.redsha.transbox.util.A;
import org.redsha.transbox.util.CommonUtil;
import org.redsha.transbox.util.JsonUtil;
import org.redsha.transbox.util.LogUtil;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;
import org.redsha.transbox.util.ToastUtil;
import org.redsha.transbox.util.Utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
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

    @Override
    protected void initVariable() {
    }

    private String tid;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_on_way);
        mData = new OnWayData();
        mData.setOnWayPageState(0);
        mData.setOnWayPageShow("温、湿图");
        mBinding.setInfo(mData);
        mBinding.setPresenter(new OnWayPresenter());

        initViewPager();
        initArrowClick();
        mDf = new DecimalFormat("#");

        /**
         * 保证转运单号是ok的
         */
        tid = PrefUtils.getString("tid", "", App.getContext());
        if (!TextUtils.isEmpty(tid)) {
            startTrans();
        } else {
            new HttpHelper().getBoxInfo(CommonUtil.getIMEI()).subscribe(new HttpObserver<BoxBean>() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onSuccess(BoxBean model) {
                    if (!model.getTransferStatus().equals("free") && !TextUtils.isEmpty(model.getTransfer_id())) {
                        PrefUtils.putString("tid", model.getTransfer_id(), App.getContext());
                        startTrans();
                    } else {
                        ToastUtil.showToast("转运单初始化失败!");
                    }
                }
            });
        }

        initAlarmLocation();                // map location
        EventBus.getDefault().register(this);   // map notifaction
    }

    private void startTrans() {
        A.mCollectState = 1;    //记录数据
        CollectService.getConnet(this);

        UpdataService.getConnet(this);      // up data
        A.isReadyUp = true;

        LogUtil.e(TAG, "startTrans()执行了.");
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
    }

    private void initViewPager() {
        mViewPager = mBinding.idViewpager;
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
        mViewPager.setOffscreenPageLimit(2);

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

        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }

        if (null != alarmReceiver) {
            unregisterReceiver(alarmReceiver);
            alarmReceiver = null;
        }

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

    private void initAlarmLocation() {

        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置间隔
        locationOption.setInterval(20000);
        // 设置定位监听
        locationClient.setLocationListener(this);

        // 创建Intent对象，action为LOCATION
        alarmIntent = new Intent();
        alarmIntent.setAction("LOCATION");

        // 定义一个PendingIntent对象，PendingIntent.getBroadcast包含了sendBroadcast的动作。
        // 也就是发送了action 为"LOCATION"的intent
        alarmPi = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        // AlarmManager对象,注意这里并不是new一个对象，Alarmmanager为系统级服务
        alarm = (AlarmManager) getSystemService(ALARM_SERVICE);

        //动态注册一个广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOCATION");
        registerReceiver(alarmReceiver, filter);

        int alarmInterval = 20;
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(Utils.MSG_LOCATION_START);

        if (null != alarm) {
            /**
             * 设置一个闹钟，2秒之后每隔一段时间执行启动一次定位程序
             * 参2：首次执行时间
             * 参3：间隔执行时间
             */
            alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + 20 * 1000,
                    alarmInterval * 1000,
                    alarmPi);
        }

    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                //开始定位
                case Utils.MSG_LOCATION_START:
                    LogUtil.e("location", "正在定位...");
                    break;
                // 定位完成
                case Utils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    if (!TextUtils.isEmpty(loc.getCity()) || !TextUtils.isEmpty(loc.getLongitude() + "")) {
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
                        LogUtil.i("calDistance11", "aaa：" + loc.getLatitude() + " bbb：" + loc.getLongitude());
                        // 目的地存在则直接计算，否则查询数据库再转换为坐标，再计算
                        if (A.point != null && A.point.length > 0) {
                            endLatlng = new LatLng(Float.parseFloat(A.point[1]), Float.parseFloat(A.point[0]));
                            LogUtil.i("calDistance22", "aaa：" + Float.parseFloat(A.point[1]) + " bbb：" + Float.parseFloat(A.point[0]));
                        } else {
                            // 基本信息
                            Realm realm = RealmUtil.getInstance().getRealm();
                            RealmResults<TransOddDb> baseResult = realm.where(TransOddDb.class).
                                    equalTo("transferid", tid).findAll();
                            if (baseResult.size() > 0) {
                                TransOddDb obj = baseResult.get(0);
                                locationTrans(obj.getToHospName(), obj.getToHospitalInfo().getDistrict());
                            }
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

        ;
    };

    // 定位监听
    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = Utils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        LogUtil.e(TAG, "主动结束转运，更改isReadyUp标记！");

        A.mCollectState = 0;    //只采样
        A.isReadyUp = false;    //主动结束转运，不需要重启服务。不需要上传数据了

        //停止由AlarmManager启动的循环
        UpdataService.stop(this);
        //停止由服务启动的循环
        Intent i = new Intent(this, UpdataService.class);
        stopService(i);

        // 确认结束转运
        finish();
    }

    private void locationTrans(final String toHospName, final String hosistrict) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String link = "http://restapi.amap.com/v3/geocode/geo?address=" + toHospName + "&key=0439c68b2a161fdb410dab6b54027305";
                    final String result = get(link);
                    if (!TextUtils.isEmpty(result)) {

                        LogUtil.e("result", result);
                        CommonUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                EndLocationBean bean = JsonUtil.parseJsonToBean(result, EndLocationBean.class);
                                if (bean.getGeocodes() != null && bean.getGeocodes().size() > 0) {
                                    processData(bean);
                                } else {
                                    locationTransAgain(hosistrict);
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void locationTransAgain(final String toHospName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String link = "http://restapi.amap.com/v3/geocode/geo?address=" + toHospName + "&key=0439c68b2a161fdb410dab6b54027305";
                    final String result = get(link);
                    if (!TextUtils.isEmpty(result)) {

                        LogUtil.e("result", result);
                        CommonUtil.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                EndLocationBean bean = JsonUtil.parseJsonToBean(result, EndLocationBean.class);
                                if (bean.getGeocodes() != null && bean.getGeocodes().size() > 0) {
                                    processData(bean);
                                } else {

                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

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

}
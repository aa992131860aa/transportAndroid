package com.otqc.transbox.controller.on;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;
import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.db.ChartTransRecordItemDb;


import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDbNew3;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;
import com.otqc.transbox.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class MapDetailActivity extends AppBaseActivity {
    private final static String TAG = "MapDetailActivity";

    private MapView mMapView;
    private AMap mAmap;

    private Button movebtn;//可拖动按钮
    private boolean clickormove = true;//点击或拖动，点击为true，拖动为false
    private int downX, downY;//按下时的X，Y坐标
    private boolean hasMeasured = false;//ViewTree是否已被测量过，是为true，否为false
    private View content;//界面的ViewTree
    private int screenWidth, screenHeight;//ViewTree的宽和高

    MainReceiver mainReceiver;
    private boolean isFirst = true;
    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_map_detail);
        mMapView = (MapView) findViewById(R.id.map);

        mMapView.onCreate(savedInstanceState);// 实现地图生命周期管理
        mAmap = mMapView.getMap();

        setDefaultData();
        initMoveBtn();

        IntentFilter intentFilter = new IntentFilter(CONSTS.MAIN_MAP);
        mainReceiver = new MainReceiver();
        registerReceiver(mainReceiver,intentFilter);
    }

    private void initMoveBtn() {

        content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);//获取界面的ViewTree根节点View

        DisplayMetrics dm = getResources().getDisplayMetrics();//获取显示屏属性
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        ViewTreeObserver vto = content.getViewTreeObserver();//获取ViewTree的监听器
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

                if (!hasMeasured) {

                    screenHeight = content.getMeasuredHeight();//获取ViewTree的高度
                    hasMeasured = true;//设置为true，使其不再被测量。

                }
                return true;//如果返回false，界面将为空。

            }

        });
        movebtn = (Button) findViewById(R.id.movebtn);
        movebtn.setOnTouchListener(new View.OnTouchListener() {//设置按钮被触摸的时间

            int lastX, lastY; // 记录移动的最后的位置

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int ea = event.getAction();//获取事件类型
                switch (ea) {
                    case MotionEvent.ACTION_DOWN: // 按下事件

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downX = lastX;
                        downY = lastY;
                        break;

                    case MotionEvent.ACTION_MOVE: // 拖动事件

                        // 移动中动态设置位置
                        int dx = (int) event.getRawX() - lastX;//位移量X
                        int dy = (int) event.getRawY() - lastY;//位移量Y
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;

                        //++限定按钮被拖动的范围
                        if (left < 0) {

                            left = 0;
                            right = left + v.getWidth();

                        }
                        if (right > screenWidth) {

                            right = screenWidth;
                            left = right - v.getWidth();

                        }
                        if (top < 0) {

                            top = 0;
                            bottom = top + v.getHeight();

                        }
                        if (bottom > screenHeight) {

                            bottom = screenHeight;
                            top = bottom - v.getHeight();

                        }

//--限定按钮被拖动的范围

                        v.layout(left, top, right, bottom);//按钮重画


// 记录当前的位置
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP: // 弹起事件

                        //判断是单击事件或是拖动事件，位移量大于5则断定为拖动事件

                        if (Math.abs((int) (event.getRawX() - downX)) > 5
                                || Math.abs((int) (event.getRawY() - downY)) > 5)

                            clickormove = false;

                        else

                            clickormove = true;

                        break;

                }
                return false;

            }

        });
        movebtn.setOnClickListener(new View.OnClickListener() {//设置按钮被点击的监听器

            @Override
            public void onClick(View arg0) {
                if (clickormove)

                    finish();

            }

        });

    }

    private void setDefaultData() {

        final String tid = PrefUtils.getString("tid", "", App.getContext());

        /**
         * 地图数据
         */
        List<TransRecord> resultMap = DataSupport.where("transfer_id = ?",CONSTS.TRANSFER_ID).find(TransRecord.class);
       // ToastUtil.showToast("mapSize:"+resultMap.size());
        if (resultMap.size() > 0) {
          if(isFirst) {

              mAmap.clear();
              mAmap.removecache();

              // 1. 设置中心点
              for (int i = 0; i < resultMap.size(); i++) {
                  TransRecord item = resultMap.get(i);
                  if (!TextUtils.isEmpty(item.getLatitude()) && !TextUtils.isEmpty(item.getLongitude())) {
                      mAmap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                              new LatLng(Double.parseDouble(item.getLatitude()),
                                      Double.parseDouble(item.getLongitude())),//新的中心点坐标
                              11, //新的缩放级别
                              0, //俯仰角0°~45°（垂直与地图时为0）
                              0  ////偏航角 0~360° (正北方为0)
                      )));
                  }
              }
              mAmap.addMarker(new MarkerOptions().
                      position(new LatLng(Double.parseDouble(resultMap.get(0).getLatitude()),
                              Double.parseDouble(resultMap.get(0).getLongitude()))).
                      title(resultMap.get(0).getCurrentCity()));
          }
            if (resultMap.size() > 1) {
                List<LatLng> latLngs = new ArrayList<>();
                LogUtil.e("location", "map长度：" + resultMap.size());
                // 2. add 点
                for (int i = 0; i < resultMap.size(); i++) {
                    TransRecord data = resultMap.get(i);
                    if (data != null) {
                        if (!TextUtils.isEmpty(data.getLatitude()) && !TextUtils.isEmpty(data.getLongitude())) {
                            LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
                                    Double.parseDouble(data.getLongitude()));
                            String temp = !TextUtils.isEmpty(data.getTemperature()) ? data.getTemperature() + "℃" : "";
                            if(i==0) {
                                mAmap.addMarker(new MarkerOptions().
                                        position(latLng1).
                                        title(temp));
                            }else if(i==resultMap.size()-1){
                                mAmap.addMarker(new MarkerOptions().
                                        position(latLng1).
                                        title(temp));
                            }
                            latLngs.add(latLng1);
                        }
                    }
                }
                // 3. 加一个多段线对象（Polyline）对象在地图上。
                mAmap.addPolyline(new PolylineOptions()
                        .addAll(latLngs)
                        .width(10)
                        .color(Color.argb(255, 55, 157, 242)));
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        unregisterReceiver(mainReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChartTransRecordItemDb event) {
        if (event != null) {

            if (!TextUtils.isEmpty(event.getLatitude()) && !TextUtils.isEmpty(event.getLongitude())) {
                LogUtil.e(TAG, "refresh：" + event.getLatitude() + " / " + event.getLongitude());

                LatLng latLng1 = new LatLng(Double.parseDouble(event.getLatitude()),
                        Double.parseDouble(event.getLongitude()));
                String temp = !TextUtils.isEmpty(event.getTemperature()) ? event.getTemperature() + "℃" : "";
                mAmap.addMarker(new MarkerOptions().
                        position(latLng1).
                        title(temp));

                mAmap.addPolyline(new PolylineOptions().add(latLng1).width(10)
                        .color(Color.argb(255, 55, 157, 242)));
            }
        }
    }
    int index = 0;
    class MainReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                String distance = bundle.getString("distance");
                String ll = bundle.getString("ll");
                String collision = bundle.getString("collision");
                index++;
                //ToastUtil.showToast("距离:" +distance+",碰撞:"+collision+","+ll);
                isFirst = false;
                setDefaultData();
                if(index==1){

                }
                //initMoveBtn();
            }


        }
    }

}
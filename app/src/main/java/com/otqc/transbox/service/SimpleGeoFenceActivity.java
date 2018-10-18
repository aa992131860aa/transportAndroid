package com.otqc.transbox.service;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;


public class SimpleGeoFenceActivity extends AppCompatActivity implements AMapLocationListener, LocationSource {
    final String tag = SimpleGeoFenceActivity.class.getSimpleName();
    final int REQ_LOCATION = 0x12;
    final int REQ_GEO_FENCE = 0x13;
    final String ACTION_GEO_FENCE = "geo fence action";
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private IntentFilter intentFilter;
    private Vibrator vibrator;
    private LatLng centerLatLng;
    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener onLocationChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = new MapView(this);
        setContentView(mapView);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mLocationClient = new AMapLocationClient(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        applyPermission();
        //处理进出地理围栏事件
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GEO_FENCE);
        //show my location
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver
            broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播
            if (intent.getAction().equals(ACTION_GEO_FENCE)) {
                Bundle bundle = intent.getExtras();
                // 根据广播的event来确定是在区域内还是在区域外
                int status = bundle.getInt("event");
                String geoFenceId = bundle.getString("fenceId");
                if (status == 1) {
                    Toast.makeText(SimpleGeoFenceActivity.this, "进入地理围栏~", Toast.LENGTH_LONG).show();
                    vibrator.vibrate(3000);
                } else if (status == 2) {
                    // 离开围栏区域
                    Toast.makeText(SimpleGeoFenceActivity.this, "离开地理围栏~", Toast.LENGTH_LONG).show();
                    vibrator.vibrate(3000);
                }
            }
        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(broadcastReceiver);
    }

    public void applyPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationClient.startLocation();
            } else {
                Toast.makeText(SimpleGeoFenceActivity.this, "没有权限，无法获取位置信息~", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (loc != null && loc.getErrorCode() == 0) {
            //设置地理围栏
            if (centerLatLng == null) {
                centerLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                Intent intent = new Intent(ACTION_GEO_FENCE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQ_GEO_FENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //100:是围栏半径（测试发现，设置的太小，不会发出广播）；-1：是超时时间（单位：ms，-1代表永不超时）
                mLocationClient.addGeoFenceAlert("fenceId", centerLatLng.latitude, centerLatLng.longitude, 100, -1, pendingIntent);
                addCircle(centerLatLng,100);
            } else {
                double latitude = loc.getLatitude();
                double longitude = loc.getLongitude();
                Log.d(tag, "当前经纬度: " + latitude + "," + longitude);
                LatLng endLatlng = new LatLng(loc.getLatitude(), loc.getLongitude());
// 计算量坐标点距离
                double distances = AMapUtils.calculateLineDistance(centerLatLng, endLatlng);
                Toast.makeText(SimpleGeoFenceActivity.this, "当前距离中心点：" + ((int) distances), Toast.LENGTH_LONG).show();
                if (onLocationChangedListener != null) {
                    onLocationChangedListener.onLocationChanged(loc);
                }
            }
        }
    }
    public void addCircle(LatLng latLng,int radius){
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeWidth(4);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(Color.BLUE);
        aMap.addCircle(circleOptions);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }

    }
}
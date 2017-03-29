package org.redsha.transbox.controller.mixture;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.google.gson.Gson;

import org.redsha.transbox.R;
import org.redsha.transbox.databinding.ActivityItemDetailBinding;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.http.request.TransRecordItemRequest;
import org.redsha.transbox.util.CommonUtil;
import org.redsha.transbox.util.JsonUtil;
import org.redsha.transbox.util.LogUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 历史运单详情
 */
public class ItemDetailActivity extends AppBaseActivity {

    private ItemDetailData mData;
    private WebView webView, mWv;
    private TransRecordItemRequest[] request;
    private MapView mMapView;
    private AMap mAmap;

    @Override
    protected void initVariable() {
        String data = getIntent().getStringExtra("data");
        if (!TextUtils.isEmpty(data)) {
            mData = JsonUtil.parseJsonToBean(data, ItemDetailData.class);
            if (mData.getRecords() != null && mData.getRecords().size() > 0) {

                // 拼接数组
                request = new TransRecordItemRequest[mData.getRecords().size()];
                for (int i = 0; i < mData.getRecords().size(); i++) {
                    TransRecordItemRequest record = new TransRecordItemRequest();
                    if (mData.getRecords().get(i) != null) {
                        try {
                            record.setTransferRecordid(mData.getRecords().get(i).getTransferRecordid());

                            record.setTemperature(mData.getRecords().get(i).getTemperature());
                            record.setAvgTemperature(mData.getRecords().get(i).getAvgTemperature());
                            record.setPower(mData.getRecords().get(i).getPower());
                            record.setExpendPower(mData.getRecords().get(i).getExpendPower());
                            record.setHumidity(mData.getRecords().get(i).getHumidity());

                            record.setDuration(mData.getRecords().get(i).getDuration());
                            record.setCurrentCity(mData.getRecords().get(i).getCurrentCity());
                            record.setLongitude(mData.getRecords().get(i).getLongitude());
                            record.setLatitude(mData.getRecords().get(i).getLatitude());
                            record.setDistance(mData.getRecords().get(i).getDistance());

                            record.setTransfer_id(mData.getRecords().get(i).getTransfer_id());

                            record.setRecordAt(CommonUtil.dateToStamp(mData.getRecords().get(i).getRecordAt()));
//                            LogUtil.e(TAG, CommonUtil.dateToStamp(mData.getRecords().get(i).getRecordAt()));

                            record.setType(mData.getRecords().get(i).getType());
                            record.setRemark(mData.getRecords().get(i).getRemark());
                            request[i] = record;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActivityItemDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_item_detail);
        binding.setInfo(mData);
        binding.setPresenter(new ItemDetailPresenter(this, mData));

        // morris
        webView = binding.webView;
        mWv = binding.wv;

        webView.setVerticalScrollbarOverlay(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/morris.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                initWebViewData();  // 主动刷新
            }
        });

        mWv.setVerticalScrollbarOverlay(true);
        mWv.getSettings().setJavaScriptEnabled(true);
        mWv.loadUrl("file:///android_asset/morrisHum.html");
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                initWebViewData();  // 主动刷新
            }
        });
        // map
        mMapView = binding.idFinishMap;
        mMapView.onCreate(savedInstanceState);// 实现地图生命周期管理
        mAmap = mMapView.getMap();
    }

    @Override
    protected void initData() {
        setDefaultData();
    }

    private void initWebViewData() {
        if (request != null && request.length > 0) {
            String jsData = new Gson().toJson(request);
            if (!TextUtils.isEmpty(jsData)) {
                webView.loadUrl("javascript:set('" + jsData + "')");
                mWv.loadUrl("javascript:set('" + jsData + "')");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void setDefaultData() {
        if (request != null && request.length > 0) {
//            TransRecordItemRequest firstData = request[0];
//            // 1. 设置中心点
//            mAmap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                    new LatLng(Double.parseDouble(firstData.getLatitude()),
//                            Double.parseDouble(firstData.getLongitude())),//新的中心点坐标
//                    11, //新的缩放级别
//                    0, //俯仰角0°~45°（垂直与地图时为0）
//                    0  ////偏航角 0~360° (正北方为0)
//            )));
//            List<LatLng> latLngs = new ArrayList<>();
//            for (TransRecordItemRequest data : request) {
//                if (data != null) {
//                    if (!TextUtils.isEmpty(data.getLatitude()) && !TextUtils.isEmpty(data.getLongitude())) {
//                        // 2. 默认点
//                        LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
//                                Double.parseDouble(data.getLongitude()));
//                        // 3. mk点
//                        final Marker marker1 = mAmap.addMarker(new MarkerOptions().
//                                position(latLng1).
//                                title(data.getTemperature() + "℃"));
//                        latLngs.add(latLng1);
//                    }
//                }
//            }
//            mAmap.addPolyline(new PolylineOptions()
//                    .addAll(latLngs)
//                    .width(10)
//                    .color(Color.argb(255, 55, 157, 242)));


            mAmap.clear();
            mAmap.removecache();

            // 1. 设置中心点
            for (int i = 0; i < request.length; i++) {
                TransRecordItemRequest item = request[i];
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

            if (request.length > 1) {
                List<LatLng> latLngs = new ArrayList<>();
                LogUtil.e("location", "map长度：" + request.length);
                // 2. add 点
                for (int i = 0; i < request.length; i++) {
                    TransRecordItemRequest data = request[i];
                    if (data != null) {
                        if (!TextUtils.isEmpty(data.getLatitude()) && !TextUtils.isEmpty(data.getLongitude())) {
                            LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
                                    Double.parseDouble(data.getLongitude()));
                            String temp = !TextUtils.isEmpty(data.getTemperature()) ? data.getTemperature() + "℃" : "";
                            mAmap.addMarker(new MarkerOptions().
                                    position(latLng1).
                                    title(temp));
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

}

package com.otqc.transbox.controller.mixture;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.google.gson.Gson;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.bean.QueryOpoInfoBean;
import com.otqc.transbox.bean.QueryOrganInfoBean;
import com.otqc.transbox.bean.QueryToHospitalInfoBean;
import com.otqc.transbox.bean.QueryTransferPersonInfoBean;
import com.otqc.transbox.databinding.ActivityTransFinishDetailBinding;
import com.otqc.transbox.db.TransOddDb;
import com.otqc.transbox.db.TransRecordItemDb;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.http.request.TransRecordItemRequest;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class TransFinishDetailActivity extends AppBaseActivity {
    private static final String TAG = "TransFinishDetailActivity";

    private ItemDetailData mData;
    private TransOddDb mOddDb;
    private WebView webView, mWv;
    private TransRecordItemRequest[] request;
    private MapView mMapView;
    private AMap mAmap;

    @Override
    protected void initVariable() {
        String tid = PrefUtils.getString("tid", "", App.getContext());
        // 基本信息
        Realm realm = RealmUtil.getInstance().getRealm();
        RealmResults<TransOddDb> baseResult = realm.where(TransOddDb.class).
                equalTo("transferid", tid).findAll();
        if (baseResult.size() > 0) {
            mOddDb = baseResult.get(0);
        }

        /**
         * 曲线数据
         */
        RealmResults<TransRecordItemDb> query = realm.where(TransRecordItemDb.class).
                equalTo("transfer_id", tid).
                findAll();
        if (query.size() > 0) {

            // 拼接数组
            request = new TransRecordItemRequest[query.size()];
            for (int i = 0; i < query.size(); i++) {
                TransRecordItemRequest record = new TransRecordItemRequest();
                if (query.get(i) != null) {
                    try {
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

                        record.setRecordAt(CommonUtil.dateToStamp(query.get(i).getRecordAt()));
                        LogUtil.e(TAG, CommonUtil.dateToStamp(query.get(i).getRecordAt()));

                        record.setType(query.get(i).getType());
                        record.setRemark(query.get(i).getRemark());
                        request[i] = record;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        realm.close();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActivityTransFinishDetailBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_trans_finish_detail);
        mData = new ItemDetailData();
        mData.setOpoInfo(new QueryOpoInfoBean());
        mData.setOrganInfo(new QueryOrganInfoBean());
        mData.setToHospitalInfo(new QueryToHospitalInfoBean());
        mData.setTransferPersonInfo(new QueryTransferPersonInfoBean());

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

        binding.setInfo(mData);
    }

    @Override
    protected void initData() {
        mData.getOrganInfo().setSegNumber(mOddDb.getOrganInfo().getSegNumber());
        mData.setGetOrganAt(mOddDb.getGetOrganAt());

        mData.getOrganInfo().setType(mOddDb.getOrganInfo().getType());
        mData.setOrganCount(mOddDb.getOrganCount() + "");
        mData.getOrganInfo().setBloodType(mOddDb.getOrganInfo().getBloodType());
        mData.getOrganInfo().setBloodSampleCount(mOddDb.getOrganInfo().getBloodSampleCount() + "");
        mData.getOrganInfo().setOrganizationSampleType(mOddDb.getOrganInfo().getOrganizationSampleType());
        mData.getOrganInfo().setOrganizationSampleCount(mOddDb.getOrganInfo().getOrganizationSampleCount() + "");

        mData.setFromCity(mOddDb.getFromCity());
        mData.getToHospitalInfo().setName(mOddDb.getToHospitalInfo().getName());
        mData.getTransferPersonInfo().setName(mOddDb.getTransferPersonInfo().getName());
        mData.getTransferPersonInfo().setPhone(mOddDb.getTransferPersonInfo().getPhone());
        mData.setTracfficType(mOddDb.getTracfficType());
        mData.setTracfficNumber(mOddDb.getTracfficNumber());

        mData.getOpoInfo().setName(mOddDb.getOpoInfo().getName());
        mData.getOpoInfo().setContactPerson(mOddDb.getOpoInfo().getContactPerson());
        mData.getOpoInfo().setContactPhone(mOddDb.getOpoInfo().getContactPhone());

        setDefaultData();
    }

    private void initWebViewData() {
        if (request != null && request.length > 0) {
            String jsData = new Gson().toJson(request);
            webView.loadUrl("javascript:set('" + jsData + "')");
            mWv.loadUrl("javascript:set('" + jsData + "')");
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
            TransRecordItemRequest firstData = request[0];
            // 1. 设置中心点
            mAmap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                    new LatLng(Double.parseDouble(firstData.getLatitude()),
                            Double.parseDouble(firstData.getLongitude())),//新的中心点坐标
                    11, //新的缩放级别
                    0, //俯仰角0°~45°（垂直与地图时为0）
                    0  ////偏航角 0~360° (正北方为0)
            )));
            List<LatLng> latLngs = new ArrayList<>();
            for (TransRecordItemRequest data : request) {
                if (data != null) {
                    if (!TextUtils.isEmpty(data.getLatitude()) && !TextUtils.isEmpty(data.getLongitude())) {
                        // 2. 默认点
                        LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
                                Double.parseDouble(data.getLongitude()));
                        // 3. mk点
                        final Marker marker1 = mAmap.addMarker(new MarkerOptions().
                                position(latLng1).
                                title(data.getTemperature() + "℃"));
                        latLngs.add(latLng1);
                    }
                }
            }
            mAmap.addPolyline(new PolylineOptions()
                    .addAll(latLngs)
                    .width(10)
                    .color(Color.argb(255, 55, 157, 242)));
        }
    }

}

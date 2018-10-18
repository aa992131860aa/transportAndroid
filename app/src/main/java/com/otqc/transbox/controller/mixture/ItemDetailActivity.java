package com.otqc.transbox.controller.mixture;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.google.gson.Gson;

import com.otqc.transbox.R;
import com.otqc.transbox.controller.query.QueryPresenter;
import com.otqc.transbox.databinding.ActivityItemDetailBinding;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.http.request.TransRecordItemRequest;
import com.otqc.transbox.json.TransferHistoryJson;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.JsonUtil;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史运单详情
 */
public class ItemDetailActivity extends AppBaseActivity implements AMap.InfoWindowAdapter, AMap.OnMarkerClickListener, AMap.OnMapClickListener {

    private ItemDetailData mData;
    private WebView webView, mWv;
    private TransRecordItemRequest[] request;
    private MapView mMapView;
    private AMap mAmap;
    private Marker temperatureMarker;
    private String TAG = "ItemDetailActivity";

    private TextView tv_organ_seg;
    private TextView tv_time;
    private TextView tv_organ;
    private TextView tv_organ_num;
    private TextView tv_blood;
    private TextView tv_blood_num;
    private TextView tv_sample;
    private TextView tv_sample_num;
    private TextView tv_start;
    private TextView tv_end;
    private TextView tv_person;
    private TextView tv_person_phone;
    private TextView tv_opo_name;
    private TextView tv_opo_phone;
    private TextView tv_transfer;
    private TextView tv_no;

    @Override
    protected void initVariable() {
        String data = QueryPresenter.QUERY_DATA;
        if (!TextUtils.isEmpty(data)) {
            mData = JsonUtil.parseJsonToBean(data, ItemDetailData.class);
            if (mData.getRecords() != null && mData.getRecords().size() > 0) {

                // 拼接数组
                request = new TransRecordItemRequest[mData.getRecords().size()];
                for (int i = 0; i < mData.getRecords().size(); i++) {
                    TransRecordItemRequest record = new TransRecordItemRequest();
                    if (mData.getRecords().get(i) != null) {
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

                        record.setRecordAt(mData.getRecords().get(i).getRecordAt() != null ? CommonUtil.dateToMD(mData.getRecords().get(i).getRecordAt()) : "");
                        // record.setRecordAt(mData.getRecords().get(i).getRecordAt()!=null?mData.getRecords().get(i).getRecordAt():"");
                        //LogUtil.e("ItemDetailActivity", record.getRecordAt());
//                            LogUtil.e(TAG, CommonUtil.dateToStamp(mData.getRecords().get(i).getRecordAt()));
                        record.setRecordAtReal(mData.getRecords().get(i).getRecordAt());

                        record.setType(mData.getRecords().get(i).getType());
                        record.setRemark(mData.getRecords().get(i).getRemark());
                        request[i] = record;
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
        LogUtil.e("ItemDetailActivity", "mAmap:" + mAmap + ",savedInstanceState:" + savedInstanceState);
        // 绑定 Marker 被点击事件
        mAmap.setOnMarkerClickListener(this);
        mAmap.setInfoWindowAdapter(this);
        mAmap.setOnMapClickListener(this);
        tv_organ_seg = (TextView) findViewById(R.id.tv_organ_seg);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_organ = (TextView) findViewById(R.id.tv_organ);
        tv_organ_num = (TextView) findViewById(R.id.tv_organ_num);
        tv_blood = (TextView) findViewById(R.id.tv_blood);
        tv_blood_num = (TextView) findViewById(R.id.tv_blood_num);
        tv_sample = (TextView) findViewById(R.id.tv_sample);
        tv_sample_num = (TextView) findViewById(R.id.tv_sample_num);
        tv_start = (TextView) findViewById(R.id.tv_start);
        tv_end = (TextView) findViewById(R.id.tv_end);
        tv_person = (TextView) findViewById(R.id.tv_person);
        tv_person_phone = (TextView) findViewById(R.id.tv_person_phone);
        tv_opo_name = (TextView) findViewById(R.id.tv_opo_name);
        tv_opo_phone = (TextView) findViewById(R.id.tv_opo_phone);
        tv_transfer = (TextView) findViewById(R.id.tv_transfer);
        tv_no = (TextView) findViewById(R.id.tv_no);
        TransferHistoryJson.ObjBean objBean = (TransferHistoryJson.ObjBean) getIntent().getSerializableExtra("detail");

        if (objBean != null) {

            tv_organ_seg.setText(objBean.getOrganSeg());
            tv_time.setText(objBean.getGetTime());
            tv_organ.setText(objBean.getOrgan());
            tv_organ_num.setText(objBean.getOrganNum());
            tv_blood.setText(objBean.getBlood());
            tv_blood_num.setText(objBean.getBloodNum());
            tv_sample.setText(objBean.getSampleOrgan());
            tv_sample_num.setText(objBean.getSampleOrganNum());
            tv_start.setText(objBean.getFromCity());
            tv_end.setText(objBean.getToHospName());
            tv_person.setText(objBean.getTrueName());
            tv_person_phone.setText(objBean.getPhone());
            tv_opo_name.setText(objBean.getContactName());
            tv_opo_phone.setText(objBean.getContactPhone());
            tv_transfer.setText(objBean.getTracfficType());
            tv_no.setText(objBean.getTracfficNumber());
            //ToastUtil.showToast("bb:"+objBean.getOrganSeg());
        }

    }

    @Override
    protected void initData() {
        LogUtil.e(TAG, QueryPresenter.OPENS_COLLISIONS + ":Open");
        LogUtil.e(TAG, QueryPresenter.COLLISION_OPENS + ":Collision");
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


            mAmap.clear();
            mAmap.removecache();
            int flag = 0;
            double latitudeTemp = 0.0;
            double longitudeTemp = 0.0;
            List<LatLng> latLngs = new ArrayList<>();

            for (int i = 0; i < request.length; i++) {

                TransRecordItemRequest data = request[i];
                //开始位置
                if (data != null && !TextUtils.isEmpty(data.getLatitude()) && !TextUtils.isEmpty(data.getLongitude())) {
                    if (flag == 0) {
                        //中心位置
                        mAmap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                new LatLng(Double.parseDouble(data.getLatitude()),
                                        Double.parseDouble(data.getLongitude())),//新的中心点坐标
                                13, //新的缩放级别
                                0, //俯仰角0°~45°（垂直与地图时为0）
                                0  ////偏航角 0~360° (正北方为0)
                        )));
                        LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
                                Double.parseDouble(data.getLongitude()));

                        mAmap.addMarker(new MarkerOptions().
                                position(latLng1).icon(BitmapDescriptorFactory.fromResource(R.drawable.start)));
                        latLngs.add(latLng1);
                    }
                    double temperature = Double.parseDouble(data.getTemperature() != null ? data.getTemperature() : "-1");

                    //异常温度
                    if (temperature < 0 || temperature > 6) {
                        LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
                                Double.parseDouble(data.getLongitude()));
                        String tem = "";
                        String temTime = "";
                        tem = data.getTemperature();
                        temTime = data.getRecordAt();
                        if (data.getTemperature() == null || "".equals(data.getTemperature())) {
                            tem = "-1";
                        }
                        if (data.getRecordAt() == null || "".equals(data.getRecordAt())) {
                            temTime = "2017-04-21";
                        }
                        String temperatureInfo = "温度异常p";
                        temperatureInfo += tem + "p";
                        temperatureInfo += temTime;
                        //String temp = !TextUtils.isEmpty(data.getTemperature()) ? temperatureInfo : "";
                        mAmap.addMarker(new MarkerOptions()
                                .title(temperatureInfo)
                                .position(latLng1)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.temperature)));
                        latLngs.add(latLng1);
                    }

                    //碰撞
                    if (QueryPresenter.COLLISION_OPENS != null) {
                        for (int c = 0; c < QueryPresenter.COLLISION_OPENS.size(); c++) {
                            if (data.getRecordAtReal().equals(QueryPresenter.COLLISION_OPENS.get(c).getRecordAt())) {

                                LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
                                        Double.parseDouble(data.getLongitude()));
                                String temTime = data.getRecordAtReal();

                                if (data.getRecordAtReal() == null || "".equals(data.getRecordAtReal())) {
                                    temTime = "2017-04-21";
                                }
                                String temperatureInfo = "碰撞异常p";
                                temperatureInfo += temTime;
                                mAmap.addMarker(new MarkerOptions()
                                        .title(temperatureInfo)
                                        .position(latLng1)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.collision)));
                                latLngs.add(latLng1);
                            }
                        }
                    }
                    //打开
                    if (QueryPresenter.OPENS_COLLISIONS != null) {
                        for (int c = 0; c < QueryPresenter.OPENS_COLLISIONS.size(); c++) {
                            if (data.getRecordAtReal().equals(QueryPresenter.OPENS_COLLISIONS.get(c).getRecordAt())) {

                                LatLng latLng1 = new LatLng(Double.parseDouble(data.getLatitude()),
                                        Double.parseDouble(data.getLongitude()));
                                String temTime = data.getRecordAtReal();
                                if (data.getRecordAtReal() == null || "".equals(data.getRecordAtReal())) {
                                    temTime = "2017-04-21";
                                }
                                String temperatureInfo = "打开异常p";
                                temperatureInfo += temTime;
                                mAmap.addMarker(new MarkerOptions()
                                        .title(temperatureInfo)
                                        .position(latLng1)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.open)));
                                latLngs.add(latLng1);
                            }
                        }
                    }

                    latitudeTemp = Double.parseDouble(data.getLatitude());
                    longitudeTemp = Double.parseDouble(data.getLongitude());
                    flag++;
                }


            }
            //结束位置

            if (flag > 0) {

                LatLng latLng1 = new LatLng(latitudeTemp,
                        longitudeTemp);

                temperatureMarker = mAmap.addMarker(new MarkerOptions().
                        position(latLng1).icon(BitmapDescriptorFactory.fromResource(R.drawable.end)));
                latLngs.add(latLng1);
            }


            // 3. 加一个多段线对象（Polyline）对象在地图上。
            PolylineOptions options = new PolylineOptions();
            options.width(10f);
            options.addAll(latLngs);
            options.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.texture));

            //.addAll(latLngs).width(10).color(Color.argb(255, 0, 255, 0))

            mAmap.addPolyline(options);
        }


    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View root = LayoutInflater.from(this).inflate(R.layout.custom_info_contents, null);
        TextView info_tv_title = (TextView) root.findViewById(R.id.info_tv_title);
        TextView info_tv_temperature = (TextView) root.findViewById(R.id.info_tv_temperature);
        TextView info_tv_time = (TextView) root.findViewById(R.id.info_tv_time);
        LinearLayout info_llyt_temperature = (LinearLayout) root.findViewById(R.id.info_llyt_temperature);
        LinearLayout info_llyt_time = (LinearLayout) root.findViewById(R.id.info_llyt_time);
        LinearLayout info_llyt = (LinearLayout) root.findViewById(R.id.info_llyt);
        String markerTitle = marker.getTitle();
        String[] markerTitles = markerTitle.split("p");
        //碰撞处理
        if (markerTitles.length > 2) {
            info_llyt_temperature.setVisibility(View.VISIBLE);
            info_tv_title.setText(markerTitles[0]);
            info_tv_temperature.setText(markerTitles[1]);
            info_tv_time.setText(markerTitles[2]);
        } else {
            info_llyt_temperature.setVisibility(View.GONE);
            info_tv_title.setText(markerTitles[0]);
            info_tv_time.setText(markerTitles[1]);

        }
        info_llyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                marker.hideInfoWindow();
            }
        });

        return root;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        //ToastUtil.showToast("点击:" + marker.getTitle());
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }
}

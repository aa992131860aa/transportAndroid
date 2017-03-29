package org.redsha.transbox.controller.main;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.databinding.ActivityMainBinding;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.service.CollectService;
import org.redsha.transbox.service.event.MainEvent;
import org.redsha.transbox.service.event.MapEvent;
import org.redsha.transbox.util.A;
import org.redsha.transbox.util.PrefUtils;

/**
 * 模块1：main
 */
public class MainActivity extends AppBaseActivity {
    private MainData mData;

    @Override
    protected void initVariable() {
        // 地图 数据
        MapEvent.city = "";
        MapEvent.lont = "";
        MapEvent.lati = "";
        MapEvent.Distance = "";
        // key
        PrefUtils.putString("tid", "", App.getContext());
        PrefUtils.putString("key", "", App.getContext());
        PrefUtils.putString("boxid", "", App.getContext());
        PrefUtils.putString("qrcode", "", App.getContext());
        PrefUtils.putString("hospitalid", "", App.getContext());
        PrefUtils.putString("address", "", App.getContext());
        // A
        A.point = null;
        A.isBoxInfo = false;
        A.isKwdInfo = false;
        A.isSerialPort = false;
        A.isReadyUp = false;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mData = new MainData();
        mData.setSerialStatus("检测中");
        mData.setTemperature("检测中");
        mData.setPower("检测中");
        binding.setInfo(mData);
        binding.setPresenter(new MainPresenter());
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        A.mCollectState = 0;    //只采样
        CollectService.getConnet(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MainEvent event) {
        if (event != null) {
            if (!TextUtils.isEmpty(event.getSerialStatus())) {
                mData.setSerialStatus(event.getSerialStatus());
            }

            if (!TextUtils.isEmpty(event.getTemperature())) {
                mData.setTemperature(event.getTemperature());
            }

            if (!TextUtils.isEmpty(event.getPower())) {
                mData.setPower(event.getPower());
            }
        }
    }

}

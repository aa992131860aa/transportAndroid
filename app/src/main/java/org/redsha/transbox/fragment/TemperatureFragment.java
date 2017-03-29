package org.redsha.transbox.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.redsha.transbox.R;
import org.redsha.transbox.controller.on.OnWayData;
import org.redsha.transbox.databinding.FragmentTemperatureBinding;
import org.redsha.transbox.db.TransRecordItemDb;

/**
 * tab 1：温度 / 定位
 */
public class TemperatureFragment extends Fragment {

    private OnWayData mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature, container, false);
        FragmentTemperatureBinding binding = DataBindingUtil.bind(view);
        mData = new OnWayData();
        mData.setTemperature("检测中");
        mData.setAvgTemperature("检测中");
        mData.setPower("检测中");
        mData.setExpendPower("检测中");
        mData.setHumidity("检测中");
        binding.setInfo(mData);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TransRecordItemDb event) {
        if (event != null) {
            if (!TextUtils.isEmpty(event.getTemperature())) {
                mData.setTemperature(event.getTemperature() + "℃");
            }
            if (!TextUtils.isEmpty(event.getAvgTemperature())) {
                mData.setAvgTemperature(event.getAvgTemperature() + "℃");
            }
            if (!TextUtils.isEmpty(event.getPower())) {
                mData.setPower(event.getPower());
            }
            if (!TextUtils.isEmpty(event.getExpendPower())) {
                mData.setExpendPower(event.getExpendPower());
            }
            if (!TextUtils.isEmpty(event.getHumidity())) {
                mData.setHumidity(event.getHumidity() + "%");
            }
        }
    }

}

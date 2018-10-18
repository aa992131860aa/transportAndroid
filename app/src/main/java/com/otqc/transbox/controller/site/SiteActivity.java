package com.otqc.transbox.controller.site;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.otqc.transbox.R;
import com.otqc.transbox.engine.BaseActivity;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SerialUtil;

/**
 * Created by 99213 on 2018/1/18.
 */

public class SiteActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private Button btn_back;

    private boolean isStart = true;
    private boolean isStop = true;
    private boolean isClose = true;
    private boolean isPlaneShow = true;
    private boolean isTemperature = true;
    private boolean isOpen = true;
    private boolean isHour24 = true;
    //默认一键转运关闭
    private boolean isTransfer = false;

    private Switch switch_start;
    private Switch switch_stop;
    private Switch switch_close;
    private Switch switch_plane_show;
    private Switch switch_temperature;
    private Switch switch_open;
    private Switch switch_hour24;
    private Switch switch_transfer;

    //服务器返回的一些设置信息
    private TextView tv_site;

    @Override
    protected void initVariable() {
        isStart = PrefUtils.getBoolean("isStart", true, this);
        isStop = PrefUtils.getBoolean("isStop", true, this);
        isClose = PrefUtils.getBoolean("isClose", true, this);
        isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, this);
        isTemperature = PrefUtils.getBoolean("isTemperature", true, this);
        isOpen = PrefUtils.getBoolean("isOpen", true, this);
        isHour24 = PrefUtils.getBoolean("isHour24", true, this);
        isTransfer = PrefUtils.getBoolean("isTransfer", false, this);


    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.site);
        btn_back = (Button) findViewById(R.id.btn_back);

        switch_start = (Switch) findViewById(R.id.switch_start);
        switch_stop = (Switch) findViewById(R.id.switch_stop);
        switch_close = (Switch) findViewById(R.id.switch_close);
        switch_plane_show = (Switch) findViewById(R.id.switch_plane_show);
        switch_temperature = (Switch) findViewById(R.id.switch_temperature);
        switch_open = (Switch) findViewById(R.id.switch_open);
        switch_hour24 = (Switch) findViewById(R.id.switch_hour24);
        switch_transfer = (Switch) findViewById(R.id.switch_transfer);
        tv_site = (TextView) findViewById(R.id.tv_site);

        btn_back.setOnClickListener(this);

        switch_start.setOnCheckedChangeListener(this);
        switch_stop.setOnCheckedChangeListener(this);
        switch_close.setOnCheckedChangeListener(this);
        switch_plane_show.setOnCheckedChangeListener(this);
        switch_temperature.setOnCheckedChangeListener(this);
        switch_open.setOnCheckedChangeListener(this);
        switch_hour24.setOnCheckedChangeListener(this);
        switch_transfer.setOnCheckedChangeListener(this);

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {


        switch_start.setChecked(isStart);
        switch_stop.setChecked(isStop);
        switch_close.setChecked(isClose);
        switch_plane_show.setChecked(isPlaneShow);
        switch_temperature.setChecked(isTemperature);
        switch_open.setChecked(isOpen);
        switch_hour24.setChecked(isHour24);
        switch_transfer.setChecked(isTransfer);

        tv_site.setText(
                "结束距离:" + CONSTS.END_DISTANCE + "\n"
                + "20公里发送短信(km):" + CONSTS.END_DISTANCE_20 + "\n"
                + "停止转运时间(s):" + CONSTS.END_TIME + "\n"
                + "温度的异常时间ms:" + CONSTS.EXCEPTION_TIME + "\n"
                + "串口发送的间隔时间ms:" + CONSTS.SERIAL_PERIOD + "\n"
                + "上传的值:" + CONSTS.UPLOAD_NUM_VALUE + "\n"
                + "串口循环的时间:" + CONSTS.SERIAL_NUM + "\n"
                + "间隔时间ms:" + CONSTS.SERIAL_TIME + "\n"
                + "每页的页数:" + CONSTS.PAGE_SIZE + "\n"
                + "电量%:" + CONSTS.POWER + "\n"
                + "自动开始的时间s:" + CONSTS.START_TIME + "\n"
                +"deviceIds:"+CONSTS.STOP_DEVICES

        );







    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.switch_start:
                if (switch_start.isChecked()) {
                    switch_start.setChecked(true);
                    PrefUtils.putBoolean("isStart", true, this);
                } else {
                    switch_start.setChecked(false);
                    PrefUtils.putBoolean("isStart", false, this);
                }
                break;

            case R.id.switch_stop:
                if (switch_stop.isChecked()) {
                    switch_stop.setChecked(true);
                    PrefUtils.putBoolean("isStop", true, this);
                } else {
                    switch_stop.setChecked(false);
                    PrefUtils.putBoolean("isStop", false, this);
                }
                break;

            case R.id.switch_close:
                if (switch_close.isChecked()) {
                    switch_close.setChecked(true);
                    PrefUtils.putBoolean("isClose", true, this);
                } else {
                    switch_close.setChecked(false);
                    PrefUtils.putBoolean("isClose", false, this);
                }
                break;

            case R.id.switch_plane_show:
                if (switch_plane_show.isChecked()) {
                    switch_plane_show.setChecked(true);
                    PrefUtils.putBoolean("isPlaneShow", true, this);
                } else {
                    switch_plane_show.setChecked(false);
                    PrefUtils.putBoolean("isPlaneShow", false, this);
                }
                boolean isTemperature1 = PrefUtils.getBoolean("isTemperature", true, this);
                boolean isPlaneShow1 = PrefUtils.getBoolean("isPlaneShow", true, this);
                SerialUtil.openTemperaturePlanePwd(isTemperature1, isPlaneShow1, false);

                break;
            case R.id.switch_temperature:

                if (switch_temperature.isChecked()) {
                    switch_temperature.setChecked(true);
                    PrefUtils.putBoolean("isTemperature", true, this);
                    //ToastUtil.showToast("lala" + true, this);
                } else {
                    switch_temperature.setChecked(false);
                    PrefUtils.putBoolean("isTemperature", false, this);
                    //ToastUtil.showToast("lala" + false, this);
                }
                boolean isTemperature2 = PrefUtils.getBoolean("isTemperature", true, this);
                boolean isPlaneShow2 = PrefUtils.getBoolean("isPlaneShow", true, this);
                SerialUtil.openTemperaturePlanePwd(isTemperature2, isPlaneShow2, false);

                break;
            case R.id.switch_open:
                if (switch_open.isChecked()) {
                    switch_open.setChecked(true);
                    PrefUtils.putBoolean("isOpen", true, this);
                } else {
                    switch_open.setChecked(false);
                    PrefUtils.putBoolean("isOpen", false, this);
                }
                //ToastUtil.showToast("what" , this);
                break;
            case R.id.switch_hour24:
                if (switch_hour24.isChecked()) {
                    switch_hour24.setChecked(true);
                    PrefUtils.putBoolean("isHour24", true, this);
                } else {
                    switch_hour24.setChecked(false);
                    PrefUtils.putBoolean("isHour24", false, this);
                }
                //ToastUtil.showToast("what" , this);
                break;
            case R.id.switch_transfer:
                if (switch_transfer.isChecked()) {
                    switch_transfer.setChecked(true);
                    PrefUtils.putBoolean("isTransfer", true, this);
                } else {
                    switch_transfer.setChecked(false);
                    PrefUtils.putBoolean("isTransfer", false, this);
                }
                CONSTS.IS_TRANSFER = true;
                break;

        }
    }
}

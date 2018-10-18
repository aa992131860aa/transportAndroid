package com.otqc.transbox.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayData;

import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayData;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayData;
import com.otqc.transbox.databinding.FragmentTimeBinding;
import com.otqc.transbox.db.TransOddDb;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDb;
import com.otqc.transbox.db.TransRecordItemDb2;
import com.otqc.transbox.db.TransRecordItemDbNew3;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;
import com.otqc.transbox.util.ToastUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * tab 2 ：转运时间 / 剩余电量 / 湿度
 */
public class TimeFragment extends Fragment {

    private OnWayData mData;
    private String TAG = "TimeFragment";
    private MainReceiver mainReceiver;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        FragmentTimeBinding binding = DataBindingUtil.bind(view);
        mData = new OnWayData();
        //注册广播
        IntentFilter intentFilter = new IntentFilter(CONSTS.ON_WAY_TRANS);
        mainReceiver = new MainReceiver();
        getActivity().registerReceiver(mainReceiver, intentFilter);

        mData.setDuration("检测中");
        mData.setCurrentCity("检测中");
        mData.setDistance("检测中");
        binding.setInfo(mData);
//        new Thread() {
//            @Override
//            public void run() {
//                while (CONSTS.IS_START == 1) {
//                    try {
//
//
//                            TransRecordItemDbNew3 itemDb2 = CONSTS.TRANS_DETAIL;
//                            if (itemDb2 != null) {
//                                mData.setDuration(itemDb2.getDuration() / 60 + "分");
//                                mData.setCurrentCity(itemDb2.getCurrentCity());
//                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//                                mData.setStartTime(sdf.format(new Date(CONSTS.TRANS_START)));
//                                mData.setDistance(itemDb2.getDistance() + "km");
//                            }
//
//
//                        Thread.sleep(CONSTS.SCREEN_TIME);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Log.e(TAG,"message："+e.getMessage());
//                    }
//                }
//                super.run();
//            }
//        }.start();
        //第一次初始化

        if(MainActivity.mObjBean!=null){
            try {

                mData.setCurrentCity(MainActivity.mObjBean.getFromCity());
                DecimalFormat df = new DecimalFormat("######0.00");
                mData.setDistance(0 + "km");



                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                double distanceDouble = (sdfAll.parse(CommonUtil.getTrueTime()).getTime() - sdf.parse(MainActivity.mObjBean.getGetTime()).getTime())/1000/60;
                mData.setDuration((int)distanceDouble/60 + "时"+(int)distanceDouble%60+"分");
                //ToastUtil.showToast(CommonUtil.getTrueTime()+distanceDouble);
                //Log.e(TAG,distanceDouble+","+CommonUtil.getTrueTime()+","+MainActivity.mObjBean.getGetTime());

                    Date date1 = sdf.parse(MainActivity.mObjBean.getGetTime());
                    sdf = new SimpleDateFormat("MM-dd HH:mm");
                    mData.setStartTime(sdf.format(date1));

            } catch (ParseException e) {
                ToastUtil.showToast(e.getMessage());
                e.printStackTrace();
            }
        }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mainReceiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TransRecordItemDb event) {
        if (event != null) {
            if (!TextUtils.isEmpty(event.getDuration())) {
                mData.setDuration(event.getDuration());
            }

            if (!TextUtils.isEmpty(event.getCurrentCity())) {
                mData.setCurrentCity(event.getCurrentCity());
            }

            if (!TextUtils.isEmpty(event.getDistance())) {
                mData.setDistance(event.getDistance());
            }
        }
    }
    class MainReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String stopTransfer = bundle.getString("stopTransfer");
                //if("stop".equals(stopTransfer)){
                    //ToastUtil.showToast("stop:");
                    //getDown(PrefUtils.getString("organSeg", "", getActivity()));
               // }else {

                if ("stopTransfer".equals(stopTransfer)) {
                    return;
                }
                    String duration = bundle.getString("duration");
                    String distance = bundle.getString("distance");
                    String city = bundle.getString("city");


                    try {
                          if(duration==null||distance==null||city==null){
                              return;
                          }
                        mData.setCurrentCity(city);
                        DecimalFormat df = new DecimalFormat("######0.00");
                        mData.setDistance(df.format(Double.parseDouble(distance)) + "km");
                        double distanceDouble = Double.parseDouble(duration);


                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        distanceDouble = (sdfAll.parse(CommonUtil.getTrueTime()).getTime() - sdf.parse(MainActivity.mObjBean.getGetTime()).getTime())/1000/60;
                        mData.setDuration((int)distanceDouble/60 + "时"+(int)distanceDouble%60+"分");
                        //ToastUtil.showToast(CommonUtil.getTrueTime()+distanceDouble);
                        //Log.e(TAG,distanceDouble+","+CommonUtil.getTrueTime()+","+MainActivity.mObjBean.getGetTime());
                        if(MainActivity.mObjBean!=null) {
                            Date date1 = sdf.parse(MainActivity.mObjBean.getGetTime());
                            sdf = new SimpleDateFormat("MM-dd HH:mm");
                            mData.setStartTime(sdf.format(date1));
                        }
                    } catch (ParseException e) {
                        ToastUtil.showToast(e.getMessage());
                        e.printStackTrace();
                    }
                }

           // }


        }
    }

}

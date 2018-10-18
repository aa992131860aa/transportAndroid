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
import android.widget.TextView;

import com.google.gson.Gson;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayData;

import com.otqc.transbox.R;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayActivity;
import com.otqc.transbox.controller.on.OnWayData;
import com.otqc.transbox.databinding.FragmentTemperatureBinding;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDb;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.Datas;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SerialUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayData;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * tab 1：温度 / 定位
 */
public class TemperatureFragment extends Fragment {

    private OnWayData mData;
    private String TAG = "TemperatureFragment";
    MainReceiver mainReceiver;

    private TextView tv_open;
    private TextView tv_collision;
    private TextView tv_organ_seg;

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
        mData.setOpen("检测中");
        mData.setCollision("检测中");

        binding.setInfo(mData);
        //初始化view
        initView(view);
        // ToastUtil.showToast("检查中");
        //注册广播
        IntentFilter intentFilter = new IntentFilter(CONSTS.ON_WAY_TRANS);
        mainReceiver = new MainReceiver();
        getActivity().registerReceiver(mainReceiver, intentFilter);

        String temperature = PrefUtils.getString("temperature", "", getActivity());
        String power = PrefUtils.getString("power", "", getActivity());
        String humidity = PrefUtils.getString("humidity", "", getActivity());

        if (!"".equals(temperature)) {
            mData.setTemperature(temperature + "℃");
        }
        if (!"".equals(power)) {
            mData.setPower(power + "%");
        }
        if (!"".equals(humidity)) {
            mData.setHumidity(humidity + "%");



        }

        if (MainActivity.mObjBean != null) {

            if (!"".equals(MainActivity.mObjBean.getModifyOrganSeg())) {
                tv_organ_seg.setText(MainActivity.mObjBean.getModifyOrganSeg());
            } else {
                tv_organ_seg.setText(MainActivity.mObjBean.getOrganSeg());
            }

            int open = DataSupport.where("transfer_id=? and open = 1", CONSTS.TRANSFER_ID).count(TransRecord.class);
            mData.setOpen(open + "次");
            int collision = DataSupport.where("transfer_id=? and collision = 1", CONSTS.TRANSFER_ID).count(TransRecord.class);
            mData.setCollision(collision + "次");

        }


//        List<TransRecord> transRecords = DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).find(TransRecord.class);
//        for(int i=0;i<transRecords.size();i++){
//            Log.e(TAG,"recordAt:"+transRecords.get(i).getRecordAt()+",collision:"+transRecords.get(i).getCollision()+",open:"+transRecords.get(i).getOpen());
//        }
        return view;
    }

    private void initView(View view) {
        tv_open = (TextView) view.findViewById(R.id.tv_open);
        tv_collision = (TextView) view.findViewById(R.id.tv_collision);
        tv_organ_seg = (TextView) view.findViewById(R.id.tv_organ_seg);
    }


//    private void getDown(final String organSeg) {
//        RequestParams params = new RequestParams(URL.TRANSFER);
//        params.addBodyParameter("action", "transferDown");
//        params.addBodyParameter("organSeg", organSeg);
//        x.http().get(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//
//                //Log.e(TAG, organSeg + ":result:" + result);
//                Datas datas = new Gson().fromJson(result, Datas.class);
//                if (datas != null && datas.getResult() == CONSTS.SEND_OK) {
//                    //发送电量信息
//                    sendPowerException();
//
//                    CONSTS.IS_START = 2;
//                    CONSTS.TRANSFER_ID = "";
//                    DataSupport.deleteAll(TransRecord.class);
//
//
//                    //转运已经停止
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//
//                    getActivity().finish();
//
//                }
//                if ("true".equals(datas.getMsg())) {
//                    stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
//                }
//                String longitude = PrefUtils.getString("longitude", "0.0", getActivity());
//                String latitude = PrefUtils.getString("latitude", "0.0" + "", getActivity());
//                if (!"".equals(latitude) && !"".equals(longitude) && MainActivity.mObjBean != null) {
//                    double pDistance = LocationUtils.getDistance(Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong()), Double.parseDouble(latitude), Double.parseDouble(longitude)) / 1000;
//                    if (pDistance < CONSTS.END_DISTANCE) {
//                        stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
//                    }
//
//                } else {
//
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }

    private void sendPowerException() {
        //服务器发送异常(开箱,碰撞,温度)
        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
        params.addBodyParameter("action", "recordException");
        params.addBodyParameter("transferId", CONSTS.TRANSFER_ID);
        params.addBodyParameter("organSeg", MainActivity.mObjBean.getOrganSeg());

        params.addBodyParameter("powerException", "true");
        params.addBodyParameter("power", CONSTS.POWER + "");
        params.addBodyParameter("powerType", "end");


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mainReceiver);
//        if (realm != null) {
//            realm.close();
//        }
    }

    class MainReceiver extends BroadcastReceiver {


        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                SerialUtil.transferSite(true);



                if (MainActivity.mObjBean != null) {

                    if (!"".equals(MainActivity.mObjBean.getModifyOrganSeg())) {
                        tv_organ_seg.setText(MainActivity.mObjBean.getModifyOrganSeg());
                    } else {
                        tv_organ_seg.setText(MainActivity.mObjBean.getOrganSeg());
                    }

                }
//                if ("stop".equals(stopTransfer)) {
//                    stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
//                } else {
                String temperature = bundle.getString("temperature");
                String humidity = bundle.getString("humidity");
                int collision = bundle.getInt("collision");
                //String open = bundle.getString("open");
                String power = bundle.getString("power");
                String expendPower = bundle.getString("expendPower");


                mData.setTemperature(temperature + "℃");
                //  mData.setAvgTemperature(temperature + "℃");
                mData.setPower(power);
                //   mData.setExpendPower(expendPower);
                mData.setHumidity(humidity + "%");
                //mData.setCollision(collision + "次");
                int open = DataSupport.where("transfer_id=? and open = 1", CONSTS.TRANSFER_ID).count(TransRecord.class);
                //mData.setOpen(open + "次");
                collision = DataSupport.where("transfer_id=? and collision = 1", CONSTS.TRANSFER_ID).count(TransRecord.class);
                mData.setCollision(collision + "次");
                mData.setOpen(open + "次");

                tv_open.setText(open + "次");
                tv_collision.setText(collision + "次");
                //ToastUtil.showToast("stop:"+stopTransfer+",open:"+CONSTS.OPEN+",collision:"+collision);

                //点亮图标
                SerialUtil.transferSite(true);

            }


//                index++;
//                if (index % 10 == 0 && CONSTS.SCREEN_STATUS) {
//                    RealmResults<TransRecordItemDbNew3> query = realm.where(TransRecordItemDbNew3.class)
//                            .equalTo("transfer_id", CONSTS.TRANSFER_ID)
//                            .findAll();
//                    double avg = 0;
//                    for (int i = 0; i < query.size(); i++) {
//                        TransRecordItemDbNew3 itemDbNew3 = query.get(i);
//                        try {
//                            avg += Double.parseDouble(itemDbNew3.getTemperature());
//                        } catch (Exception e) {
//
//                        }
//
//
//                    }
//                    DecimalFormat df = new DecimalFormat("######0.00");
//                    avgTem = df.format(avg / query.size()) + "℃";
//
//                }
//                if ("".equals(avgTem)) {
//                    mData.setAvgTemperature(temperature + "℃");
//                } else {
//                    mData.setAvgTemperature(avgTem);
//                }

        }


//        }
    }

//    private void stopTransfer(final String organSeg, String boxNo) {
//
//        RequestParams params = new RequestParams(URL.TRANSFER);
//        params.addBodyParameter("action", "shutDownTransfer");
//        params.addBodyParameter("organSeg", organSeg);
//        params.addBodyParameter("boxNo", boxNo);
//        x.http().get(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                Datas photoJson = new Gson().fromJson(result, Datas.class);
//                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
//                    ToastUtil.showToast("转运已结束");
//                    CONSTS.END_FLAG_AUTO = "";
//                    CONSTS.END_FLAG = "";
//                    //通知转运监控
//                    noticeTransfer(organSeg);
////                    //发送短信
//                    getGroupPhones(organSeg);
//
//                    CONSTS.OPEN = 0;
//                    CONSTS.COLLISION = 0;
//                    CONSTS.DISTANCE = 0;
//
//                    CONSTS.TRANS_DETAIL = new TransRecordItemDbNew3();
//
//
//                } else {
//                    ToastUtil.showToast("停止转运失败");
//
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }

    private void getGroupPhones(final String organSeg) {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "getGroupInfoOrganSeg");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Datas photoJson = new Gson().fromJson(result, Datas.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    String phones = photoJson.getMsg();
                    String organSegTemp = "";

                    if (!TextUtils.isEmpty(MainActivity.mObjBean.getModifyOrganSeg())) {
                        organSegTemp = MainActivity.mObjBean.getModifyOrganSeg();
                    } else {
                        organSegTemp = organSeg;
                    }
                    //请至APP或后台查看，下载或补全信息。
                    String content = "器官段号：" + organSegTemp + "，" + MainActivity.mObjBean.getFromCity() + "的" + MainActivity.mObjBean.getOrgan() + "转运已结束，当前设备电量为" + CONSTS.POWER + "%。";
                    sendTransferSms(phones, content);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void sendTransferSms(String phones, String content) {
        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}

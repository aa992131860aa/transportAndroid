package com.otqc.transbox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.otqc.transbox.controller.main.MainActivity;

import com.otqc.transbox.controller.main.MainActivity;

import org.litepal.crud.DataSupport;
import com.otqc.transbox.App;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDbNew3;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.Datas;
import com.otqc.transbox.json.TransferJson;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.CRC16M;
import com.otqc.transbox.util.LocationUtils;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SerialUtil;
import com.otqc.transbox.util.ToastUtil;

import com.otqc.transbox.controller.main.MainActivity;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.util.Date;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by 99213 on 2017/11/23.
 */

public class MyYunBaService extends BroadcastReceiver {
    private String TAG = "MyYunBaService";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (YunBaManager.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {

            String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
            String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);

            //在这里处理从服务器发布下来的消息， 比如显示通知栏， 打开 Activity 等等
            StringBuilder showMsg = new StringBuilder();
            showMsg.append("Received message from server: ")
                    .append(YunBaManager.MQTT_TOPIC)
                    .append(" = ")
                    .append(topic)
                    .append(" ")
                    .append(YunBaManager.MQTT_MSG)
                    .append(" = ").append(msg);
            //Log.e(TAG, "topic:" + topic + ",msg:" + msg);

            //ToastUtil.showToast("msg:"+msg);
            // DemoUtil.showNotifation(context, topic, msg);
            // transferStatus=1,2,3  未开始  开始  停止

            //android.intent.action.MAIN
            //com.action.OnWayActivity

//            try {
//                String status = msg.split("=")[1];
//
//                if (CONSTS.START.equals(status)) {
//                    getTransferInfo(context);
//                } else if (CONSTS.STOP.equals(status)) {
//                    shutDownTransfer(PrefUtils.getString("organSeg", "", context), context);
//                } else if (CONSTS.DELETE.equals(status)) {
//                    Intent intentDel = new Intent(CONSTS.MAIN_ACTION);
//                    intentDel.putExtra("status", CONSTS.DELETE);
//                    context.sendBroadcast(intentDel);
//                } else if (CONSTS.NO_START.equals(status)) {
//                    Intent intentDel = new Intent(CONSTS.MAIN_ACTION);
//                    intentDel.putExtra("status", CONSTS.NO_START);
//                    context.sendBroadcast(intentDel);
//                }
//
//            } catch (Exception e) {
//                ToastUtil.showToast("转运错误");
//            }


        }
    }

    /**
     * 获取转运信息
     */
    private void getTransferInfo(final Context pContext) {

        final String deviceId = PrefUtils.getString("deviceId", "", App.getContext());
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferByDeviceId");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                //ToastUtil.showToast(result);
                TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);

                if (CONSTS.SERVER_TIME == 0L) {
                    CONSTS.SERVER_TIME = Long.parseLong(transferJson.getMsg());
                }


                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {


                    TransferJson.ObjBean objBean = transferJson.getObj();


                    if (objBean.getIsStart().equals("0")) {

                        Intent intent = new Intent(CONSTS.MAIN_ACTION);
                        intent.putExtra("status", CONSTS.NO_START);
                        pContext.sendBroadcast(intent);


                        CONSTS.IS_START = 0;
                        MainActivity.mObjBean = objBean;
                    } else if (objBean.getIsStart().equals("1")) {

                        CONSTS.TRANSFER_ID = objBean.getTransferid();
                        //清零碰撞次数
//                        clearCollisionNumber();
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    clearCollisionNumber();
                                    Thread.sleep(100);

                                    SerialUtil.collision();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();


                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        MainActivity.mObjBean = objBean;


                        CONSTS.IS_START = 1;
                        PrefUtils.putString("organSeg", MainActivity.mObjBean.getOrganSeg(), pContext);
                        CONSTS.TRANS_START = new Date().getTime();

                        CONSTS.OPEN = getOpen();

                        CONSTS.COLLISION = getCollsion();
                        CONSTS.DISTANCE = getDistance();
                        CONSTS.DURATION_OLD = getDuration();

                        CONSTS.COUNT = getCount();


                        Intent intent = new Intent(CONSTS.ON_WAY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        pContext.startActivity(intent);


                    } else {
                        CONSTS.IS_START = 2;
                        Intent intent = new Intent(CONSTS.MAIN_ACTION);
                        intent.putExtra("status", CONSTS.DELETE);
                        pContext.sendBroadcast(intent);
                    }

                } else {
                    CONSTS.IS_START = 2;
                    Intent intent = new Intent(CONSTS.MAIN_ACTION);
                    intent.putExtra("status", CONSTS.DELETE);
                    pContext.sendBroadcast(intent);
                    //imageView.setBackgroundResource(R.drawable.logo);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                CONSTS.IS_START = 2;
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void shutDownTransfer(final String organSeg, final Context pContext) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "transferDown");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                //Log.e(TAG, organSeg + ":result:" + result);
                //ToastUtil.showToast(result);
                Datas datas = new Gson().fromJson(result, Datas.class);
                if (datas != null && datas.getResult() == CONSTS.SEND_OK) {
                    //发送电量信息
                    sendPowerException();

                    CONSTS.IS_START = 2;
                    CONSTS.TRANSFER_ID = "";
                    DataSupport.deleteAll(TransRecord.class);


                    //转运已经停止
                    Intent intent = new Intent(pContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    pContext.startActivity(intent);


                }
                if ("true".equals(datas.getMsg())) {
                    stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
                }
                double longitude = CONSTS.LONGITUDE;
                double latitude = CONSTS.LATITUDE;
                if (latitude!=0 && longitude!=0 && MainActivity.mObjBean != null) {
                    double pDistance = LocationUtils.getDistance(Double.parseDouble(MainActivity.mObjBean.getEndLati()), Double.parseDouble(MainActivity.mObjBean.getEndLong()), latitude, longitude) / 1000;
                    if (pDistance < CONSTS.END_DISTANCE) {
                        stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
                    }

                } else {

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

    private void stopTransfer(final String organSeg, String boxNo) {

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "shutDownTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("boxNo", boxNo);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Datas photoJson = new Gson().fromJson(result, Datas.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    ToastUtil.showToast("转运已结束");
                    CONSTS.END_FLAG_AUTO = "";
                    CONSTS.END_FLAG = "";
                    //通知转运监控
                    noticeTransfer(organSeg);
//                    //发送短信
                    getGroupPhones(organSeg);

                    CONSTS.OPEN = 0;
                    CONSTS.COLLISION = 0;
                    CONSTS.DISTANCE = 0;

                    CONSTS.TRANS_DETAIL = new TransRecordItemDbNew3();



                } else {
                    ToastUtil.showToast("停止转运失败");

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

                    if(!TextUtils.isEmpty(MainActivity.mObjBean.getModifyOrganSeg())){
                        organSegTemp = MainActivity.mObjBean.getModifyOrganSeg();
                    }else{
                        organSegTemp = organSeg;
                    }//请至APP或后台查看，下载或补全信息。
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

    private void sendPowerException() {
        //服务器发送异常(开箱,碰撞,温度)
        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
        params.addBodyParameter("action", "recordException");
        params.addBodyParameter("transferId", CONSTS.TRANSFER_ID);
        params.addBodyParameter("organSeg", MainActivity.mObjBean.getOrganSeg());
        params.addBodyParameter("modifyOrganSeg", MainActivity.mObjBean.getModifyOrganSeg());
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

    private void clearCollisionNumber() {

        //+++++++++++++++++清空碰撞+++++++++++++++++
        // 主机发送：7B 30 20 08 00 00 00 86 4C 7D
        //从机返回：7B 30 21 08 00 02 BC BB 5D 7D
        byte[] collisionBytes = new byte[10];
        collisionBytes[0] = 0x7B;
        collisionBytes[1] = 0x30;
        collisionBytes[2] = 0x10;
        collisionBytes[3] = 0x0D;
        collisionBytes[4] = 0x00;
        collisionBytes[5] = 0x00;
        collisionBytes[6] = 0x00;
        collisionBytes[7] = (byte) 0xC6;
        collisionBytes[8] = (byte) 0x84;
        collisionBytes[9] = 0x7D;

        byte[] powerBytes = new byte[6];

        powerBytes[0] = 0x30;
        powerBytes[1] = 0x10;
        powerBytes[2] = 0x0D;
        powerBytes[3] = 0x00;
        powerBytes[4] = 0x00;


        powerBytes[5] = (byte) (getCollsion() & 0x00FF);
        try {

            int powerCrc = new CRC16M().updateCheck(powerBytes, 6);

            int[] powerInts = new int[10];


            powerInts[0] = 0x7B;
            powerInts[1] = 0x30;
            powerInts[2] = 0x10;
            powerInts[3] = 0x0D;
            powerInts[4] = 0x00;
            powerInts[5] = 0x00;
            powerInts[6] = getCollsion();
            powerInts[7] = ((powerCrc & 0xFF00) >> 8);
            powerInts[8] = (powerCrc & 0x00FF);
            powerInts[9] = 0x7D;
            String s = "";
            for (int i = 0; i < powerInts.length; i++) {
                s += Integer.toHexString(powerInts[i]) + " ";
            }
            //Log.e(TAG, "ssss:" + s);

            for (int i = 0; i < powerInts.length; i++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(powerInts[i]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Log.e(TAG, "error4:" + e.getMessage());
        }


    }

    public int getCollsion() {

        return DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).max(TransRecord.class, "collision", int.class);

    }


    public int getOpen() {
        return DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).max(TransRecord.class, "open", int.class);
    }

    public int getDuration() {
        return DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).max(TransRecord.class, "duration", int.class);
    }

    public int getDistance() {
        return DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).max(TransRecord.class, "distance", int.class);
    }

    /**
     * 获取已转移的总数
     *
     * @return
     */
    public int getCount() {

        return DataSupport.where("transfer_id=?", CONSTS.TRANSFER_ID).count(TransRecord.class);

    }
}

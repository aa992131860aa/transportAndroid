package com.otqc.transbox.controller.main;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.otqc.transbox.controller.create.ConfirmCreateActivity;
import com.otqc.transbox.controller.query.QueryActivity;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.bean.KeywordBean;
import com.otqc.transbox.controller.create.ConfirmCreateActivity;
import com.otqc.transbox.controller.query.QueryActivity;
import com.otqc.transbox.databinding.ActivityMainBinding;
import com.otqc.transbox.db.PowerTemp;
import com.otqc.transbox.http.HttpHelper;
import com.otqc.transbox.http.HttpObserver;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.Datas;
import com.otqc.transbox.json.PhotoJson;
import com.otqc.transbox.json.QrImagesJson;
import com.otqc.transbox.json.TransferJson;
import com.otqc.transbox.service.LinkService;
import com.otqc.transbox.util.A;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SDFileHelper;
import com.otqc.transbox.util.SerialUtil;
import com.otqc.transbox.util.ToastUtil;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import com.otqc.transbox.controller.create.ConfirmCreateActivity;
import com.otqc.transbox.controller.query.QueryActivity;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import io.yunba.android.manager.YunBaManager;

public class MainPresenter {
    private String TAG = "MainPresenter";
    private MainActivity mainActivity;
    private ActivityMainBinding binding;
    private LinearLayout ll_transfer;

    public MainPresenter(MainActivity mainActivity, ActivityMainBinding binding, LinearLayout ll_transfer) {
        this.mainActivity = mainActivity;
        this.binding = binding;
        View view = LayoutInflater.from(mainActivity).inflate(R.layout.activity_main, null);

        this.ll_transfer = ll_transfer;

        setDeviceId();
        //getBoxInfo(PrefUtils.getString("deviceId", "", App.getContext()));
        getKey();

        Uri uri = Uri.fromFile(new File(new SDFileHelper().getFilePath("box_no1.png")));
        binding.imageView.setImageURI(uri);

    }


    private void setDeviceId() {
        //生成设备号
        //deviceId

        if (PrefUtils.getString("deviceId", "", App.getContext()).trim().equals("")) {
            //Log.e(TAG,PrefUtils.getString("deviceId", "", App.getContext())+"2sdfa");
            String deviceId = "35";
            for (int i = 0; i < 12; i++) {
                deviceId += new Random().nextInt(10) + "";
            }
            RequestParams
                    params = new RequestParams(URL.BOX);
            params.addQueryStringParameter("action", "device");
            params.addQueryStringParameter("device", deviceId);
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Datas datas = new Gson().fromJson(result, Datas.class);
                    if (datas != null && datas.getResult() == CONSTS.SEND_OK) {

                        PrefUtils.putString("deviceId", datas.getObj().toString(), App.getContext());
                        getBoxInfo(datas.getObj().toString());
                    } else {
                        setDeviceId();
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
        } else {
            A.isBoxInfo = true;
        }

    }

    private void initLinkService() {
        Intent intent = new Intent(App.getContext(), LinkService.class);
        App.getContext().startService(intent);
    }

    /**
     * 查历史
     */
    public void checkHistory() {

        //SerialUtil.power();
        //ToastUtil.showToast("查历史");
        if (ll_transfer.getVisibility() == View.VISIBLE) {

        } else {
            Intent intent = new Intent(App.getContext(), QueryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getContext().startActivity(intent);
        }
    }

    /**
     * 获取转运信息
     */
    private void isStartTransfer() {

        final String deviceId = PrefUtils.getString("deviceId", "", App.getContext());
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferByDeviceId");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {


                TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);


                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {


                    final TransferJson.ObjBean objBean = transferJson.getObj();


                    if (objBean.getIsStart().equals("0")) {

                        //设置为未修改
                        CommonUtil.saveNoStart(mainActivity, objBean);
                    }
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

    /**
     * 新建转运
     */
    public void createNewTrans() {
//        if (true) {
//            boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mainActivity);
//            boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mainActivity);
//            SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
//            return;
//        }

        //判断是否有网络
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        if (!CommonUtil.isNetworkAvalible(mainActivity) || year.contains("2010") || CONSTS.LATITUDE==0) {
            ToastUtil.showToast("数据连接中，请稍后再试！");
            return;
        }
        //setDeviceId();

        isStartTransfer();
        getBoxInfo(PrefUtils.getString("deviceId", "", App.getContext()));
//        ImageView iv_start = (ImageView) mainActivity.findViewById(R.id.iv_start);
//        if (iv_start.getVisibility() == View.VISIBLE) {
//            ToastUtil.showToast("请在APP上修改转运");
//            return;
//        }

        if (ll_transfer.getVisibility() == View.VISIBLE) {

        } else {
            Intent intent = new Intent(App.getContext(), ConfirmCreateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getContext().startActivity(intent);
        }
    }

    private void uploadPower(String time, String level, String deviceId, final PowerTemp powerTemp) {
        RequestParams params = new RequestParams(URL.TRANSFER_RECORD);
        params.addBodyParameter("action", "powerTemp");
        params.addBodyParameter("time", time);
        params.addBodyParameter("level", level);
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    powerTemp.delete();
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

    /**
     * 开始转运
     */
    public void startTransfer() {


    }

    private void getBoxInfo(String deviceId) {
        RequestParams params = new RequestParams(URL.QR_IMAGE);
        params.addBodyParameter("action", "boxHosp");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                QrImagesJson qrImagesJson = new Gson().fromJson(result, QrImagesJson.class);
                if (qrImagesJson != null && qrImagesJson.getResult() == CONSTS.SEND_OK) {
                    QrImagesJson.ObjBean model = qrImagesJson.getObj();
                    PrefUtils.putString("boxid", model.getBoxId(), App.getContext());
                    PrefUtils.putString("qrcode", model.getQrImages(), App.getContext());
                    PrefUtils.putString("hospitalid", model.getHospitalId(), App.getContext());
                    PrefUtils.putString("hospitalName", model.getHospitalName(), App.getContext());
                    PrefUtils.putString("boxNo", model.getBoxNo(), App.getContext());

                    //云巴
//                    YunBaManager.setAlias(mainActivity, model.getBoxNo(), new IMqttActionListener() {
//                        @Override
//                        public void onSuccess(IMqttToken iMqttToken) {
//
//                        }
//
//                        @Override
//                        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
//
//                        }
//                    });
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

    private void getKey() {

        new HttpHelper().getKey().subscribe(new HttpObserver<KeywordBean>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(KeywordBean model) {
                PrefUtils.putString("key", new Gson().toJson(model), App.getContext());

                A.isKwdInfo = true;
            }
        });
    }


}
package com.otqc.transbox.controller.on;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;

import com.google.gson.Gson;
import com.otqc.transbox.controller.main.MainActivity;

import com.otqc.transbox.controller.main.MainActivity;

import org.litepal.crud.DataSupport;
import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.Datas;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.view.DialogMaker;
import com.otqc.transbox.view.NewMonitorPopup;

import com.otqc.transbox.controller.main.MainActivity;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class OnWayPresenter {
    private Activity activity;
    private AlertDialog.Builder builder;
    private Dialog dialog;
    private Button btn_over;

    public OnWayPresenter(Activity activity) {
        this.activity = activity;
        btn_over = (Button) activity.findViewById(R.id.btn_over);
    }

    /**
     * 去 确认结束
     */
    public void goFinishTs() {
//        Intent intent = new Intent(App.getContext(), ConfirmFinishTsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        App.getContext().startActivity(intent);
        final String pwd = MainActivity.mObjBean == null ? "" : MainActivity.mObjBean.getOpenPsd();
        if ("".equals(pwd) || pwd == null) {
            AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(activity);
            mAlertDialog.setMessage("结束后,转运将不在进行,是否结束?");

            mAlertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (MainActivity.mObjBean != null) {
                        stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());
                    } else {
                        Intent intent = new Intent(activity, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                }
            });
            mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            mAlertDialog.show();
        } else {

//            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_et, null);
//            final EditText et = (EditText) view.findViewById(R.id.dialog_et);
//
//            new AlertDialog.Builder(activity).setTitle("开箱密码")
//                    .setView(view)
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//
//
//                            if (pwd.equals(et.getText().toString().trim())) {
//                                stopTransfer(TestMainActivity.mObjBean.getOrganSeg(), TestMainActivity.mObjBean.getBoxNo());
//
//                            } else {
//                                ToastUtil.showToast("开箱密码错误");
//
//                            }
//                        }
//                    })
//                    .setNegativeButton("取消", null)
//                    .show();
            final NewMonitorPopup newMonitorPopup = new NewMonitorPopup(activity, "结束");
            newMonitorPopup.showAtLocation(btn_over, Gravity.CENTER, 0, 0);
            newMonitorPopup.setOnClickChangeListener(new NewMonitorPopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(String number) {
                    if (number.length() >= 4 && pwd.equals(number.substring(0, 4))) {
                        newMonitorPopup.dismiss();
                        stopTransfer(MainActivity.mObjBean.getOrganSeg(), MainActivity.mObjBean.getBoxNo());

                    } else {
                        ToastUtil.showToast("开箱密码错误");

                    }
                }
            });
        }
    }

    public void checkDetail(OnWayData info) {
        switch (info.getOnWayPageState()) {
            case 0:
                Intent i = new Intent(App.getContext(), MorrisActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(i);
                break;
            case 1:
                Intent intent = new Intent(App.getContext(), MapDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(intent);
                break;
        }
    }

    /**
     * 开箱
     */
    public void openBox() {


    }


    private void stopTransfer(final String organSeg, String boxNo) {
        showWaitDialog("loading", false, "loading");
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
                    PrefUtils.putString("pwd", "", activity);

                    CONSTS.END_FLAG_AUTO = "";
                    CONSTS.END_FLAG = "";
                    boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, activity);
                    boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, activity);
                    //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, true);
                    //通知转运监控
                    noticeTransfer(organSeg);

                    //发送短信
                    getGroupPhones(organSeg);

                    shutDownTransfer();


                } else {
                    ToastUtil.showToast("停止转运失败");
                    dismissDialog();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void shutDownTransfer() {


        //发送电量信息
        sendPowerException();

        CONSTS.IS_START = 2;
        CONSTS.TRANSFER_ID = "";
        DataSupport.deleteAll(TransRecord.class);
        CONSTS.OPEN = 0;
        CONSTS.COLLISION = 0;
        MainActivity.mObjBean = null;
        //转运已经停止
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();

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
                dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
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
     * 等待对话框
     *
     * @author blue
     */
    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
        if (null == dialog || !dialog.isShowing()) {
            dialog = DialogMaker.showCommenWaitDialog(activity, msg, null, isCanCancelabel, tag);
        }
        return dialog;
    }

    /**
     * 关闭对话框
     *
     * @author blue
     */
    public void dismissDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}

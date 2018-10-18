package com.otqc.transbox.controller.query;


import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.otqc.transbox.controller.mixture.BoxNumActivity;

import com.otqc.transbox.controller.mixture.BoxNumActivity;

import com.otqc.transbox.bean.OpenCollision;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.mixture.BoxNumActivity;
import com.otqc.transbox.controller.site.SiteActivity;
import com.otqc.transbox.databinding.ActivityQueryBinding;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.TransferHistoryJson;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.view.DialogMaker;

import com.otqc.transbox.controller.mixture.BoxNumActivity;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class QueryPresenter {

    private QueryActivity mActivity;
    private QueryData mData;
    private ActivityQueryBinding mBinding;
    private String TAG = "QueryPresenter";
    //记录数据
    public static String QUERY_DATA = "";
    //碰撞数据
    public static List<OpenCollision> COLLISION_OPENS;
    //打开数据
    public static List<OpenCollision> OPENS_COLLISIONS;
    private Dialog dialog;

    public QueryPresenter(QueryActivity queryActivity, QueryData info, ActivityQueryBinding binding) {
        this.mActivity = queryActivity;
        this.mData = info;
        this.mBinding = binding;
    }

    public void back() {
        mActivity.finish();
    }

    public void queryBoxNum() {
//        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_et, null);
//        final EditText et = (EditText) view.findViewById(R.id.dialog_et);
//        et.setText("357897123567893");
//        new AlertDialog.Builder(mActivity).setTitle("箱子编号")
//                //.setIcon(android.R.drawable.ic_dialog_info)
//                .setView(view)
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String input = et.getText().toString();
//
//                        if (input.equals("")) {
//                            Toast.makeText(mActivity, "箱子编号不能为空！" + input, Toast.LENGTH_LONG).show();
//                        } else {
//                            Intent i = new Intent(mActivity, BoxNumActivity.class);
//                            i.putExtra("boxNum",input);
//                            mActivity.startActivity(i);
//
//                        }
//                    }
//                })
//                .setNegativeButton("取消", null)
//                .show();
       // showWaitDialog("loading", true, "loading");
        final String deviceId = PrefUtils.getString("deviceId", "", mActivity);
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getPadTransferHistory");
        params.addBodyParameter("deviceId", deviceId);
        params.addBodyParameter("page", 0 + "");
        params.addBodyParameter("pageSize", 1 + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TransferHistoryJson transferJson = new Gson().fromJson(result, TransferHistoryJson.class);
                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {
                    Intent i = new Intent(mActivity, BoxNumActivity.class);
                    i.putExtra("deviceId", deviceId);
                    mActivity.startActivity(i);
                } else {
                    ToastUtil.showToast("暂无历史数据");
                }
               // dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
                //ToastUtil.showToast("错误："+ex.getMessage());
                //Log.e(TAG,"error:"+ex.getMessage());
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
     * 历史查询,根据器官段号和转运单号
     *
     * @param info
     */
    public void queryOdd(final QueryData info) {
//        mBinding.queryBtn.setEnabled(false);
//        mBinding.queryBtn.setText("加载中...");
//        QUERY_DATA = "";
//        COLLISION_OPENS = null;
//        OPENS_COLLISIONS = null;
        boolean queryInfo = checkQueryInfo(info);
        if (queryInfo) {




            showWaitDialog("加载中", true, "loading");
            final String deviceId = PrefUtils.getString("deviceId", "", mActivity);

            if ("0079".equals(info.getOrgNum())) {
                ToastUtil.showToast(":"+deviceId);
                dismissDialog();
                return;

            }
            if ("140079".equals(info.getOrgNum())) {
                mActivity.startActivity(new Intent(mActivity, SiteActivity.class));
                dismissDialog();
                return;
            }
            if ("04140079".equals(info.getOrgNum())) {

                try {
                    //跳转开发者选项
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    mActivity.startActivity(intent);

                } catch (Exception e) {
                    ToastUtil.showToast("无法跳转:" + e.getMessage());
                }
                dismissDialog();
                return;
            }
            //ToastUtil.showToast("无法:" + info.getOrgNum());
            RequestParams params = new RequestParams(URL.TRANSFER);
            params.addBodyParameter("action", "getPadTransferHistory");
            params.addBodyParameter("deviceId", deviceId);
            params.addBodyParameter("organSeg", info.getOrgNum());
            params.addBodyParameter("page", 0 + "");
            params.addBodyParameter("pageSize", 1 + "");
            x.http().get(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    TransferHistoryJson transferJson = new Gson().fromJson(result, TransferHistoryJson.class);
                    if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {
                        Intent i = new Intent(mActivity, BoxNumActivity.class);
                        i.putExtra("deviceId", deviceId);
                        i.putExtra("organSeg", info.getOrgNum());
                        mActivity.startActivity(i);
                    } else {
                        ToastUtil.showToast("暂无历史数据");
                    }
                    dismissDialog();
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    //ToastUtil.showToast("无法跳转:" + ex.getMessage());
                    dismissDialog();
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });


        } else {
//            mBinding.queryBtn.setEnabled(true);
//            mBinding.queryBtn.setText("查询");
        }

    }

    private boolean checkQueryInfo(QueryData info) {

        if (TextUtils.isEmpty(info.getOrgNum())) {
            mActivity.showNullToast("请输入历史器官段号");
            return false;
        }

        return true;
    }

    /**
     * 等待对话框
     *
     * @author blue
     */
    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
        if (null == dialog || !dialog.isShowing()) {
            dialog = DialogMaker.showCommenWaitDialog(mActivity, msg, null, isCanCancelabel, tag);
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

package com.otqc.transbox.controller.create;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.otqc.transbox.R;
import com.otqc.transbox.databinding.ActivityConfirmCreateBinding;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SDFileHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class ConfirmCreateActivity extends AppBaseActivity {

    private AlertDialog.Builder mAlertDialog;

    @Override
    protected void initVariable() {
    }

    private ActivityConfirmCreateBinding mBinding;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_create);
        mBinding.setPresenter(new ConfirmCreatePresenter(this, mBinding));

        boolean isSave = PrefUtils.getBoolean("isSave", false, this);
        if (isSave) {
            alert();
        }
    }

    private void alert() {
        mAlertDialog = new AlertDialog.Builder(ConfirmCreateActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.confirm_no_start, null);
        TextView tv_confirm = (TextView) view.findViewById(R.id.tv_confirm);
        TextView tv_restart = (TextView) view.findViewById(R.id.tv_restart);
        mAlertDialog.setView(view);
        //mAlertDialog.setMessage("您有保存的转运，是否要去修改？");
        final AlertDialog dialog = mAlertDialog.create();
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("删除中...", true, "");
                deleteTransfer(dialog);
            }
        });

        dialog.show();
    }

    private void deleteTransfer(final AlertDialog dialog) {

        String organSeg = PrefUtils.getString("segNumber", "", this);
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "deleteTransfer");
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("phone", "18398850872");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                dismissDialog();
                dialog.dismiss();
                CONSTS.IS_START = 2;
                PrefUtils.putBoolean("isSave", false, ConfirmCreateActivity.this);
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

    @Override
    protected void initData() {
        String qrcode = PrefUtils.getString("qrcode", "", getApplicationContext());
        LogUtil.e("qrcode", qrcode);
        //Glide.with(this).load(qrcode).dontAnimate().placeholder(R.drawable.logo).into(mBinding.imageView);
        mBinding.imageView.setImageResource(R.drawable.logo);
        File parent = Environment.getExternalStorageDirectory();
        File file = new File(parent, "box_no1.png");
        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        //mBinding.imageView.setImageURI(uri);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            if (!TextUtils.isEmpty(versionName)) {
                ((TextView) findViewById(R.id.version)).setText("版本：" + versionName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}

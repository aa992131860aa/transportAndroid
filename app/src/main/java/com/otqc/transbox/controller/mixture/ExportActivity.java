package com.otqc.transbox.controller.mixture;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.otqc.transbox.R;
import com.otqc.transbox.databinding.ActivityExportBinding;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.view.dialog.OkDialog;

public class ExportActivity extends AppBaseActivity {

    private String mExId;

    @Override
    protected void initVariable() {
        mExId = getIntent().getStringExtra("exporttid");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActivityExportBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_export);
        binding.setInfo(new ExportData());
        binding.setPresenter(new ExportPresenter(this, mExId, binding));
    }

    @Override
    protected void initData() {

    }

    /**
     * 手机为空
     */
    public void phoneNullToast() {
        ToastUtil.showToast(getString(R.string.export_phoneNull));
    }

    /**
     * 手机不正确
     */
    public void phoneErrorToast() {
        ToastUtil.showToast(getString(R.string.export_phoneError));
    }

    public void showDlg() {
        // 收到开箱指令返回成功，且展示dialog
        final OkDialog dlg = new OkDialog(ExportActivity.this, "发送成功，请点击确定返回", "确 定");
        dlg.setOnChoiceClickListener(new OkDialog.OnChoiceClickListener() {
            @Override
            public void setOkClick() {
                finish();
                dlg.dismiss();
            }
        });
        dlg.show();
    }

}
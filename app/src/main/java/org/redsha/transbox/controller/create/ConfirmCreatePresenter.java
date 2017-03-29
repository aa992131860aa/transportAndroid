package org.redsha.transbox.controller.create;

import android.content.Intent;

import org.redsha.transbox.App;
import org.redsha.transbox.databinding.ActivityConfirmCreateBinding;
import org.redsha.transbox.util.A;
import org.redsha.transbox.util.ToastUtil;

public class ConfirmCreatePresenter {
    private ConfirmCreateActivity mActivity;
    private ActivityConfirmCreateBinding mBinding;

    public ConfirmCreatePresenter(ConfirmCreateActivity confirmCreateActivity, ActivityConfirmCreateBinding binding) {
        this.mActivity = confirmCreateActivity;
        this.mBinding = binding;
    }

    /**
     * 取消
     */
    public void cancel() {
        mActivity.finish();
    }

    /**
     * 确定
     */
    public void confirm() {
        mBinding.createTrans.setEnabled(false);
        mBinding.createTrans.setText("加载中...");

        boolean canCreate = checkCanCreate();
        if (!canCreate) {
            Intent intent = new Intent(App.getContext(), CreateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getContext().startActivity(intent);
            mActivity.finish();
        } else {
            mBinding.createTrans.setEnabled(true);
            mBinding.createTrans.setText("确定");
        }

    }

    private boolean checkCanCreate() {
        if (!A.isBoxInfo || !A.isBoxInfo) {
            ToastUtil.showToast("设备信息不正确，稍后再试或重启应用。");
            return false;
        }

        return true;
    }

}
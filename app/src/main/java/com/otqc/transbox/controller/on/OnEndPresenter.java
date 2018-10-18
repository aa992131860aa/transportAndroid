package com.otqc.transbox.controller.on;

import android.content.Intent;

import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.mixture.TransFinishDetailActivity;

import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.mixture.TransFinishDetailActivity;

import com.otqc.transbox.App;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.mixture.TransFinishDetailActivity;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.view.dialog.ChoiceDialog;

import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.mixture.TransFinishDetailActivity;

public class OnEndPresenter {
    private OnEndActivity mActivity;

    public OnEndPresenter(OnEndActivity onEndActivity) {
        this.mActivity = onEndActivity;
    }

    /**
     * 去详情页
     */
    public void goDetail() {
        Intent intent = new Intent(App.getContext(), TransFinishDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    /**
     * 关机页
     */
    public void goCloseDevice() {

        final ChoiceDialog dlg = new ChoiceDialog(mActivity, "确认关机？", "取消", "关机");
        dlg.setOnChoiceClickListener(new ChoiceDialog.OnChoiceClickListener() {
            @Override
            public void setCancelClick() {
                dlg.dismiss();
            }

            @Override
            public void setOkClick() {
                ToastUtil.showToast("关机...");
                dlg.dismiss();
            }
        });
        dlg.show();

    }

    /**
     * 去主页
     */
    public void gotMainMenu() {
        Intent intent = new Intent(App.getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // App.getContext().startActivity(intent);
      //  mActivity.finish();
    }

}

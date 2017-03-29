package org.redsha.transbox.controller.on;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.redsha.transbox.R;
import org.redsha.transbox.databinding.ActivityOpenBoxBinding;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.service.event.BoxStateEvent;
import org.redsha.transbox.service.event.OpenBoxEvent;
import org.redsha.transbox.util.ToastUtil;
import org.redsha.transbox.view.dialog.OkDialog;

public class OpenBoxActivity extends AppBaseActivity {

    private ActivityOpenBoxBinding mBinding;
    private OpenBoxData mInfo;

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_open_box);
        mInfo = new OpenBoxData();
        mBinding.setInfo(mInfo);
        mBinding.setPresenter(new OpenBoxPresenter(this, mBinding));
    }

    @Override
    protected void initData() {
        initEdittextFocus();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BoxStateEvent event) {

        if (event.isState() && event.isShowDlg()) {
            // 收到开箱指令返回成功，且展示dialog
            final OkDialog dlg = new OkDialog(OpenBoxActivity.this, "箱子已开启，关闭后请点击确认按键。", "确 认");
            dlg.setOnChoiceClickListener(new OkDialog.OnChoiceClickListener() {
                @Override
                public void setOkClick() {
                    /**
                     * 关 指令：关之前查看箱子状态
                     */
                    OpenBoxEvent openBoxEvent = new OpenBoxEvent();
                    openBoxEvent.setState(false);
                    EventBus.getDefault().post(openBoxEvent);

                    dlg.dismiss();
                    finish();
                }
            });
            dlg.show();

//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("箱子已开启，关闭后请点击确认按键。");
//            builder.setCancelable(false);
//            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    /**
//                     * 关 指令：关之前查看箱子状态
//                     */
//                    OpenBoxEvent openBoxEvent = new OpenBoxEvent();
//                    openBoxEvent.setState(false);
//                    EventBus.getDefault().post(openBoxEvent);
//
//                    dialog.dismiss();
//                    finish();
//                }
//            });
//            builder.show();
        }

    }

    private void initEdittextFocus() {
        View v = mBinding.buttonClear1;
        v.setOnClickListener(mOnClickListener);

        EditText et = mBinding.etPs1;
        et.setOnFocusChangeListener(new OpenBoxActivity.LoginOnFocusChangeListener(
                R.id.button_clear1, true));
    }

    public void openPsNullToast() {
        ToastUtil.showToast(getString(R.string.common_nullToast));
    }

    public void openPsError() {
        ToastUtil.showToast(getString(R.string.common_psError));
    }

    public class LoginOnFocusChangeListener implements View.OnFocusChangeListener {

        private int mClearBtnId;
        private boolean mShowToast;

        public LoginOnFocusChangeListener(int clearBtnId, boolean showToast) {
            this.mClearBtnId = clearBtnId;
            this.mShowToast = showToast;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Button bt = (Button) findViewById(mClearBtnId);
            EditText et = (EditText) v;
            if (hasFocus && !TextUtils.isEmpty(et.getText().toString())) {
                bt.setVisibility(View.VISIBLE);
            } else {
                if (mShowToast && !hasFocus
                        && et.getText().toString().length() != 4) {
                    ToastUtil.showToast("密码必须4位哦！");
                }
                bt.setVisibility(View.INVISIBLE);
            }
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_clear1: {
                    mInfo.setOpenPs1("");
                    break;
                }
            }
        }
    };

}
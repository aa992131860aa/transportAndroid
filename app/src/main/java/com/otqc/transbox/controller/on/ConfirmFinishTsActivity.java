package com.otqc.transbox.controller.on;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.otqc.transbox.R;
import com.otqc.transbox.databinding.ActivityConfirmFinishTsBinding;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.util.ToastUtil;

/**
 * 确认结束转运（实际就是开箱，确认结束）
 */
public class ConfirmFinishTsActivity extends AppBaseActivity {

    private ActivityConfirmFinishTsBinding mBinding;
    private ConfirmFinishTsData mInfo;

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_finish_ts);
        mInfo = new ConfirmFinishTsData();
        mBinding.setInfo(mInfo);
        mBinding.setPresenter(new ConfirmFinishTsPresenter(this, mInfo, mBinding));
        initEdittextFocus();
    }

    private void initEdittextFocus() {
        View v = mBinding.buttonClear1;
        v.setOnClickListener(mOnClickListener);

        EditText et = mBinding.etPs1;
        et.setOnFocusChangeListener(new ConfirmFinishTsActivity.LoginOnFocusChangeListener(
                R.id.button_clear1, true));
    }

    @Override
    protected void initData() {

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

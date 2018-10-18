package com.otqc.transbox.controller.query;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.otqc.transbox.R;
import com.otqc.transbox.databinding.ActivityQueryBinding;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.util.ToastUtil;

public class QueryActivity extends AppBaseActivity {

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActivityQueryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_query);
        QueryData info = new QueryData();

        binding.setInfo(info);
        binding.setPresenter(new QueryPresenter(this, info,binding));
    }

    @Override
    protected void initData() {

    }

    /**
     * 非空提示
     */
    public void showNullToast(String content) {
        ToastUtil.showToast(content);
    }

}

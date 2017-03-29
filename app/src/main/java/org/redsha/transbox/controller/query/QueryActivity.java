package org.redsha.transbox.controller.query;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import org.redsha.transbox.R;
import org.redsha.transbox.databinding.ActivityQueryBinding;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.util.ToastUtil;

public class QueryActivity extends AppBaseActivity {

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActivityQueryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_query);
        QueryData info = new QueryData();
//        info.setOddNum("91277518");
//        info.setOrgNum("123456789");

        binding.setInfo(info);
        binding.setPresenter(new QueryPresenter(this, info,binding));
    }

    @Override
    protected void initData() {

    }

    /**
     * 非空提示
     */
    public void showNullToast() {
        ToastUtil.showToast(getString(R.string.common_nullToast));
    }

}

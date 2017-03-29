package org.redsha.transbox.controller.create;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.redsha.transbox.R;
import org.redsha.transbox.databinding.ActivityConfirmCreateBinding;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.util.PrefUtils;

public class ConfirmCreateActivity extends AppBaseActivity {

    @Override
    protected void initVariable() {
    }

    private ActivityConfirmCreateBinding mBinding;

    @Override
    protected void initView(Bundle savedInstanceState) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_create);
        mBinding.setPresenter(new ConfirmCreatePresenter(this, mBinding));
    }

    @Override
    protected void initData() {
        String qrcode = PrefUtils.getString("qrcode", "", getApplicationContext());
        Glide.with(this)
                .load(qrcode)
                .dontAnimate()
                .placeholder(R.drawable.logo)
                .into(mBinding.imageView);

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

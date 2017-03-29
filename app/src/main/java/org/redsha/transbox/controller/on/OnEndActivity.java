package org.redsha.transbox.controller.on;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.databinding.ActivityOnEndBinding;
import org.redsha.transbox.db.TransRecordItemDb;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 转运节后 后的界面
 */
public class OnEndActivity extends AppBaseActivity {

    private OnEndData mData;

    @Override
    protected void initVariable() {
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        ActivityOnEndBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_on_end);
        mData = new OnEndData();
        mData.setDuration("未统计");
        mData.setAvgTemperature("未统计");
        mData.setPower("未统计");
        binding.setInfo(mData);
        binding.setPresenter(new OnEndPresenter(this));
    }

    @Override
    protected void initData() {

        final String tid = PrefUtils.getString("tid", "", App.getContext());
        Realm realm = RealmUtil.getInstance().getRealm();

        RealmResults<TransRecordItemDb> tsResult = realm.where(TransRecordItemDb.class).
                equalTo("transfer_id", tid).findAll();
        if (tsResult.size() > 0) {
            TransRecordItemDb data = tsResult.get(tsResult.size() - 1);
            if (!TextUtils.isEmpty(data.getDuration())) {
                mData.setDuration(data.getDuration());
            }
            if (!TextUtils.isEmpty(data.getAvgTemperature())) {
                mData.setAvgTemperature(data.getAvgTemperature()+"℃");
            }
            if (!TextUtils.isEmpty(data.getPower())) {
                mData.setPower(data.getPower());
            }
        }

    }

}

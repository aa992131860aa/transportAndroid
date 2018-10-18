package com.otqc.transbox.controller.on;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;

import org.litepal.crud.DataSupport;
import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.databinding.ActivityOnEndBinding;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDb;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;

import java.util.List;

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
        //Realm realm = RealmUtil.getInstance().getRealm();

        List<TransRecord> tsResult = DataSupport.where("transfer_id = ?", CONSTS.TRANSFER_ID).find(TransRecord.class);
        if (tsResult.size() > 0) {
            TransRecord data = tsResult.get(tsResult.size() - 1);
//            if (!TextUtils.isEmpty(data.getDuration()) {
//                mData.setDuration(data.getDuration());
//            }
            if (!TextUtils.isEmpty(data.getAvgTemperature())) {
                mData.setAvgTemperature(data.getAvgTemperature()+"℃");
            }
            if (!TextUtils.isEmpty(data.getPower())) {
                mData.setPower(data.getPower());
            }
        }

    }

}

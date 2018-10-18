package com.otqc.transbox.controller.on;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import com.otqc.transbox.App;
import com.otqc.transbox.databinding.ActivityOpenBoxBinding;
import com.otqc.transbox.db.TransOddDb;
import com.otqc.transbox.service.event.OpenBoxEvent;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;

public class OpenBoxPresenter {

    private OpenBoxActivity mActivity;
    private ActivityOpenBoxBinding mBinding;

    public OpenBoxPresenter(OpenBoxActivity activity, ActivityOpenBoxBinding binding) {
        mActivity = activity;
        mBinding = binding;
    }

    public void back() {
        mActivity.finish();
    }
    public void skip(OpenBoxData info){
        OpenBoxEvent openBoxEvent = new OpenBoxEvent();
        openBoxEvent.setState(true);
        EventBus.getDefault().post(openBoxEvent);
    }
    public void finishTs(OpenBoxData info) {

        boolean checkOpenPs = checkOpenPs(info);
        if (checkOpenPs) {
            // 密码正确。开指令
            OpenBoxEvent openBoxEvent = new OpenBoxEvent();
            openBoxEvent.setState(true);
            EventBus.getDefault().post(openBoxEvent);
        }

    }

    private boolean checkOpenPs(OpenBoxData info) {
        if (TextUtils.isEmpty(info.getOpenPs1())) {
            mActivity.openPsNullToast();
            return false;
        }

        String tid = PrefUtils.getString("tid", "", App.getContext());
        if (!TextUtils.isEmpty(tid)) {
            String resultPs = info.getOpenPs1();

//            Realm realm = RealmUtil.getInstance().getRealm();
//            RealmResults<TransOddDb> dbs
//                    = realm.where(TransOddDb.class).equalTo("transferid", tid).findAll();
//            if (dbs.size() > 0) {
//                TransOddDb oddDb = dbs.get(0);
//                String boxPin = oddDb.getBoxPin();
//                if (!boxPin.equals(resultPs)) {
//                    mActivity.openPsError();
//                    return false;
//                }
//            }
//            realm.close();
        }

        return true;
    }

}

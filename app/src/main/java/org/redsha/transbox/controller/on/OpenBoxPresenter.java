package org.redsha.transbox.controller.on;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.redsha.transbox.App;
import org.redsha.transbox.databinding.ActivityOpenBoxBinding;
import org.redsha.transbox.db.TransOddDb;
import org.redsha.transbox.service.event.OpenBoxEvent;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;

import io.realm.Realm;
import io.realm.RealmResults;

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

            Realm realm = RealmUtil.getInstance().getRealm();
            RealmResults<TransOddDb> dbs
                    = realm.where(TransOddDb.class).equalTo("transferid", tid).findAll();
            if (dbs.size() > 0) {
                TransOddDb oddDb = dbs.get(0);
                String boxPin = oddDb.getBoxPin();
                if (!boxPin.equals(resultPs)) {
                    mActivity.openPsError();
                    return false;
                }
            }
            realm.close();
        }

        return true;
    }

}

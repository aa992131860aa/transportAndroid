package org.redsha.transbox.controller.on;

import android.content.Intent;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.redsha.transbox.App;
import org.redsha.transbox.bean.TransFinshBean;
import org.redsha.transbox.databinding.ActivityConfirmFinishTsBinding;
import org.redsha.transbox.db.TransOddDb;
import org.redsha.transbox.http.HttpHelper;
import org.redsha.transbox.http.HttpObserver;
import org.redsha.transbox.service.event.MessageEvent;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;

import io.realm.Realm;
import io.realm.RealmResults;

public class ConfirmFinishTsPresenter {

    private ConfirmFinishTsActivity mActivity;
    private ConfirmFinishTsData mData;
    private ActivityConfirmFinishTsBinding nBinding;

    public ConfirmFinishTsPresenter(ConfirmFinishTsActivity mActivity, ConfirmFinishTsData mData, ActivityConfirmFinishTsBinding binding) {
        this.mActivity = mActivity;
        this.mData = mData;
        this.nBinding = binding;
    }

    public void back() {
        mActivity.finish();
    }

    public void finishTs(ConfirmFinishTsData info) {

        nBinding.finishBtn.setEnabled(false);
        nBinding.finishBtn.setText("加载中...");

        boolean checkOpenPs = checkOpenPs(info);
        if (checkOpenPs) {
            final String tid = PrefUtils.getString("tid", "", App.getContext());
            if (!TextUtils.isEmpty(tid)) {
                new HttpHelper().finishTrans(tid).subscribe(new HttpObserver<TransFinshBean>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onSuccess(TransFinshBean model) {
                        /**
                         * 发通知结束掉 转运中界面
                         */
                        EventBus.getDefault().post(new MessageEvent());

                        Intent intent = new Intent(App.getContext(), OnEndActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.getContext().startActivity(intent);
                        mActivity.finish();
                    }
                });
            }
        } else {
            nBinding.finishBtn.setEnabled(true);
            nBinding.finishBtn.setText("确定");
        }

    }

    private boolean checkOpenPs(ConfirmFinishTsData info) {
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

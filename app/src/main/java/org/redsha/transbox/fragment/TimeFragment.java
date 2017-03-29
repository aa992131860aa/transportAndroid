package org.redsha.transbox.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.controller.on.OnWayData;
import org.redsha.transbox.databinding.FragmentTimeBinding;
import org.redsha.transbox.db.TransOddDb;
import org.redsha.transbox.db.TransRecordItemDb;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * tab 2 ：转运时间 / 剩余电量 / 湿度
 */
public class TimeFragment extends Fragment {

    private OnWayData mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time, container, false);
        FragmentTimeBinding binding = DataBindingUtil.bind(view);
        mData = new OnWayData();

        final String tid = PrefUtils.getString("tid", "", App.getContext());
        Realm realm = RealmUtil.getInstance().getRealm();
        RealmResults<TransOddDb> db = realm.where(TransOddDb.class).equalTo("transferid", tid).findAll();
        if (db.size() > 0) {
            TransOddDb result = db.get(0);
            mData.setStartTime(result.getStartAt().substring(11, 16));
        }
        mData.setDuration("检测中");
        mData.setCurrentCity("检测中");
        mData.setDistance("检测中");
        binding.setInfo(mData);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TransRecordItemDb event) {
        if (event != null) {
            if (!TextUtils.isEmpty(event.getDuration())) {
                mData.setDuration(event.getDuration());
            }

            if (!TextUtils.isEmpty(event.getCurrentCity())) {
                mData.setCurrentCity(event.getCurrentCity());
            }

            if (!TextUtils.isEmpty(event.getDistance())) {
                mData.setDistance(event.getDistance());
            }
        }
    }

}

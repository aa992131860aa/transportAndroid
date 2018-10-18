package com.otqc.transbox.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.litepal.crud.DataSupport;
import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.databinding.FragmentExceptionBinding;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.PrefUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * tab 3：开箱次数 / 碰撞次数
 */
public class ExceptionFragment extends BaseFragment {
    private final static String TAG = "ExceptionFragment";

    private FragmentExceptionBinding mBinding;
    /**
     * init数据
     */
    ArrayList<String> openData = new ArrayList<>();
    /**
     * init数据
     */
    ArrayList<String> collisionData = new ArrayList<>();
    ListView lvOpen;
    ListView lvCollision;
    MainReceiver mainReceiver;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            lvOpen.setAdapter(new OpenTimeAdapter(App.getContext(), openData));
            lvCollision.setAdapter(new OpenTimeAdapter(App.getContext(), collisionData));
            return false;
        }
    });

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exception, container, false);
        mBinding = DataBindingUtil.bind(view);
        lvOpen = mBinding.lvOpen;
        lvCollision = mBinding.lvCollision;
        mainReceiver = new MainReceiver();
        IntentFilter intentFilter = new IntentFilter(CONSTS.EXCEPTION);
        getActivity().registerReceiver(mainReceiver, intentFilter);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mainReceiver);
    }

    private void refreshData() {


        /**
         * 开箱数据
         */
        List<TransRecord> openQuery = DataSupport.where("transfer_id = ? and open = 1", CONSTS.TRANSFER_ID).order("recordAt").find(TransRecord.class);
        List<TransRecord> collisionQuery = DataSupport.where("transfer_id = ? and collision = 1", CONSTS.TRANSFER_ID).order("recordAt").find(TransRecord.class);
        openData = new ArrayList<>();
        collisionData = new ArrayList<>();

        for (int i = 0; i < openQuery.size(); i++) {
            openData.add(openQuery.get(i).getRecordAt());
        }
        for (int i = 0; i < collisionQuery.size(); i++) {
            collisionData.add(collisionQuery.get(i).getRecordAt());
        }

        Collections.reverse(openData);
        Collections.reverse(collisionData);


        handler.sendEmptyMessage(1);


    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    class MainReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {

            refreshData();
        }
    }

    @Override
    public void initData() {
        //ToastUtil.showToast("refresh");
        new Thread() {
            @Override
            public void run() {
                super.run();
                refreshData();  //每次可见时 刷新数据
            }
        }.start();

    }
}
package org.redsha.transbox.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.bean.CrashBean;
import org.redsha.transbox.databinding.FragmentExceptionBinding;
import org.redsha.transbox.db.TransRecordItemDb;
import org.redsha.transbox.util.LogUtil;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * tab 3：开箱次数 / 碰撞次数
 */
public class ExceptionFragment extends Fragment {
    private final static String TAG = "ExceptionFragment";

    private FragmentExceptionBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exception, container, false);
        mBinding = DataBindingUtil.bind(view);
        return view;
    }

    private void refreshData() {
        final String tid = PrefUtils.getString("tid", "", App.getContext());
        Realm realm = RealmUtil.getInstance().getRealm();
        /**
         * 开箱数据
         */
        RealmQuery<TransRecordItemDb> openQuery = realm.where(TransRecordItemDb.class);
        openQuery.equalTo("transfer_id", tid).beginGroup();

        List<Integer> types = new ArrayList<>();
        for (int j = 0; j < 32; j++) {
            if ((j & 8) != 0) {
                types.add(j);
            }
        }

        int k = 0;
        for (Integer t : types) {
            if (k != 0) {
                openQuery = openQuery.or();
            }
            openQuery = openQuery.equalTo("type", t);
            k++;
        }

        RealmResults<TransRecordItemDb> openData = openQuery.endGroup().findAll();

        /**
         * 碰撞数据
         */
        RealmQuery<TransRecordItemDb> recrodQuery = realm.where(TransRecordItemDb.class);
        recrodQuery.equalTo("transfer_id", tid).beginGroup();

        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            if ((i & 4) != 0) {
                ids.add(i);
            }
        }
        int i = 0;
        for (Integer id : ids) {
            if (i != 0) {
                recrodQuery = recrodQuery.or();
            }
            recrodQuery = recrodQuery.equalTo("type", id);
            i++;
        }
        RealmResults<TransRecordItemDb> collisionData = recrodQuery.endGroup().findAll();

        /**
         * init数据
         */
        ArrayList<CrashBean> crashData = new ArrayList<>();
        int oSize = openData.size();
        int cSize = collisionData.size();
        if (oSize > 0 || cSize > 0) {
            int size = oSize > cSize ? oSize : cSize;   // 拿出较大的长度
            for (int m = 0; m < size; m++) {
                String openTime = "--";
                String crashTime = "--";
                if (m < openData.size()) {
                    openTime = openData.get(m).getRecordAt().substring(11, 16);
                }
                if (m < collisionData.size()) {
                    crashTime = collisionData.get(m).getRecordAt().substring(11, 16);
                }
                crashData.add(new CrashBean(openTime, crashTime));
                LogUtil.e(this, openTime + " / " + crashTime);
            }

        } else {
            crashData.add(new CrashBean("--", "--"));
        }

        ListView lvOpen = mBinding.lvOpen;
        lvOpen.setAdapter(new OpenTimeAdapter(App.getContext(), crashData));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

        refreshData();  //每次可见时 刷新数据
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TransRecordItemDb event) {
        if (event != null && event.getType() != 0) {
            refreshData();
        }
    }

}
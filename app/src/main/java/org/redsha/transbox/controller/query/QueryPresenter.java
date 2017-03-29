package org.redsha.transbox.controller.query;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.redsha.transbox.App;
import org.redsha.transbox.bean.OddDetailBean;
import org.redsha.transbox.controller.mixture.ItemDetailActivity;
import org.redsha.transbox.databinding.ActivityQueryBinding;
import org.redsha.transbox.http.HttpHelper;
import org.redsha.transbox.http.HttpObserver;

public class QueryPresenter {

    private QueryActivity mActivity;
    private QueryData mData;
    private ActivityQueryBinding mBinding;

    public QueryPresenter(QueryActivity queryActivity, QueryData info, ActivityQueryBinding binding) {
        this.mActivity = queryActivity;
        this.mData = info;
        this.mBinding = binding;
    }

    public void back() {
        mActivity.finish();
    }

    public void queryOdd(QueryData info) {
        mBinding.queryBtn.setEnabled(false);
        mBinding.queryBtn.setText("加载中...");

        boolean queryInfo = checkQueryInfo(info);
        if (queryInfo) {

            new HttpHelper().getOddDetail(info.getOddNum(), info.getOrgNum()).
                    subscribe(new HttpObserver<OddDetailBean>() {
                        @Override
                        public void onComplete() {
                            mBinding.queryBtn.setEnabled(true);
                            mBinding.queryBtn.setText("查询");
                        }

                        @Override
                        public void onSuccess(OddDetailBean model) {
                            Intent intent = new Intent(App.getContext(), ItemDetailActivity.class);
                            intent.putExtra("data", new Gson().toJson(model));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            App.getContext().startActivity(intent);
                            mActivity.finish();
                        }
                    });
        } else {
            mBinding.queryBtn.setEnabled(true);
            mBinding.queryBtn.setText("查询");
        }

    }

    private boolean checkQueryInfo(QueryData info) {

        if (TextUtils.isEmpty(info.getOddNum()) || TextUtils.isEmpty(info.getOrgNum())) {
            mActivity.showNullToast();
            return false;
        }

        return true;
    }

}

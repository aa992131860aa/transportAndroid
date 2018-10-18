package com.otqc.transbox.controller.mixture;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import com.otqc.transbox.R;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.json.TransferHistoryJson;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.DensityUtil;
import com.otqc.transbox.util.SpacesItemDecoration;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 99213 on 2017/4/21.
 */

public class BoxNumActivity extends AppBaseActivity implements View.OnClickListener {
    private Button box_num_btn_back;
    private XRecyclerView box_num_rv;

    private LinearLayoutManager mLayoutManager;
    private BoxNumAdapter mBoxNumAdapter;
    private List<TransferHistoryJson.ObjBean> mBoxNums;
    private int lastVisibleItem;
    private int pageSize = 20;
    private int page = 0;

    private String organSeg;
    private String deviceId;

    @Override
    protected void initVariable() {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.box_num);
        box_num_rv = (XRecyclerView) findViewById(R.id.box_num_rv);
        box_num_btn_back = (Button) findViewById(R.id.box_num_btn_back);
        box_num_btn_back.setOnClickListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        //RecycleView 增加边距
        int spacingInPixels = DensityUtil.dip2px(this, 10);
        box_num_rv.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        box_num_rv.setLayoutManager(mLayoutManager);
        box_num_rv.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        box_num_rv.setLoadingMoreProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mBoxNums = new ArrayList<>();
        mBoxNumAdapter = new BoxNumAdapter(mBoxNums, this);
        box_num_rv.setAdapter(mBoxNumAdapter);
        mBoxNumAdapter.setOnItemClickListener(new BoxNumAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //ToastUtil.showToast("click");

                Intent intent = new Intent(BoxNumActivity.this,ItemDetailActivity.class);
                intent.putExtra("detail",mBoxNums.get(position));
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view) {
                // ToastUtil.showToast("long click");
            }
        });
        box_num_rv.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                page = 0;
                loadData(deviceId,organSeg);
            }

            @Override
            public void onLoadMore() {
                page++;
                loadData(deviceId,organSeg);
            }
        });


    }

    @Override
    protected void initData() {

        deviceId = getIntent().getStringExtra("deviceId");
        organSeg = getIntent().getStringExtra("organSeg");

        page = 0;
        loadData(deviceId,organSeg);



    }

    private void loadData(String deviceId, String organSeg) {
        showWaitDialog("loading", true, "loading");

        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getPadTransferHistory");
        params.addBodyParameter("deviceId", deviceId);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("page", 0 + "");
        params.addBodyParameter("pageSize", CONSTS.PAGE_SIZE + "");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                TransferHistoryJson transferJson = new Gson().fromJson(result, TransferHistoryJson.class);
                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {
                    if (page == 0) {
                        mBoxNums = transferJson.getObj();
                    } else {
                        mBoxNums.addAll(transferJson.getObj());
                    }
                    mBoxNumAdapter.refreshList(mBoxNums);
                }
                dismissDialog();
                box_num_rv.refreshComplete();
                box_num_rv.loadMoreComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                dismissDialog();
                box_num_rv.refreshComplete();
                box_num_rv.loadMoreComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.box_num_btn_back:
                this.finish();
                break;

        }
    }
}

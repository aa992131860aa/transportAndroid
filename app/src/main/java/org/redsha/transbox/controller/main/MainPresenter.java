package org.redsha.transbox.controller.main;

import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.redsha.transbox.App;
import org.redsha.transbox.bean.BoxBean;
import org.redsha.transbox.bean.KeywordBean;
import org.redsha.transbox.controller.create.ConfirmCreateActivity;
import org.redsha.transbox.controller.on.OnWayActivity;
import org.redsha.transbox.controller.query.QueryActivity;
import org.redsha.transbox.http.HttpHelper;
import org.redsha.transbox.http.HttpObserver;
import org.redsha.transbox.service.LinkService;
import org.redsha.transbox.util.A;
import org.redsha.transbox.util.CommonUtil;
import org.redsha.transbox.util.PrefUtils;

public class MainPresenter {

    public MainPresenter() {
        getBoxInfo();
        getKey();
        initLinkService();
    }

    private void initLinkService() {
        Intent intent = new Intent(App.getContext(), LinkService.class);
        App.getContext().startService(intent);
    }

    /**
     * 查历史
     */
    public void checkHistory() {
        Intent intent = new Intent(App.getContext(), QueryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    /**
     * 新建转运
     */
    public void createNewTrans() {
        Intent intent = new Intent(App.getContext(), ConfirmCreateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    private void getBoxInfo() {
        new HttpHelper().getBoxInfo(CommonUtil.getIMEI()).subscribe(new HttpObserver<BoxBean>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(BoxBean model) {
                PrefUtils.putString("boxid", model.getBoxid(), App.getContext());
                PrefUtils.putString("qrcode", model.getQrcode(), App.getContext());
                PrefUtils.putString("hospitalid", model.getHospital().getHospitalid(), App.getContext());
                PrefUtils.putString("hospitalName", model.getHospital().getName(), App.getContext());

                A.isBoxInfo = true;

                if (!model.getTransferStatus().equals("free") && !TextUtils.isEmpty(model.getTransfer_id())) {
                    PrefUtils.putString("tid", model.getTransfer_id(), App.getContext());

                    Intent intent = new Intent(App.getContext(), OnWayActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.getContext().startActivity(intent);
                }
            }
        });
    }

    private void getKey() {

        new HttpHelper().getKey().subscribe(new HttpObserver<KeywordBean>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(KeywordBean model) {
                PrefUtils.putString("key", new Gson().toJson(model), App.getContext());

                A.isKwdInfo = true;
            }
        });
    }

}
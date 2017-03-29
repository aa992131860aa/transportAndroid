package org.redsha.transbox.controller.mixture;


import android.content.Intent;

import org.redsha.transbox.App;

public class ItemDetailPresenter {

    private ItemDetailActivity mActivity;
    private ItemDetailData mData;

    public ItemDetailPresenter(ItemDetailActivity mActivity, ItemDetailData mData) {
        this.mActivity = mActivity;
        this.mData = mData;
    }

    public void back() {
        mActivity.finish();
    }

    /**
     * 去导出界面
     */
    public void goExport() {
        Intent intent = new Intent(App.getContext(), ExportActivity.class);
        intent.putExtra("exporttid", mData.getTransferid());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

}

package org.redsha.transbox.controller.on;


import android.content.Intent;

import org.redsha.transbox.App;

public class OnWayPresenter {

    /**
     * 去 确认结束
     */
    public void goFinishTs() {
        Intent intent = new Intent(App.getContext(), ConfirmFinishTsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    public void checkDetail(OnWayData info) {
        switch (info.getOnWayPageState()) {
            case 0:
                Intent i = new Intent(App.getContext(), MorrisActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(i);
                break;
            case 1:
                Intent intent = new Intent(App.getContext(), MapDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.getContext().startActivity(intent);
                break;
        }
    }

    /**
     * 开箱
     */
    public void openBox() {
        Intent i = new Intent(App.getContext(), OpenBoxActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(i);
    }

}

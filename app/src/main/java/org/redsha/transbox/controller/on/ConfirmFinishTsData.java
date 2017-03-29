package org.redsha.transbox.controller.on;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.redsha.transbox.BR;

public class ConfirmFinishTsData extends BaseObservable {

    private String openPs1;
//    private String openPs2;
//    private String openPs3;
//    private String openPs4;

    @Bindable
    public String getOpenPs1() {
        return openPs1;
    }

    public void setOpenPs1(String openPs1) {
        this.openPs1 = openPs1;
        notifyPropertyChanged(BR.openPs1);
    }

//    @Bindable
//    public String getOpenPs2() {
//        return openPs2;
//    }
//
//    public void setOpenPs2(String openPs2) {
//        this.openPs2 = openPs2;
//        notifyPropertyChanged(BR.openPs2);
//    }
//
//    @Bindable
//    public String getOpenPs3() {
//        return openPs3;
//    }
//
//    public void setOpenPs3(String openPs3) {
//        this.openPs3 = openPs3;
//        notifyPropertyChanged(BR.openPs3);
//    }
//
//    @Bindable
//    public String getOpenPs4() {
//        return openPs4;
//    }
//
//    public void setOpenPs4(String openPs4) {
//        this.openPs4 = openPs4;
//        notifyPropertyChanged(BR.openPs4);
//    }

}

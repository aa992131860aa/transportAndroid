package com.otqc.transbox.controller.query;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class QueryData extends BaseObservable {

    //private String oddNum;
    private String orgNum;

//    @Bindable
//    public String getOddNum() {
//        return oddNum;
//    }
//
//    public void setOddNum(String oddNum) {
//        this.oddNum = oddNum;
//        notifyPropertyChanged(BR.oddNum);
//    }

    @Bindable
    public String getOrgNum() {
        return orgNum;
    }

    public void setOrgNum(String orgNum) {
        this.orgNum = orgNum;
        notifyPropertyChanged(BR.orgNum);
    }

}

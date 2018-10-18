package com.otqc.transbox.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class CreateTransToInfo extends BaseObservable {

    private String toHospName;  //转运目的地
    private String dataType;

    @Bindable
    public String getToHospName() {
        return toHospName;
    }

    public void setToHospName(String toHospName) {
        this.toHospName = toHospName;
        notifyPropertyChanged(BR.toHospName);
    }

    @Bindable
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
        notifyPropertyChanged(BR.dataType);
    }

}

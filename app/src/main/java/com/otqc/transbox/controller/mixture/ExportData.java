package com.otqc.transbox.controller.mixture;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class ExportData extends BaseObservable {
    private String phone;

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

}

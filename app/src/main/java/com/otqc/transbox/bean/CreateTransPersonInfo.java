package com.otqc.transbox.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class CreateTransPersonInfo extends BaseObservable {

    private String name;    //转运人名称
    private String phone;    //转运人手机
    private String dataType;
    private String transferPersonid;    //转运人id

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
        notifyPropertyChanged(BR.dataType);
    }

    @Bindable
    public String getTransferPersonid() {
        return transferPersonid;
    }

    public void setTransferPersonid(String transferPersonid) {
        this.transferPersonid = transferPersonid;
        notifyPropertyChanged(BR.transferPersonid);
    }

}

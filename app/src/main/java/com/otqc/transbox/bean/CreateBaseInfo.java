package com.otqc.transbox.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class CreateBaseInfo extends BaseObservable {
    private String box_id;  //箱子id
    private String boxPin;  // 开箱密码
    private String getOrganAt;  // 获取器官时间
    private String organCount;     // 器官数量
    private String fromCity;    // 起始地
    private String tracfficType;    // 转运方式
    private String tracfficNumber;    // 转运车号
    private String deviceType;

    @Bindable
    public String getBox_id() {
        return box_id;
    }

    public void setBox_id(String box_id) {
        this.box_id = box_id;
        notifyPropertyChanged(BR.box_id);
    }

    @Bindable
    public String getBoxPin() {
        return boxPin;
    }

    public void setBoxPin(String boxPin) {
        this.boxPin = boxPin;
        notifyPropertyChanged(BR.boxPin);
    }

    @Bindable
    public String getGetOrganAt() {
        return getOrganAt;
    }

    public void setGetOrganAt(String getOrganAt) {
        this.getOrganAt = getOrganAt;
        notifyPropertyChanged(BR.getOrganAt);
    }

    @Bindable
    public String getOrganCount() {
        return organCount;
    }

    public void setOrganCount(String organCount) {
        this.organCount = organCount;
        notifyPropertyChanged(BR.organCount);
    }

    @Bindable
    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
        notifyPropertyChanged(BR.fromCity);
    }

    @Bindable
    public String getTracfficType() {
        return tracfficType;
    }

    public void setTracfficType(String tracfficType) {
        this.tracfficType = tracfficType;
        notifyPropertyChanged(BR.tracfficType);
    }

    @Bindable
    public String getTracfficNumber() {
        return tracfficNumber;
    }

    public void setTracfficNumber(String tracfficNumber) {
        this.tracfficNumber = tracfficNumber;
        notifyPropertyChanged(BR.tracfficNumber);
    }

    @Bindable
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        notifyPropertyChanged(BR.deviceType);
    }
}

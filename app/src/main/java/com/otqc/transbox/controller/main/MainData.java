package com.otqc.transbox.controller.main;


import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class MainData extends BaseObservable {
    private String serialStatus;
    private String temperature;
    private String power;

    @Bindable
    public String getSerialStatus() {
        return serialStatus;
    }

    public void setSerialStatus(String serialStatus) {
        this.serialStatus = serialStatus;
        notifyPropertyChanged(BR.serialStatus);
    }

    @Bindable
    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
        notifyPropertyChanged(BR.temperature);
    }

    @Bindable
    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
        notifyPropertyChanged(BR.power);
    }

}

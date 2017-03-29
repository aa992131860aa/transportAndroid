package org.redsha.transbox.controller.on;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.redsha.transbox.BR;

public class OnEndData extends BaseObservable {
    private String duration;
    private String avgTemperature;
    private String power;

    @Bindable
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }

    @Bindable
    public String getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(String avgTemperature) {
        this.avgTemperature = avgTemperature;
        notifyPropertyChanged(BR.avgTemperature);
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

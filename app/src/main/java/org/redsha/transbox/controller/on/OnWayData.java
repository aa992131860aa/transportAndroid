package org.redsha.transbox.controller.on;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.redsha.transbox.BR;

public class OnWayData extends BaseObservable {

    private int onWayPageState;
    private String onWayPageShow;
    // page1
    private String temperature;
    private String avgTemperature;
    private String power;
    private String expendPower;
    private String humidity;

    // page2
    private String startTime;
    private String duration;
    private String currentCity;
    private String distance;

    @Bindable
    public int getOnWayPageState() {
        return onWayPageState;
    }

    public void setOnWayPageState(int onWayPageState) {
        this.onWayPageState = onWayPageState;
        notifyPropertyChanged(BR.onWayPageState);
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

    @Bindable
    public String getExpendPower() {
        return expendPower;
    }

    public void setExpendPower(String expendPower) {
        this.expendPower = expendPower;
        notifyPropertyChanged(BR.expendPower);
    }

    @Bindable
    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
        notifyPropertyChanged(BR.humidity);
    }

    @Bindable
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        notifyPropertyChanged(BR.startTime);
    }

    @Bindable
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
        notifyPropertyChanged(BR.duration);
    }

    @Bindable
    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
        notifyPropertyChanged(BR.currentCity);
    }

    @Bindable
    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
        notifyPropertyChanged(BR.distance);
    }

    @Bindable
    public String getOnWayPageShow() {
        return onWayPageShow;
    }

    public void setOnWayPageShow(String onWayPageShow) {
        this.onWayPageShow = onWayPageShow;
        notifyPropertyChanged(BR.onWayPageShow);
    }
}

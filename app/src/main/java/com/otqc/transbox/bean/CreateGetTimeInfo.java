package com.otqc.transbox.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class CreateGetTimeInfo extends BaseObservable {
    private String yy;
    private String mm;
    private String dd;
    private String hh;
    private String m;
    private String ss;

    @Bindable
    public String getYy() {
        return yy;
    }

    public void setYy(String yy) {
        this.yy = yy;
        notifyPropertyChanged(BR.yy);
    }

    @Bindable
    public String getMm() {
        return mm;
    }

    public void setMm(String mm) {
        this.mm = mm;
        notifyPropertyChanged(BR.mm);
    }

    @Bindable
    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
        notifyPropertyChanged(BR.dd);
    }

    @Bindable
    public String getHh() {
        return hh;
    }

    public void setHh(String hh) {
        this.hh = hh;
        notifyPropertyChanged(BR.hh);
    }

    @Bindable
    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
        notifyPropertyChanged(BR.m);
    }

    @Bindable
    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
        notifyPropertyChanged(BR.ss);
    }

}

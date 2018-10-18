package com.otqc.transbox.controller.create;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;
import com.otqc.transbox.bean.CreateBaseInfo;
import com.otqc.transbox.bean.CreateGetTimeInfo;
import com.otqc.transbox.bean.CreateOpenPsInfo;
import com.otqc.transbox.bean.CreateOpoInfo;
import com.otqc.transbox.bean.CreateOrganInfo;
import com.otqc.transbox.bean.CreateTransPersonInfo;
import com.otqc.transbox.bean.CreateTransToInfo;

public class CreateData extends BaseObservable {
    private int pageState;  //当前页面状态
    private String pageShow;    //根据当前页面状态显示不同文字
    private int transPersonSize;

    private CreateGetTimeInfo time;   //时间
    private CreateOpenPsInfo ps;
//    private CreateOpenPsConfirmInfo cps;

    // order info
    private CreateBaseInfo baseInfo;
    private CreateOrganInfo organ;
    private CreateTransPersonInfo person;
    private CreateTransToInfo to;
    private CreateOpoInfo opo;

    @Bindable
    public int getPageState() {
        return pageState;
    }

    public void setPageState(int pageState) {
        this.pageState = pageState;
        notifyPropertyChanged(BR.pageState);
    }

    @Bindable
    public String getPageShow() {
        return pageShow;
    }

    public void setPageShow(String pageShow) {
        this.pageShow = pageShow;
        notifyPropertyChanged(BR.pageShow);
    }

    public CreateGetTimeInfo getTime() {
        return time;
    }

    public void setTime(CreateGetTimeInfo time) {
        this.time = time;
    }

    public CreateOpenPsInfo getPs() {
        return ps;
    }

    public void setPs(CreateOpenPsInfo ps) {
        this.ps = ps;
    }
//
//    public CreateOpenPsConfirmInfo getCps() {
//        return cps;
//    }
//
//    public void setCps(CreateOpenPsConfirmInfo cps) {
//        this.cps = cps;
//    }

    public CreateBaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(CreateBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public CreateOrganInfo getOrgan() {
        return organ;
    }

    public void setOrgan(CreateOrganInfo organ) {
        this.organ = organ;
    }

    public CreateTransPersonInfo getPerson() {
        return person;
    }

    public void setPerson(CreateTransPersonInfo person) {
        this.person = person;
    }

    public CreateTransToInfo getTo() {
        return to;
    }

    public void setTo(CreateTransToInfo to) {
        this.to = to;
    }

    public CreateOpoInfo getOpo() {
        return opo;
    }

    public void setOpo(CreateOpoInfo opo) {
        this.opo = opo;
    }

    @Bindable
    public int getTransPersonSize() {
        return transPersonSize;
    }

    public void setTransPersonSize(int transPersonSize) {
        this.transPersonSize = transPersonSize;
        notifyPropertyChanged(BR.transPersonSize);
    }

}

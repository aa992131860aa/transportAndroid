package com.otqc.transbox.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class CreateOrganInfo extends BaseObservable {
    private String segNumber;   //器官段号
    private String type;        //器官类型
    private String bloodType;   //血型
    private String bloodSampleCount;   //血液样本数量
    private String organizationSampleType;   //组织样本类型
    private String organizationSampleCount;   //组织样本数量
    private String dataType;

    @Bindable
    public String getSegNumber() {
        return segNumber;
    }

    public void setSegNumber(String segNumber) {
        this.segNumber = segNumber;
        notifyPropertyChanged(BR.segNumber);
    }

    @Bindable
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        notifyPropertyChanged(BR.type);
    }

    @Bindable
    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
        notifyPropertyChanged(BR.bloodType);
    }

    @Bindable
    public String getBloodSampleCount() {
        return bloodSampleCount;
    }

    public void setBloodSampleCount(String bloodSampleCount) {
        this.bloodSampleCount = bloodSampleCount;
        notifyPropertyChanged(BR.bloodSampleCount);
    }

    @Bindable
    public String getOrganizationSampleType() {
        return organizationSampleType;
    }

    public void setOrganizationSampleType(String organizationSampleType) {
        this.organizationSampleType = organizationSampleType;
        notifyPropertyChanged(BR.organizationSampleType);
    }

    @Bindable
    public String getOrganizationSampleCount() {
        return organizationSampleCount;
    }

    public void setOrganizationSampleCount(String organizationSampleCount) {
        this.organizationSampleCount = organizationSampleCount;
        notifyPropertyChanged(BR.organizationSampleCount);
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

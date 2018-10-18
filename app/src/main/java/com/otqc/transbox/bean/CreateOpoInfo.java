package com.otqc.transbox.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.otqc.transbox.BR;

public class CreateOpoInfo extends BaseObservable {
    private String name;
    private String contactPerson;
    private String contactPhone;
    private String opoid;
    private String dataType;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        notifyPropertyChanged(BR.contactPerson);
    }

    @Bindable
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        notifyPropertyChanged(BR.contactPhone);
    }

    @Bindable
    public String getOpoid() {
        return opoid;
    }

    public void setOpoid(String opoid) {
        this.opoid = opoid;
        notifyPropertyChanged(BR.opoid);
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

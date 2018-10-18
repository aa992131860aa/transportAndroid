package com.otqc.transbox.bean;

/**
 * Created by 99213 on 2017/4/23.
 */

public class BoxNum {
    private String transferid;
    private String transferNumber;
    private String fromCity;
    private String toHospName;
    private String createAt ;
    private String segNumber;

    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToHospName() {
        return toHospName;
    }

    public void setToHospName(String toHospName) {
        this.toHospName = toHospName;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getSegNumber() {
        return segNumber;
    }

    public void setSegNumber(String segNumber) {
        this.segNumber = segNumber;
    }
}

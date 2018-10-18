package com.otqc.transbox.controller.mixture;


import com.otqc.transbox.bean.QueryBoxInfoBean;
import com.otqc.transbox.bean.QueryOpoInfoBean;
import com.otqc.transbox.bean.QueryOrganInfoBean;
import com.otqc.transbox.bean.QueryRecordItemBean;
import com.otqc.transbox.bean.QueryToHospitalInfoBean;
import com.otqc.transbox.bean.QueryTransferPersonInfoBean;

import java.util.List;

public class ItemDetailData {

    private String transferid;
    private String transferNumber;
    private String organCount;
    private String boxPin;
    private String fromCity;
    private String tracfficType;
    private String tracfficNumber;
    private String deviceType;
    private String getOrganAt;
    private String startAt;
    private String status;
    private String createAt;
    private String modifyAt;

    private QueryBoxInfoBean boxInfo;

    private QueryOrganInfoBean organInfo;

    private QueryToHospitalInfoBean toHospitalInfo;

    private QueryTransferPersonInfoBean transferPersonInfo;

    private QueryOpoInfoBean opoInfo;

    private List<QueryRecordItemBean> records;


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

    public String getOrganCount() {
        return organCount;
    }

    public void setOrganCount(String organCount) {
        this.organCount = organCount;
    }

    public String getBoxPin() {
        return boxPin;
    }

    public void setBoxPin(String boxPin) {
        this.boxPin = boxPin;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getTracfficType() {
        return tracfficType;
    }

    public void setTracfficType(String tracfficType) {
        this.tracfficType = tracfficType;
    }

    public String getTracfficNumber() {
        return tracfficNumber;
    }

    public void setTracfficNumber(String tracfficNumber) {
        this.tracfficNumber = tracfficNumber;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getGetOrganAt() {
        return getOrganAt;
    }

    public void setGetOrganAt(String getOrganAt) {
        this.getOrganAt = getOrganAt;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(String modifyAt) {
        this.modifyAt = modifyAt;
    }

    public QueryBoxInfoBean getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(QueryBoxInfoBean boxInfo) {
        this.boxInfo = boxInfo;
    }

    public QueryOrganInfoBean getOrganInfo() {
        return organInfo;
    }

    public void setOrganInfo(QueryOrganInfoBean organInfo) {
        this.organInfo = organInfo;
    }

    public QueryToHospitalInfoBean getToHospitalInfo() {
        return toHospitalInfo;
    }

    public void setToHospitalInfo(QueryToHospitalInfoBean toHospitalInfo) {
        this.toHospitalInfo = toHospitalInfo;
    }

    public QueryTransferPersonInfoBean getTransferPersonInfo() {
        return transferPersonInfo;
    }

    public void setTransferPersonInfo(QueryTransferPersonInfoBean transferPersonInfo) {
        this.transferPersonInfo = transferPersonInfo;
    }

    public QueryOpoInfoBean getOpoInfo() {
        return opoInfo;
    }

    public void setOpoInfo(QueryOpoInfoBean opoInfo) {
        this.opoInfo = opoInfo;
    }

    public List<QueryRecordItemBean> getRecords() {
        return records;
    }

    public void setRecords(List<QueryRecordItemBean> records) {
        this.records = records;
    }
}

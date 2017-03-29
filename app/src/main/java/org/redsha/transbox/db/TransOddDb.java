package org.redsha.transbox.db;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TransOddDb extends RealmObject {

    @PrimaryKey
    private String transferid;
    private String transferNumber;
    private int organCount;
    private String boxPin;
    private String fromCity;
    private String toHospName;
    private String tracfficType;
    private String tracfficNumber;
    private String deviceType;
    private String getOrganAt;
    private String startAt;
    private String status;
    private String createAt;
    private String modifyAt;

    private BoxInfoDb boxInfo;

    private OrganInfoDb organInfo;

    private ToHospitalInfoDb toHospitalInfo;

    private TransferPersonInfoDb transferPersonInfo;

    private OpoInfoDb opoInfo;

    public String getToHospName() {
        return toHospName;
    }

    public void setToHospName(String toHospName) {
        this.toHospName = toHospName;
    }

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

    public int getOrganCount() {
        return organCount;
    }

    public void setOrganCount(int organCount) {
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

    public BoxInfoDb getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(BoxInfoDb boxInfo) {
        this.boxInfo = boxInfo;
    }

    public OrganInfoDb getOrganInfo() {
        return organInfo;
    }

    public void setOrganInfo(OrganInfoDb organInfo) {
        this.organInfo = organInfo;
    }

    public ToHospitalInfoDb getToHospitalInfo() {
        return toHospitalInfo;
    }

    public void setToHospitalInfo(ToHospitalInfoDb toHospitalInfo) {
        this.toHospitalInfo = toHospitalInfo;
    }

    public TransferPersonInfoDb getTransferPersonInfo() {
        return transferPersonInfo;
    }

    public void setTransferPersonInfo(TransferPersonInfoDb transferPersonInfo) {
        this.transferPersonInfo = transferPersonInfo;
    }

    public OpoInfoDb getOpoInfo() {
        return opoInfo;
    }

    public void setOpoInfo(OpoInfoDb opoInfo) {
        this.opoInfo = opoInfo;
    }
}

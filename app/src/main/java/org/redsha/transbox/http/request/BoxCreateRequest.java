package org.redsha.transbox.http.request;

public class BoxCreateRequest {
    private String boxNumber;
    private String deviceId;
    private String hosp_id;
    private String remark;

    public BoxCreateRequest(String boxNumber, String deviceId, String hosp_id, String remark) {
        this.boxNumber = boxNumber;
        this.deviceId = deviceId;
        this.hosp_id = hosp_id;
        this.remark = remark;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getHosp_id() {
        return hosp_id;
    }

    public void setHosp_id(String hosp_id) {
        this.hosp_id = hosp_id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

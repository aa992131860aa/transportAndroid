package org.redsha.transbox.http.request;

/**
 * Created by Yian on 2016/11/26.
 */

public class TransRecordItemRequest {

    private String transferRecordid; // random主键
    // p1
    private String temperature; //当前温度
    private String avgTemperature; //当前温度
    private String power;    //剩余电量
    private String expendPower;    //消耗电量
    private String humidity;    //湿度
    // p2
    private String duration;    //持续时间
    private String currentCity;    //当前位置
    private String longitude;   // 经度
    private String latitude;   // 纬度
    private String distance;   // 剩余距离
    // mix
    private String transfer_id; //转运id
    private String recordAt;    //这条信息的记录时间
    private int type;    // 规则
    private String remark;  // 描述


    public String getTransferRecordid() {
        return transferRecordid;
    }

    public void setTransferRecordid(String transferRecordid) {
        this.transferRecordid = transferRecordid;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(String avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getExpendPower() {
        return expendPower;
    }

    public void setExpendPower(String expendPower) {
        this.expendPower = expendPower;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(String transfer_id) {
        this.transfer_id = transfer_id;
    }

    public String getRecordAt() {
        return recordAt;
    }

    public void setRecordAt(String recordAt) {
        this.recordAt = recordAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

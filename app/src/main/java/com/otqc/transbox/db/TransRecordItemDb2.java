package com.otqc.transbox.db;

import io.realm.annotations.PrimaryKey;

/**
 * 转运监控中item
 * 开箱时间、碰撞时间 每次生成一条记录
 * 转运开始时间：生成转运单的时间。
 */
public class TransRecordItemDb2  {

    @PrimaryKey
    private String transferRecordid; // random主键
    // p1
    private String temperature; //当前温度
    private String avgTemperature; //当前温度
    private String power;    //剩余电量
    private String expendPower;    //消耗电量
    private String humidity;    //湿度
    private String collision; //碰撞
    private String open; //开箱

    private int num; //标识数据
    // p2
    private String duration;    //持续时间
    private String currentCity;    //当前位置
    private String longitude;   // 经度
    private String latitude;   // 纬度
    private String distance;   // 剩余距离
    // mix
    private String transfer_id; //转运id
    private String recordAt;    //这条信息的记录时间
    private int isUp;   // 1 未上传，2 上传成功
    private int type;    // 规则
    private String remark;  // 描述
    // p3


    @Override
    public String toString() {
        return "transfer_id:"+transfer_id+",temperature:"+temperature+",power:"+power+",humidity:"+humidity+",collision:"
                +collision+",open:"+open+",duration:"+duration+",currentCity:"+currentCity;
    }

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

    public int isUp() {
        return isUp;
    }

    public void setUp(int up) {
        isUp = up;
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

    public String getCollision() {
        return collision;
    }

    public void setCollision(String collision) {
        this.collision = collision;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}

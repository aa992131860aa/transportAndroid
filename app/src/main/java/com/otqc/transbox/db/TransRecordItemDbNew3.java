package com.otqc.transbox.db;

import com.otqc.transbox.util.CONSTS;

import com.otqc.transbox.util.CONSTS;

import com.otqc.transbox.util.CONSTS;

import com.otqc.transbox.util.CONSTS;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 转运监控中item
 * 开箱时间、碰撞时间 每次生成一条记录
 * 转运开始时间：生成转运单的时间。
 */
public class TransRecordItemDbNew3 extends RealmObject {

    @PrimaryKey
    private String transferRecordid; // random主键
    // p1
    private String temperature; //当前温度
    private String avgTemperature; //当前温度
    private String power;    //剩余电量
    private String expendPower;    //消耗电量
    private String humidity;    //湿度
    private double collision = 0; //碰撞
    private double open = 0; //开箱

    private int num; //标识数据
    // p2
    private double duration;    //持续时间
    private String currentCity;    //当前位置
    private String longitude;   // 经度
    private String latitude;   // 纬度
    private double distance;   // 剩余距离
    // mix
    private String transfer_id; //转运id
    private String recordAt;    //这条信息的记录时间
    private int isUp;   // 1 未上传，2 上传成功
    private int type;    // 规则
    private String remark;  // 描述 替换为箱子ID,
    // p3
    private String voltage;
    private String trueTemperature;
    private String other;


    @Override
    public String toString() {
        return "{\"transfer_id\":\"" + transfer_id + "\",\"temperature\":\"" + temperature + "\",\"power\":\"" + power + "\",\"humidity\":\"" + humidity + "\",\"collision\":\""
                + collision + "\",\"open\":\"" + open + "\",\"duration\":\"" + duration + "\",\"currentCity\":\"" + currentCity + "\",\"expendPower\":\"" + expendPower +
                "\",\"recordAt\":\"" + recordAt + "\",\"distance\":\"" + distance + "\",\"longitude\":\"" + longitude + "\",\"latitude\":\"" + latitude + "\",\"transferId\":\"" + CONSTS.TRANSFER_ID + "\",\"remark\":\"" + remark + "\"}";
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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
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

    public double getCollision() {
        return collision;
    }

    public void setCollision(double collision) {
        this.collision = collision;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getIsUp() {
        return isUp;
    }

    public void setIsUp(int isUp) {
        this.isUp = isUp;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getTrueTemperature() {
        return trueTemperature;
    }

    public void setTrueTemperature(String trueTemperature) {
        this.trueTemperature = trueTemperature;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}

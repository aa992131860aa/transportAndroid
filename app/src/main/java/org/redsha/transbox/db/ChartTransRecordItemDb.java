package org.redsha.transbox.db;

/**
 * 转运监控中item    --- morris / map 需要的数据
 */
public class ChartTransRecordItemDb {

    private String temperature; //当前温度
    private String longitude;   // 经度
    private String latitude;   // 纬度
    // mix
    private String transfer_id; //转运id

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
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
}
package org.redsha.transbox.bean;

public class ChartDataBean {

    private String temperature; //当前温度
    private String humidity;    //湿度
    private String recordAt;    //这条信息的记录时间

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getRecordAt() {
        return recordAt;
    }

    public void setRecordAt(String recordAt) {
        this.recordAt = recordAt;
    }
}

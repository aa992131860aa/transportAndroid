package org.redsha.transbox.service.event;

/**
 * Created by Yian on 2016/11/22.
 */

public class MainEvent {
    private String serialStatus;
    private String temperature;
    private String power;

    public String getSerialStatus() {
        return serialStatus;
    }

    public void setSerialStatus(String serialStatus) {
        this.serialStatus = serialStatus;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

}

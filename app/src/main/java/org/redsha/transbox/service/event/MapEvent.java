package org.redsha.transbox.service.event;


public class MapEvent {
    public static String city;
    public static String lont;
    public static String lati;
    public static String Distance;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLont() {
        return lont;
    }

    public void setLont(String lont) {
        this.lont = lont;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    @Override
    public String toString() {
        return "MapEvent{" +
                "city='" + city + '\'' +
                ", lont='" + lont + '\'' +
                ", lati='" + lati + '\'' +
                ", Distance='" + Distance + '\'' +
                '}';
    }
}

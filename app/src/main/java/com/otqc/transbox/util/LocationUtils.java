package com.otqc.transbox.util;

import com.amap.api.maps.model.LatLng;

import com.otqc.transbox.db.TransRecord;

import java.util.List;

public class LocationUtils {
    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10d;

        return s;
    }

    public static double getMoveDistance(List<LatLng> mLongitudes) {
        double endLat;
        double endLong;
        double startLat;
        double startLong;
        double mDistance = 0;
        for (int i = 0; i < mLongitudes.size(); i++) {


            startLat = mLongitudes.get(i).latitude;
            startLong = mLongitudes.get(i).longitude;


            endLat = mLongitudes.get(mLongitudes.size() - 1).latitude;
            endLong = mLongitudes.get(mLongitudes.size() - 1).longitude;


            mDistance = LocationUtils.getDistance(endLat, endLong, startLat, startLong);
            if (mDistance > 0) {
                break;
            }
        }
        return mDistance;
    }

    public static double getMoveDistanceRecord(List<TransRecord> mLongitudes) {
        double endLat;
        double endLong;
        double startLat;
        double startLong;
        double mDistance = 0;
        for (int i = 0; i < mLongitudes.size(); i++) {


            startLat = Double.parseDouble(mLongitudes.get(i).getLatitude());
            startLong = Double.parseDouble(mLongitudes.get(i).getLongitude());


            endLat = Double.parseDouble(mLongitudes.get(mLongitudes.size() - 1).getLatitude());
            endLong = Double.parseDouble(mLongitudes.get(mLongitudes.size() - 1).getLongitude());


            mDistance = LocationUtils.getDistance(endLat, endLong, startLat, startLong);
            if (mDistance > 0) {
                break;
            }
        }
        return mDistance;
    }
}  
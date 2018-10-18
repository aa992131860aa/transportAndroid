package com.otqc.transbox.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/10.
 */

public class LatiLongJson {


    /**
     * status : 1
     * info : OK
     * infocode : 10000
     * count : 1
     * geocodes : [{"formatted_address":"浙江省杭州市下城区东新路|848号","province":"浙江省","citycode":"0571","city":"杭州市","district":"下城区","township":[],"neighborhood":{"name":[],"type":[]},"building":{"name":[],"type":[]},"adcode":"330103","street":"东新路","number":"848号","location":"120.175088,30.329957","level":"门牌号"}]
     */

    private String status;
    private String info;
    private String infocode;
    private String count;
    private GeocodesBean [] geocodes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfocode() {
        return infocode;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public GeocodesBean[] getGeocodes() {
        return geocodes;
    }

    public void setGeocodes(GeocodesBean[] geocodes) {
        this.geocodes = geocodes;
    }

    public static class GeocodesBean {
        /**
         * formatted_address : 浙江省杭州市下城区东新路|848号
         * province : 浙江省
         * citycode : 0571
         * city : 杭州市
         * district : 下城区
         * township : []
         * neighborhood : {"name":[],"type":[]}
         * building : {"name":[],"type":[]}
         * adcode : 330103
         * street : 东新路
         * number : 848号
         * location : 120.175088,30.329957
         * level : 门牌号
         */




        private String location;




        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }


        public static class NeighborhoodBean {
            private List<?> name;
            private List<?> type;

            public List<?> getName() {
                return name;
            }

            public void setName(List<?> name) {
                this.name = name;
            }

            public List<?> getType() {
                return type;
            }

            public void setType(List<?> type) {
                this.type = type;
            }
        }

        public static class BuildingBean {
            private List<?> name;
            private List<?> type;

            public List<?> getName() {
                return name;
            }

            public void setName(List<?> name) {
                this.name = name;
            }

            public List<?> getType() {
                return type;
            }

            public void setType(List<?> type) {
                this.type = type;
            }
        }
    }
}

package com.otqc.transbox.json;

/**
 * Created by 99213 on 2017/7/29.
 */

public class TransferJson {

    /**
     * result : 0
     * msg : 获取转运中的信息
     * obj : {"transferid":"456","organSeg":"99999","openPsd":"","fromCity":"杭州市江干区","toHospName":"四川省人民医院","tracfficType":"飞机","tracfficNumber":"","organ":"心","organNum":"1","blood":"A","bloodNum":"1","sampleOrgan":"脾脏","sampleOrganNum":"1","opoName":"南京军区第117医院OPO","contactName":"唐金梅","contactPhone":"15336568476","phone":"15336568476","trueName":"唐金梅","getTime":"2017-07-23 22:02","isStart":"1","startLong":"120.329365","startLati":"30.287901","endLong":"104.039636","endLati":"30.663642","distance":"1563.40","toHosp":"成都"}
     */

    private int result;
    private String msg;
    private ObjBean obj;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public static class ObjBean {
        /**
         * transferid : 456
         * organSeg : 99999
         * openPsd :
         * fromCity : 杭州市江干区
         * toHospName : 四川省人民医院
         * tracfficType : 飞机
         * tracfficNumber :
         * organ : 心
         * organNum : 1
         * blood : A
         * bloodNum : 1
         * sampleOrgan : 脾脏
         * sampleOrganNum : 1
         * opoName : 南京军区第117医院OPO
         * contactName : 唐金梅
         * contactPhone : 15336568476
         * phone : 15336568476
         * trueName : 唐金梅
         * getTime : 2017-07-23 22:02
         * isStart : 1
         * startLong : 120.329365
         * startLati : 30.287901
         * endLong : 104.039636
         * endLati : 30.663642
         * distance : 1563.40
         * toHosp : 成都
         */

        private String transferid;
        private String organSeg;
        private String openPsd;
        private String fromCity;
        private String toHospName;
        private String tracfficType;
        private String tracfficNumber;
        private String organ;
        private String organNum;
        private String blood;
        private String bloodNum;
        private String sampleOrgan;
        private String sampleOrganNum;
        private String opoName;
        private String contactName;
        private String contactPhone;
        private String opoContactName;
        private String opoContactPhone;
        private String phone;
        private String trueName;
        private String getTime;
        private String isStart;
        private String startLong;
        private String startLati;
        private String endLong;
        private String endLati;
        private String distance;
        private String toHosp;
        private String boxNo;
        private String phones;
        private String modifyOrganSeg;

        public String getTransferid() {
            return transferid;
        }

        public void setTransferid(String transferid) {
            this.transferid = transferid;
        }

        public String getOrganSeg() {
            return organSeg;
        }

        public void setOrganSeg(String organSeg) {
            this.organSeg = organSeg;
        }

        public String getOpenPsd() {
            return openPsd;
        }

        public void setOpenPsd(String openPsd) {
            this.openPsd = openPsd;
        }

        public String getFromCity() {
            return fromCity;
        }

        public void setFromCity(String fromCity) {
            this.fromCity = fromCity;
        }

        public String getToHospName() {
            return toHospName;
        }

        public void setToHospName(String toHospName) {
            this.toHospName = toHospName;
        }

        public String getTracfficType() {
            return tracfficType;
        }

        public void setTracfficType(String tracfficType) {
            this.tracfficType = tracfficType;
        }

        public String getTracfficNumber() {
            return tracfficNumber;
        }

        public void setTracfficNumber(String tracfficNumber) {
            this.tracfficNumber = tracfficNumber;
        }

        public String getOrgan() {
            return organ;
        }

        public void setOrgan(String organ) {
            this.organ = organ;
        }

        public String getOrganNum() {
            return organNum;
        }

        public void setOrganNum(String organNum) {
            this.organNum = organNum;
        }

        public String getBlood() {
            return blood;
        }

        public void setBlood(String blood) {
            this.blood = blood;
        }

        public String getBloodNum() {
            return bloodNum;
        }

        public void setBloodNum(String bloodNum) {
            this.bloodNum = bloodNum;
        }

        public String getSampleOrgan() {
            return sampleOrgan;
        }

        public void setSampleOrgan(String sampleOrgan) {
            this.sampleOrgan = sampleOrgan;
        }

        public String getSampleOrganNum() {
            return sampleOrganNum;
        }

        public void setSampleOrganNum(String sampleOrganNum) {
            this.sampleOrganNum = sampleOrganNum;
        }

        public String getOpoName() {
            return opoName;
        }

        public void setOpoName(String opoName) {
            this.opoName = opoName;
        }

        public String getContactName() {
            return contactName;
        }

        public void setContactName(String contactName) {
            this.contactName = contactName;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getGetTime() {
            return getTime;
        }

        public void setGetTime(String getTime) {
            this.getTime = getTime;
        }

        public String getIsStart() {
            return isStart;
        }

        public void setIsStart(String isStart) {
            this.isStart = isStart;
        }

        public String getStartLong() {
            return startLong;
        }

        public void setStartLong(String startLong) {
            this.startLong = startLong;
        }

        public String getStartLati() {
            return startLati;
        }

        public void setStartLati(String startLati) {
            this.startLati = startLati;
        }

        public String getEndLong() {
            return endLong;
        }

        public void setEndLong(String endLong) {
            this.endLong = endLong;
        }

        public String getEndLati() {
            return endLati;
        }

        public void setEndLati(String endLati) {
            this.endLati = endLati;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getToHosp() {
            return toHosp;
        }

        public void setToHosp(String toHosp) {
            this.toHosp = toHosp;
        }

        public String getBoxNo() {
            return boxNo;
        }

        public void setBoxNo(String boxNo) {
            this.boxNo = boxNo;
        }

        public String getOpoContactName() {
            return opoContactName;
        }

        public void setOpoContactName(String opoContactName) {
            this.opoContactName = opoContactName;
        }

        public String getOpoContactPhone() {
            return opoContactPhone;
        }

        public void setOpoContactPhone(String opoContactPhone) {
            this.opoContactPhone = opoContactPhone;
        }

        public String getPhones() {
            return phones;
        }

        public void setPhones(String phones) {
            this.phones = phones;
        }

        public String getModifyOrganSeg() {
            return modifyOrganSeg;
        }

        public void setModifyOrganSeg(String modifyOrganSeg) {
            this.modifyOrganSeg = modifyOrganSeg;
        }

        @Override
        public String toString() {
            return "ObjBean{" +
                    "transferid='" + transferid + '\'' +
                    ", organSeg='" + organSeg + '\'' +
                    ", openPsd='" + openPsd + '\'' +
                    ", fromCity='" + fromCity + '\'' +
                    ", toHospName='" + toHospName + '\'' +
                    ", tracfficType='" + tracfficType + '\'' +
                    ", tracfficNumber='" + tracfficNumber + '\'' +
                    ", organ='" + organ + '\'' +
                    ", organNum='" + organNum + '\'' +
                    ", blood='" + blood + '\'' +
                    ", bloodNum='" + bloodNum + '\'' +
                    ", sampleOrgan='" + sampleOrgan + '\'' +
                    ", sampleOrganNum='" + sampleOrganNum + '\'' +
                    ", opoName='" + opoName + '\'' +
                    ", contactName='" + contactName + '\'' +
                    ", contactPhone='" + contactPhone + '\'' +
                    ", opoContactName='" + opoContactName + '\'' +
                    ", opoContactPhone='" + opoContactPhone + '\'' +
                    ", phone='" + phone + '\'' +
                    ", trueName='" + trueName + '\'' +
                    ", getTime='" + getTime + '\'' +
                    ", isStart='" + isStart + '\'' +
                    ", startLong='" + startLong + '\'' +
                    ", startLati='" + startLati + '\'' +
                    ", endLong='" + endLong + '\'' +
                    ", endLati='" + endLati + '\'' +
                    ", distance='" + distance + '\'' +
                    ", toHosp='" + toHosp + '\'' +
                    ", boxNo='" + boxNo + '\'' +
                    '}';
        }
    }
}

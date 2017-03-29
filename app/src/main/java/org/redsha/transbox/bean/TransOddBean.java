package org.redsha.transbox.bean;

/**
 * 转运 生成成功的单
 */

public class TransOddBean {

    private String transferid;
    private String transferNumber;
    private int organCount;
    private String boxPin;
    private String fromCity;
    private String toHospName;
    private String tracfficType;
    private String tracfficNumber;
    private String deviceType;
    private String getOrganAt;
    private String startAt;
    private String status;
    private String createAt;
    private String modifyAt;

    private BoxInfoBean boxInfo;

    private OrganInfoBean organInfo;

    private ToHospitalInfoBean toHospitalInfo;

    private TransferPersonInfoBean transferPersonInfo;

    private OpoInfoBean opoInfo;

    public String getToHospName() {
        return toHospName;
    }

    public void setToHospName(String toHospName) {
        this.toHospName = toHospName;
    }

    public String getTransferid() {
        return transferid;
    }

    public void setTransferid(String transferid) {
        this.transferid = transferid;
    }

    public String getTransferNumber() {
        return transferNumber;
    }

    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }

    public int getOrganCount() {
        return organCount;
    }

    public void setOrganCount(int organCount) {
        this.organCount = organCount;
    }

    public String getBoxPin() {
        return boxPin;
    }

    public void setBoxPin(String boxPin) {
        this.boxPin = boxPin;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getGetOrganAt() {
        return getOrganAt;
    }

    public void setGetOrganAt(String getOrganAt) {
        this.getOrganAt = getOrganAt;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(String modifyAt) {
        this.modifyAt = modifyAt;
    }

    public BoxInfoBean getBoxInfo() {
        return boxInfo;
    }

    public void setBoxInfo(BoxInfoBean boxInfo) {
        this.boxInfo = boxInfo;
    }

    public OrganInfoBean getOrganInfo() {
        return organInfo;
    }

    public void setOrganInfo(OrganInfoBean organInfo) {
        this.organInfo = organInfo;
    }

    public ToHospitalInfoBean getToHospitalInfo() {
        return toHospitalInfo;
    }

    public void setToHospitalInfo(ToHospitalInfoBean toHospitalInfo) {
        this.toHospitalInfo = toHospitalInfo;
    }

    public TransferPersonInfoBean getTransferPersonInfo() {
        return transferPersonInfo;
    }

    public void setTransferPersonInfo(TransferPersonInfoBean transferPersonInfo) {
        this.transferPersonInfo = transferPersonInfo;
    }

    public OpoInfoBean getOpoInfo() {
        return opoInfo;
    }

    public void setOpoInfo(OpoInfoBean opoInfo) {
        this.opoInfo = opoInfo;
    }

    public static class BoxInfoBean {
        private String boxid;
        private String boxNumber;
        private String deviceId;
        private String qrcode;
        private String model;
        private String transferStatus;
        private String buyFrom;
        private String buyAt;
        private String remark;
        private String status;
        private String createAt;
        private String modifyAt;

        public String getBoxid() {
            return boxid;
        }

        public void setBoxid(String boxid) {
            this.boxid = boxid;
        }

        public String getBoxNumber() {
            return boxNumber;
        }

        public void setBoxNumber(String boxNumber) {
            this.boxNumber = boxNumber;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getTransferStatus() {
            return transferStatus;
        }

        public void setTransferStatus(String transferStatus) {
            this.transferStatus = transferStatus;
        }

        public String getBuyFrom() {
            return buyFrom;
        }

        public void setBuyFrom(String buyFrom) {
            this.buyFrom = buyFrom;
        }

        public String getBuyAt() {
            return buyAt;
        }

        public void setBuyAt(String buyAt) {
            this.buyAt = buyAt;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getModifyAt() {
            return modifyAt;
        }

        public void setModifyAt(String modifyAt) {
            this.modifyAt = modifyAt;
        }
    }

    public static class OrganInfoBean {
        private String organid;
        private String segNumber;
        private String type;
        private String bloodType;
        private int bloodSampleCount;
        private int organizationSampleCount;
        private String organizationSampleType;
        private String createAt;
        private String modifyAt;

        public String getOrganid() {
            return organid;
        }

        public void setOrganid(String organid) {
            this.organid = organid;
        }

        public String getSegNumber() {
            return segNumber;
        }

        public void setSegNumber(String segNumber) {
            this.segNumber = segNumber;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getBloodType() {
            return bloodType;
        }

        public void setBloodType(String bloodType) {
            this.bloodType = bloodType;
        }

        public int getBloodSampleCount() {
            return bloodSampleCount;
        }

        public void setBloodSampleCount(int bloodSampleCount) {
            this.bloodSampleCount = bloodSampleCount;
        }

        public int getOrganizationSampleCount() {
            return organizationSampleCount;
        }

        public void setOrganizationSampleCount(int organizationSampleCount) {
            this.organizationSampleCount = organizationSampleCount;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getModifyAt() {
            return modifyAt;
        }

        public void setModifyAt(String modifyAt) {
            this.modifyAt = modifyAt;
        }

        public String getOrganizationSampleType() {
            return organizationSampleType;
        }

        public void setOrganizationSampleType(String organizationSampleType) {
            this.organizationSampleType = organizationSampleType;
        }

    }

    public static class ToHospitalInfoBean {
        private String hospitalid;
        private String name;
        private String district;
        private String address;
        private String grade;
        private String contactPerson;
        private String contactPhone;
        private String remark;
        private String createAt;
        private String modifyAt;
        private String account_id;

        public String getHospitalid() {
            return hospitalid;
        }

        public void setHospitalid(String hospitalid) {
            this.hospitalid = hospitalid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getContactPerson() {
            return contactPerson;
        }

        public void setContactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getModifyAt() {
            return modifyAt;
        }

        public void setModifyAt(String modifyAt) {
            this.modifyAt = modifyAt;
        }

        public String getAccount_id() {
            return account_id;
        }

        public void setAccount_id(String account_id) {
            this.account_id = account_id;
        }
    }

    public static class TransferPersonInfoBean {
        private String transferPersonid;
        private String name;
        private String phone;
        private String organType;
        private String createAt;
        private String modifyAt;

        public String getTransferPersonid() {
            return transferPersonid;
        }

        public void setTransferPersonid(String transferPersonid) {
            this.transferPersonid = transferPersonid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getOrganType() {
            return organType;
        }

        public void setOrganType(String organType) {
            this.organType = organType;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getModifyAt() {
            return modifyAt;
        }

        public void setModifyAt(String modifyAt) {
            this.modifyAt = modifyAt;
        }
    }

    public static class OpoInfoBean {
        private String opoid;
        private String name;
        private String district;
        private String address;
        private String grade;
        private String contactPerson;
        private String contactPhone;
        private String remark;
        private String createAt;
        private String modifyAt;

        public String getOpoid() {
            return opoid;
        }

        public void setOpoid(String opoid) {
            this.opoid = opoid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getContactPerson() {
            return contactPerson;
        }

        public void setContactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
        }

        public String getContactPhone() {
            return contactPhone;
        }

        public void setContactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String getModifyAt() {
            return modifyAt;
        }

        public void setModifyAt(String modifyAt) {
            this.modifyAt = modifyAt;
        }
    }
}

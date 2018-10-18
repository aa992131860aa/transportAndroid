package com.otqc.transbox.bean;

/**
 * 2016/11/9.
 */

public class QueryOrganInfoBean {

    private String organid;
    private String segNumber;
    private String type;
    private String bloodType;
    private String bloodSampleCount;
    private String organizationSampleType;
    private String organizationSampleCount;
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

    public String getBloodSampleCount() {
        return bloodSampleCount;
    }

    public void setBloodSampleCount(String bloodSampleCount) {
        this.bloodSampleCount = bloodSampleCount;
    }

    public String getOrganizationSampleType() {
        return organizationSampleType;
    }

    public void setOrganizationSampleType(String organizationSampleType) {
        this.organizationSampleType = organizationSampleType;
    }

    public String getOrganizationSampleCount() {
        return organizationSampleCount;
    }

    public void setOrganizationSampleCount(String organizationSampleCount) {
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
}

package org.redsha.transbox.db;

import io.realm.RealmObject;

/**
 * 2016/11/9.
 */

public class OrganInfoDb extends RealmObject {

    private String organid;
    private String segNumber;
    private String type;
    private String bloodType;
    private int bloodSampleCount;
    private String organizationSampleType;
    private int organizationSampleCount;
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

    public String getOrganizationSampleType() {
        return organizationSampleType;
    }

    public void setOrganizationSampleType(String organizationSampleType) {
        this.organizationSampleType = organizationSampleType;
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
}

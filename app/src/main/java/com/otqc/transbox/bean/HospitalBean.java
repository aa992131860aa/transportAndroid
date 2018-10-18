package com.otqc.transbox.bean;

import java.util.List;

/**
 * del******
 */
public class HospitalBean {

    /**
     * boxes : []
     * hospitalid : 532c1979-4002-4faf-bb59-c532fe379164
     * username : blackmatch
     * name : 上海第五人民医院
     * district : 闵行区
     * address : 剑川路951号
     * grade : 三级乙等
     * contactPerson : 李某某
     * contactPhone : 18817311936
     * remark : 测试
     * createAt : 2016-10-28 10:44:41
     * modifyAt : 2016-10-28 10:44:41
     */

    private String hospitalid;
    private String username;
    private String name;
    private String district;
    private String address;
    private String grade;
    private String contactPerson;
    private String contactPhone;
    private String remark;
    private String createAt;
    private String modifyAt;
    private List<?> boxes;

    public String getHospitalid() {
        return hospitalid;
    }

    public void setHospitalid(String hospitalid) {
        this.hospitalid = hospitalid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public List<?> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<?> boxes) {
        this.boxes = boxes;
    }
}

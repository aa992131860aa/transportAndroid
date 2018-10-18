package com.otqc.transbox.bean;

/**
 * 2016/11/9.
 */

public class QueryTransferPersonInfoBean {

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

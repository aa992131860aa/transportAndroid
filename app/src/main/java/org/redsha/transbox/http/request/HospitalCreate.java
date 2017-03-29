package org.redsha.transbox.http.request;

/**
 * 新建预约
 */
public class HospitalCreate {
    private String username;
    private String pwd;
    private String name;
    private String address;

    public HospitalCreate(String username, String pwd, String name, String address) {
        this.username = username;
        this.pwd = pwd;
        this.name = name;
        this.address = address;
    }

    public HospitalCreate() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

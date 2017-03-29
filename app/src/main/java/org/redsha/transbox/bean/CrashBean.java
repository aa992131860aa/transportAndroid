package org.redsha.transbox.bean;

public class CrashBean {
    private String oInfo;
    private String cInfo;

    public CrashBean(String oInfo, String cInfo) {
        this.oInfo = oInfo;
        this.cInfo = cInfo;
    }

    public String getoInfo() {
        return oInfo;
    }

    public void setoInfo(String oInfo) {
        this.oInfo = oInfo;
    }

    public String getcInfo() {
        return cInfo;
    }

    public void setcInfo(String cInfo) {
        this.cInfo = cInfo;
    }
}

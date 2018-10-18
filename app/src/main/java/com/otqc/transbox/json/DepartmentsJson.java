package com.otqc.transbox.json;

import java.util.List;

/**
 * Created by 99213 on 2017/9/28.
 */

public class DepartmentsJson {

    /**
     * result : 0
     * msg : 没查到该手机的信息
     * obj : [{"name":"肖","phone":"18973576909"},{"name":"张悦红","phone":"18368889119"}]
     */

    private int result;
    private String msg;
    private List<ObjBean> obj;

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

    public List<ObjBean> getObj() {
        return obj;
    }

    public void setObj(List<ObjBean> obj) {
        this.obj = obj;
    }

    public static class ObjBean {
        /**
         * name : 肖
         * phone : 18973576909
         */

        private String name;
        private String phone;

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
    }
}

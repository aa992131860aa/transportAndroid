package com.otqc.transbox.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/30.
 */

public class UsersListJson {


    /**
     * result : 0
     * msg : 获得成功
     * obj : [{"trueName":"卢小堂","phone":"18398850874"},{"trueName":"张小康","phone":"18398850875"},{"trueName":"陈杨","phone":"18398850872"},{"trueName":"卢双堂","phone":"15757164739"},{"trueName":"张康康","phone":"13393852552"},{"phone":"18638720407"},{"trueName":"杨子江","phone":"17621180006"},{"trueName":"李建辉","phone":"13486160973"},{"trueName":"huangtang","phone":"15355481205"},{"trueName":"唐","phone":"15336568476"},{"trueName":"周云鹏","phone":"18767103122"},{"trueName":"周燕飞","phone":"18767167366"},{"phone":"18917995795"},{"trueName":"张绍康","phone":"18639805480"}]
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
         * trueName : 卢小堂
         * phone : 18398850874
         */

        private String trueName;
        private String phone;

        public String getTrueName() {
            return trueName;
        }

        public void setTrueName(String trueName) {
            this.trueName = trueName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}

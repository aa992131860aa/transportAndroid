package com.otqc.transbox.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/2.
 */

public class OpoJson {

    /**
     * result : 0
     * msg : 获取转运中的信息
     * obj : [{"name":"树兰(杭州)医院OPO"},{"name":"浙一医院OPO"}]
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
         * name : 树兰(杭州)医院OPO
         */

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

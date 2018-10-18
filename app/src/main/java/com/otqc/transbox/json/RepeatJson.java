package com.otqc.transbox.json;

/**
 * Created by 99213 on 2018/3/26.
 */

public class RepeatJson {

    /**
     * result : 0
     * msg : 不存在器官段号
     * obj : {"type":"肝脏"}
     */

    private int result;
    private String msg;
    private ObjBean obj;

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

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public static class ObjBean {
        /**
         * type : 肝脏
         */

        private String type;
        private String modifyOrganSeg;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getModifyOrganSeg() {
            return modifyOrganSeg;
        }

        public void setModifyOrganSeg(String modifyOrganSeg) {
            this.modifyOrganSeg = modifyOrganSeg;
        }
    }
}

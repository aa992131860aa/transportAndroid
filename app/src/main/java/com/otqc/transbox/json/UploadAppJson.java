package com.otqc.transbox.json;

/**
 * Created by 99213 on 2017/7/26.
 */

public class UploadAppJson {

    /**
     * result : 0
     * msg : 获取正确
     * obj : {"id":5,"version":3,"url":"dd","createTime":"Jul 26, 2017 10:53:23 AM"}
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
         * id : 5
         * version : 3
         * url : dd
         * createTime : Jul 26, 2017 10:53:23 AM
         */

        private int id;
        private int version;
        private String url;
        private String createTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}

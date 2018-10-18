package com.otqc.transbox.json;

import java.util.List;

/**
 * Created by 99213 on 2017/8/17.
 */

public class RecordStringJson {

    /**
     * result : 0
     * obj : ["2017-08-16 15:15:39","2017-08-16 15:15:44","2017-08-16 15:15:49","2017-08-16 15:15:54","2017-08-16 15:15:59","2017-08-16 15:16:04","2017-08-16 15:16:09","2017-08-16 15:16:14","2017-08-16 15:16:19","2017-08-16 15:16:24"]
     */

    private int result;
    private String msg;
    private List<Integer> obj;
    private TransferJson.ObjBean info;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<Integer> getObj() {
        return obj;
    }

    public void setObj(List<Integer> obj) {
        this.obj = obj;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public TransferJson.ObjBean getInfo() {
        return info;
    }

    public void setInfo(TransferJson.ObjBean info) {
        this.info = info;
    }
}

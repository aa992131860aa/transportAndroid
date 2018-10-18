package com.otqc.transbox.json;

import java.util.List;

/**
 * Created by 99213 on 2017/7/21.
 */

public class OpoProvinceJson {


    /**
     * result : 0
     * msg : 获取opoInfo成功
     * obj : [{"name":"浙江大学医学院附属第一医院OPO","contactName":"唐金梅","contactPhone":"15336568476"},{"name":"浙江省人民医院OPO","contactName":"唐金梅","contactPhone":"15336568476"},{"name":"温州医学院附属第一医院OPO","contactName":"唐金梅","contactPhone":"15336568476"},{"name":"浙江大学医学院附属第二医院OPO","contactName":"唐金梅","contactPhone":"15336568476"},{"name":"宁波市医疗中心李惠利医院OPO","contactName":"唐金梅","contactPhone":"15336568476"},{"name":"宁波市鄞州第二医院OPO","contactName":"唐金梅","contactPhone":"15336568476"},{"name":"树兰（杭州）医院OPO","contactName":"唐金梅","contactPhone":"15336568476"},{"name":"南京军区第117医院OPO","contactName":"唐金梅","contactPhone":"15336568476"}]
     */

    private int result;
    private String msg;
    private List<Opo> obj;

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

    public List<Opo> getObj() {
        return obj;
    }

    public void setObj(List<Opo> obj) {
        this.obj = obj;
    }


}

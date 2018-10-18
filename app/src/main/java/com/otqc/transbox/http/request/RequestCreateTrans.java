package com.otqc.transbox.http.request;

import com.otqc.transbox.bean.CreateBaseInfo;
import com.otqc.transbox.bean.CreateOrganInfo;
import com.otqc.transbox.bean.CreateTransPersonInfo;
import com.otqc.transbox.bean.CreateTransToInfo;
import com.otqc.transbox.bean.CreateOpoInfo;

/**
 * 新建转运
 */

public class RequestCreateTrans {
    // order info
    private CreateBaseInfo baseInfo;
    private CreateOrganInfo organ;
    private CreateTransPersonInfo person;
    private CreateTransToInfo to;
    private CreateOpoInfo opo;

    public CreateBaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(CreateBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public CreateOrganInfo getOrgan() {
        return organ;
    }

    public void setOrgan(CreateOrganInfo organ) {
        this.organ = organ;
    }

    public CreateTransPersonInfo getPerson() {
        return person;
    }

    public void setPerson(CreateTransPersonInfo person) {
        this.person = person;
    }

    public CreateTransToInfo getTo() {
        return to;
    }

    public void setTo(CreateTransToInfo to) {
        this.to = to;
    }

    public CreateOpoInfo getOpo() {
        return opo;
    }

    public void setOpo(CreateOpoInfo opo) {
        this.opo = opo;
    }

}

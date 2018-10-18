package com.otqc.transbox.http.request;

import com.otqc.transbox.db.TransRecordItemDb;

public class TransferRecord {

    private TransRecordItemDb[] records;

    public TransRecordItemDb[] getRecords() {
        return records;
    }

    public void setRecords(TransRecordItemDb[] records) {
        this.records = records;
    }
}

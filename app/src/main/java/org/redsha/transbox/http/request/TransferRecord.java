package org.redsha.transbox.http.request;

import org.redsha.transbox.db.TransRecordItemDb;

public class TransferRecord {

    private TransRecordItemDb[] records;

    public TransRecordItemDb[] getRecords() {
        return records;
    }

    public void setRecords(TransRecordItemDb[] records) {
        this.records = records;
    }
}

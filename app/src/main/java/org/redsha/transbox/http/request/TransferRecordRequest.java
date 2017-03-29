package org.redsha.transbox.http.request;

/**
 * Created by Yian on 2016/11/26.
 */

public class TransferRecordRequest {

    private TransRecordItemRequest[] records;

    public TransRecordItemRequest[] getRecords() {
        return records;
    }

    public void setRecords(TransRecordItemRequest[] records) {
        this.records = records;
    }
}

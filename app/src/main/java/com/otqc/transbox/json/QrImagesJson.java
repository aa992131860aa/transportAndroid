package com.otqc.transbox.json;

/**
 * Created by 99213 on 2017/7/30.
 */

public class QrImagesJson {

    /**
     * result : 0
     * msg : ok
     * obj : {"hospitalName":"北京大学第三医院","boxId":"a5158454-e27b-4d8a-8f4d-96ba5823f7b9","hospitalId":"ab30bca4-d246-4d07-a910-f7fe2e9618b2","qrImages":"http://116.62.28.28:8080/transbox/images/35225498820160.jpg"}
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
         * hospitalName : 北京大学第三医院
         * boxId : a5158454-e27b-4d8a-8f4d-96ba5823f7b9
         * hospitalId : ab30bca4-d246-4d07-a910-f7fe2e9618b2
         * qrImages : http://116.62.28.28/transbox/images/35225498820160.jpg
         */

        private String hospitalName;
        private String boxId;
        private String hospitalId;
        private String qrImages;
        private String boxNo;

        public String getHospitalName() {
            return hospitalName;
        }

        public void setHospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
        }

        public String getBoxId() {
            return boxId;
        }

        public void setBoxId(String boxId) {
            this.boxId = boxId;
        }

        public String getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(String hospitalId) {
            this.hospitalId = hospitalId;
        }

        public String getQrImages() {
            return qrImages;
        }

        public void setQrImages(String qrImages) {
            this.qrImages = qrImages;
        }

        public String getBoxNo() {
            return boxNo;
        }

        public void setBoxNo(String boxNo) {
            this.boxNo = boxNo;
        }
    }
}

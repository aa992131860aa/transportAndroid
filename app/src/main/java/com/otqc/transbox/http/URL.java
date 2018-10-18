package com.otqc.transbox.http;

public interface URL {
    String SOCKET = "http://116.62.28.28:1337";
    //String TOMCAT = "http://192.168.1.29:8080/transbox/";
    String TOMCAT = "http://www.lifeperfusor.com/transbox/";
    String BASE_URL = SOCKET+"/transbox/api/";
    //String BASE_URL = "http://www.lifeperfusor.com/transbox/api/";
    String UPLOAD_APP = TOMCAT + "uploadApp.do";
    String BOX = TOMCAT + "box.do";
    String SMS = TOMCAT + "sendSms.do";

    String QR_IMAGE = TOMCAT + "qrCode.do";
    String USERS = TOMCAT + "user.do";
    //融云
    String RONG = TOMCAT + "rong.do";


    //好友
    String CONTACT = TOMCAT + "contact.do";
    //转运
    String TRANSFER = TOMCAT + "transfer.do";
    //推送
    String PUSH = TOMCAT + "push.do";
    //天气
    String WEATHER = TOMCAT + "weather.do";
    //opo
    String OPO = TOMCAT + "opo.do";
    //转运详细记录
    String TRANSFER_RECORD = TOMCAT + "transferRecord.do";
    //根据地址后去经纬度
    String GAO_DE_LOCATION_URL = "http://restapi.amap.com/v3/geocode/geo?key=3eb77c68c96bf6c2a3cf251c93865993&address=";
}

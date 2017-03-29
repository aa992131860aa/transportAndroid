package org.redsha.transbox.util;

public class A {
    public static String[] point;
    public static boolean isBoxInfo = false; //标记获取箱子信息是否成功
    public static boolean isKwdInfo = false; //标记获取keyword是否成功
    public static boolean isSerialPort = false; //串口是否打开成功

    public static boolean isReadyUp = false; // 是否上传数据

    public static int mCollectState = 0;    // 0只采样 ，1记录数据
}
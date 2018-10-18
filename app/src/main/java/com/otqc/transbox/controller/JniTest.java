package com.otqc.transbox.controller;

/**
 * Created by 99213 on 2017/7/25.
 */

public class JniTest{
    static {
        System.loadLibrary("org");
    }
    public native String getString();
    //D:\git\transportAndroid\app\build\intermediates\classes\debug com.otqc.transbox.controller.JniTest
}

package com.otqc.transbox.util;

import android.os.Handler;
import android.os.Message;

import com.otqc.transbox.engine.SerialDataUtils;

import com.otqc.transbox.engine.SerialDataUtils;

import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.engine.SerialDataUtils;
import com.otqc.transbox.service.CommServer;

import com.otqc.transbox.engine.SerialDataUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * Created by 99213 on 2017/7/26.
 */

public class SerialPortUtil {

    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private SerialPortUtil.ReadThread mReadThread;
    private String sPort = "/dev/ttyMT1";
    private int iBaudRate = 9600;
    private String receiveString;
    private ReadThreadComm mReadThreadComm;
    private String TAG = "SerialPortUtil";





    /**
     * 打开一次性串口
     */
    public void openSerialOne(Handler handler, byte[] sendFlag, String result) throws IOException {

        if (mSerialPort == null) {
            mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);

        }
        if (mInputStream == null) {
            mInputStream = mSerialPort.getInputStream();

        }

        if (mOutputStream == null) {
            mOutputStream = mSerialPort.getOutputStream();
            mOutputStream.write(sendFlag);
        }

        if (mSerialPort != null) {
            A.isSerialPort = true;  // 串口是否打开

        }


        if (mReadThread == null) {
            mReadThreadComm = new SerialPortUtil.ReadThreadComm(handler, sendFlag, result);
            mReadThreadComm.start();
        }


    }

    public void closeCommSerialPort() throws IOException {

        if (mSerialPort != null) {
            mSerialPort.close();

        }

        if (mOutputStream != null) {

            mOutputStream.close();

        }
        if (mInputStream != null) {
            mInputStream.close();

        }
        if (mReadThreadComm != null) {
            mReadThreadComm = null;
        }

    }

    /**
     * 读串口线程
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();

            while (!isInterrupted()) {
                if (mInputStream != null) {
                    byte[] buffer = new byte[1024];
                    int size = 0;
                    try {
                        size = mInputStream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (size > 0) {
                        byte[] buffer2 = new byte[size];
                        for (int i = 0; i < size; i++) {
                            buffer2[i] = buffer[i];
                        }
                        receiveString = SerialDataUtils.ByteArrToHex(buffer2).trim();


                        boolean isPass = CRC16M.checkBuf(buffer2);
                        if (!isPass) {
                            LogUtil.e("serialError", "校验失败：" + receiveString.replace(" ", ""));
                            send("30 03 00 01 00 02 91 EA");
                        }

                    }
                    try {
                        //延时50ms
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
    }

    /**
     * 读串口线程
     */
    private class ReadThreadComm extends Thread {
        Handler handler;
        boolean isUnlock;
        String result;
        byte[] sendFlag;

        public ReadThreadComm(Handler handler, byte[] sendFlag, String result) {
            this.handler = handler;
            this.result = result;
            this.sendFlag = sendFlag;
        }

        public ReadThreadComm() {

        }

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (mInputStream != null) {
                    byte[] buffer = new byte[1024];
                    int size = 0;
                    try {
                        size = mInputStream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (size > 0) {
                        byte[] buffer2 = new byte[size];
                        for (int i = 0; i < size; i++) {
                            buffer2[i] = buffer[i];
                        }
                        receiveString = SerialDataUtils.ByteArrToHex(buffer2).trim();
                        isUnlock = receiveString.equals(result);
                        if (isUnlock) {
                            Message msg = new Message();
                            msg.obj = "result:" + receiveString + "," + isUnlock;
                            handler.sendMessage(msg);

                            try {
                                closeCommSerialPort();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        boolean isPass = CRC16M.checkBuf(buffer2);
                        if (!isPass) {
                            LogUtil.e("serialError", "校验失败：" + receiveString.replace(" ", ""));


                            try {
                                mOutputStream.write(sendFlag);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    try {
                        //延时50ms
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
    }

    /**
     * 发串口数据
     */
    public void send(final String string) {
        try {
            //去掉空格
            String s = string;
            s = s.replace(" ", "");
            byte[] bytes = SerialDataUtils.HexToByteArr(s);

            LogUtil.e("serial", "发送串口数据：" + s);

            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开锁：
     * 主机发送：7B 30 10 06 00 00 64 C5 4B 7D
     * 从机返回：7B 30 11 06 00 00 64 F8 8B 7D
     */
    public void openlock(Handler handler) throws IOException {
        String result = "7B 30 11 06 00 00 64 F8 8B 7D";
        byte[] newBytes = new byte[10];
        newBytes[0] = 0x7b;
        newBytes[1] = 0x30;
        newBytes[2] = 0x10;
        newBytes[3] = 0x06;
        newBytes[4] = 0x00;
        newBytes[5] = 0x00;
        newBytes[6] = 0x64;
        newBytes[7] = (byte) 0xC5;
        newBytes[8] = 0x4b;
        newBytes[9] = 0x7D;

        openSerialOne(handler, newBytes, result);

    }

    /**
     * 关锁：
     * 主机发送：7B 30 10 06 00 00 00 C4 A0 7D
     * 从机返回：7B 30 11 06 00 00 00 F9 60 7D
     */
    public void closeLock(Handler handler) throws IOException {
        String result = "7B 30 11 06 00 00 00 F9 60 7D";

        byte[] newBytes = new byte[10];
        newBytes[0] = 0x7b;
        newBytes[1] = 0x30;
        newBytes[2] = 0x10;
        newBytes[3] = 0x06;
        newBytes[4] = 0x00;
        newBytes[5] = 0x00;
        newBytes[6] = 0x00;
        newBytes[7] = (byte) 0xC4;
        newBytes[8] = (byte) 0xA0;
        newBytes[9] = 0x7D;

        openSerialOne(handler, newBytes, result);


    }

    /**
     * 读取碰撞次数： 7 次
     * 主机发送：7B 30 20 08 00 00 00 86 4C 7D
     * 从机返回：7B 30 21 08 00 02 BC BB 5D 7D
     */
    public void readCollision(Handler handler) {
        boolean isUnlock = false;
        try {
            // openSerial();
            byte[] buffer = new byte[1024];
            byte[] newBytes = new byte[10];
            newBytes[0] = 0x7b;
            newBytes[1] = 0x30;
            newBytes[2] = 0x20;
            newBytes[3] = 0x08;
            newBytes[4] = 0x00;
            newBytes[5] = 0x00;
            newBytes[6] = 0x00;
            newBytes[7] = (byte) 0x86;
            newBytes[8] = 0x4c;
            newBytes[9] = 0x7D;

            mOutputStream.write(newBytes);

            int size = mInputStream.read(buffer);
            byte[] buffer2 = new byte[size];
            for (int i = 0; i < size; i++) {
                buffer2[i] = buffer[i];
            }
            receiveString = SerialDataUtils.ByteArrToHex(buffer2).trim();
            isUnlock = receiveString.equals("7B 30 11 06 00 00 00 F9 60 7D");

            Message msg = new Message();
            msg.obj = "result:" + receiveString + "," + isUnlock;
            handler.sendMessage(msg);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     关闭屏幕
     从机发送：7B 30 30 05 00 00 00 45 23 7D
     主机返回：7B 30 31 05 00 00 00 78 E3 7D
     */

    /**
     显示屏幕
     从机发送：7B 30 30 05 00 00 64 44 C8 7D
     主机返回：7B 30 31 05 00 00 64 79 08 7D
     */

}







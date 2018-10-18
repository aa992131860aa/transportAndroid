package com.otqc.transbox.util;

import android.util.Log;

import com.otqc.transbox.service.CommServer;

import com.otqc.transbox.service.CommServer;

import com.otqc.transbox.App;
import com.otqc.transbox.service.CommServer;

import com.otqc.transbox.service.CommServer;

import java.io.File;
import java.io.IOException;

import android_serialport_api.SerialPort;

/**
 * Created by fantasy on 2018/1/1.
 */

public class SerialUtil {

    private static String sPort = "/dev/ttyMT1";
    private static int iBaudRate = 9600;
    private static String TAG = "SerialUtil";

    public static void startSerial() {
        if (CommServer.mSerialPort == null) {
            try {
                CommServer.mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
            } catch (IOException e) {
                e.printStackTrace();

            }



        }

        if (CommServer.mInputStream == null) {
            CommServer.mInputStream = CommServer.mSerialPort.getInputStream();

        }

        if (CommServer.mOutputStream == null) {
            CommServer.mOutputStream = CommServer.mSerialPort.getOutputStream();

        }
    }

    /**
     * 打开屏幕
     */
    public static void openScreen() {


        // 主机发送：7B 30 10 06 00 00 64 C5 4B 7D
        // 从机返回：7B 30 11 06 00 00 64 F8 8B 7D
        int[] openScreenBytes = new int[10];
        openScreenBytes[0] = 0x7B;
        openScreenBytes[1] = 0x30;
        openScreenBytes[2] = 0x31;
        openScreenBytes[3] = 0x05;
        openScreenBytes[4] = 0x00;
        openScreenBytes[5] = 0x00;
        openScreenBytes[6] = 0x64;
        openScreenBytes[7] = 0x79;
        openScreenBytes[8] = 0x08;
        openScreenBytes[9] = 0x7D;
        try {
            for (int j = 0; j < openScreenBytes.length; j++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(openScreenBytes[j]);
                } else {
                    startSerial();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 关闭屏幕
     */
    public static void closeScreen() {


        // 主机发送：7B 30 10 06 00 00 64 C5 4B 7D
        // 从机返回：7B 30 11 06 00 00 64 F8 8B 7D
        int[] closeScreenBytes = new int[10];
        closeScreenBytes[0] = 0x7B;
        closeScreenBytes[1] = 0x30;
        closeScreenBytes[2] = 0x31;
        closeScreenBytes[3] = 0x05;
        closeScreenBytes[4] = 0x00;
        closeScreenBytes[5] = 0x00;
        closeScreenBytes[6] = 0x00;
        closeScreenBytes[7] = 0x78;
        closeScreenBytes[8] = 0xE3;
        closeScreenBytes[9] = 0x7D;
        try {
            for (int j = 0; j < closeScreenBytes.length; j++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(closeScreenBytes[j]);
                } else {
                    startSerial();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void clearCollisionNumber() {

        //+++++++++++++++++清空碰撞+++++++++++++++++
        // 主机发送：7B 30 20 08 00 00 00 86 4C 7D
        //从机返回：7B 30 21 08 00 02 BC BB 5D 7D
        int[] collisionBytes = new int[10];
        collisionBytes[0] = 0x7B;
        collisionBytes[1] = 0x30;
        collisionBytes[2] = 0x10;
        collisionBytes[3] = 0x0D;
        collisionBytes[4] = 0x00;
        collisionBytes[5] = 0x00;
        collisionBytes[6] = 0x00;
        collisionBytes[7] = 0xC6;
        collisionBytes[8] = 0x84;
        collisionBytes[9] = 0x7D;


        try {
            for (int j = 0; j < collisionBytes.length; j++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(collisionBytes[j]);
                } else {
                    startSerial();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 开锁
     */
    public static void open() {
        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }


            // 主机发送：7B 30 10 06 00 00 64 C5 4B 7D
            // 从机返回：7B 30 11 06 00 00 64 F8 8B 7D
            int[] openBoxBytes = new int[10];
            openBoxBytes[0] = 0x7B;
            openBoxBytes[1] = 0x30;
            openBoxBytes[2] = 0x10;
            openBoxBytes[3] = 0x06;
            openBoxBytes[4] = 0x00;
            openBoxBytes[5] = 0x00;
            openBoxBytes[6] = 0x64;
            openBoxBytes[7] = 0xC5;
            openBoxBytes[8] = 0x4B;
            openBoxBytes[9] = 0x7D;
            try {
                for (int j = 0; j < openBoxBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(openBoxBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 电量
     */
    public static void power() {
        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }

            //+++++++++++++++++电量+++++++++++++++++
            // 主机发送：7B 30 20 08 00 00 00 86 4C 7D
            //从机返回：7B 30 21 08 00 02 BC BB 5D 7D
            int[] collisionBytes = new int[10];
            collisionBytes[0] = 0x7B;
            collisionBytes[1] = 0x30;
            collisionBytes[2] = 0x20;
            collisionBytes[3] = 0x0E;
            collisionBytes[4] = 0x00;
            collisionBytes[5] = 0x00;
            collisionBytes[6] = 0x00;
            collisionBytes[7] = 0x86;
            collisionBytes[8] = 0xC4;
            collisionBytes[9] = 0x7D;
            try {
                if (CommServer.mOutputStream != null) {
                    for (int j = 0; j < collisionBytes.length; j++) {
                        if (CommServer.mOutputStream != null) {
                            CommServer.mOutputStream.write(collisionBytes[j]);
                        } else {
                            startSerial();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 碰撞
     */
    public static void collision() {
//        CONSTS.TRANSFER_OPEN = false;
//        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
//            if (CONSTS.TRANSFER_OPEN) {
//                break;
//            }


        //+++++++++++++++++碰撞+++++++++++++++++
        // 主机发送：7B 30 20 08 00 00 00 86 4C 7D
        //从机返回：7B 30 21 08 00 02 BC BB 5D 7D
        int[] collisionBytes = new int[10];
        collisionBytes[0] = 0x7B;
        collisionBytes[1] = 0x30;
        collisionBytes[2] = 0x20;
        collisionBytes[3] = 0x08;
        collisionBytes[4] = 0x00;
        collisionBytes[5] = 0x00;
        collisionBytes[6] = 0x00;
        collisionBytes[7] = 0x86;
        collisionBytes[8] = 0x4C;
        collisionBytes[9] = 0x7D;
        try {
            if (CommServer.mOutputStream != null) {
                for (int j = 0; j < collisionBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(collisionBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();

        }

        try {
            Thread.sleep(CONSTS.SERIAL_PERIOD);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //}
    }

    /**
     * 温度
     */
    public static void temperature() {
//        CONSTS.TRANSFER_OPEN = false;
//        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
//            if (CONSTS.TRANSFER_OPEN) {
//                break;
//            }


        //发送温度
        // 1.读取温度：29.7 °C
        // 主机发送：7B 30 20 01 00 00 00 85 D0 7D
        //从机返回：7B 30 21 01 00 0B 9A 3F 48 7D

        int[] temperatureBytes = new int[10];
        temperatureBytes[0] = 0x7b;
        temperatureBytes[1] = 0x30;
        temperatureBytes[2] = 0x20;
        temperatureBytes[3] = 0x01;
        temperatureBytes[4] = 0x00;
        temperatureBytes[5] = 0x00;
        temperatureBytes[6] = 0x00;
        temperatureBytes[7] = 0x85;
        temperatureBytes[8] = 0xD0;
        temperatureBytes[9] = 0x7D;

        try {
            for (int j = 0; j < temperatureBytes.length; j++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(temperatureBytes[j]);
                } else {
                    startSerial();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(CONSTS.SERIAL_PERIOD);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // }
    }

    /**
     * 湿度
     */
    public static void humidity() {
//        CONSTS.TRANSFER_OPEN = false;
//        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
//            if (CONSTS.TRANSFER_OPEN) {
//                break;
//            }


        //+++++++++++++++++湿度+++++++++++++++++
        // 主机发送：7B 30 20 02 00 00 00 85 94 7D
        // 从机返回：7B 30 21 02 00 1A 2C B2 E9 7D
        int[] humidityBytes = new int[10];
        humidityBytes[0] = 0x7B;
        humidityBytes[1] = 0x30;
        humidityBytes[2] = 0x20;
        humidityBytes[3] = 0x02;
        humidityBytes[4] = 0x00;
        humidityBytes[5] = 0x00;
        humidityBytes[6] = 0x00;
        humidityBytes[7] = 0x85;
        humidityBytes[8] = 0x94;
        humidityBytes[9] = 0x7D;
        try {
            for (int j = 0; j < humidityBytes.length; j++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(humidityBytes[j]);
                } else {
                    startSerial();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //  Log.e(TAG, "error3:" + e.getMessage());
        }

        try {
            Thread.sleep(CONSTS.SERIAL_PERIOD);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // }
    }

//    public static void pwd() {
//        CONSTS.TRANSFER_OPEN = false;
//        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
//            if (CONSTS.TRANSFER_OPEN) {
//                break;
//            }
//
//
//            //+++++++++++++++++是否有密码+++++++++++++++++
//            // 主机发送：7B 30 20 08 00 00 00 86 4C 7D
//            //从机返回：7B 30 21 08 00 02 BC BB 5D 7D
//            int[] collisionBytes = new int[10];
//            collisionBytes[0] = 0x7B;
//            collisionBytes[1] = 0x30;
//            collisionBytes[2] = 0x10;
//            collisionBytes[3] = 0x0F;
//            collisionBytes[4] = 0x00;
//            collisionBytes[5] = 0x00;
//            collisionBytes[6] = 0x64;
//            collisionBytes[7] = 0xC6;
//            collisionBytes[8] = 0xD7;
//            collisionBytes[9] = 0x7D;
//            try {
//                if (CommServer.mOutputStream != null) {
//                    for (int j = 0; j < collisionBytes.length; j++) {
//                        if (CommServer.mOutputStream != null) {
//                            CommServer.mOutputStream.write(collisionBytes[j]);
//                        } else {
//                            startSerial();
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
//            try {
//                Thread.sleep(CONSTS.SERIAL_PERIOD);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    public static void pwdClear() {
//        CONSTS.TRANSFER_OPEN = false;
//        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
//            if (CONSTS.TRANSFER_OPEN) {
//                break;
//            }
//
//
//            //+++++++++++++++++是否有密码+++++++++++++++++
//            // 主机发送：7B 30 20 08 00 00 00 86 4C 7D
//            //从机返回：7B 30 21 08 00 02 BC BB 5D 7D
//            int[] collisionBytes = new int[10];
//            collisionBytes[0] = 0x7B;
//            collisionBytes[1] = 0x30;
//            collisionBytes[2] = 0x10;
//            collisionBytes[3] = 0x0F;
//            collisionBytes[4] = 0x00;
//            collisionBytes[5] = 0x00;
//            collisionBytes[6] = 0x00;
//            collisionBytes[7] = 0xC7;
//            collisionBytes[8] = 0x3C;
//            collisionBytes[9] = 0x7D;
//            try {
//                if (CommServer.mOutputStream != null) {
//                    for (int j = 0; j < collisionBytes.length; j++) {
//                        if (CommServer.mOutputStream != null) {
//                            CommServer.mOutputStream.write(collisionBytes[j]);
//                        } else {
//                            startSerial();
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
//            try {
//                Thread.sleep(CONSTS.SERIAL_PERIOD);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    public static void powerOff() {
        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }


            int[] openBoxBytes = new int[10];
            openBoxBytes[0] = 0x7B;
            openBoxBytes[1] = 0x30;
            openBoxBytes[2] = 0x10;
            openBoxBytes[3] = 0x0A;
            openBoxBytes[4] = 0x00;
            openBoxBytes[5] = 0x00;
            openBoxBytes[6] = 0x00;
            openBoxBytes[7] = 0xC7;
            openBoxBytes[8] = 0xF0;
            openBoxBytes[9] = 0x7D;

            try {
                for (int j = 0; j < openBoxBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(openBoxBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //关闭GPS
    //主机返回 7B 30 31 03 00 00 00 78 6B 7D
    //从机发送 7B 30 30 03 00 00 00 45 AB 7D

    public static void closeGPS() {
        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }


            int[] serialBytes = new int[10];
            serialBytes[0] = 0x7B;
            serialBytes[1] = 0x30;
            serialBytes[2] = 0x31;
            serialBytes[3] = 0x03;
            serialBytes[4] = 0x00;
            serialBytes[5] = 0x00;
            serialBytes[6] = 0x00;
            serialBytes[7] = 0x78;
            serialBytes[8] = 0x6B;
            serialBytes[9] = 0x7D;

            try {
                for (int j = 0; j < serialBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(serialBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //开启GPS
    //主机返回 7B 30 31 03 00 00 64 79 80 7D
    //从机发送 7B 30 30 03 00 00 64 44 40 7D

    public static void openGPS() {
        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }


            int[] serialBytes = new int[10];
            serialBytes[0] = 0x7B;
            serialBytes[1] = 0x30;
            serialBytes[2] = 0x31;
            serialBytes[3] = 0x03;
            serialBytes[4] = 0x00;
            serialBytes[5] = 0x00;
            serialBytes[6] = 0x64;
            serialBytes[7] = 0x79;
            serialBytes[8] = 0x80;
            serialBytes[9] = 0x7D;

            try {
                for (int j = 0; j < serialBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(serialBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //开启GPS,加密,温度补偿
    //主机返回 7B 30 31 03 00 00 64 79 80 7D
    //从机发送 7B 30 30 03 00 00 64 44 40 7D
    public static void openTemperaturePlanePwd(boolean isTemperature, boolean isPlaneShow, boolean isPwd) {
        int temperature = 0;
        int planeShow = 0;
        int pwd = 0;
        if (isTemperature) {
            temperature = 0x64;
        } else {
            temperature = 0x00;
        }
        if (isPlaneShow) {
            planeShow = 0x64;
        } else {
            planeShow = 0x00;
        }
        boolean isOpen = PrefUtils.getBoolean("isOpen", true, App.getContext());
        if (isPwd && isOpen) {

            //pwd = 0x64;
            //modify
            pwd = 0x00;
        } else {

            pwd = 0x00;
        }

        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }
            int[] crcBytes = new int[6];
            crcBytes[0] = 0x30;
            crcBytes[1] = 0x10;
            crcBytes[2] = 0x0F;
            crcBytes[3] = temperature;
            crcBytes[4] = planeShow;
            crcBytes[5] = pwd;
            int powerCrc = new CRC16M().updateCheckInt(crcBytes, 6);

            int[] serialBytes = new int[10];
            serialBytes[0] = 0x7B;
            serialBytes[1] = 0x30;
            serialBytes[2] = 0x10;
            serialBytes[3] = 0x0F;
            serialBytes[4] = temperature;
            serialBytes[5] = planeShow;
            serialBytes[6] = pwd;
            serialBytes[7] = ((powerCrc & 0xFF00) >> 8);
            serialBytes[8] = (powerCrc & 0x00FF);
            serialBytes[9] = 0x7D;
            Log.e(TAG, temperature + "," + planeShow + "," + pwd + "," + serialBytes[7] + "," + serialBytes[8]);
            try {
                for (int j = 0; j < serialBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(serialBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //一键转运
    //主机返回 7B 30 31 03 00 00 64 79 80 7D
    //从机发送 7B 30 30 03 00 00 64 44 40 7D
    public static void transfer(boolean isTransfer) {
        int transfer = 0;

        if (isTransfer) {
            transfer = 0x64;
        } else {
            transfer = 0x00;
        }


        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }
            int[] crcBytes = new int[6];
            crcBytes[0] = 0x30;
            crcBytes[1] = 0x31;
            crcBytes[2] = 0x10;
            crcBytes[3] = 0x00;
            crcBytes[4] = 0x00;
            crcBytes[5] = transfer;
            int powerCrc = new CRC16M().updateCheckInt(crcBytes, 6);

            int[] serialBytes = new int[10];
            serialBytes[0] = 0x7B;
            serialBytes[1] = 0x30;
            serialBytes[2] = 0x31;
            serialBytes[3] = 0x10;
            serialBytes[4] = 0x00;
            serialBytes[5] = 0x00;
            serialBytes[6] = transfer;
            serialBytes[7] = ((powerCrc & 0xFF00) >> 8);
            serialBytes[8] = (powerCrc & 0x00FF);
            serialBytes[9] = 0x7D;

            try {
                for (int j = 0; j < serialBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(serialBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //一键转运
    //主机返回 7B 30 10 12 00 00 64 79 80 7D
    //从机发送 7B 30 11 12 00 00 64 44 40 7D
    public static void transferIsStart(boolean isTransfer) {
        int transfer = 0;

        if (isTransfer) {
            transfer = 0x64;
        } else {
            transfer = 0x00;
        }


        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }
            int[] crcBytes = new int[6];
            crcBytes[0] = 0x30;
            crcBytes[1] = 0x10;
            crcBytes[2] = 0x12;
            crcBytes[3] = 0x00;
            crcBytes[4] = 0x00;
            crcBytes[5] = transfer;
            int powerCrc = new CRC16M().updateCheckInt(crcBytes, 6);

            int[] serialBytes = new int[10];
            serialBytes[0] = 0x7B;
            serialBytes[1] = 0x30;
            serialBytes[2] = 0x10;
            serialBytes[3] = 0x12;
            serialBytes[4] = 0x00;
            serialBytes[5] = 0x00;
            serialBytes[6] = transfer;
            serialBytes[7] = ((powerCrc & 0xFF00) >> 8);
            serialBytes[8] = (powerCrc & 0x00FF);
            serialBytes[9] = 0x7D;

            try {
                for (int j = 0; j < serialBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(serialBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //是否开启一键转运设置
    //主机返回 7B 30 31 03 00 00 64 79 80 7D
    //从机发送 7B 30 30 03 00 00 64 44 40 7D
    public static void transferSite(boolean isTransfer) {
        int transfer = 0;

        if (isTransfer) {
            transfer = 0x64;
        } else {
            transfer = 0x00;
        }


        CONSTS.TRANSFER_OPEN = false;
        for (int i = 0; i < CONSTS.SERIAL_NUM; i++) {
            if (CONSTS.TRANSFER_OPEN) {
                break;
            }
            int[] crcBytes = new int[6];
            crcBytes[0] = 0x30;
            crcBytes[1] = 0x10;
            crcBytes[2] = 0x11;
            crcBytes[3] = 0x00;
            crcBytes[4] = 0x00;
            crcBytes[5] = transfer;
            int powerCrc = new CRC16M().updateCheckInt(crcBytes, 6);

            int[] serialBytes = new int[10];
            serialBytes[0] = 0x7B;
            serialBytes[1] = 0x30;
            serialBytes[2] = 0x10;
            serialBytes[3] = 0x11;
            serialBytes[4] = 0x00;
            serialBytes[5] = 0x00;
            serialBytes[6] = transfer;
            serialBytes[7] = ((powerCrc & 0xFF00) >> 8);
            serialBytes[8] = (powerCrc & 0x00FF);
            serialBytes[9] = 0x7D;
            for(int j=0;j<serialBytes.length;j++) {
                Log.e(TAG,"bytes:"+ serialBytes[j]);
            }
            try {
                for (int j = 0; j < serialBytes.length; j++) {
                    if (CommServer.mOutputStream != null) {
                        CommServer.mOutputStream.write(serialBytes[j]);
                    } else {
                        startSerial();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
            try {
                Thread.sleep(CONSTS.SERIAL_PERIOD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearCollisionNumberDetail() {

        //+++++++++++++++++清空碰撞+++++++++++++++++
        // 主机发送：7B 30 20 08 00 00 00 86 4C 7D
        //从机返回：7B 30 21 08 00 02 BC BB 5D 7D
        byte[] collisionBytes = new byte[10];
        collisionBytes[0] = 0x7B;
        collisionBytes[1] = 0x30;
        collisionBytes[2] = 0x10;
        collisionBytes[3] = 0x0D;
        collisionBytes[4] = 0x00;
        collisionBytes[5] = 0x00;
        collisionBytes[6] = 0x00;
        collisionBytes[7] = (byte) 0xC6;
        collisionBytes[8] = (byte) 0x84;
        collisionBytes[9] = 0x7D;

        byte[] powerBytes = new byte[6];

        powerBytes[0] = 0x30;
        powerBytes[1] = 0x10;
        powerBytes[2] = 0x0D;
        powerBytes[3] = 0x00;
        powerBytes[4] = 0x00;


        powerBytes[5] = (byte) (0 & 0x00FF);
        try {

            int powerCrc = new CRC16M().updateCheck(powerBytes, 6);

            int[] powerInts = new int[10];


            powerInts[0] = 0x7B;
            powerInts[1] = 0x30;
            powerInts[2] = 0x10;
            powerInts[3] = 0x0D;
            powerInts[4] = 0x00;
            powerInts[5] = 0x00;
            powerInts[6] = 0;
            powerInts[7] = ((powerCrc & 0xFF00) >> 8);
            powerInts[8] = (powerCrc & 0x00FF);
            powerInts[9] = 0x7D;
            String s = "";

            for (int i = 0; i < powerInts.length; i++) {
                s += Integer.toHexString(powerInts[i]) + " ";
            }


            for (int i = 0; i < powerInts.length; i++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(powerInts[i]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    public static String substringSerial(String pSerial) {
        String result;
//        if (pSerial.trim().length() >= 29) {
//
//            result = pSerial.trim().substring(0, 29);
//            if (!result.startsWith("7B")) {
//
//                int startPosition = pSerial.trim().indexOf("7B");
//                int endPosition = pSerial.trim().indexOf("7D");
//                result = pSerial.trim().substring(startPosition, endPosition + 2);
//            }
//        } else {
//            result = pSerial.trim();
//        }
        result = pSerial.trim();
        return result;
    }
}

package com.otqc.transbox.util;


import com.otqc.transbox.db.TransRecordItemDb2;
import com.otqc.transbox.db.TransRecordItemDbNew3;

import java.util.ArrayList;

/**
 * Created by cy on 2015/8/31.
 */
public class CONSTS {

    public final static int SEND_OK = 0;
    public final static int SEND_FAIL = 1;
    public final static int BAD_PARAM = 2;
    public final static String NO_START = "1";
    public final static String START = "2";
    public final static String STOP = "3";
    public final static String DELETE = "4";
    //是否在mainActivity 检测
    //public static boolean IS_START_CHECK = true;

    //屏幕是否开启
    public static boolean SCREEN_LIGHT = true;

    //每页的页数
    public static int PAGE_SIZE = 20;
    //电量
    public static int POWER = 13;

    public static long SERVER_TIME = 0L;

    public static double INIT_SERIAL = -100;
    //第一次进入转运
    public static int FIRST_NUM = 103;
    public static int FIRST_STAND = 100;
    //30s 30000ms  间隔时间
    public static long SERIAL_TIME = 30000;

    public static double N_TEMPERATURE = 0;
    public static double N_HUMIDITY = 0;
    public static double N_POWER = 0;
    public static double N_COLLISION = 0;

    //设置当前屏幕的状态
    public static boolean SCREEN_STATUS = true;
    //经纬度
    public static double LONGITUDE = 0;
    public static double LATITUDE = 0;

    //当前定位经纬度的城市
    public static String CITY = "";
    public static String LOCATION_TYPE = "";

    //距离
    public static double DISTANCE = 0;

    //距离目的地多远停止转运(km)
    public static double END_DISTANCE = 0.2;


    //自动开始的条件
    public static String AUTO_CONDITION = "";


    //20公里发送短信(km)
    public static double END_DISTANCE_20 = 20;
    //停止转运后,多少时间关机 60*60s
    public static long END_TIME = 60 * 60;

    ///自动开始的时间
    public static long START_TIME = 60 * 15;

    //开始的时间
    public static long TRANS_START = 0;

    //转运是否开始  0待转运   1转运中  2未转运
    public static int IS_START = 2;

    //转运的id
    public static String TRANSFER_ID = "";

    //开箱的次数
    public static int OPEN = 0;
    public static int COLLISION = 0;
    public static double DISTANCE_OLD = 0;
    public static double DURATION_OLD = 0;
    //总数,用来判断电量
    public static int COUNT = -1;
    //存入本地数据库的次数,如果大于等于5分钟/30秒=10就上传
    public static int UPLOAD_NUM = 0;

    //上传的值
   //fantasy
    public  static int UPLOAD_NUM_VALUE = 2;

    public static int INSERT_NUM = 0;
    public static boolean TRANSFER_OPEN = false;
    //串口循环的时间
    public static int SERIAL_NUM = 1;

    //串口发送的间隔时间 ms
    public static int SERIAL_PERIOD = 500;


    public static boolean MAIN_LOCATION = false;

    //判断结束的时间
    public static String END_FLAG = "";
    public static String STOP_DEVICES = "";

    //自动转运的时间
    public static String END_FLAG_AUTO = "";

    //自动结束的时间
    public static String END_FLAG_OVER = "";

    //获取转运的数据
    public static ArrayList<TransRecordItemDb2> TRANS_DATAS = new ArrayList<>();
    //获取转运的数据
    public static TransRecordItemDb2 TRANS = new TransRecordItemDb2();
    public static TransRecordItemDbNew3 TRANS_DETAIL = new TransRecordItemDbNew3();
    //记录删除的index
    public static ArrayList<Integer> TRANS_DEL = new ArrayList<>();

    //初始化状态
    public static boolean INIT = true;

    //跳回主页重新检测转运.
    public static boolean TRANSFER_INIT = true;
    public final static String ON_WAY_ACTION = "com.transfer.on_way_action";
    public final static String MAIN_ACTION = "com.transfer.main_action";
    public final static String ON_WAY_TRANS = "com.transfer.main_transfer";
    public final static String EXCEPTION = "com.transfer.exception";
    public final static String MAIN_MAP = "com.transfer.main_map";
    //温度的异常时间 ms
    public static int EXCEPTION_TIME = 1000 * 60 * 20;
    //android.intent.action.MAIN
    //com.action.OnWayActivity
    public final static String MAIN = "android.intent.action.MAIN";
    public final static String ON_WAY = "com.action.OnWayActivity";

    //是否发送一键转运的设置
    public static boolean IS_TRANSFER = true;
    public static boolean IS_OPEN = false;

    //20公里发送短信

}

package com.otqc.transbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.bugtags.library.BugtagsOptions;
import com.otqc.transbox.engine.PreferencesHelper;

import com.otqc.transbox.engine.PreferencesHelper;
import com.tencent.bugly.crashreport.CrashReport;

import org.litepal.LitePal;
import com.otqc.transbox.engine.PreferencesHelper;

import com.otqc.transbox.engine.PreferencesHelper;
import org.xutils.x;

import io.realm.Realm;

public class App extends MultiDexApplication {

    private String TAG = "App";
    private static Context context; //全局的context
    private PreferencesHelper mPreferencesHelper;
    private static App sApp;
    private static Handler handler; //主线程handler
    public int screenW;
    public int screenH;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sApp = this;
        handler = new Handler();
        Realm.init(this);

        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(false).//是否获取位置，默认 true
                build();
        CrashReport.initCrashReport(getApplicationContext(), "f97ee0988d", false);

        //xutils3
        //Xutils3 初始化
        x.Ext.init(this);
        // 设置是否输出debug
        x.Ext.setDebug(true);

        //数据库初始化
        LitePal.initialize(this);
        SQLiteDatabase db = LitePal.getDatabase();

        //云巴推送
//        YunBaManager.start(getApplicationContext());
//
//        YunBaManager.subscribe(getApplicationContext(), new String[]{"t1"}, new IMqttActionListener() {
//
//            @Override
//            public void onSuccess(IMqttToken arg0) {
//                Log.d(TAG, "Subscribe topic succeed");
//            }
//
//            @Override
//            public void onFailure(IMqttToken arg0, Throwable arg1) {
//                Log.d(TAG, "Subscribe topic failed");
//            }
//        });

        // 得到屏幕的宽度和高度
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       // MultiDex.install(this) ;
        // initialize最好放在attachBaseContext最前面，初始化直接在Application类里面，切勿封装到其他类
//        SophixManager.getInstance().setContext(this)
//                .setAppVersion("1.3.8")
//                .setAesKey(null)
//                .setEnableDebug(true)
//                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
//                    @Override
//                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
//                        // 补丁加载回调通知
//                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
//                            // 表明补丁加载成功
//                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
//                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
//                            // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
//                        } else {
//                            // 其它错误信息, 查看PatchStatus类说明
//                        }
//                    }
//                }).initialize();

    }
    public static Context getContext() {
        return context;
    }

    public static App get() {
        App inst = sApp;  // <<< 在这里创建临时变量
        if (inst == null) {
            synchronized (App.class) {
                inst = sApp;
                if (inst == null) {
                    inst = new App();
                    sApp = inst;
                }
            }
        }
        return inst;  // <<< 注意这里返回的是临时变量
    }

    public PreferencesHelper getPreferencesHelper() {
        if (mPreferencesHelper == null)
            mPreferencesHelper = new PreferencesHelper(this);
        return mPreferencesHelper;
    }


    public static Handler getHandler() {
        return handler;
    }
}

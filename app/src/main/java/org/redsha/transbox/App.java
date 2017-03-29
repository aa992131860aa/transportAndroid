package org.redsha.transbox;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;

import org.redsha.transbox.engine.PreferencesHelper;

import io.realm.Realm;

public class App extends Application {

    private static Context context; //全局的context
    private PreferencesHelper mPreferencesHelper;
    private static App sApp;
    private static Handler handler; //主线程handler

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
        Bugtags.start("4851f674f5bbeed3bc038376574b12cf", this, Bugtags.BTGInvocationEventNone, options);

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

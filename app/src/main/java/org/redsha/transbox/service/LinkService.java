package org.redsha.transbox.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.redsha.transbox.App;
import org.redsha.transbox.bean.EmitBean;
import org.redsha.transbox.controller.on.OnWayActivity;
import org.redsha.transbox.db.TransOddDb;
import org.redsha.transbox.http.URL;
import org.redsha.transbox.util.CommonUtil;
import org.redsha.transbox.util.LogUtil;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;

import java.net.URISyntaxException;

import io.realm.Realm;

/**
 * 2016/11/9.
 */

public class LinkService extends Service {
    private static String TAG = "LinkService";

    private Socket mSocket;

    {
        try {
//            mSocket = IO.socket("http://116.62.28.28/transbox/api/socket.io");

//            IO.Options options = new IO.Options();
//            options.path = "/transbox/api";
           // mSocket = IO.socket("http://116.62.28.28:1337");
            mSocket = IO.socket(URL.SOCKET);
        } catch (URISyntaxException e) {
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSocket.connect();
        mSocket.on("connect", onNewMessage);
        mSocket.on("created", onNewCreate);
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            LogUtil.e(TAG, "连接socket.io成功了");

            CommonUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {

                    String boxid = PrefUtils.getString("boxid", "", App.getContext());
                    if (!TextUtils.isEmpty(boxid)) {

                        EmitBean bean = new EmitBean();
                        bean.setQuery(boxid);
                        bean.setUrl("/transbox/api/socket/getSocketId/" + boxid);

                        try {
                            JSONObject obj = new JSONObject(new Gson().toJson(bean));
                            mSocket.emit("get", obj);

                            String url = obj.getString("url");
                            String query = obj.getString("query");
                            LogUtil.e(TAG, url + " / " + query);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });

        }
    };

    private Emitter.Listener onNewCreate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            CommonUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {

                    final JSONObject result = (JSONObject) args[0];
                    try {
                        LogUtil.e(TAG,"接收转运ok："+result.toString());

                        PrefUtils.putString("tid", result.getString("transferid"), App.getContext());

                        Realm realm = RealmUtil.getInstance().getRealm();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.createObjectFromJson(TransOddDb.class, result.toString());
                            }
                        });

                        Intent intent = new Intent(App.getContext(), OnWayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.getContext().startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        LogUtil.e(TAG, "onDestroy()");
    }

}

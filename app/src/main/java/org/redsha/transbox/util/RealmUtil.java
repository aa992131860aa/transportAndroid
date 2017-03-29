package org.redsha.transbox.util;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmUtil {

    private String realmName = "trans.realm";

    private RealmUtil() {
    }

    private static class SingletonHolder {
        private static final RealmUtil INSTANCE = new RealmUtil();
    }

    public static RealmUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取realm对象
     */
    public Realm getRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(realmName)// db name
//                .encryptionKey(key)// if need encrypt
                .build();

        // use this config
        Realm realm = Realm.getInstance(config);
        return realm;
    }

}

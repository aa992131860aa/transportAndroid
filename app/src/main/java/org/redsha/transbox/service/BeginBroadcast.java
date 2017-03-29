package org.redsha.transbox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.redsha.transbox.App;
import org.redsha.transbox.controller.main.MainActivity;

public class BeginBroadcast extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent i = new Intent(App.getContext(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            App.getContext().startActivity(i);
        }
    }

}
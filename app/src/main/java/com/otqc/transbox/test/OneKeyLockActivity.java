package com.otqc.transbox.test;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/*第二个启动界面*/
public class OneKeyLockActivity extends AppCompatActivity {
    private DevicePolicyManager devicePolicyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        try {
            devicePolicyManager.lockNow();
            finish();
        } catch (Exception e) {
            Intent intent = new Intent(this, TestMainActivity.class);
           // startActivity(intent);
          //  finish();
        }
    }
}

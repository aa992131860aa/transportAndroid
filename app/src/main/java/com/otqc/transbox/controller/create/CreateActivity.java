package com.otqc.transbox.controller.create;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.otqc.transbox.R;
import com.otqc.transbox.bean.CreateBaseInfo;
import com.otqc.transbox.bean.CreateGetTimeInfo;
import com.otqc.transbox.bean.CreateOpenPsInfo;
import com.otqc.transbox.bean.CreateOpoInfo;
import com.otqc.transbox.bean.CreateOrganInfo;
import com.otqc.transbox.bean.CreateTransPersonInfo;
import com.otqc.transbox.bean.CreateTransToInfo;
import com.otqc.transbox.databinding.ActivityCreateBinding;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.ToastUtil;

public class CreateActivity extends AppBaseActivity {

    private CreateData mData;
    private ActivityCreateBinding binding;
    public static boolean isWriteSample = false;
    Handler handler = new Handler();
    Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据

        }
    };
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String TAG = CreateActivity.class.getSimpleName();
    @Override
    protected void initVariable() {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: ");
                Log.d(TAG, "onLocationChanged: latitude = "+location.getLatitude());
                Log.d(TAG, "onLocationChanged: longitude = "+location.getLongitude());
                Log.d(TAG, "onLocationChanged: provider = "+location.getProvider());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged: ");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled: ");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled: ");
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            return ;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            return ;
        }

        //ocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListener);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create);
        mData = new CreateData();
        mData.setPageState(1);
        mData.setPageShow("下一步");

        // test
        CreateBaseInfo createBaseInfo = new CreateBaseInfo();
        createBaseInfo.setOrganCount("1");
        mData.setBaseInfo(createBaseInfo);
        CreateOrganInfo createOrganInfo = new CreateOrganInfo();
        createOrganInfo.setBloodSampleCount("1");
        createOrganInfo.setOrganizationSampleCount("1");
        mData.setOrgan(createOrganInfo);
        mData.setTime(new CreateGetTimeInfo());
        mData.setTo(new CreateTransToInfo());
        mData.setPerson(new CreateTransPersonInfo());
        mData.setOpo(new CreateOpoInfo());
        mData.setPs(new CreateOpenPsInfo());
//        mData.setCps(new CreateOpenPsConfirmInfo());
        binding.setInfo(mData);
        binding.setPresenter(new CreatePresenter(this, mData, binding));
        initEditEvent();



    }

    //判断弹出软键盘
    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return screenHeight - rect.bottom != 0;
    }

    private void initEditEvent() {
        View v = binding.buttonClear1;
        v.setOnClickListener(mOnClickListener);

        v = binding.buttonClear2;
        v.setOnClickListener(mOnClickListener);



//        t .setOnFocusChangeListener(new LoginOnFocusChangeListener(
//                R.id.button_clear1, true));
//
//
//        et.setOnFocusChangeListener(new LoginOnFocusChangeListener(
//                R.id.button_clear2, true));
    }

    public class LoginOnFocusChangeListener implements View.OnFocusChangeListener {

        private int mClearBtnId;
        private boolean mShowToast;

        public LoginOnFocusChangeListener(int clearBtnId, boolean showToast) {
            this.mClearBtnId = clearBtnId;
            this.mShowToast = showToast;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Button bt = (Button) findViewById(mClearBtnId);
            EditText et = (EditText) v;
            if (hasFocus && !TextUtils.isEmpty(et.getText().toString())) {
                bt.setVisibility(View.VISIBLE);
            } else {
                if (mShowToast && !hasFocus
                        && et.getText().toString().length() != 4) {
                    ToastUtil.showToast("密码必须4位哦！");
                }
                bt.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void initData() {
        /**
         * 初始化需要的的数据
         */
        mData.getBaseInfo().setDeviceType("android");
        mData.getOrgan().setDataType("new");
        mData.getPerson().setDataType("new");
        mData.getPerson().setTransferPersonid("");

        String boxid = PrefUtils.getString("boxid", "", getApplicationContext());
        String hospitalName = PrefUtils.getString("hospitalName", "", getApplicationContext());
        if (!TextUtils.isEmpty(boxid)) {
            mData.getBaseInfo().setBox_id(boxid);
        }
        if (!TextUtils.isEmpty(hospitalName)) {
            mData.getTo().setToHospName(hospitalName);
        }
    }

    public void nullToast() {
        ToastUtil.showToast(getString(R.string.common_nullToast));
    }

    public void phoneErrorToast() {
        ToastUtil.showToast(getString(R.string.export_phoneError));
    }

    public void nullOpoToast() {
        ToastUtil.showToast(getString(R.string.create_nullOpo));
    }

    public void psDiff() {
        ToastUtil.showToast(getString(R.string.create_ps_diff));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_clear1: {
                    mData.getPs().setOpenPs1("");
                    break;
                }
                case R.id.button_clear2: {
                    mData.getPs().setOpenPs2("");
                    break;
                }
            }
        }
    };

}
package com.otqc.transbox.controller.create;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.bean.CreateOrganInfo;
import com.otqc.transbox.bean.TransferPersonBean;
import com.otqc.transbox.controller.main.MainActivity;
import com.otqc.transbox.controller.on.OnWayActivity;
import com.otqc.transbox.databinding.ActivityCreateBinding;
import com.otqc.transbox.http.HttpHelper;
import com.otqc.transbox.http.HttpObserver;
import com.otqc.transbox.http.URL;
import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.json.Datas;
import com.otqc.transbox.json.DepartmentsJson;
import com.otqc.transbox.json.HospitalJson;
import com.otqc.transbox.json.LatiLongJson;
import com.otqc.transbox.json.Opo;
import com.otqc.transbox.json.OpoInfoContact;
import com.otqc.transbox.json.OpoInfoJson;
import com.otqc.transbox.json.OpoListJson;
import com.otqc.transbox.json.OpoProvinceJson;
import com.otqc.transbox.json.PhotoJson;
import com.otqc.transbox.json.RepeatJson;
import com.otqc.transbox.json.TransferJson;
import com.otqc.transbox.json.UsersListJson;
import com.otqc.transbox.service.CommServer;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.CRC16M;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.LocationUtils;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.SerialUtil;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.util.Validator;
import com.otqc.transbox.view.BloodPopup;
import com.otqc.transbox.view.DialogMaker;
import com.otqc.transbox.view.OrganSamplePopup;
import com.otqc.transbox.view.OrganTypePopup;
import com.otqc.transbox.view.SinglePopup;
import com.otqc.transbox.view.TracfficTypePopup;
import com.otqc.transbox.view.TransPersonPopup;
import com.otqc.transbox.view.pickerview.TimePickerView;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class CreatePresenter {
    private CreateActivity mActivity;
    private CreateData mData;
    private ActivityCreateBinding mBinding;
    private List<TransferPersonBean> mTransferPersonBean;   //转运人
    private List<Opo> mOpoBeen;
    private List<String> mOrganType;
    private List<String> mTracfficType;
    private List<String> mBloodType;
    private List<String> mOrganisationSample;
    private AMapLocationClient locationClient = null;
    private boolean isSave = false; //是否保存信息
    private String TAG = "CreatePresenter";

    private String toHospitalAddress;
    private String startLocation;
    private String endLocation;
    private double distance;
    private String organSeg;
    private String oldOrganSeg;
    private String getTime;
    private String organ;
    private String blood;
    private String sample;
    private String fromAll;
    private String from;
    private String hosp;
    private String person;
    private String phone;
    private String opo;
    private String contanctPerson;
    private String contactPhone;
    private String departmentName;
    private String departmentPhone;
    private String method;
    private String no;

    private String organNum;
    private String bloodNum;
    private String sampleNum;
    private String pwd1;
    private String pwd2;


    private EditText edt_organ_org;
    private EditText edt_time;
    private EditText edt_organ;
    private EditText edt_organ_num;
    private EditText edt_blood;
    private EditText edt_blood_num;
    private EditText edt_sample;
    private EditText edt_sample_num;
    private TextView edt_person;
    private EditText edt_hosp;
    private EditText edt_from;
    private EditText edt_phone;
    private EditText edt_opo;
    private EditText edt_contact_person;
    private EditText edt_contact_phone;
    private EditText edt_method;
    private EditText edt_no;
    private EditText edt_pwd1;
    private EditText edt_pwd2;

    private EditText edt_department_name;
    private EditText edt_department_phone;

    //用户信息
    private List<UsersListJson.ObjBean> mUsersBean;
    private Dialog dialog;

    private List<OpoInfoContact> mOpoInfoContact;

    //科室协调员s
    private List<DepartmentsJson.ObjBean> mDepartments;
    private TimePickerView pvDate;

    //开始的经纬度,是否获取
    private boolean isStartLatLng = false;
    //结束的经纬度,是否获取
    private boolean isEndLatLng = false;
    private String modifyOrganSeg = "";
    private String mType = "器官";

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if(msg.what==1){
                ToastUtil.showToast("come in here");
                edt_time.setText(CommonUtil.getTrueTimeMM());
            }
        }
    };

    public CreatePresenter(CreateActivity createActivity, CreateData info, ActivityCreateBinding binding) {
        this.mActivity = createActivity;
        this.mData = info;
        this.mBinding = binding;


        initView();
//        getTime();  //初始化时间选择器
        getTransferPersons();   //初始化转运人

        initPopupState();
        initKey();
        //获取转运人列表
        getUsersList();
        //初始化肝脏 血型 样本 转运方式

        //edt_blood.setText("A");
        //edt_sample.setText("脾脏");
        edt_method.setText("救护车");

        //判断是否保存,来初始化数据
        initSaveData();
        if (!isSave) {
            //初始化定位
            initLocation();
            // 启动定位
            locationClient.startLocation();
            getOpo();


        }
        getOpos();
        //获取科室协调员列表
        String deviceId = PrefUtils.getString("deviceId", "", mActivity);
        getDepartments(deviceId);

        if (!isSave) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            //edt_time.setText();
        }

        //初始化选择时间
        initDatePicker();
        //设置默认时间
        edt_time.setText(CommonUtil.getTrueTimeMM());
//        if(edt_time.getText().toString().trim().contains("2010-01-01 08:00")){
//            new Thread(){
//                @Override
//                public void run() {
//                    super.run();
//                    try {
//                        Thread.sleep(2000);
//                        Message message = new Message();
//                        message.what = 1;
//                        mHandler.sendMessage(message);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//        }

        mData.setPageShow("确定");

    }

    private void initView() {
        edt_organ_org = (EditText) mActivity.findViewById(R.id.edt_organ_seg);
        edt_time = (EditText) mActivity.findViewById(R.id.edt_time);
        edt_organ = (EditText) mActivity.findViewById(R.id.edt_organ);
        edt_organ_num = (EditText) mActivity.findViewById(R.id.edt_organ_num);
        edt_blood = (EditText) mActivity.findViewById(R.id.edt_blood);
        edt_blood_num = (EditText) mActivity.findViewById(R.id.edt_blood_num);
        edt_sample = (EditText) mActivity.findViewById(R.id.edt_sample);
        edt_sample_num = (EditText) mActivity.findViewById(R.id.edt_sample_num);

        edt_from = (EditText) mActivity.findViewById(R.id.edt_from);
        edt_person = (TextView) mActivity.findViewById(R.id.edt_person);
        edt_phone = (EditText) mActivity.findViewById(R.id.edt_phone);
        edt_hosp = (EditText) mActivity.findViewById(R.id.edt_hosp);
        edt_department_name = (EditText) mActivity.findViewById(R.id.edt_department_name);
        edt_department_phone = (EditText) mActivity.findViewById(R.id.edt_department_phone);

        edt_opo = (EditText) mActivity.findViewById(R.id.edt_opo);
        edt_contact_person = (EditText) mActivity.findViewById(R.id.edt_contact_person);
        edt_contact_phone = (EditText) mActivity.findViewById(R.id.edt_contact_phone);
        edt_method = (EditText) mActivity.findViewById(R.id.edt_method);
        edt_no = (EditText) mActivity.findViewById(R.id.edt_no);

        edt_pwd1 = (EditText) mActivity.findViewById(R.id.edt_pwd1);
        edt_pwd2 = (EditText) mActivity.findViewById(R.id.edt_pwd2);


        edt_organ_num.setText("1");
        edt_blood_num.setText("1");
        edt_sample_num.setText("1");
        edt_hosp.setText(PrefUtils.getString("hospitalName", "", mActivity));
    }

    /**
     * 保存信息
     *
     * @param info
     */
    public void save(CreateData info) {

        //器官段号
        PrefUtils.putString("segNumber", organSeg, mActivity);
        //器官段号
        PrefUtils.putString("modifyOrganSeg", modifyOrganSeg, mActivity);
        //获取器官时间
        PrefUtils.putString("organAt", getTime, mActivity);
        //器官类型
        PrefUtils.putString("organType", organ, mActivity);
        //器官数量
        PrefUtils.putString("organCount", organNum, mActivity);
        //血型
        PrefUtils.putString("bloodType", blood, mActivity);
        //血液样本数量
        PrefUtils.putString("bloodSampleCount", bloodNum, mActivity);
        //组织样本类型
        PrefUtils.putString("organizationSampleType", sample, mActivity);
        //组织样本数量
        PrefUtils.putString("organizationSampleCount", sampleNum, mActivity);
        //转运起始地
        PrefUtils.putString("fromCity", fromAll, mActivity);
        //转运目的地
        PrefUtils.putString("toHospName", hosp, mActivity);
        //转运人
        PrefUtils.putString("personName", person, mActivity);
        //转运人电话
        PrefUtils.putString("personPhone", phone, mActivity);
        //获取组织
        PrefUtils.putString("opoName", opo, mActivity);
        //联系人
        PrefUtils.putString("contactPerson", contanctPerson, mActivity);
        //联系人电话
        PrefUtils.putString("contactPhone", contactPhone, mActivity);

        //联系人
        PrefUtils.putString("departmentName", departmentName, mActivity);
        //联系人电话
        PrefUtils.putString("departmentPhone", departmentPhone, mActivity);

        //转运方式
        PrefUtils.putString("tracfficType", method, mActivity);
        //航班/车次
        PrefUtils.putString("tracfficNumber", no, mActivity);
        //开箱密码1
        PrefUtils.putString("openPs1", pwd1, mActivity);
        //开箱密码2
        PrefUtils.putString("openPs2", pwd2, mActivity);
        //其他信息
        //PrefUtils.putString("transferPersonId", mData.getPerson().getTransferPersonid(), mActivity);

        PrefUtils.putString("opoId", mData.getOpo().getOpoid(), mActivity);

        LogUtil.e(TAG, "db2:" + mData.getPerson().getDataType());
        if (isSave) {
            modifyTransfer(info, false);
        } else {
            insertTransfer(info, "0");
        }
        //是否保存的标志
        PrefUtils.putBoolean("isSave", true, mActivity);


        mActivity.finish();
    }

    private void initSaveData() {
        //转运人和转运电话,保存上一次的数据
        //转运人
        mData.getPerson().setName(PrefUtils.getString("personName", "", mActivity));
        //转运人电话
        mData.getPerson().setPhone(PrefUtils.getString("personPhone", "", mActivity));

        isSave = PrefUtils.getBoolean("isSave", false, mActivity);

        if (isSave) {
            //器官段号
            modifyOrganSeg = PrefUtils.getString("modifyOrganSeg", "", mActivity);
            //mData.getOrgan().setSegNumber(PrefUtils.getString("segNumber", "", mActivity));
            organSeg = PrefUtils.getString("segNumber", "", mActivity);
            oldOrganSeg = PrefUtils.getString("segNumber", "", mActivity);

            EditText editText = (EditText) mActivity.findViewById(R.id.edt_organ_seg);
            if (!"".equals(modifyOrganSeg)) {
                editText.setText(modifyOrganSeg);
                editText.setSelection(modifyOrganSeg.length());
            } else {
                editText.setText(organSeg);
                editText.setSelection(organSeg.length());
            }

            //editText.setEnabled(false);
            //ToastUtil.showToast(mBinding.tvOrganSeg.getText()+","+PrefUtils.getString("segNumber", "", mActivity).length());

            //mBinding.tvOrganSeg.setSelection(3);
            //获取器官时间
            mData.getBaseInfo().setGetOrganAt(PrefUtils.getString("organAt", "", mActivity));
            edt_time.setText(PrefUtils.getString("organAt", "", mActivity));
            //器官类型
            mData.getOrgan().setType(PrefUtils.getString("organType", "", mActivity));
            edt_organ.setText(PrefUtils.getString("organType", "", mActivity));
            //器官数量
            mData.getBaseInfo().setOrganCount(PrefUtils.getString("organCount", "", mActivity));
            edt_organ_num.setText(PrefUtils.getString("organCount", "", mActivity));
            //血型
            mData.getOrgan().setBloodType(PrefUtils.getString("bloodType", "", mActivity));
            edt_blood.setText(PrefUtils.getString("bloodType", "", mActivity));
            //血液样本数量
            mData.getOrgan().setBloodSampleCount(PrefUtils.getString("bloodSampleCount", "", mActivity));
            edt_blood_num.setText(PrefUtils.getString("bloodSampleCount", "", mActivity));
            //组织样本类型
            mData.getOrgan().setOrganizationSampleType(PrefUtils.getString("organizationSampleType", "", mActivity));
            edt_sample.setText(PrefUtils.getString("organizationSampleType", "", mActivity));
            //组织样本数量
            mData.getOrgan().setOrganizationSampleCount(PrefUtils.getString("organizationSampleCount", "", mActivity));
            edt_sample_num.setText(PrefUtils.getString("organizationSampleCount", "", mActivity));
            //转运起始地
            fromAll = PrefUtils.getString("fromCity", "", mActivity);
            if (fromAll.contains("区")) {
                edt_from.setText(fromAll.split("区")[0] + "区");
            } else if (fromAll.contains("县")) {
                edt_from.setText(fromAll.split("县")[0] + "县");
            } else {
                edt_from.setText(fromAll);
            }
            edt_from.setSelection(edt_from.getText().length());
            //edt_from.setText(PrefUtils.getString("fromCity", "", mActivity));
            //转运目的地
            //mData.getTo().setToHospName(PrefUtils.getString("toHospName", "", mActivity));
            //edt_hosp.setText(PrefUtils.getString("toHospName", "", mActivity));
            //bbb
            edt_person.setText(PrefUtils.getString("personName", "", mActivity));
            edt_phone.setText(PrefUtils.getString("personPhone", "", mActivity));


            //获取组织
            mData.getOpo().setName(PrefUtils.getString("opoName", "", mActivity));
            edt_opo.setText(PrefUtils.getString("opoName", "", mActivity));
            //联系人
            mData.getOpo().setContactPerson(PrefUtils.getString("contactPerson", "", mActivity));
            edt_contact_person.setText(PrefUtils.getString("contactPerson", "", mActivity));

            //联系人电话
            mData.getOpo().setContactPhone(PrefUtils.getString("contactPhone", "", mActivity));
            edt_contact_phone.setText(PrefUtils.getString("contactPhone", "", mActivity));

            edt_department_name.setText(PrefUtils.getString("departmentName", "", mActivity));
            edt_department_phone.setText(PrefUtils.getString("departmentPhone", "", mActivity));


            //转运方式
            mData.getBaseInfo().setTracfficType(PrefUtils.getString("tracfficType", "", mActivity));
            edt_method.setText(PrefUtils.getString("tracfficType", "", mActivity));
            //航班/车次
            mData.getBaseInfo().setTracfficNumber(PrefUtils.getString("tracfficNumber", "", mActivity));
            edt_no.setText(PrefUtils.getString("tracfficNumber", "", mActivity));
            //开箱密码1
            mData.getPs().setOpenPs1(PrefUtils.getString("openPs1", "", mActivity));
            edt_pwd1.setText(PrefUtils.getString("openPs1", "", mActivity));
            //开箱密码2
            mData.getPs().setOpenPs2(PrefUtils.getString("openPs2", "", mActivity));
            edt_pwd2.setText(PrefUtils.getString("openPs2", "", mActivity));
            //其他信息
            mData.getPerson().setTransferPersonid(PrefUtils.getString("transferPersonId", "", mActivity));
            mData.getPerson().setDataType(PrefUtils.getString("personDataType", "", mActivity));
            mData.getOpo().setOpoid(PrefUtils.getString("opoId", "", mActivity));
            mData.getOpo().setDataType(PrefUtils.getString("opoDataType", "", mActivity));
            LogUtil.e(TAG, "personData:" + PrefUtils.getString("personDataType", "", mActivity));
            LogUtil.e(TAG, "opoData:" + PrefUtils.getString("opoDataType", "", mActivity));

            String edt = edt_opo.getText().toString();
            if (edt.contains("OPO")) {
                getOneOpo(edt.split("OPO")[0]);
            }


        }
    }

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(App.getContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false

        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {

            LogUtil.e("CreatePresenter：", "start" + loc);
            if (null != loc && !"".equals(loc.getAddress())) {
                String position = loc.getAddress();
                LogUtil.e("CreatePresenter：", position);
                if (position.contains("县")) {
                    edt_from.setText(position.split("县")[0] + "县");
                } else if (position.contains("区")) {
                    edt_from.setText(position.split("区")[0] + "区");
                } else {
                    edt_from.setText(position);
                }
                fromAll = position;
                edt_from.setSelection(position.length());

                destroyMapObj();
            } else {
                LogUtil.e("CreatePresenter：", "失败");
                //locationClient.startLocation();
                ToastUtil.showToast("定位失败，获取当前位置失败。");
            }
        }
    };

    private void destroyMapObj() {
        locationClient.stopLocation(); // stop location
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    private void initKey() {
        String key = PrefUtils.getString("key", "", App.getContext());
//        if (!TextUtils.isEmpty(key)) {
//            KeywordBean keywordData = JsonUtil.parseJsonToBean(key, KeywordBean.class);
//            if (keywordData != null) {
//                mOrganType = keywordData.getOrgan();
//                mTracfficType = keywordData.getTracfficType();
//                mBloodType = keywordData.getBloodType();
//                mOrganisationSample = keywordData.getOrganisationSample();
//            }
//        }
        mOrganType = new ArrayList<>();
        mOrganType.add("肝脏");
        mOrganType.add("肾脏");
        mOrganType.add("心脏");
        mOrganType.add("肺");
        mOrganType.add("胰脏");
        mOrganType.add("眼角膜");
        mBloodType = new ArrayList<>();
        mBloodType.add("A");
        mBloodType.add("B");
        mBloodType.add("AB");
        mBloodType.add("O");
        mOrganisationSample = new ArrayList<>();
        mOrganisationSample.add("脾脏");
        mOrganisationSample.add("血管");

        mTracfficType = new ArrayList<>();
        mTracfficType.add("飞机");
        mTracfficType.add("火车");
        mTracfficType.add("救护车");

    }

    private void initPopupState() {
        BloodPopup.lastPosition = -1;
        OrganSamplePopup.lastPosition = -1;
        OrganTypePopup.lastPosition = -1;
        SinglePopup.lastPosition = -1;
        TracfficTypePopup.lastPosition = -1;
        TransPersonPopup.lastPosition = -1;
    }

    private void getTransferPersons() {
        String hospitalid = PrefUtils.getString("hospitalid", "", App.getContext());

        new HttpHelper().getTransPerson(hospitalid).subscribe(new HttpObserver<List<TransferPersonBean>>() {
            @Override
            public void onComplete() {
                LogUtil.e("getTransPerson", "onComplete()");
            }

            @Override
            public void onSuccess(List<TransferPersonBean> model) {
                LogUtil.e("getTransPerson", "onSuccess()");

                if (model != null && model.size() > 0) {
                    mTransferPersonBean = model;
                    mData.setTransPersonSize(model.size());
                } else {
                    mData.setTransPersonSize(0);
                }

            }
        });
    }

//    private void getOpoInfo() {11
//        new HttpHelper().getAllOpo().subscribe(new HttpObserver<List<OpoBean>>() {
//            @Override
//            public void onComplete() {
//
//            }
//
//            @Override
//            public void onSuccess(List<OpoBean> model) {
//
//                if (model != null && model.size() > 0) {
//                    mOpoBeen = model;
//                }
//
//            }
//        });
//    }

    /**
     * 返回
     */
    public void back() {
        switch (mData.getPageState()) {
            case 1:
                mActivity.finish();
                break;
            case 2:

                mData.setPageState(1);
                mBinding.saveBtn.setVisibility(View.GONE);
                break;
//            case 3:
//                mData.setPageState(2);
//                mBinding.saveBtn.setVisibility(View.GONE);
//                break;
//            case 4:
//                mData.setPageShow(mActivity.getString(R.string.common_next));
//                mBinding.saveBtn.setVisibility(View.GONE);
//                mData.setPageState(3);
//                break;
//            case 5:
//                mData.setPageShow(mActivity.getString(R.string.common_pre));
//                mBinding.saveBtn.setVisibility(View.GONE);
//                mData.setPageState(4);
//                break;
//            case 6:
//                mData.setPageShow(mActivity.getString(R.string.common_confirmOk));
//                mBinding.saveBtn.setVisibility(View.GONE);
//                mData.setPageState(5);
//                break;
            case 3:
                mData.setPageShow(mActivity.getString(R.string.common_confirmOk));
                mData.setPageState(1);
                mBinding.saveBtn.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * 下一步
     */
    public void next(CreateData info) {

        switch (mData.getPageState()) {
            case 1:
                //   boolean page1 = checkPage1(info);
                organSeg = edt_organ_org.getText().toString().trim();
                getTime = edt_time.getText().toString().trim();
                person = edt_person.getText().toString();
                phone = edt_phone.getText().toString();


                if ("".equals(organSeg)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        //Log.e(TAG, CommonUtil.getTrueTime());
                        organSeg = "DP" + sdf.format(sdfAll.parse(CommonUtil.getTrueTime()));
                    } catch (Exception e) {

                    }


                }

                if (organSeg.contains("P") && organSeg.length() > 14) {
                    modifyOrganSeg = "";
                } else {
                    modifyOrganSeg = organSeg;
                }
                edt_organ_org.setText(modifyOrganSeg);

                if (isSave) {
                    mData.setPageState(2);
                    mData.setPageShow(mActivity.getString(R.string.common_confirmOk));
                } else {
                    if ("".equals(edt_time.getText().toString().trim())) {
                        edt_time.setText(CommonUtil.getTrueTimeMM());
                    }
                    if ("".equals(person)) {
                        ToastUtil.showToast("请选择转运人");
                    } else {
                        isRepeatOrganSeg(organSeg);
                    }
                }


                break;
            case 2:

                organ = edt_organ.getText().toString();
                organNum = edt_organ_num.getText().toString();
                blood = edt_blood.getText().toString();
                bloodNum = edt_blood_num.getText().toString();
                sample = edt_sample.getText().toString();
                sampleNum = edt_sample_num.getText().toString();


                from = edt_from.getText().toString();
                hosp = edt_hosp.getText().toString();

                method = edt_method.getText().toString();
                no = edt_no.getText().toString();
                mData.getOrgan().setSegNumber(organSeg);
                mData.getBaseInfo().setFromCity(from);
                mData.getBaseInfo().setTracfficNumber(no);


                opo = edt_opo.getText().toString();
                contanctPerson = edt_contact_person.getText().toString();
                contactPhone = edt_contact_phone.getText().toString();
                departmentName = edt_department_name.getText().toString();
                departmentPhone = edt_department_phone.getText().toString();
                mData.getBaseInfo().setTracfficNumber(no);

                //加载起始结束经纬度  和医院地址
                loadStartLocation(fromAll);
                loadHospitalAddress(hosp);


//                if ("".equals(organ)) {
//                    ToastUtil.showToast("请选择器官类型");
//                }
//                else if ("".equals(blood)) {
//                    ToastUtil.showToast("请选择血型");
//                } else if ("".equals(sample)) {
//                    ToastUtil.showToast("请选择组织样本");
//                }
//                else if ("".equals(from)) {
//                    ToastUtil.showToast("请输入转运起始地");
//                } else if ("".equals(method)) {
//                    ToastUtil.showToast("请选择转运方式");
//                } else if ("".equals(opo)) {
//                    ToastUtil.showToast("请选择获取组织");
//                }
////                else if ("".equals(departmentName)) {
//                    ToastUtil.showToast("请选择科室协调员");
//                }
                // else {


                //器官基本信息
                mData.getOrgan().setSegNumber(edt_organ_org.getText().toString());
                mData.getBaseInfo().setGetOrganAt(edt_time.getText().toString());

                //基本血型信息
                mData.getOrgan().setType(edt_organ.getText().toString());
                mData.getBaseInfo().setOrganCount(edt_organ_num.getText().toString());
                mData.getOrgan().setBloodType(edt_blood.getText().toString());
                mData.getOrgan().setBloodSampleCount(edt_blood_num.getText().toString());
                mData.getOrgan().setOrganizationSampleType(edt_sample.getText().toString());
                mData.getOrgan().setOrganizationSampleCount(edt_sample_num.getText().toString());

                //转运单管理
                mData.getPerson().setName(edt_person.getText().toString());
                mData.getPerson().setPhone(edt_phone.getText().toString());

                //opo组织信息
                mData.getOpo().setName(edt_opo.getText().toString());
                mData.getOpo().setContactPerson(edt_contact_person.getText().toString());
                mData.getOpo().setContactPhone(edt_contact_phone.getText().toString());
                mData.getBaseInfo().setTracfficType(edt_method.getText().toString());
                mData.getBaseInfo().setTracfficNumber(edt_no.getText().toString());

                mData.setPageState(3);
                mBinding.saveBtn.setVisibility(View.VISIBLE);
                mData.setPageShow("确定");
                //}

                break;
            case 3:


                //mData.setPageState(3);
                if (isStartLatLng && isEndLatLng) {


                    // 拼接获取器官时间
                    if (!TextUtils.isEmpty(mData.getBaseInfo().getGetOrganAt()) && !isSave) {
                        mData.getBaseInfo().setGetOrganAt(mData.getBaseInfo().getGetOrganAt() + ":00");
                    }

                    RequestCreateTrans data = new RequestCreateTrans();
                    data.setBaseInfo(mData.getBaseInfo());
                    data.setTo(mData.getTo());
                    data.setPerson(mData.getPerson());
                    data.setOrgan(mData.getOrgan());
                    data.setOpo(mData.getOpo());

                    showWaitDialog("加载中", false, "加载中");
                    if (isSave) {
                        modifyTransfer(info, true);
                    } else {
                        insertTransfer(info, "1");
                    }
                } else {
                    ToastUtil.showToast("获取起始点地址,请稍后");
                }

                break;
            case 13:


                mData.setPageShow(mActivity.getString(R.string.common_pre));
                mData.setPageState(4);


                LogUtil.e("page3", info.getBaseInfo().getFromCity() + " / " + info.getTo().getToHospName() + " / " +
                        info.getPerson().getName() + " / " + info.getPerson().getPhone() + " / " +
                        info.getBaseInfo().getTracfficType() + " / " + info.getBaseInfo().getTracfficNumber());
                break;
            case 4:


                mData.setPageShow(mActivity.getString(R.string.common_confirmOk));
                mData.setPageState(5);


                LogUtil.e("page4", info.getOpo().getOpoid() + " / " + info.getOpo().getName() + " / " +
                        info.getOpo().getContactPerson() + " / " + info.getOpo().getContactPhone());
                break;
            case 5:


                mData.setPageShow(mActivity.getString(R.string.common_next));
                mData.setPageState(6);
                break;
            case 6:


                pwd1 = edt_pwd1.getText().toString().trim();
                pwd2 = edt_pwd2.getText().toString().trim();
                if (pwd1.equals(pwd2)) {
                    if (pwd1.length() == 0 || pwd1.length() == 4) {
                        mData.getBaseInfo().setBoxPin(mData.getPs().getOpenPs2());
                        mData.setPageShow(mActivity.getString(R.string.common_start));
                        mData.setPageState(7);
                        mBinding.saveBtn.setVisibility(View.VISIBLE);
                    } else {
                        ToastUtil.showToast("请确保输入四位开箱密码");
                    }

                } else {
                    ToastUtil.showToast("请确保密码一致");
                }


                LogUtil.e("page7", mData.getBaseInfo().getBoxPin() + " / ");
                break;
            case 7:

//
//                mData.getPerson().setDataType(PrefUtils.getString("personDataType", "", mActivity));
//                mData.getOpo().setDataType(PrefUtils.getString("opoDataType", "", mActivity));
//                mData.getPerson().setTransferPersonid(PrefUtils.getString("transferPersonId", "", mActivity));
//                mData.setPageShow("加载中...");
//                mBinding.nextBtn.setEnabled(false);

//                if (!A.isSerialPort) {
//                    ToastUtil.showToast("串口打开失败，稍后再试或重启应用。");
//                    mData.setPageShow(mActivity.getString(R.string.common_start));
//                    mBinding.nextBtn.setEnabled(true);
//                    return;
//                }


                break;
        }
    }

    private void sendGroupMessage() {
        RequestParams params = new RequestParams(URL.RONG);
        params.addBodyParameter("action", "sendGroupMessage");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void isRepeatOrganSeg(final String organSeg) {
        RequestParams params = new RequestParams(URL.TRANSFER);
        String boxNo = PrefUtils.getString("boxNo", "", mActivity);
        params.addBodyParameter("action", "organRepeatType");
        params.addBodyParameter("modifyOrganSeg", modifyOrganSeg);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("boxNo", boxNo);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                RepeatJson photoJson = new Gson().fromJson(result, RepeatJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    mData.setPageState(2);
                    mData.setPageShow("下一步");
                    if ("器官".equals(mType)) {
                        mType = photoJson.getObj().getType();
                        edt_organ.setText(mType);
                    }
                    if ("".equals(modifyOrganSeg)) {
                        modifyOrganSeg = photoJson.getObj().getModifyOrganSeg();
                    }


                } else {
                    ToastUtil.showToast("器官段号重复,请重新填写");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void getOneOpo(String hospital) {
        RequestParams params = new RequestParams(URL.OPO);
        params.addBodyParameter("action", "opo");
        params.addBodyParameter("hospital", hospital);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                OpoInfoJson photoJson = new Gson().fromJson(result, OpoInfoJson.class);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    mOpoInfoContact = photoJson.getObj().getOpoInfoContacts();
                } else {

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    public void choiceBloodType() {
        if (mBloodType != null && mBloodType.size() > 0) {
            final ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mBloodType.size(); i++) {
                list.add(mBloodType.get(i));
            }
//            list.add("其他(可填写)");

            BloodPopup popup = new BloodPopup(mActivity, list);
            popup.showPopupWindow(edt_blood);
            popup.setOnClickChangeListener(new BloodPopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
//                    if (position == list.size() - 1) {
//                        mBinding.idBloodType.setCursorVisible(true);
//                        mBinding.idBloodType.setFocusableInTouchMode(true);
//                        mData.getOrgan().setBloodType("");
//                    } else {
                    edt_blood.setCursorVisible(false);
                    edt_blood.setFocusableInTouchMode(false);
                    String s = mBloodType.get(position);
                    edt_blood.setText(s);
//                    }

                }
            });
        }
    }

    public void choiceOrgSample() {
        if (mOrganisationSample != null && mOrganisationSample.size() > 0 && !CreateActivity.isWriteSample) {
            final ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mOrganisationSample.size(); i++) {
                list.add(mOrganisationSample.get(i));
            }
            //list.add("其他(可填写)");

            OrganSamplePopup popup = new OrganSamplePopup(mActivity, list);
            popup.showPopupWindow(edt_sample);
            popup.setOnClickChangeListener(new OrganSamplePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
//                    if (position == list.size() - 1) {
//                        CreateActivity.isWriteSample = true;
//                        edt_sample.setCursorVisible(true);
//                        edt_sample.setFocusableInTouchMode(true);
//
//                        edt_sample.setText("");
//                    } else {
                    edt_sample.setCursorVisible(false);
                    edt_sample.setFocusableInTouchMode(false);
                    String s = mOrganisationSample.get(position);
                    edt_sample.setText(s);
                    //                   }

                }
            });
        }

    }

    public void choiceOrgSampleAll() {
        if (mOrganisationSample != null && mOrganisationSample.size() > 0) {
            final ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mOrganisationSample.size(); i++) {
                list.add(mOrganisationSample.get(i));
            }
            list.add("其他(可填写)");

            OrganSamplePopup popup = new OrganSamplePopup(mActivity, list);
            popup.showPopupWindow(edt_sample);
            popup.setOnClickChangeListener(new OrganSamplePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
                    if (position == list.size() - 1) {
                        CreateActivity.isWriteSample = true;
                        edt_sample.setCursorVisible(true);
                        edt_sample.setFocusableInTouchMode(true);
                        mData.getOrgan().setOrganizationSampleType("");
                    } else {
                        CreateActivity.isWriteSample = false;
                        edt_sample.setCursorVisible(false);
                        edt_sample.setFocusableInTouchMode(false);
                        edt_sample.setFocusable(false);
                        String s = mOrganisationSample.get(position);
                        mData.getOrgan().setOrganizationSampleType(s);
                    }

                }
            });
        }

    }

    public void choiceOrgType() {
        if (mOrganType != null && mOrganType.size() > 0) {

            final ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mOrganType.size(); i++) {
                list.add(mOrganType.get(i));
            }
//            list.add("其他(可填写)");

            final OrganTypePopup popup = new OrganTypePopup(mActivity, list);
            popup.showPopupWindow(edt_organ);
            popup.setOnClickChangeListener(new OrganTypePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
//                    if (position == list.size() - 1) {
//                        mBinding.idOrganType.setCursorVisible(true);
//                        mBinding.idOrganType.setFocusableInTouchMode(true);
//                        mData.getOrgan().setType("");
//                    } else {
                    edt_organ.setCursorVisible(false);
                    edt_organ.setFocusableInTouchMode(false);
                    String s = mOrganType.get(position);
                    edt_organ.setText(s);

//                    }
                }
            });
        }
    }

    public void choiceTracfficType() {
        if (mTracfficType != null && mTracfficType.size() > 0) {
            final ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < mTracfficType.size(); i++) {
                list.add(mTracfficType.get(i));
            }
            list.add("其他");

            TracfficTypePopup popup = new TracfficTypePopup(mActivity, list);
            popup.showPopupWindow(edt_method);
            popup.setOnClickChangeListener(new TracfficTypePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {


                    edt_method.setCursorVisible(false);
                    edt_method.setFocusableInTouchMode(false);
                    String s = list.get(position);
                    edt_method.setText(s);

                }
            });
        }

    }

    public void choiceOpoInfo() {


//        if (mOpoBeen != null && mOpoBeen.size() > 0) {
//            ArrayList<String> list = new ArrayList<>();
//            for (int i = 0; i < mOpoBeen.size(); i++) {
//                list.add(mOpoBeen.get(i).getName());
//            }
//
//            SinglePopup popup = new SinglePopup(mActivity, list);
//            popup.showPopupWindow(edt_opo);
//            popup.setOnClickChangeListener(new SinglePopup.OnClickChangeListener() {
//                @Override
//                public void OnClickChange(int position) {
//
//                    List<OpoInfoContact> opoInfoContacts = mOpoBeen.get(position).getOpoInfoContacts();
//                    mOpoInfoContact = opoInfoContacts;
//                    edt_opo.setText(mOpoBeen.get(position).getName());
//                    edt_contact_person.setText(opoInfoContacts.get(0).getContactName());
//                    edt_contact_phone.setText(opoInfoContacts.get(0).getContactPhone());
//
//                }
//            });
//        }


    }

    public void choiceTransPerson() {
        //ToastUtil.showToast("size:"+mUsersBean.size());
        if (mUsersBean != null && mUsersBean.size() > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mUsersBean.size(); i++) {

                list.add(mUsersBean.get(i).getTrueName());
            }

            TransPersonPopup tp = new TransPersonPopup(mActivity, list);
            tp.showPopupWindow(edt_person);
            tp.setOnClickChangeListener(new TransPersonPopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {

                    UsersListJson.ObjBean info = mUsersBean.get(position);
                    edt_person.setText(info.getTrueName());
                    edt_phone.setText(info.getPhone());
                    //mData.getPerson().setTransferPersonid(info.getTransferPersonid());
                    mData.getPerson().setDataType("db");
                    PrefUtils.putString("personDataType", "db", mActivity);
                    //PrefUtils.putString("transferPersonId", info.getTransferPersonid(), mActivity);
                    LogUtil.e(TAG, "db1:" + mData.getPerson().getDataType());

                }
            });

        }

    }

    public void choiceDepartment() {
        //ToastUtil.showToast("size:"+mUsersBean.size());
        if (mDepartments != null && mDepartments.size() > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mDepartments.size(); i++) {

                list.add(mDepartments.get(i).getName());
            }

            TransPersonPopup tp = new TransPersonPopup(mActivity, list);
            tp.showPopupWindow(edt_department_name);
            tp.setOnClickChangeListener(new TransPersonPopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {


                    edt_department_name.setText(mDepartments.get(position).getName());
                    edt_department_phone.setText(mDepartments.get(position).getPhone());
                    //mData.getPerson().setTransferPersonid(info.getTransferPersonid());

                }
            });

        }

    }

    public void choiceOpoPerson() {
        //ToastUtil.showToast("size:"+mUsersBean.size());
        if (mOpoInfoContact != null && mOpoInfoContact.size() > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mOpoInfoContact.size(); i++) {

                list.add(mOpoInfoContact.get(i).getContactName());
            }

            TransPersonPopup tp = new TransPersonPopup(mActivity, list);
            tp.showPopupWindow(edt_contact_person);
            tp.setOnClickChangeListener(new TransPersonPopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {

                    OpoInfoContact info = mOpoInfoContact.get(position);
                    edt_contact_person.setText(info.getContactName());
                    edt_contact_phone.setText(info.getContactPhone());


                }
            });

        }

    }

    private void getDepartments(String deviceId) {
        RequestParams params = new RequestParams(URL.CONTACT);
        params.addBodyParameter("action", "getDepartments");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                DepartmentsJson departmentsJson = new Gson().fromJson(result, DepartmentsJson.class);
                if (departmentsJson != null && departmentsJson.getResult() == CONSTS.SEND_OK) {
                    mDepartments = departmentsJson.getObj();
                    if (!isSave) {
                        edt_department_name.setText(mDepartments.get(0).getName());
                        edt_department_phone.setText(mDepartments.get(0).getPhone());
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 通知云监控改变
     *
     * @param organSeg
     */
    private void noticeTransfer(String organSeg) {
        RequestParams params = new RequestParams(URL.PUSH);
        params.addBodyParameter("action", "sendPushTransfer");
        params.addBodyParameter("organSeg", organSeg);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private boolean checkPage1(CreateData info) {
        if (TextUtils.isEmpty(info.getOrgan().getSegNumber()) || TextUtils.isEmpty(info.getBaseInfo().getGetOrganAt())) {
            mActivity.nullToast();
            return false;
        }

        return true;
    }

    private boolean checkPage2(CreateData info) {

        CreateOrganInfo organ = info.getOrgan();
        if (TextUtils.isEmpty(organ.getType()) || TextUtils.isEmpty(info.getBaseInfo().getOrganCount()) ||
                TextUtils.isEmpty(organ.getBloodType()) || TextUtils.isEmpty(organ.getBloodSampleCount()) ||
                TextUtils.isEmpty(organ.getOrganizationSampleType()) || TextUtils.isEmpty(organ.getOrganizationSampleCount())) {
            mActivity.nullToast();
            return false;
        }

        return true;
    }

    private boolean checkPage3(CreateData info) {
//        if (TextUtils.isEmpty(info.getBaseInfo().getFromCity()) || TextUtils.isEmpty(info.getTo().getToHospName()) ||
//                TextUtils.isEmpty(info.getPerson().getName()) || TextUtils.isEmpty(info.getPerson().getPhone()) ||
//                TextUtils.isEmpty(info.getBaseInfo().getTracfficType())) {
//            mActivity.nullToast();
//            return false;
//        }
        if (TextUtils.isEmpty(info.getBaseInfo().getFromCity()) || TextUtils.isEmpty(info.getTo().getToHospName()) ||
                TextUtils.isEmpty(info.getPerson().getName()) || TextUtils.isEmpty(info.getPerson().getPhone())
                ) {
            mActivity.nullToast();
            return false;
        }

        if (!Validator.isMobile(info.getPerson().getPhone())) {
            mActivity.phoneErrorToast();
            return false;
        }
        return true;
    }

    private boolean checkPage4(CreateData info) {
//        if (TextUtils.isEmpty(info.getOpo().getOpoid())) {
//            mActivity.nullOpoToast();
//            return false;
//        }
        if (TextUtils.isEmpty(info.getOpo().getOpoid()) || TextUtils.isEmpty(info.getBaseInfo().getTracfficType())) {

            mActivity.nullOpoToast();
            return false;
        }

        return true;
    }

    private boolean checkPage6(CreateData info) {
        if (TextUtils.isEmpty(info.getPs().getOpenPs1()) || TextUtils.isEmpty(info.getPs().getOpenPs2())) {
            mActivity.nullToast();
            return false;
        }

        if (!info.getPs().getOpenPs1().equals(info.getPs().getOpenPs2())) {
            mActivity.psDiff();
            return false;
        }

        return true;
    }

    public String getDateBase(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    private void initDatePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2013, 0, 23);
        Calendar endDate = Calendar.getInstance();

        //endDate.set(2019, 11, 28);
        //时间选择器
        pvDate = new TimePickerView.Builder(mActivity, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null

                /*btn_Time.setText(getTime(date));*/
                //Button btn = (Button) v;
                //btn.setText(getTime(date));
                edt_time.setText(getDateBase(date));
            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, true, true, false})
                .setLabel("年", "月", "日", "点", "分", "秒")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                // .setTitleBgColor(0xFF666666)//标题背景颜色 Night mode
                .setContentSize(18)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setBackgroundId(0x55999999) //设置外部遮罩颜色

                .setDecorView(null)
                .build();
    }


    public void choiceTime() {
        pvDate.show();
//        // 结束时间
//        Date end = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            //设备的当前时间
//            String deviceDateStr = sdf.format(new Date());
//            long deviceDate = sdf.parse(deviceDateStr).getTime();
//
//            //设备的默认启动时间
//            //+ 10 * 60 * 1000
//            long defaultDate = sdf.parse("2010-01-01 08:00:00").getTime();
//            //如果设备时间已经纠正成功
//
//            if (deviceDate > CONSTS.SERVER_TIME) {
//                end = new Date(deviceDate);
//
//
//            } else {
//                long nowDateLong = CONSTS.SERVER_TIME + (deviceDate - defaultDate) + 10 * 60 * 1000;
//               end = new Date(nowDateLong);
//
//
//            }
//        }catch (Exception e){
//
//        }
//
//
//        String endTime = CommonUtil.getTime(end);
//
//        // 开始时间
//        Calendar c = Calendar.getInstance();
//        c.setTime(end);
//        c.add(Calendar.MONTH, -1);
//        Date start = c.getTime();
//        String startTime = CommonUtil.getTime(start);
//
//
//        TimeSelector timeSelector = new TimeSelector(mActivity, new TimeSelector.ResultHandler() {
//            @Override
//            public void handle(String time) {
//                edt_time.setText(time);
//            }
//        }, startTime, endTime);
//
//        timeSelector.show();


    }

    public void tvDown(int state) {
        switch (state) {
            case 0:
                int orgNum = Integer.parseInt(edt_organ_num.getText().toString());
                if (orgNum > 1) {
                    edt_organ_num.setText(--orgNum + "");
                }
                break;
            case 1:
                int bloodNum = Integer.parseInt(edt_blood_num.getText().toString());
                if (bloodNum > 1) {
                    edt_blood_num.setText(--bloodNum + "");
                }
                break;
            case 2:
                int orgSampleNum = Integer.parseInt(edt_sample_num.getText().toString());
                if (orgSampleNum > 1) {
                    edt_sample_num.setText(--orgSampleNum + "");
                }
                break;
        }
    }

    public void tvUp(int state) {
        switch (state) {
            case 0:

                int orgNum = Integer.parseInt(edt_organ_num.getText().toString());
                if (orgNum <= 10) {
                    edt_organ_num.setText(++orgNum + "");
                }

                break;
            case 1:
                int bloodNum = Integer.parseInt(edt_blood_num.getText().toString());
                if (bloodNum <= 10) {
                    edt_blood_num.setText(++bloodNum + "");
                }
                break;
            case 2:
                int orgSampleNum = Integer.parseInt(edt_sample_num.getText().toString());
                if (orgSampleNum <= 10) {
                    edt_sample_num.setText(++orgSampleNum + "");
                }
                break;
        }
    }

    /**
     * 获取转运人列表
     */
    private void getUsersList() {
        RequestParams params = new RequestParams(URL.USERS);
        params.addBodyParameter("action", "usersPadList");
        params.addBodyParameter("hospitalName", PrefUtils.getString("hospitalName", "", mActivity));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                UsersListJson usersListJson = new Gson().fromJson(result, UsersListJson.class);
                if (usersListJson != null && usersListJson.getResult() == CONSTS.SEND_OK) {
                    mUsersBean = usersListJson.getObj();

                } else {
                    mUsersBean = new ArrayList<UsersListJson.ObjBean>();
                    UsersListJson.ObjBean objBean = new UsersListJson.ObjBean();
                    objBean.setTrueName(PrefUtils.getString("hospitalName", "", mActivity));
                    objBean.setPhone("18398850872");
                    mUsersBean.add(objBean);
                }
                // ToastUtil.showToast("b"+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //ToastUtil.showToast("e"+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取opo信息
     */
    private void getOpo() {
        RequestParams params = new RequestParams(URL.USERS);
        params.addBodyParameter("action", "opoPadList");
        params.addBodyParameter("hospitalName", PrefUtils.getString("hospitalName", "", mActivity));
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                OpoListJson usersListJson = new Gson().fromJson(result, OpoListJson.class);
                if (usersListJson != null && usersListJson.getResult() == CONSTS.SEND_OK) {
                    OpoListJson.ObjBean objBean = usersListJson.getObj().get(0);

                    edt_opo.setText(objBean.getName());
                    edt_contact_person.setText(objBean.getContactName());
                    edt_contact_phone.setText(objBean.getContactPhone());

                }
                // ToastUtil.showToast("b"+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //ToastUtil.showToast("e"+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取opo信息
     */
    private void getOpos() {
        RequestParams params = new RequestParams(URL.USERS);
        params.addBodyParameter("action", "oposPadList");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                OpoProvinceJson usersListJson = new Gson().fromJson(result, OpoProvinceJson.class);
                if (usersListJson != null && usersListJson.getResult() == CONSTS.SEND_OK) {
                    mOpoBeen = usersListJson.getObj();

                }
                // ToastUtil.showToast("b"+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //ToastUtil.showToast("e"+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(picker, new ColorDrawable(App.getContext().getResources().getColor(R.color.et_color)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    /**
     * 根据医院名称获取地址 上海市
     *
     * @param hospitalName
     */
    private void loadHospitalAddress(final String hospitalName) {
        LogUtil.e(TAG, "hospitalName:" + hospitalName);
        RequestParams params = new RequestParams(URL.USERS);
        params.addBodyParameter("action", "getHospitalAddress");
        params.addBodyParameter("hospitalName", hospitalName);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                HospitalJson hospitalJson = new Gson().fromJson(result, HospitalJson.class);
                if (hospitalJson != null && hospitalJson.getResult() == CONSTS.SEND_OK) {
                    toHospitalAddress = hospitalJson.getObj() == null ? null : hospitalJson.getObj().getAddress();
                    // Log.e(TAG, toHospitalAddress + ":toHospitalAddress");
                    loadEndLocation(toHospitalAddress);

                } else {

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 获取开始的经纬度 杭州市江干区
     */
    private void loadStartLocation(final String pStartLocation) {

        String url = URL.GAO_DE_LOCATION_URL + pStartLocation;
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //Log.e(TAG, "result:" + e.getMessage());
                isStartLatLng = true;
                startLocation = CONSTS.LONGITUDE+","+ CONSTS.LATITUDE ;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Log.e(TAG, "response:" + response.body().string());
                LatiLongJson latiLongJson = new Gson().fromJson(response.body().string(), LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        startLocation = latiLongJson.getGeocodes()[0].getLocation();
                        isStartLatLng = true;
                        //Log.e(TAG, "startLocation:" + startLocation);
                    }
                }else{
                    isStartLatLng = true;
                    startLocation = CONSTS.LONGITUDE+","+ CONSTS.LATITUDE ;
                }
            }
        });


    }

    private void startTransfer(String phones) {

        noticeTransfer(organSeg);
        String organSegTemp = "";

        if (!TextUtils.isEmpty(modifyOrganSeg)) {
            organSegTemp = modifyOrganSeg;
        } else {
            organSegTemp = organSeg;
        }
        String content = "器官段号：" + organSegTemp + "，" + from + "的" + organ + "转运已经开始。";

        if (!isSave) {
            sendTransferSms(phones, content);
        }

        sendGroupMessage();

        //跳转到转运界面

        getTransferInfo();


        PrefUtils.putBoolean("isSave", false, mActivity);

    }

    private void modifyTransfer(CreateData info, final boolean pType) { //成都市

        if (startLocation != null && startLocation.contains(",") && endLocation != null && endLocation.contains(",")) {

            distance = LocationUtils.getDistance(Double.parseDouble(startLocation.split(",")[1]), Double.parseDouble(startLocation.split(",")[0]), Double.parseDouble(endLocation.split(",")[1]), Double.parseDouble(endLocation.split(",")[0])) / 1000;
        }

        String groupName = "转运中-" + modifyOrganSeg + "-" + info.getOrgan().getType();

        String usersIds = "";
        usersIds += phone + ",";
        usersIds += departmentPhone + ",";
        usersIds += contactPhone + ",";
        String phones = PrefUtils.getString("phones", "", mActivity);
        String phonesStr[] = phones.split(",");
        for (int i = 0; i < phonesStr.length; i++) {
            if (i > 2) {
                usersIds += phonesStr[i];

                if (i != (phonesStr.length - 1)) {
                    usersIds += ",";
                }
            }
        }

        final String phonesStart = usersIds;
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "updateTransfer");
        params.addBodyParameter("phone", phone);
        //未修改器官段号
        if (organSeg.equals(oldOrganSeg)) {
            params.addBodyParameter("modifyOrganSeg", "");
            params.addBodyParameter("organSeg", organSeg);
        } else {
            params.addBodyParameter("modifyOrganSeg", organSeg);
            params.addBodyParameter("organSeg", oldOrganSeg);
        }
        //Log.e("CreatePresenter",organSeg+","+oldOrganSeg);

        params.addBodyParameter("organ", organ);
        params.addBodyParameter("organNum", organNum);
        params.addBodyParameter("blood", blood);
        params.addBodyParameter("bloodNum", bloodNum);
        params.addBodyParameter("sampleOrgan", sample);
        params.addBodyParameter("sampleOrganNum", sampleNum);

        params.addBodyParameter("opoContactName", contanctPerson);
        params.addBodyParameter("opoContactPhone", contactPhone);
        params.addBodyParameter("contactName", departmentName);
        params.addBodyParameter("contactPhone", departmentPhone);

        if (from.contains("省")) {
            String pFrom = from.split("省")[1];
            params.addBodyParameter("fromCity", pFrom);
        } else {
            params.addBodyParameter("fromCity", from);
        }
        if ("2010-01-01 08:00".equals(getTime)) {
            getTime = CommonUtil.getTrueTimeMM();
        }
        params.addBodyParameter("getTime", getTime);
        params.addBodyParameter("openPsd", pwd1);
        params.addBodyParameter("opoName", opo);
        params.addBodyParameter("toHospName", hosp);
        params.addBodyParameter("trueName", person);
        params.addBodyParameter("tracfficType", method);
        params.addBodyParameter("tracfficNumber", no);
        params.addBodyParameter("distance", distance + "");
        params.addBodyParameter("groupName", groupName);

        params.addBodyParameter("usersIds", usersIds);
        params.addBodyParameter("toHosp", toHospitalAddress.split("市")[0]);
        String boxNo = PrefUtils.getString("boxNo", "", App.getContext());
        params.addBodyParameter("boxNo", boxNo);

        //开始
        if (pType) {
            params.addBodyParameter("isStart", "1");
        } else {
            params.addBodyParameter("isStart", "0");
        }
        //保存


        if (startLocation != null && startLocation.contains(",") && endLocation != null && endLocation.contains(",")) {

            params.addBodyParameter("startLong", startLocation.split(",")[0]);
            params.addBodyParameter("startLati", startLocation.split(",")[1]);
            params.addBodyParameter("endLong", endLocation.split(",")[0]);
            params.addBodyParameter("endLati", endLocation.split(",")[1]);

        } else {
            params.addBodyParameter("startLong", "0");
            params.addBodyParameter("startLati", "0");
            params.addBodyParameter("endLong", "0");
            params.addBodyParameter("endLati", "0");
        }
        final String smsPhones = usersIds;

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                PhotoJson photoJson = new Gson().fromJson(result, PhotoJson.class);
                //Log.e(TAG, "result:" + result);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {

                    startTransfer(smsPhones);

                    //发送短信和自定义推送
                    if (pType) {
                        String content = "本次转运医师：" + person + "，科室协调员：" + contanctPerson + "。器官段号：" + organSeg + "，" + from + "的" + organ + "转运已开始。";
                        sendListTransferSms(phonesStart, content);
                        noticeTransfer(organSeg);

                    }

                } else {

                    ToastUtil.showToast("修改失败");

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(TAG, "ex:" + ex.getMessage());
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void sendListTransferSms(String phones, String content) {

        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendListTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void insertTransfer(CreateData info, final String isStart) {

        if (startLocation != null && startLocation.contains(",") && endLocation != null && endLocation.contains(",")) {

            distance = LocationUtils.getDistance(Double.parseDouble(startLocation.split(",")[1]), Double.parseDouble(startLocation.split(",")[0]), Double.parseDouble(endLocation.split(",")[1]), Double.parseDouble(endLocation.split(",")[0])) / 1000;
        }

        String groupName = "转运中-" + modifyOrganSeg + "-" + info.getOrgan().getType();

        String usersIds = "";
        usersIds += phone + ",";
        usersIds += departmentPhone + ",";
        usersIds += contactPhone;
        String phones = PrefUtils.getString("phones", "", mActivity);
        String phonesStr[] = phones.split(",");
//        for (int i = 0; i < phonesStr.length; i++) {
//            if (i > 2) {
//                if(i==2){
//                    usersIds += ",";
//                }
//                usersIds += phonesStr[i];
//
//                if (i != (phonesStr.length - 1)) {
//                    usersIds += ",";
//                }
//            }
//        }
        final String phonesStart = usersIds;
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "create");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("organSeg", organSeg);
        params.addBodyParameter("organ", organ);
        params.addBodyParameter("organNum", organNum);
        params.addBodyParameter("blood", blood);
        params.addBodyParameter("bloodNum", bloodNum);
        params.addBodyParameter("sampleOrgan", sample);
        params.addBodyParameter("sampleOrganNum", sampleNum);

        params.addBodyParameter("opoContactName", contanctPerson);
        params.addBodyParameter("opoContactPhone", contactPhone);
        params.addBodyParameter("contactName", departmentName);
        params.addBodyParameter("contactPhone", departmentPhone);

        if (from.contains("省")) {
            String pFrom = from.split("省")[1];
            params.addBodyParameter("fromCity", pFrom);
        } else {
            params.addBodyParameter("fromCity", from);
        }
        if ("2010-01-01 08:00".equals(getTime)) {
            getTime = CommonUtil.getTrueTimeMM();
        }
        params.addBodyParameter("getTime", getTime);
        params.addBodyParameter("openPsd", pwd1);
        params.addBodyParameter("opoName", opo);
        params.addBodyParameter("toHospName", hosp);
        params.addBodyParameter("trueName", person);
        params.addBodyParameter("tracfficType", method);
        params.addBodyParameter("tracfficNumber", no);
        params.addBodyParameter("distance", distance + "");
        params.addBodyParameter("groupName", groupName);
        params.addBodyParameter("modifyOrganSeg", modifyOrganSeg);

        params.addBodyParameter("usersIds", usersIds);
        params.addBodyParameter("toHosp", toHospitalAddress.split("市")[0]);
        String boxNo = PrefUtils.getString("boxNo", "", App.getContext());
        LogUtil.e(TAG, "boxNo:" + boxNo);
        params.addBodyParameter("boxNo", boxNo);
//        if (isSave) {
//            params.addBodyParameter("isStart", "0");
//        }
//        else {
        params.addBodyParameter("isStart", isStart);
//        }


        if (startLocation != null && startLocation.contains(",") && endLocation != null && endLocation.contains(",")) {

            params.addBodyParameter("startLong", startLocation.split(",")[0]);
            params.addBodyParameter("startLati", startLocation.split(",")[1]);
            params.addBodyParameter("endLong", endLocation.split(",")[0]);
            params.addBodyParameter("endLati", endLocation.split(",")[1]);

        } else {
            params.addBodyParameter("startLong", "0");
            params.addBodyParameter("startLati", "0");
            params.addBodyParameter("endLong", "0");
            params.addBodyParameter("endLati", "0");
        }


        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Datas photoJson = new Gson().fromJson(result, Datas.class);
                //Log.e(TAG, "result1:" + result);
                if (photoJson != null && photoJson.getResult() == CONSTS.SEND_OK) {
                    //Log.e(TAG, "error:isStart0");
                    startTransfer(phonesStart);
                    //Log.e(TAG, "error:isStart1");
                    if ("1".equals(isStart)) {
                        //Log.e(TAG, "error:isStart2");
                        String organSegTemp;

                        if (!TextUtils.isEmpty(modifyOrganSeg)) {
                            // Log.e(TAG, "error:isStart3");
                            organSegTemp = modifyOrganSeg;
                        } else {
                            //Log.e(TAG, "error:isStart4");
                            organSegTemp = organSeg;
                        }
                        //Log.e(TAG, "error:isStart5");
                        String content = "本次转运医师：" + person + "，科室协调员：" + contanctPerson + "。器官段号：" + organSegTemp + "，" + from + "的" + organ + "转运已开始。";
                        sendListTransferSms(phonesStart, content);
                        //Log.e(TAG, "error:isStart6");
                    }
                    //Log.e(TAG, "error:isStart7");
                    noticeTransfer(organSeg);
                    //Log.e(TAG, "error:isStart8");
                } else if (photoJson != null && photoJson.getResult() == CONSTS.SEND_FAIL) {


                    ToastUtil.showToast("器官段号重复");
                    //Log.e(TAG, "error:器官段号重复");

                } else if (photoJson != null && photoJson.getResult() == CONSTS.BAD_PARAM) {

                    ToastUtil.showToast("箱子已被使用");

                    //Log.e(TAG, "error:箱子已被使用");
                }
                //dismissDialog();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Log.e(TAG, "ex1:" + ex.getLocalizedMessage() + "," + ex.getMessage());
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    /**
     * 获取转运信息
     */
    private void getTransferInfo() {

        final String deviceId = PrefUtils.getString("deviceId", "", App.getContext());
        RequestParams params = new RequestParams(URL.TRANSFER);
        params.addBodyParameter("action", "getTransferByDeviceId");
        params.addBodyParameter("deviceId", deviceId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                dismissDialog();

                TransferJson transferJson = new Gson().fromJson(result, TransferJson.class);


                if (transferJson != null && transferJson.getResult() == CONSTS.SEND_OK) {


                    final TransferJson.ObjBean objBean = transferJson.getObj();


                    if (objBean.getIsStart().equals("1")) {


                        CONSTS.TRANSFER_ID = objBean.getTransferid();

                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    clearCollisionNumber();
                                    Thread.sleep(100);


                                    SerialUtil.power();

                                    Thread.sleep(100);
                                    if (!"".equals(objBean.getOpenPsd()) && objBean.getOpenPsd() != null) {
                                        PrefUtils.putString("pwd", objBean.getOpenPsd(), mActivity);
                                        CONSTS.TRANSFER_OPEN = false;
                                        boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mActivity);
                                        boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mActivity);
                                        //SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, true);
                                    } else {
                                        PrefUtils.putString("pwd", "", mActivity);
                                    }
                                    //collision();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();


                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        MainActivity.mObjBean = objBean;

                        CONSTS.IS_START = 1;
                        PrefUtils.putString("organSeg", objBean.getOrganSeg(), mActivity);
                        CONSTS.TRANS_START = new Date().getTime();

                        CONSTS.OPEN = 0;

                        CONSTS.COLLISION = 0;
                        CONSTS.DISTANCE = 0;
                        CONSTS.DURATION_OLD = 0;

                        CONSTS.COUNT = 0;
                        CONSTS.UPLOAD_NUM = CONSTS.UPLOAD_NUM_VALUE;

                        SerialUtil.clearCollisionNumber();

                        //Log.e(TAG, "OPEN:" + getOpen() + ",getDuration:" + getDuration() + ",getCollision:" + getCollsion());
                        Intent intent = new Intent(mActivity, OnWayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(intent);
                        PrefUtils.putBoolean("isSave", false, mActivity);

                    } else if (objBean.getIsStart().equals("0")) {
                        PrefUtils.putBoolean("isSave", true, mActivity);
                        //ToastUtil.showToast("gg",mActivity);
                        CONSTS.IS_START = 0;
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("noStart", "noStart");
                        mActivity.startActivity(intent);
                    } else {
                        CONSTS.IS_START = 2;

                    }

                } else {
                    PrefUtils.putString("pwd", "", mActivity);
                    CONSTS.IS_START = 2;


                    boolean isTemperature = PrefUtils.getBoolean("isTemperature", true, mActivity);
                    boolean isPlaneShow = PrefUtils.getBoolean("isPlaneShow", true, mActivity);
                    // SerialUtil.openTemperaturePlanePwd(isTemperature, isPlaneShow, false);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                CONSTS.IS_START = 2;
                dismissDialog();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }


    private void clearCollisionNumber() {

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
            //Log.e(TAG, "ssss:" + s);

            for (int i = 0; i < powerInts.length; i++) {
                if (CommServer.mOutputStream != null) {
                    CommServer.mOutputStream.write(powerInts[i]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Log.e(TAG, "error4:" + e.getMessage());
        }


    }

    private void sendTransferSms(String phones, String content) {
        RequestParams params = new RequestParams(URL.SMS);
        params.addBodyParameter("action", "sendTransfer");
        params.addBodyParameter("phones", phones);
        params.addBodyParameter("content", content);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 获取结束的经纬度
     */
    private void loadEndLocation(final String pEndLoaction) {
        String url = URL.GAO_DE_LOCATION_URL + pEndLoaction;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e(TAG, "result:" + e.getMessage());
                isEndLatLng = true;
                endLocation = CONSTS.LONGITUDE + "," + CONSTS.LATITUDE;

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // LogUtil.e(TAG, "response:" + response.body().string());
                LatiLongJson latiLongJson = new Gson().fromJson(response.body().string(), LatiLongJson.class);

                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {

                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
                        endLocation = latiLongJson.getGeocodes()[0].getLocation();
                        isEndLatLng = true;
                        LogUtil.e(TAG, "endLocation:" + endLocation);
                    }
                } else {
                    isEndLatLng = true;
                    endLocation = CONSTS.LONGITUDE + "," + CONSTS.LATITUDE;

                }
            }
        });


//        RequestParams params = new RequestParams(URL.GAO_DE_LOCATION_URL + pEndLoaction);
//        x.http().get(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                LogUtil.e(TAG,"loadEndLocation:"+result+","+URL.GAO_DE_LOCATION_URL + pEndLoaction);
//                LatiLongJson latiLongJson = new Gson().fromJson(result, LatiLongJson.class);
//
//                if (latiLongJson != null && "1".equals(latiLongJson.getStatus())) {
//
//                    if (latiLongJson.getGeocodes() != null && latiLongJson.getGeocodes().length > 0) {
//                        endLocation = latiLongJson.getGeocodes()[0].getLocation();
//                        LogUtil.e(TAG, "endLocation:" + endLocation);
//                    }
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                LogUtil.e(TAG, "onErrorEnd:" + ex.getMessage());
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
    }

    /**
     * 等待对话框
     *
     * @author blue
     */
    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
        if (null == dialog || !dialog.isShowing()) {
            dialog = DialogMaker.showCommenWaitDialog(mActivity, msg, null, isCanCancelabel, tag);
        }
        return dialog;
    }

    /**
     * 关闭对话框
     *
     * @author blue
     */
    public void dismissDialog() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}

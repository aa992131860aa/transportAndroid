package org.redsha.transbox.controller.create;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.widget.NumberPicker;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;


import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.bean.CreateOrganInfo;
import org.redsha.transbox.bean.KeywordBean;
import org.redsha.transbox.bean.OpoBean;
import org.redsha.transbox.bean.TransOddBean;
import org.redsha.transbox.bean.TransferPersonBean;
import org.redsha.transbox.controller.on.OnWayActivity;
import org.redsha.transbox.databinding.ActivityCreateBinding;
import org.redsha.transbox.db.TransOddDb;
import org.redsha.transbox.http.HttpHelper;
import org.redsha.transbox.http.HttpObserver;
import org.redsha.transbox.http.request.RequestCreateTrans;
import org.redsha.transbox.util.A;
import org.redsha.transbox.util.CommonUtil;
import org.redsha.transbox.util.JsonUtil;
import org.redsha.transbox.util.LogUtil;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;
import org.redsha.transbox.util.SelectTime.TimeSelector;
import org.redsha.transbox.util.ToastUtil;
import org.redsha.transbox.util.Validator;
import org.redsha.transbox.view.BloodPopup;
import org.redsha.transbox.view.OrganSamplePopup;
import org.redsha.transbox.view.OrganTypePopup;
import org.redsha.transbox.view.SinglePopup;
import org.redsha.transbox.view.TracfficTypePopup;
import org.redsha.transbox.view.TransPersonPopup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;


public class CreatePresenter {
    private CreateActivity mActivity;
    private CreateData mData;
    private ActivityCreateBinding mBinding;
    private List<TransferPersonBean> mTransferPersonBean;   //转运人
    private List<OpoBean> mOpoBeen;
    private List<String> mOrganType;
    private List<String> mTracfficType;
    private List<String> mBloodType;
    private List<String> mOrganisationSample;

    public CreatePresenter(CreateActivity createActivity, CreateData info, ActivityCreateBinding binding) {
        this.mActivity = createActivity;
        this.mData = info;
        this.mBinding = binding;
//        getTime();  //初始化时间选择器
        getTransferPersons();   //初始化转运人
        getOpoInfo();   //初始化opo信息
        initPopupState();
        initKey();
        //初始化定位
        initLocation();
        // 启动定位
        locationClient.startLocation();
    }

    private AMapLocationClient locationClient = null;

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
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(true);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
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
            if (null != loc) {
                String position = loc.getAddress();
                LogUtil.i("position：", position);
                mData.getBaseInfo().setFromCity(position);
                destroyMapObj();
            } else {
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
        if (!TextUtils.isEmpty(key)) {
            KeywordBean keywordData = JsonUtil.parseJsonToBean(key, KeywordBean.class);
            if (keywordData != null) {
                mOrganType = keywordData.getOrgan();
                mTracfficType = keywordData.getTracfficType();
                mBloodType = keywordData.getBloodType();
                mOrganisationSample = keywordData.getOrganisationSample();
            }
        }
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

    private void getOpoInfo() {
        new HttpHelper().getAllOpo().subscribe(new HttpObserver<List<OpoBean>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(List<OpoBean> model) {

                if (model != null && model.size() > 0) {
                    mOpoBeen = model;
                }

            }
        });
    }

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
                break;
            case 3:
                mData.setPageState(2);
                break;
            case 4:
                mData.setPageShow(mActivity.getString(R.string.common_next));
                mData.setPageState(3);
                break;
            case 5:
                mData.setPageShow(mActivity.getString(R.string.common_pre));
                mData.setPageState(4);
                break;
            case 6:
                mData.setPageShow(mActivity.getString(R.string.common_confirmOk));
                mData.setPageState(5);
                break;
            case 7:
                mData.setPageShow(mActivity.getString(R.string.common_confirmOk));
                mData.setPageState(6);
                break;
        }
    }

    /**
     * 下一步
     */
    public void next(CreateData info) {
        switch (mData.getPageState()) {
            case 1:
                boolean page1 = checkPage1(info);
                if (page1) {
                    mData.setPageState(2);
                }
                LogUtil.e("page1", info.getOrgan().getSegNumber() + " / " + info.getBaseInfo().getGetOrganAt());

                break;
            case 2:
                boolean page2 = checkPage2(info);
                if (page2) {
                    mData.setPageState(3);
                }

                LogUtil.e("page2", info.getOrgan().getType() + " / " + info.getBaseInfo().getOrganCount() + " / " +
                        info.getOrgan().getBloodType() + " / " + info.getOrgan().getBloodSampleCount() + " / " +
                        info.getOrgan().getOrganizationSampleType() + " / " + info.getOrgan().getOrganizationSampleCount());
                break;
            case 3:
                boolean page3 = checkPage3(info);
                if (page3) {
                    mData.setPageShow(mActivity.getString(R.string.common_pre));
                    mData.setPageState(4);
                    String name = PrefUtils.getString("name", "", App.getContext());
                    if (name.equals(mData.getTo().getToHospName())) {
                        mData.getTo().setDataType("db");
                    } else {
                        mData.getTo().setDataType("new");
                    }
                }

                LogUtil.e("page3", info.getBaseInfo().getFromCity() + " / " + info.getTo().getToHospName() + " / " +
                        info.getPerson().getName() + " / " + info.getPerson().getPhone() + " / " +
                        info.getBaseInfo().getTracfficType() + " / " + info.getBaseInfo().getTracfficNumber());
                break;
            case 4:
                boolean page4 = checkPage4(info);
                if (page4) {
                    mData.setPageShow(mActivity.getString(R.string.common_confirmOk));
                    mData.setPageState(5);
                }

                LogUtil.e("page4", info.getOpo().getOpoid() + " / " + info.getOpo().getName() + " / " +
                        info.getOpo().getContactPerson() + " / " + info.getOpo().getContactPhone());
                break;
            case 5:
                mData.setPageShow(mActivity.getString(R.string.common_next));
                mData.setPageState(6);
                break;
            case 6:

                boolean page6 = checkPage6(info);
                if (page6) {
                    mData.getBaseInfo().setBoxPin(mData.getPs().getOpenPs2());
                    mData.setPageShow(mActivity.getString(R.string.common_start));
                    mData.setPageState(7);
                }

                LogUtil.e("page7", mData.getBaseInfo().getBoxPin() + " / ");
                break;
            case 7:

                mData.setPageShow("加载中...");
                mBinding.nextBtn.setEnabled(false);

                if (!A.isSerialPort) {
                    ToastUtil.showToast("串口打开失败，稍后再试或重启应用。");
                    mData.setPageShow(mActivity.getString(R.string.common_start));
                    mBinding.nextBtn.setEnabled(true);
                    return;
                }

                // 拼接获取器官时间
                if (!TextUtils.isEmpty(mData.getBaseInfo().getGetOrganAt())) {
                    mData.getBaseInfo().setGetOrganAt(mData.getBaseInfo().getGetOrganAt() + ":00");
                }

                RequestCreateTrans data = new RequestCreateTrans();
                data.setBaseInfo(mData.getBaseInfo());
                data.setTo(mData.getTo());
                data.setPerson(mData.getPerson());
                data.setOrgan(mData.getOrgan());
                data.setOpo(mData.getOpo());
                new HttpHelper().createTrans(data).subscribe(new HttpObserver<TransOddBean>() {
                    @Override
                    public void onComplete() {
                        // 响应完成恢复状态
                        mData.setPageShow(mActivity.getString(R.string.common_start));
                        mBinding.nextBtn.setEnabled(true);
                    }

                    @Override
                    public void onSuccess(final TransOddBean model) {
                        PrefUtils.putString("tid", model.getTransferid(), App.getContext());

                        Realm realm = RealmUtil.getInstance().getRealm();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.createObjectFromJson(TransOddDb.class, new Gson().toJson(model));
                            }
                        });
                        realm.close();

                        Intent intent = new Intent(App.getContext(), OnWayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        App.getContext().startActivity(intent);
                        mActivity.finish();
                    }
                });

                break;
        }
    }

    public void choiceBloodType() {
        if (mBloodType != null && mBloodType.size() > 0) {
            final ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mBloodType.size(); i++) {
                list.add(mBloodType.get(i));
            }
//            list.add("其他(可填写)");

            BloodPopup popup = new BloodPopup(mActivity, list);
            popup.showPopupWindow(mBinding.idBloodType);
            popup.setOnClickChangeListener(new BloodPopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
//                    if (position == list.size() - 1) {
//                        mBinding.idBloodType.setCursorVisible(true);
//                        mBinding.idBloodType.setFocusableInTouchMode(true);
//                        mData.getOrgan().setBloodType("");
//                    } else {
                    mBinding.idBloodType.setCursorVisible(false);
                    mBinding.idBloodType.setFocusableInTouchMode(false);
                    String s = mBloodType.get(position);
                    mData.getOrgan().setBloodType(s);
//                    }

                }
            });
        }
    }

    public void choiceOrgSample() {
        if (mOrganisationSample != null && mOrganisationSample.size() > 0&&!CreateActivity.isWriteSample) {
            final ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mOrganisationSample.size(); i++) {
                list.add(mOrganisationSample.get(i));
            }
            list.add("其他(可填写)");

            OrganSamplePopup popup = new OrganSamplePopup(mActivity, list);
            popup.showPopupWindow(mBinding.idOrganSample);
            popup.setOnClickChangeListener(new OrganSamplePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
                    if (position == list.size() - 1) {
                        CreateActivity.isWriteSample = true;
                        mBinding.idOrganSample.setCursorVisible(true);
                        mBinding.idOrganSample.setFocusableInTouchMode(true);
                        mData.getOrgan().setOrganizationSampleType("");
                    } else {
                        mBinding.idOrganSample.setCursorVisible(false);
                        mBinding.idOrganSample.setFocusableInTouchMode(false);
                        String s = mOrganisationSample.get(position);
                        mData.getOrgan().setOrganizationSampleType(s);
                    }

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
            popup.showPopupWindow(mBinding.idOrganSample);
            popup.setOnClickChangeListener(new OrganSamplePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
                    if (position == list.size() - 1) {
                        CreateActivity.isWriteSample = true;
                        mBinding.idOrganSample.setCursorVisible(true);
                        mBinding.idOrganSample.setFocusableInTouchMode(true);
                        mData.getOrgan().setOrganizationSampleType("");
                    } else {
                        CreateActivity.isWriteSample = false;
                        mBinding.idOrganSample.setCursorVisible(false);
                        mBinding.idOrganSample.setFocusableInTouchMode(false);
                        mBinding.idOrganSample.setFocusable(false);
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
            popup.showPopupWindow(mBinding.idOrganType);
            popup.setOnClickChangeListener(new OrganTypePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {
//                    if (position == list.size() - 1) {
//                        mBinding.idOrganType.setCursorVisible(true);
//                        mBinding.idOrganType.setFocusableInTouchMode(true);
//                        mData.getOrgan().setType("");
//                    } else {
                    mBinding.idOrganType.setCursorVisible(false);
                    mBinding.idOrganType.setFocusableInTouchMode(false);
                    String s = mOrganType.get(position);
                    mData.getOrgan().setType(s);
                    if (s.equals("肾")) {
                        mData.getBaseInfo().setOrganCount("2");
                    }
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
            list.add("其他(可填写)");

            TracfficTypePopup popup = new TracfficTypePopup(mActivity, list);
            popup.showPopupWindow(mBinding.idTracfficType);
            popup.setOnClickChangeListener(new TracfficTypePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {

                    if (position == list.size() - 1) {
                        mBinding.idTracfficType.setCursorVisible(true);
                        mBinding.idTracfficType.setFocusableInTouchMode(true);
                        mData.getBaseInfo().setTracfficType("");
                    } else {
                        mBinding.idTracfficType.setCursorVisible(false);
                        mBinding.idTracfficType.setFocusableInTouchMode(false);
                        String s = mTracfficType.get(position);
                        mData.getBaseInfo().setTracfficType(s);
                    }
                }
            });
        }

    }

    public void choiceOpoInfo() {
        if (mOpoBeen != null && mOpoBeen.size() > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mOpoBeen.size(); i++) {
                list.add(mOpoBeen.get(i).getName());
            }

            SinglePopup popup = new SinglePopup(mActivity, list);
            popup.showPopupWindow(mBinding.idGetOpo);
            popup.setOnClickChangeListener(new SinglePopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {

                    OpoBean info = mOpoBeen.get(position);
                    mData.getOpo().setName(info.getName());
                    mData.getOpo().setContactPerson(info.getContactPerson());
                    mData.getOpo().setContactPhone(info.getContactPhone());
                    mData.getOpo().setOpoid(info.getOpoid());
                    mData.getOpo().setDataType("db");
                }
            });
        }


    }

    public void choiceTransPerson() {
        if (mTransferPersonBean != null && mTransferPersonBean.size() > 0) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < mTransferPersonBean.size(); i++) {
                list.add(mTransferPersonBean.get(i).getName());
            }

            TransPersonPopup tp = new TransPersonPopup(mActivity, list);
            tp.showPopupWindow(mBinding.idPersonName);
            tp.setOnClickChangeListener(new TransPersonPopup.OnClickChangeListener() {
                @Override
                public void OnClickChange(int position) {

                    TransferPersonBean info = mTransferPersonBean.get(position);
                    mData.getPerson().setName(info.getName());
                    mData.getPerson().setPhone(info.getPhone());
                    mData.getPerson().setTransferPersonid(info.getTransferPersonid());
                    mData.getPerson().setDataType("db");

                }
            });

        }

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

//    private boolean checkPage7(CreateData info) {
//
//
//        return true;
//    }

    private void getTime() {
        /*

        // 年月日
        Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH) + 1;
        final int day = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR);
        int m = c.get(Calendar.MINUTE);

        mData.getTime().setYy(year + "");
        mData.getTime().setMm(month + "");
        mData.getTime().setDd(day + ""); // 选择的日期
        mData.getTime().setHh(h + "");   // 小时
        mData.getTime().setM(m + ""); // 分钟
        // 初始化时间
        mData.getBaseInfo().setGetOrganAt(year + "-" + month + "-" + mData.getTime().getDd() + " " +
                mData.getTime().getHh() + ":" + mData.getTime().getM() + ":00");

        int currentMonthMaxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 日期选择器
        NumberPicker npPicker = mBinding.npShow;
        npPicker.setMinValue(1);
        npPicker.setMaxValue(currentMonthMaxDay);
        npPicker.setValue(day); //默认日
        npPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                choiceDay = newVal;
                mData.getTime().setDd(newVal + ""); // 用户选择的日期
                mData.getBaseInfo().setGetOrganAt(year + "-" + month + "-" + mData.getTime().getDd() + " " +
                        mData.getTime().getHh() + ":" + mData.getTime().getM() + ":00");
            }
        });
        setNumberPickerDividerColor(npPicker);
        // 时间 分钟
        NumberPicker npH = mBinding.npHh;
        String[] hour = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23"};
        npH.setDisplayedValues(hour);
        setNumberPickerDividerColor(npH);
        npH.setMinValue(0);
        npH.setMaxValue(hour.length - 1);
        npH.setValue(h);
        npH.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                choiceH = newVal;

                mData.getTime().setHh(newVal + "");
                mData.getBaseInfo().setGetOrganAt(year + "-" + month + "-" + mData.getTime().getDd() + " " +
                        mData.getTime().getHh() + ":" + mData.getTime().getM() + ":00");
            }
        });
        NumberPicker npM = mBinding.npMm;
        String[] minute = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
                "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        npM.setDisplayedValues(minute);
        setNumberPickerDividerColor(npM);
        npM.setMinValue(0);
        npM.setMaxValue(minute.length - 1);
        npM.setValue(m);
        npM.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                choiceM = newVal;
                mData.getTime().setM(newVal + "");
                mData.getBaseInfo().setGetOrganAt(year + "-" + month + "-" + mData.getTime().getDd() + " " +
                        mData.getTime().getHh() + ":" + mData.getTime().getM() + ":00");
            }
        });


        npPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npH.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        npM.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        */
    }

    public void choiceTime() {

        // 结束时间
        Date end = new Date();
        String endTime = CommonUtil.getTime(end);

        // 开始时间
        Calendar c = Calendar.getInstance();
        c.setTime(end);
        c.add(Calendar.MONTH, -1);
        Date start = c.getTime();
        String startTime = CommonUtil.getTime(start);

        TimeSelector timeSelector = new TimeSelector(mActivity, new TimeSelector.ResultHandler() {
            @Override
            public void handle(String time) {
                mData.getBaseInfo().setGetOrganAt(time);
            }
        }, startTime, endTime);

        timeSelector.show();

    }

    public void tvDown(int state) {
        switch (state) {
            case 0:
                int orgNum = Integer.parseInt(mData.getBaseInfo().getOrganCount());
                if (orgNum > 1) {
                    mData.getBaseInfo().setOrganCount(--orgNum + "");
                }
                break;
            case 1:
                int bloodNum = Integer.parseInt(mData.getOrgan().getBloodSampleCount());
                if (bloodNum > 1) {
                    mData.getOrgan().setBloodSampleCount(--bloodNum + "");
                }
                break;
            case 2:
                int orgSampleNum = Integer.parseInt(mData.getOrgan().getOrganizationSampleCount());
                if (orgSampleNum > 1) {
                    mData.getOrgan().setOrganizationSampleCount(--orgSampleNum + "");
                }
                break;
        }
    }

    public void tvUp(int state) {
        switch (state) {
            case 0:
                int orgNum = Integer.parseInt(mData.getBaseInfo().getOrganCount());
                mData.getBaseInfo().setOrganCount(++orgNum + "");
                break;
            case 1:
                int bloodNum = Integer.parseInt(mData.getOrgan().getBloodSampleCount());
                mData.getOrgan().setBloodSampleCount(++bloodNum + "");
                break;
            case 2:
                int orgSampleNum = Integer.parseInt(mData.getOrgan().getOrganizationSampleCount());
                mData.getOrgan().setOrganizationSampleCount(++orgSampleNum + "");
                break;
        }
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

}

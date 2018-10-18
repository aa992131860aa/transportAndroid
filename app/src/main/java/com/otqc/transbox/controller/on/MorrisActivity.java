package com.otqc.transbox.controller.on;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;
import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.bean.ChartDataBean;
import com.otqc.transbox.db.ChartTransRecordItemDb;
import com.otqc.transbox.db.TransRecord;
import com.otqc.transbox.db.TransRecordItemDb;
import com.otqc.transbox.db.TransRecordItemDbNew;
import com.otqc.transbox.db.TransRecordItemDbNew3;
import com.otqc.transbox.engine.AppBaseActivity;
import com.otqc.transbox.util.CONSTS;
import com.otqc.transbox.util.CommonUtil;
import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.PrefUtils;
import com.otqc.transbox.util.RealmUtil;
import com.otqc.transbox.util.ToastUtil;
import com.otqc.transbox.view.DialogMaker;

import java.util.ArrayList;
import java.util.List;

public class MorrisActivity extends AppBaseActivity {
    private final static String TAG = "MorrisActivity";

    private Button movebtn;//可拖动按钮
    private boolean clickormove = true;//点击或拖动，点击为true，拖动为false
    private int downX, downY;//按下时的X，Y坐标
    private boolean hasMeasured = false;//ViewTree是否已被测量过，是为true，否为false
    private View content;//界面的ViewTree
    private int screenWidth, screenHeight;//ViewTree的宽和高
    private Dialog dialog;
    private Thread thread;
    private boolean isFresh = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // ToastUtil.showToast("position:"+position,getActivity());
                if(isFresh&&webView!=null) {
                    webView.reload();
                }

        }
    };
    @Override
    protected void initVariable() {
        isFresh = true;
        //自动刷新

        if (thread == null) {

            thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    while (isFresh) {



                        try {
                            Thread.sleep(CONSTS.SERIAL_TIME);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(1);
                    }

                }
            };
            thread.start();

        }
    }

    private WebView webView, mWv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_morris);
        webView = (WebView) findViewById(R.id.webView);
        mWv = (WebView) findViewById(R.id.wv);
        //showWaitDialog("loading",false,"loading");
        webView.setVerticalScrollbarOverlay(true);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        LogUtil.e(TAG, "data:come on");
        webView.loadUrl("file:///android_asset/morris.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                refreshData();
            }
        });


        mWv.setVerticalScrollbarOverlay(true);
        WebSettings mWvSetting = mWv.getSettings();
        mWvSetting.setJavaScriptEnabled(true);
        mWvSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWv.loadUrl("file:///android_asset/morrisHumidity.html");
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                refreshData();
            }
        });

        initMoveBtn();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMoveBtn() {

        content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);//获取界面的ViewTree根节点View

        DisplayMetrics dm = getResources().getDisplayMetrics();//获取显示屏属性
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        ViewTreeObserver vto = content.getViewTreeObserver();//获取ViewTree的监听器
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {

                if (!hasMeasured) {

                    screenHeight = content.getMeasuredHeight();//获取ViewTree的高度
                    hasMeasured = true;//设置为true，使其不再被测量。

                }
                return true;//如果返回false，界面将为空。

            }

        });
        movebtn = (Button) findViewById(R.id.movebtn);

        movebtn.setOnTouchListener(new View.OnTouchListener() {//设置按钮被触摸的时间

            int lastX, lastY; // 记录移动的最后的位置

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int ea = event.getAction();//获取事件类型
                switch (ea) {
                    case MotionEvent.ACTION_DOWN: // 按下事件

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        downX = lastX;
                        downY = lastY;
                        break;

                    case MotionEvent.ACTION_MOVE: // 拖动事件

                        // 移动中动态设置位置
                        int dx = (int) event.getRawX() - lastX;//位移量X
                        int dy = (int) event.getRawY() - lastY;//位移量Y
                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;

                        //++限定按钮被拖动的范围
                        if (left < 0) {

                            left = 0;
                            right = left + v.getWidth();

                        }
                        if (right > screenWidth) {

                            right = screenWidth;
                            left = right - v.getWidth();

                        }
                        if (top < 0) {

                            top = 0;
                            bottom = top + v.getHeight();

                        }
                        if (bottom > screenHeight) {

                            bottom = screenHeight;
                            top = bottom - v.getHeight();

                        }

                        //--限定按钮被拖动的范围

                        v.layout(left, top, right, bottom);//按钮重画


                        // 记录当前的位置
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_UP: // 弹起事件

                        //判断是单击事件或是拖动事件，位移量大于5则断定为拖动事件

                        if (Math.abs((int) (event.getRawX() - downX)) > 5
                                || Math.abs((int) (event.getRawY() - downY)) > 5)

                            clickormove = false;

                        else

                            clickormove = true;

                        break;

                }
                return false;

            }

        });
        movebtn.setOnClickListener(new View.OnClickListener() {//设置按钮被点击的监听器

            @Override
            public void onClick(View arg0) {
                if (clickormove)

                    finish();

            }

        });

    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFresh = false;
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        if (mWv != null) {
            mWv.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWv.clearHistory();
            ((ViewGroup) mWv.getParent()).removeView(mWv);
            mWv.destroy();
            mWv = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ChartTransRecordItemDb event) {
        if (event != null) {
            refreshData();
        }
    }

//    // 获取默认 条数的数据，未启用
//    private String getDefData() {
//        final String tid = PrefUtils.getString("tid", "", App.getContext());
//
//        //Realm realm = RealmUtil.getInstance().getRealm();
//        List<TransRecord> result = DataSupport.where("transfer_id = ?",CONSTS.TRANSFER_ID).find(TransRecord.class);
//
//
//        ChartDataBean[] chartArr;
//        int defArrSize = 50;    //控制第一次加载数据的个数
//
//        if (result.size() > 0) {
//
//            if (result.size() > defArrSize) {
//                chartArr = new ChartDataBean[defArrSize];
//            } else {
//                chartArr = new ChartDataBean[result.size()];
//            }
//
//            for (int i = 0; i < result.size(); i++) {
//
//                if (i < defArrSize) {
//                    TransRecord itemDb = result.get(i);
//                    ChartDataBean record = new ChartDataBean();
//
//                    if (!TextUtils.isEmpty(itemDb.getTemperature()) && !TextUtils.isEmpty(itemDb.getHumidity())
//                            && !TextUtils.isEmpty(itemDb.getRecordAt())) {
//                        try {
//
//                            record.setTemperature(itemDb.getTemperature());
//                            record.setHumidity(itemDb.getHumidity());
//                            record.setRecordAt(CommonUtil.dateToStamp(itemDb.getRecordAt()));
//                            chartArr[i] = record;
//
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//
//            String jsData = new Gson().toJson(chartArr);
//            if (!TextUtils.isEmpty(jsData)) {
//
//                return jsData;
//            }
//
//            return "";
//
//        }
//
//        return "";
//    }

    private void refreshData() {

        final String tid = PrefUtils.getString("tid", "", App.getContext());

       // Realm realm = RealmUtil.getInstance().getRealm();
        List<TransRecord> result = DataSupport.where("transfer_id = ?",CONSTS.TRANSFER_ID).order("recordAt").find(TransRecord.class);


       // Log.e(TAG, "data1:" + result.size());

        if (result.size() <100) {


                //长度小于100
                ChartDataBean[] chartArr = new ChartDataBean[result.size()];
                for (int i = 0; i < result.size(); i++) {
                    TransRecord itemDb = result.get(i);
                    ChartDataBean record = new ChartDataBean();

                    if (!TextUtils.isEmpty(itemDb.getTemperature()) && !TextUtils.isEmpty(itemDb.getHumidity())
                            && !TextUtils.isEmpty(itemDb.getRecordAt())) {
                        try {

                            record.setTemperature(itemDb.getTemperature());
                            record.setHumidity(itemDb.getHumidity());
                            record.setRecordAt(CommonUtil.dateToMD(itemDb.getRecordAt()));
                            chartArr[i] = record;
                             //Log.e(TAG,"index"+i+":"+record.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                setJSData(chartArr);
           // }

        }else{

            int size = result.size()/30;
            ArrayList<ChartDataBean> chartArr = new ArrayList<>();

            int index = result.size()/size;
            for (int i = 0; i < result.size(); i+=index) {
                TransRecord itemDb = result.get(i);
                ChartDataBean record = new ChartDataBean();

                if (!TextUtils.isEmpty(itemDb.getTemperature()) && !TextUtils.isEmpty(itemDb.getHumidity())
                        && !TextUtils.isEmpty(itemDb.getRecordAt())) {
                    try {

                        record.setTemperature(itemDb.getTemperature());
                        record.setHumidity(itemDb.getHumidity());
                        record.setRecordAt(CommonUtil.dateToMD(itemDb.getRecordAt()));
                        chartArr.add(record);

                    } catch (Exception e) {
                        e.printStackTrace();
                        //Log.e(TAG,"error"+e.getMessage());
                    }
                }else{
                    //Log.e(TAG,"nodata");
                }


            }

            //Log.e(TAG, "data2:" + chartArr.size());
//            for(int i=0;i<chartArr.length;i++){
//                Log.e(TAG, "data3:" + chartArr[i].getRecordAt()+","+chartArr[i].getHumidity()+","+chartArr[i].getTemperature());
//
//            }
            ChartDataBean [] chartDataBeans = new ChartDataBean[chartArr.size()];
            for(int i=0;i<chartArr.size();i++){
                chartDataBeans[i] = chartArr.get(i);
            }
            setJSData(chartDataBeans);
            // }
        }

        //dismissDialog();
    }

    private void setJSData(ChartDataBean[] chartArr) {
        if (chartArr == null) {
            return;
        }

        String jsData = new Gson().toJson(chartArr);
//        for (int i = 0; i < chartArr.length; i++) {
//            LogUtil.e(TAG, "record:" + chartArr[i].getRecordAt() + ",temperature:" +  chartArr[i].getTemperature() + ",humidity:" +  chartArr[i].getHumidity());
//        }
        //Log.e(TAG,"jsData:"+jsData);
        if (!TextUtils.isEmpty(jsData)) {

            if (webView != null) {
                webView.loadUrl("javascript:set('" + jsData + "')");
            }

            if (mWv != null) {
                mWv.loadUrl("javascript:set('" + jsData + "')");
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isFresh = false;
    }


    /**
     * 等待对话框
     *
     * @author blue
     */
    public Dialog showWaitDialog(String msg, boolean isCanCancelabel, Object tag) {
        if (null == dialog || !dialog.isShowing()) {
            dialog = DialogMaker.showCommenWaitDialog(this, msg, null, isCanCancelabel, tag);
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

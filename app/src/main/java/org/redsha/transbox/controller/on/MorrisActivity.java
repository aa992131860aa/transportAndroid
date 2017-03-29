package org.redsha.transbox.controller.on;

import android.os.Bundle;
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
import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.bean.ChartDataBean;
import org.redsha.transbox.db.ChartTransRecordItemDb;
import org.redsha.transbox.db.TransRecordItemDb;
import org.redsha.transbox.engine.AppBaseActivity;
import org.redsha.transbox.util.CommonUtil;
import org.redsha.transbox.util.PrefUtils;
import org.redsha.transbox.util.RealmUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;

public class MorrisActivity extends AppBaseActivity {
    private final static String TAG = "MorrisActivity";

    private Button movebtn;//可拖动按钮
    private boolean clickormove = true;//点击或拖动，点击为true，拖动为false
    private int downX, downY;//按下时的X，Y坐标
    private boolean hasMeasured = false;//ViewTree是否已被测量过，是为true，否为false
    private View content;//界面的ViewTree
    private int screenWidth, screenHeight;//ViewTree的宽和高

    @Override
    protected void initVariable() {

    }

    private WebView webView, mWv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_morris);
        webView = (WebView) findViewById(R.id.webView);
        mWv = (WebView) findViewById(R.id.wv);

        webView.setVerticalScrollbarOverlay(true);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

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

        mWv.loadUrl("file:///android_asset/morrisHum.html");
        mWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                refreshData();
            }
        });

        initMoveBtn();
    }

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

    // 获取默认 条数的数据，未启用
    private String getDefData() {
        final String tid = PrefUtils.getString("tid", "", App.getContext());

        Realm realm = RealmUtil.getInstance().getRealm();
        RealmResults<TransRecordItemDb> result = realm.where(TransRecordItemDb.class).
                equalTo("transfer_id", tid).
                findAll();

        ChartDataBean[] chartArr;
        int defArrSize = 50;    //控制第一次加载数据的个数

        if (result.size() > 0) {

            if (result.size() > defArrSize) {
                chartArr = new ChartDataBean[defArrSize];
            } else {
                chartArr = new ChartDataBean[result.size()];
            }

            for (int i = 0; i < result.size(); i++) {

                if (i < defArrSize) {
                    TransRecordItemDb itemDb = result.get(i);
                    ChartDataBean record = new ChartDataBean();

                    if (!TextUtils.isEmpty(itemDb.getTemperature()) && !TextUtils.isEmpty(itemDb.getHumidity())
                            && !TextUtils.isEmpty(itemDb.getRecordAt())) {
                        try {

                            record.setTemperature(itemDb.getTemperature());
                            record.setHumidity(itemDb.getHumidity());
                            record.setRecordAt(CommonUtil.dateToStamp(itemDb.getRecordAt()));
                            chartArr[i] = record;

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            String jsData = new Gson().toJson(chartArr);
            if (!TextUtils.isEmpty(jsData)) {

                return jsData;
            }

            return "";

        }

        return "";
    }

    private void refreshData() {
        final String tid = PrefUtils.getString("tid", "", App.getContext());

        Realm realm = RealmUtil.getInstance().getRealm();
        RealmResults<TransRecordItemDb> result = realm.where(TransRecordItemDb.class).
                equalTo("transfer_id", tid).
                findAll();

        if (result.size() > 0) {

            if (result.size() > 100) {

                ChartDataBean[] chartArr;
                // 大于100，取100条，不包含开始和结束位置
                int[] ints = CommonUtil.randomCommon(1, result.size() - 2, 98);
                Arrays.sort(ints);

                // 判断 0 位置 和 末尾
                if (!TextUtils.isEmpty(result.get(0).getTemperature()) && !TextUtils.isEmpty(result.get(0).getHumidity())
                        && !TextUtils.isEmpty(result.get(0).getRecordAt()) &&
                        !TextUtils.isEmpty(result.get(result.size() - 1).getTemperature()) && !TextUtils.isEmpty(result.get(result.size() - 1).getHumidity())
                        && !TextUtils.isEmpty(result.get(result.size() - 1).getRecordAt())) {

                    // 不为空，则长度100
                    chartArr = new ChartDataBean[100];

                    try {
                        ChartDataBean record = new ChartDataBean();
                        record.setTemperature(result.get(0).getTemperature());
                        record.setHumidity(result.get(0).getHumidity());
                        record.setRecordAt(CommonUtil.dateToStamp(result.get(0).getRecordAt()));
                        chartArr[0] = record;

                        record.setTemperature(result.get(result.size() - 1).getTemperature());
                        record.setHumidity(result.get(result.size() - 1).getHumidity());
                        record.setRecordAt(CommonUtil.dateToStamp(result.get(result.size() - 1).getRecordAt()));
                        chartArr[99] = record;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    chartArr = new ChartDataBean[98];
                }

                // 拿到数据转为list
                ArrayList<TransRecordItemDb> itemDbsList = new ArrayList<>();
                for (Integer randomPos : ints) {
                    itemDbsList.add(result.get(randomPos));
                }

                // 遍历
                for (int i = 0; i < itemDbsList.size(); i++) {
                    TransRecordItemDb transRecordItemDb = itemDbsList.get(i);
                    ChartDataBean record = new ChartDataBean();

                    // 这种情况不判断为空了，会导致长度不一致
                    try {
                        record.setTemperature(transRecordItemDb.getTemperature());
                        record.setHumidity(transRecordItemDb.getHumidity());
                        record.setRecordAt(CommonUtil.dateToStamp(transRecordItemDb.getRecordAt()));

                        if (chartArr.length == 98) {
                            chartArr[i] = record;
                        } else if (chartArr.length == 100) {
                            chartArr[i + 1] = record;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                setJSData(chartArr);
            } else {
                //长度小于100
                ChartDataBean[] chartArr = new ChartDataBean[result.size()];
                for (int i = 0; i < result.size(); i++) {
                    TransRecordItemDb itemDb = result.get(i);
                    ChartDataBean record = new ChartDataBean();

                    if (!TextUtils.isEmpty(itemDb.getTemperature()) && !TextUtils.isEmpty(itemDb.getHumidity())
                            && !TextUtils.isEmpty(itemDb.getRecordAt())) {
                        try {

                            record.setTemperature(itemDb.getTemperature());
                            record.setHumidity(itemDb.getHumidity());
                            record.setRecordAt(CommonUtil.dateToStamp(itemDb.getRecordAt()));
                            chartArr[i] = record;

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                }

                setJSData(chartArr);
            }

        }
    }

    private void setJSData(ChartDataBean[] chartArr) {
        if (chartArr == null) {
            return;
        }

        String jsData = new Gson().toJson(chartArr);
        if (!TextUtils.isEmpty(jsData)) {

            if (webView != null) {
                webView.loadUrl("javascript:set('" + jsData + "')");
            }

            if (mWv != null) {
                mWv.loadUrl("javascript:set('" + jsData + "')");
            }

        }
    }

}

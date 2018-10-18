package com.otqc.transbox.http;

import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.http.response.ExportBean;
import com.otqc.transbox.util.LogUtil;

import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.http.response.ExportBean;
import com.otqc.transbox.util.LogUtil;

import com.otqc.transbox.bean.BoxBean;
import com.otqc.transbox.bean.BoxNum;
import com.otqc.transbox.bean.KeywordBean;
import com.otqc.transbox.bean.OddDetailBean;
import com.otqc.transbox.bean.OpenCollision;
import com.otqc.transbox.bean.OpoBean;
import com.otqc.transbox.bean.TransFinshBean;
import com.otqc.transbox.bean.TransOddBean;
import com.otqc.transbox.bean.TransferPersonBean;
import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.http.response.ExportBean;
import com.otqc.transbox.util.LogUtil;

import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.http.response.ExportBean;
import com.otqc.transbox.util.LogUtil;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HttpHelper extends BaseApi {
    private static String TAG = "HttpHelper";
    private HttpService mHttpService = getRetrofit().create(HttpService.class);

    // 获取箱子信息
    public Observable<BoxBean> getBoxInfo(String deviceId) {
        return mHttpService.getBoxInfo(deviceId)
                .map(new HttpResultFunc<BoxBean>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 获取转运人
    public Observable<List<TransferPersonBean>> getTransPerson(String hospitalid) {
        return mHttpService.getTransPerson(hospitalid)
                .map(new HttpResultFunc<List<TransferPersonBean>>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 获取opo信息
    public Observable<List<OpoBean>> getAllOpo() {
        return mHttpService.getAllOpo()
                .map(new HttpResultFunc<List<OpoBean>>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 获取keyword
    public Observable<KeywordBean> getKey() {
        return mHttpService.getKey()
                .map(new HttpResultFunc<KeywordBean>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 新建转运
    public Observable<TransOddBean> createTrans(RequestCreateTrans createTrans) {
        return mHttpService.createTrans(createTrans)
                .map(new HttpResultFunc<TransOddBean>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 转运监控中
    public Observable<List<String>> record(TransferRecordRequest record) {
        return mHttpService.record(record)
                .map(new HttpResultFunc<List<String>>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 结束转运
    public Observable<TransFinshBean> finishTrans(String transferid) {
        return mHttpService.finishTrans(transferid)
                .map(new HttpResultFunc<TransFinshBean>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 根据转运单，器官段号查询 转运信息
    public Observable<OddDetailBean> getOddDetail(String transNum, String orgNum) {
        return mHttpService.getOddDetail(transNum, orgNum)
                .map(new HttpResultFunc<OddDetailBean>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    // 根据转运单，器官段号查询 碰撞 打开的异常
    public Observable<List<OpenCollision>> getOpenCollision(String transNum, String type) {
        return mHttpService.getOpenCollision(transNum, type)
                .map(new HttpResultFunc<List<OpenCollision>>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    // 根据箱号,查询列表
    public Observable<List<BoxNum>> getBoxNum(String boxNum,String page,String pageSize) {
        LogUtil.e("HttpHelper","page:"+page+","+pageSize);
        return mHttpService.getBoxNum(boxNum,page,pageSize)
                .map(new HttpResultFunc<List<BoxNum>>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    // 导出
    public Observable<ExportBean> exportPhone(String phone, String tid) {
        return mHttpService.exportPhone(phone, tid)
                .map(new HttpResultFunc<ExportBean>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 用来统一处理status，将data剥离出来返回给subscriber
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {

            if (!httpResult.getStatus().equals("OK")) {

                LogUtil.e(TAG, "HttpResultFunc1: " + httpResult.getMsg());


                if (httpResult.getStatus().equals("NOT FOUND") && !httpResult.getMsg().equals("")) {
                    throw new HttpException(httpResult.getMsg());
                }
            }

            LogUtil.e(TAG, "HttpResultFunc2: " + httpResult.getMsg());

            return httpResult.getData();
        }

    }

}

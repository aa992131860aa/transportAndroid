package com.otqc.transbox.http;

import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.ToastUtil;

import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.ToastUtil;

import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.ToastUtil;

import com.otqc.transbox.util.LogUtil;
import com.otqc.transbox.util.ToastUtil;

import java.net.SocketTimeoutException;

import rx.Observer;

/**
 * 网络请求返回需要的模型
 */
public abstract class HttpObserver<T> implements Observer<T>, INetResult<T> {
    private static String TAG = "HttpObserver";

    /**
     * 请求失败, 对错误信息进行处理,
     * 默认显示一个Toast提醒用户.
     */
    @Override
    public void onFail(int errorCode, String msg) {
        //ToastUtil.showToast(msg);
    }

    @Override
    public void onCompleted() {
        onComplete();
    }

    @Override
    public void onError(Throwable e) {
        onComplete();

        if (e instanceof SocketTimeoutException) {
            ToastUtil.showToast("连接超时");
        } else if (e instanceof HttpException) {
            onFail(0, e.getMessage());
        } else {
            onFail(1, "很抱歉，无法从服务器获取数据。"+e.getMessage());
            LogUtil.e("HttpObserver",e.getMessage());
        }

    }

    @Override
    public void onNext(T model) {
        onSuccess(model);
    }

}

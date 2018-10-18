package com.otqc.transbox.http;

import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.http.response.ExportBean;

import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.http.response.ExportBean;

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

import com.otqc.transbox.http.request.RequestCreateTrans;
import com.otqc.transbox.http.request.TransferRecordRequest;
import com.otqc.transbox.http.response.ExportBean;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface HttpService {
    // 获取箱子信息
    @GET("boxInfo")
    Observable<HttpResult<BoxBean>> getBoxInfo(@Query("deviceId") String deviceId);

    // 获取所有转运人
    @GET("transferPersons")
    Observable<HttpResult<List<TransferPersonBean>>> getTransPerson(@Query("hospitalid") String hospitalid);

    // 获取opo信息
    @GET("opos2")
    Observable<HttpResult<List<OpoBean>>> getAllOpo();

    // 获取keywords
    @GET("kwds")
    Observable<HttpResult<KeywordBean>> getKey();

    // 新建转运
    @POST("transfer")
    Observable<HttpResult<TransOddBean>> createTrans(@Body RequestCreateTrans transRequest);

    // 结束转运
    @PUT("transfer/{transferid}/done")
    Observable<HttpResult<TransFinshBean>> finishTrans(@Path("transferid") String transferid);

    // 转运监控
    @POST("transferRecord")
    Observable<HttpResult<List<String>>> record(@Body TransferRecordRequest record);

    // 根据转运单号，器官段号获取信息
    @GET("transferInfo1")
    Observable<HttpResult<OddDetailBean>> getOddDetail(@Query("transferNumber") String transNum, @Query("organSegNumber") String orgNum);

    // 根据转运单号，器官段号获取打开 碰撞的异常
    @GET("openCollision")
    Observable<HttpResult<List<OpenCollision>>> getOpenCollision(@Query("transferNumber") String transNum, @Query("type") String type);

    // 根据箱号,获取列表
    @GET("boxNum")
    Observable<HttpResult<List<BoxNum>>> getBoxNum(@Query("boxNum") String boxNum,@Query("page") String page,@Query("pageSize") String pageSize);

    // 导出
    @GET("checkRecord/{phone}")
    Observable<HttpResult<ExportBean>> exportPhone(@Path("phone") String phone, @Query("transferid") String transferid);



}

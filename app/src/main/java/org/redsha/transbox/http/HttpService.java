package org.redsha.transbox.http;

import org.redsha.transbox.bean.BoxBean;
import org.redsha.transbox.bean.KeywordBean;
import org.redsha.transbox.bean.OddDetailBean;
import org.redsha.transbox.bean.OpoBean;
import org.redsha.transbox.bean.TransFinshBean;
import org.redsha.transbox.bean.TransOddBean;
import org.redsha.transbox.bean.TransferPersonBean;
import org.redsha.transbox.http.request.RequestCreateTrans;
import org.redsha.transbox.http.request.TransferRecordRequest;
import org.redsha.transbox.http.response.ExportBean;

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
    @GET("transferInfo")
    Observable<HttpResult<OddDetailBean>> getOddDetail(@Query("transferNumber") String transNum, @Query("organSegNumber") String orgNum);

    // 导出
    @GET("checkRecord/{phone}")
    Observable<HttpResult<ExportBean>> exportPhone(@Path("phone") String phone, @Query("transferid") String transferid);

}

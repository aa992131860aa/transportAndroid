package com.otqc.transbox.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by 99213 on 2017/8/16.
 */

public interface TomcatService {
    @GET("blog/{id}") //这里的{id} 表示是一个变量
    Call<ResponseBody> getBlog(/** 这里的id表示的是上面的{id} */@Path("id") int id);
}

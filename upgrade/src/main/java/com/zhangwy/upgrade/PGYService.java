package com.zhangwy.upgrade;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zhangwy on 2018/1/26 下午3:47.
 * Updated by zhangwy on 2018/1/26 下午3:47.
 * Description
 */
@SuppressWarnings("unused")
public interface PGYService {
    @FormUrlEncoded
    @POST("apiv2/app/view")
    Call<AppCheckEntity> view(@Field("_api_key") String apiKey, @Field("appKey") String appKey);
}

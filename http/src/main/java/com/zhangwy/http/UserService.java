package com.zhangwy.http;

import com.zhangwy.common.entities.IUserEntity;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by zhangwy on 2019/4/30.
 * Updated by zhangwy on 2019/4/30.
 * Description
 */
public interface UserService {
    @FormUrlEncoded
    @POST("login")
    Call<IUserEntity> login(@Field("wxId") String code);

    @FormUrlEncoded
    @POST("check")
    Call<IUserEntity> check(@Field("userId") String userId, @Field("token") String token);
}
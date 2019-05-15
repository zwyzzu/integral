package com.zhangwy.upgrade;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import yixia.lib.core.util.Logger;

/**
 * Created by zhangwy on 2019/5/12.
 * Updated by zhangwy on 2019/5/12.
 * Description
 */
public class ApiFactory {

    public static PGYService createPgy() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Logger.d("message-->"+message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(logging);
        builder.connectTimeout(15, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .baseUrl("https://www.pgyer.com/")
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        return retrofit.create(PGYService.class);
    }
}

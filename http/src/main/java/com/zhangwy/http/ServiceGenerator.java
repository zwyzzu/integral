package com.zhangwy.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;
import yixia.lib.core.util.Logger;

/**
 * Created by zhangwy on 2019/4/30.
 * Updated by zhangwy on 2019/4/30.
 * Description
 */
@SuppressWarnings("unused")
public abstract class ServiceGenerator {
    private static ServiceGenerator instance = new ServiceGeneratorImpl();

    public static ServiceGenerator getInstance() {
        return instance;
    }

    public abstract UserService getUserService();
    /*-----------------------------------------------------*/
    private static class ServiceGeneratorImpl extends ServiceGenerator {

        private Retrofit retrofit;
        private String apiHost = BuildConfig.BaseUrl;
        private UserService userService;
        private ServiceGeneratorImpl() {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(new PublicParamsInterceptor())
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(10, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .baseUrl(this.apiHost)
                    .build();
        }

        @Override
        public UserService getUserService() {
            if (this.userService == null) {
                synchronized (ServiceGenerator.class) {
                    if (this.userService == null) {
                        this.userService = this.retrofit.create(UserService.class);
                    }
                }
            }
            return this.userService;
        }
    }
}

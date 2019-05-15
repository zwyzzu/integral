package com.zhangwy.http;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import yixia.lib.core.util.Logger;

class PublicParamsInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody body = request.body();

        FormBody form = null;
        if (body instanceof FormBody) {
            form = (FormBody) body;
        }

        String params = null;
        String sign = null;
        try {
            params = ParameterGenerator.generate(form);
            sign = ParameterGenerator.sign(params);
        } catch (Exception e) {
            Logger.e("PublicParamsInterceptor", e);
        }

        if (!TextUtils.isEmpty(params) && !TextUtils.isEmpty(sign)) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params);
            Request.Builder builder = new Request.Builder()
                    .url(request.url())
                    .header("Content-Type", "application/json; encoding=utf-8")
                    .addHeader("sign", sign)
                    .post(requestBody);
            return chain.proceed(builder.build());
        } else {
            Logger.e("did't add public params, please make sure it's you want!");
        }
        return chain.proceed(request);
    }
}

//package com.zhangwy.http;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import yixia.lib.core.util.Logger;
//
///**
// * Created by zhangwy on 2019/4/29.
// * Updated by zhangwy on 2019/4/29.
// * Description
// */
//public class OkHttpGetRequest extends OkHttpRequest {
//
//    public static OkHttpGetRequest create() {
//        return new OkHttpGetRequest();
//    }
//
//    public void execute() {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://www.baidu.com")
//                .build();
//        client.newCall(request).enqueue(this);
//    }
//
//    @Override
//    public void onFailure(Call call, IOException e) {
//
//    }
//
//    @Override
//    public void onResponse(Call call, Response response) throws IOException {
//        if (response.isSuccessful()) {//回调的方法执行在子线程。
//            Logger.d("获取数据成功了");
//            Logger.d("response.code()==" + response.code());
//            Logger.d("response.body().string()==" + response.body().string());
//        }
//    }
//}

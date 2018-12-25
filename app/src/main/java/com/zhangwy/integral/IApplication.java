package com.zhangwy.integral;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by zhangwy on 2018/12/22 上午1:40.
 * Updated by zhangwy on 2018/12/22 上午1:40.
 * Description
 */
public class IApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
    }
}

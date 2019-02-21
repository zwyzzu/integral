package com.zhangwy.integral;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by zhangwy on 2018/12/22 上午1:40.
 * Updated by zhangwy on 2018/12/22 上午1:40.
 * Description
 */
public class IApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //因为引用的包过多，实现多包问题
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());
    }
}

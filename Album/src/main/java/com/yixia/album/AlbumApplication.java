package com.yixia.album;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by zhangwy on 2018/6/3 下午8:29.
 * Updated by zhangwy on 2018/6/3 下午8:29.
 * Description TODO
 */
public class AlbumApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}

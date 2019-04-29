package com.zhangwy.integral;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zhangwy.integral.data.IAddressTags;
import com.zhangwy.integral.data.IDataManager;

import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.DirMgmt;
import yixia.lib.core.util.UUID;

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
        PreferencesHelper.defaultInstance().init(getApplicationContext());
        IDataManager.initialize(this.getApplicationContext());
        IAddressTags.initialized(this.getApplicationContext());
        DirMgmt.getInstance().init(this.getApplicationContext());
        UUID.getInstance().init(this.getApplicationContext());
    }
}

package com.yixia.widget.player;

import android.content.Context;

import yixia.lib.core.sharePreferences.PreferencesHelper;

/**
 * Created by zhangwy on 2017/8/15 下午5:49.
 * Updated by zhangwy on 2017/8/15 下午5:49.
 * Description
 */
@SuppressWarnings("unused")
public abstract class UseMobileConfig {
    public static UseMobileConfig initialize(Context ctx) {
        return getInstance().init(ctx);
    }

    private static UseMobileConfig instance;
    public static UseMobileConfig getInstance() {
        if (instance == null) {
            synchronized (UseMobileConfig.class) {
                if (instance == null) {
                    instance = new UseMobileConfigImp();
                }
            }
        }
        return instance;
    }

    abstract UseMobileConfig init(Context context);

    public abstract boolean useMobile(MonitorType monitorType);

    public abstract void setUseMobile(MonitorType monitorType, boolean useMobile);

    /**********************************************************************************************/

    private static class UseMobileConfigImp extends UseMobileConfig {

        private boolean useMobile = false;
        private boolean useMobileEternal = false;

        private final String useMobilePreferencesKey = "useMobilePreferencesKey";
        @Override
        UseMobileConfig init(Context context) {
            PreferencesHelper.defaultInstance().init(context);
            this.useMobileEternal = PreferencesHelper.defaultInstance()
                    .getBoolean(useMobilePreferencesKey, false);
            return this;
        }

        @Override
        public boolean useMobile(MonitorType monitorType) {
            switch (monitorType) {
                case CURRENTSTART:
                    return useMobile;
                case ETERNAL:
                    return useMobileEternal;
            }
            return false;
        }

        @Override
        public void setUseMobile(MonitorType monitorType, boolean useMobile) {
            switch (monitorType) {
                case ETERNAL:
                    this.useMobileEternal = useMobile;
                    PreferencesHelper.defaultInstance()
                            .applyBoolean(this.useMobilePreferencesKey, useMobile);
                case CURRENTSTART:
                    this.useMobile = useMobile;
                    break;
            }
        }
    }

    public enum MonitorType {
        CURRENTMEDIA, CURRENTSTART, ETERNAL
    }
}

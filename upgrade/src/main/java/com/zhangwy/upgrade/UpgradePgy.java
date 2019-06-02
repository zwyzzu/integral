package com.zhangwy.upgrade;

import android.app.Activity;
import android.text.TextUtils;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import yixia.lib.core.util.Device;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2019/5/31.
 * Updated by zhangwy on 2019/5/31.
 * Description
 */
public abstract class UpgradePgy extends UpgradeSuper {
    /**
     * @param activity             Activity实体
     * @param checkPGY             是否检测蒲公英版本
     * @param dialogCheck          是否显示检测更新弹窗
     * @param forcedUpgrade        是否强制更新
     * @param showDownloadProgress 是否显示下载进度
     */
    UpgradePgy(Activity activity, boolean checkPGY, boolean dialogCheck, boolean forcedUpgrade, boolean showDownloadProgress) {
        super(activity, checkPGY, dialogCheck, forcedUpgrade, showDownloadProgress);
    }

    public static UpgradePgy newInstance(Activity activity, boolean checkPGY, boolean dialogCheck, boolean forcedUpgrade, boolean showDownloadProgress) {
        return new UpgradePgyImpl(activity, checkPGY, dialogCheck, forcedUpgrade, showDownloadProgress);
    }

    /*-------------------------------------------------------------------------------------*/
    private static class UpgradePgyImpl extends UpgradePgy {
        AppBean appBean;

        private UpgradePgyImpl(Activity activity, boolean checkPGY, boolean dialogCheck, boolean forcedUpgrade, boolean showDownloadProgress) {
            super(activity, checkPGY, dialogCheck, forcedUpgrade, showDownloadProgress);
        }

        @Override
        public void check() {
            PgyUpdateManager.setIsForced(this.forcedUpgrade);
            PgyUpdateManager.register(this.activity, new UpdateManagerListener() {

                @Override
                public void onUpdateAvailable(String result) {
                    appBean = getAppBeanFromString(result);
                    if (checkVersion(appBean)) {
                        showUpgradeDialog(appBean.getReleaseNote());
                    }
                }

                @Override
                public void onNoUpdateAvailable() {
                    dismissCheckDialog();
                }
            });
        }

        @Override
        protected void uploadApp() {
            if (this.appBean != null) {
                UpdateManagerListener.startDownloadTask(this.activity, this.appBean.getDownloadURL());
            }
        }

        private boolean checkVersion(AppBean entity) {
            if (entity == null) {
                return false;
            }
            if (!TextUtils.equals(entity.getVersionName(), Device.App.getVersionName(this.activity)) || Util.parseInt(entity.getVersionCode()) > Device.App.getVersionCode(this.activity)) {
                return true;
            }

            if (!this.checkPGY) {
                return false;
            }
            return false;
        }
    }

}

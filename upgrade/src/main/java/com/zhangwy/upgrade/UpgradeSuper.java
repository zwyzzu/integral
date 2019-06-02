package com.zhangwy.upgrade;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.KeyEvent;

import com.zhangwy.upgrade.entities.AppCheckEntity;

import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2019/5/31.
 * Updated by zhangwy on 2019/5/31.
 * Description
 */
public abstract class UpgradeSuper {
    protected Activity activity;
    protected ProgressDialog checkDialog;
    protected ProgressDialog updateDialog;
    protected AppCheckEntity remoteCheckEntity;
    protected boolean checkPGY;
    protected boolean dialogCheck;
    protected boolean forcedUpgrade;
    protected boolean showDownloadProgress;

    /**
     * @param activity             Activity实体
     * @param checkPGY             是否检测蒲公英版本
     * @param dialogCheck          是否显示检测更新弹窗
     * @param forcedUpgrade        是否强制更新
     * @param showDownloadProgress 是否显示下载进度
     */
    UpgradeSuper(Activity activity, boolean checkPGY, boolean dialogCheck, boolean forcedUpgrade, boolean showDownloadProgress) {
        this.activity = activity;
        this.checkPGY = checkPGY;
        this.dialogCheck = dialogCheck;
        this.forcedUpgrade = forcedUpgrade;
        this.showDownloadProgress = showDownloadProgress;
    }

    public abstract void check();

    protected void showCheckDialog() {
        this.dismissCheckDialog();
        this.checkDialog = WindowUtil.createProgressDialog(this.activity, 0, "检查新版本", false, true);
        if (checkDialog == null) {
            return;
        }
        this.checkDialog.setOnKeyListener((dialog, keyCode, event) -> (keyCode == KeyEvent.KEYCODE_BACK));
        this.checkDialog.show();
    }

    protected void dismissCheckDialog() {
        if (this.checkDialog != null && this.checkDialog.isShowing()) {
            this.checkDialog.dismiss();
        }
    }

    protected void showUpgradeDialog(final String detail) {
        String appName = this.activity.getString(R.string.upgrade_remind);
        String install = this.activity.getString(R.string.dialog_install);
        String cancel = this.activity.getString(R.string.dialog_cancel);
        if (this.forcedUpgrade) {
            cancel = "";
        }
        Dialog dialog = WindowUtil.createAlertDialog(this.activity, appName, detail, install, (dialog12, which) -> {
            uploadApp();
        }, cancel, null);
        if (dialog == null) {
            return;
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener((dialog1, keyCode, event) -> (keyCode == KeyEvent.KEYCODE_BACK));
        dialog.show();
    }

    protected abstract void uploadApp();
}

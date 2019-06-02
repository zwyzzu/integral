package com.zhangwy.upgrade;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.yixia.download.DownloadApp;
import com.yixia.download.Downloader;
import com.zhangwy.upgrade.entities.AppCheckEntity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Device;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Util;
import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2018/1/24 下午2:12.
 * Updated by zhangwy on 2018/1/24 下午2:12.
 * Description
 */
@SuppressWarnings("unused")
public abstract class Upgrade {

    public static final String ReqAppVersion = "req_app_version";
    public static final String ReqAppVersionCurrent = "req_app_version_current";

    public static Upgrade newInstance(Activity activity, boolean showDownloadProgress) {
        String apiKey = "20ffadc9886a459535c9dcac1984b78f";
        String appKey = "b345a6a954d01c9598c540b8f22ae352";
        String password = "";
        return new UpgradeImpl(activity, apiKey, appKey, password, showDownloadProgress);
    }

    public static Upgrade newInstance(Activity activity, String apiKey, String appKey, String password, boolean showDownloadProgress) {
        return new UpgradeImpl(activity, apiKey, appKey, password, showDownloadProgress);
    }

    /**
     * 检测更新
     *
     * @param checkPGY      是否检测蒲公英版本
     * @param dialogCheck   是否显示检测更新弹窗
     * @param forcedUpgrade 是否强制更新
     */
    public abstract void check(boolean checkPGY, boolean dialogCheck, boolean forcedUpgrade);

    private static class UpgradeImpl extends Upgrade implements Callback<AppCheckEntity>, Downloader.DownloadListener {
        private final String URL_DOWNLOAD = "https://www.pgyer.com/apiv2/app/install?_api_key=%1$s&appKey=%2$s%3$s&buildKey=%4$s";
        private final String URL_ICON = "https://www.pgyer.com/image/view/app_icons/%1$s";
        private ProgressDialog checkDialog;
        private ProgressDialog updateDialog;
        private AppCheckEntity remoteCheckEntity;
        private boolean checkPGY = false;
        private boolean dialogCheck = false;
        private boolean forcedUpgrade = false;

        private Activity activity;
        private String apiKey;
        private String appKey;
        private String password;
        private boolean showDownloadProgress;
        private Handler handler;

        private UpgradeImpl(Activity activity, String apiKey, String appKey, String password, boolean showDownloadProgress) {
            this.activity = activity;
            this.apiKey = apiKey;
            this.appKey = appKey;
            PreferencesHelper.defaultInstance().init(this.activity.getApplication());
            this.password = TextUtils.isEmpty(password) ? "" : String.format(Locale.getDefault(), "&buildPassword=%1$s", password);
            this.showDownloadProgress = showDownloadProgress;
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void check(boolean checkPGY, boolean dialogCheck, boolean forcedUpgrade) {
            this.checkPGY = checkPGY;
            this.dialogCheck = dialogCheck;
            this.forcedUpgrade = forcedUpgrade;
            Call<AppCheckEntity> call = ApiFactory.createPgy().view(this.apiKey, this.appKey);
            call.enqueue(this);
            if (this.dialogCheck) {
                this.showCheckDialog();
            }
        }

        @Override
        public void onResponse(Call<AppCheckEntity> call, Response<AppCheckEntity> response) {
            AppCheckEntity entity = response.body();
            if (entity == null || entity.getDetail() == null) {
                this.onFailure(call, new Throwable("数据为空"));
                return;
            }
            this.handler.post(this::dismissCheckDialog);
            if (this.checkVersion(entity)) {
                this.handler.post(() -> this.showUpgradeDialog(entity));
            }
        }

        @Override
        public void onFailure(Call<AppCheckEntity> call, Throwable throwable) {
            Logger.e("onFail", throwable);
            this.handler.post(this::dismissCheckDialog);
        }

        private void showCheckDialog() {
            this.dismissCheckDialog();
            this.checkDialog = WindowUtil.createProgressDialog(this.activity, 0, "检查新版本", false, true);
            if (checkDialog == null) {
                return;
            }
            this.checkDialog.setOnKeyListener((dialog, keyCode, event) -> (keyCode == KeyEvent.KEYCODE_BACK));
            this.checkDialog.show();
        }

        private void dismissCheckDialog() {
            if (this.checkDialog != null && this.checkDialog.isShowing()) {
                this.checkDialog.dismiss();
            }
        }

        private boolean checkVersion(AppCheckEntity entity) {
            if (!TextUtils.equals(entity.getDetail().getVersion(), Device.App.getVersionName(this.activity)) || Util.parseInt(entity.getDetail().getVersionNo()) > Device.App.getVersionCode(this.activity)) {
                return true;
            }

            if (!this.checkPGY) {
                return false;
            }

            String localVersionString = PreferencesHelper.defaultInstance().getString(ReqAppVersion, "");
            if (TextUtils.isEmpty(localVersionString))
                return true;
            try {
                AppCheckEntity localEntity = JSON.parseObject(localVersionString, AppCheckEntity.class);
                if (localEntity == null || localEntity.getDetail() == null)
                    return true;
                if (localEntity.equals(entity))
                    return false;
                AppCheckEntity.AppVersionEntity localCheckEntity = localEntity.getDetail();
                AppCheckEntity.AppVersionEntity remoteCheckEntity = entity.getDetail();
                return !localCheckEntity.equals(remoteCheckEntity) && !(TextUtils.equals(localCheckEntity.getBuildVersion(), remoteCheckEntity.getBuildVersion())
                        && TextUtils.equals(localCheckEntity.getVersionNo(), remoteCheckEntity.getVersionNo())
                        && TextUtils.equals(localCheckEntity.getVersion(), remoteCheckEntity.getVersion()));
            } catch (Exception e) {
                Logger.e("checkVersion", e);
                return true;
            }
        }

        private void showUpgradeDialog(final AppCheckEntity entity) {
            String appName = this.activity.getString(R.string.upgrade_remind);
            String detail = entity.getDetail().getUpdateDescription();
            float size = Integer.parseInt(entity.getDetail().size);
            size = size / 1024 / 1024;
            String install = this.activity.getString(R.string.dialog_install);
            String cancel = this.activity.getString(R.string.dialog_cancel);
            String content = this.activity.getString(R.string.dialog_upgrade_tip, Util.float2String(size, 2), detail);
            if (this.forcedUpgrade) {
                cancel = "";
            }
            Dialog dialog = WindowUtil.createAlertDialog(this.activity, appName, content, install, (dialog12, which) -> {
                remoteCheckEntity = entity;
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

        private void uploadApp() {
            try {
                PreferencesHelper.defaultInstance().applyString(ReqAppVersionCurrent, JSON.toJSON(this.remoteCheckEntity).toString());
            } catch (Exception e) {
                Logger.e("applyString", e);
            }
            AppCheckEntity.AppVersionEntity versionEntity = this.remoteCheckEntity.getDetail();
            this.showDownloadProgress(versionEntity);
            String url = String.format(Locale.getDefault(), URL_DOWNLOAD, this.apiKey, this.appKey, this.password, versionEntity.getBuildKey());
            String iconUrl = String.format(Locale.getDefault(), URL_ICON, versionEntity.getIcon());
            DownloadApp.newInstance(this.activity).setListener(this).download(url, versionEntity.getName(), iconUrl, "", Util.parseInt(versionEntity.getSize()));
        }

        private void showDownloadProgress(AppCheckEntity.AppVersionEntity versionEntity) {
            this.updateDialog = WindowUtil.createProgressDialog(this.activity, 0, this.activity.getString(R.string.dialog_download, versionEntity.getName(), versionEntity.getVersion()), false, false);
            if (this.updateDialog == null) {
                return;
            }
            this.updateDialog.setOnKeyListener((dialog, keyCode, event) -> (keyCode == KeyEvent.KEYCODE_BACK));
            this.updateDialog.show();
        }

        @Override
        public void onAddTask(String s, String s1, boolean b) {
            Logger.e(s);
        }

        @Override
        public void onStart(String s, String s1) {
            Logger.e(s);
        }

        @Override
        public void onProgressChanged(String s, String s1, float v) {
            if (this.updateDialog != null)
                this.updateDialog.setProgress((int) v);
        }

        @Override
        public void onStop(String s, String s1) {
        }

        @Override
        public void onSuccess(String s, String s1, String s2) {
            if (this.updateDialog != null)
                this.updateDialog.dismiss();
        }

        @Override
        public void onFailed(String s, String s1, int i, String s2) {
            if (this.updateDialog != null)
                this.updateDialog.dismiss();
            showMessage(s2);
            Logger.e(s);
        }

        private void showMessage(String msg) {
            try {
                Toast.makeText(this.activity, msg, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Logger.e("showMessage", e);
            }
        }
    }

}

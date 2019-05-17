package com.zhangwy.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Logger;

/**
 * Created by zhangwy on 2018/1/24 下午5:42.
 * Updated by zhangwy on 2018/1/24 下午5:42.
 * Description 更新版本用的广播
 */

public class UpgradeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.setLevel(Logger.LEVEL_VERBOSE);
        Logger.d("onReceive.UpgradeBroadcastReceiver");
        if (intent != null && TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)
                && intent.getData() != null && TextUtils.equals(intent.getData().getSchemeSpecificPart(), context.getPackageName())) {
            Logger.d("onReceive.UpgradeBroadcastReceiver.true");
            String currentAppVersion = PreferencesHelper.defaultInstance().getString(Upgrade.ReqAppVersionCurrent, "");
            if (TextUtils.isEmpty(currentAppVersion)) {
                return;
            }
            Logger.d("onReceive.UpgradeBroadcastReceiver.saveData");
            PreferencesHelper.defaultInstance().applyString(Upgrade.ReqAppVersion, currentAppVersion);
            PreferencesHelper.defaultInstance().applyString(Upgrade.ReqAppVersionCurrent, "");
        }
    }
}

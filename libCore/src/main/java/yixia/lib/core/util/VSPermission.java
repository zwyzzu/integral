package yixia.lib.core.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.HashMap;

import yixia.lib.core.R;

/**
 * Created by zhangwy on 2018/6/5 下午6:13.
 * Updated by zhangwy on 2018/6/5 下午6:13.
 * Description 权限判断，申请
 */
@SuppressWarnings("unused")
public class VSPermission {
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context == null || Util.isEmpty(permissions)
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (TextUtils.isEmpty(permission))
                continue;
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermission(Context context, String permission) {
        return context == null || TextUtils.isEmpty(permission)
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean applyPermission(final Activity activity, final int requestCode,
                                          HashMap<String, String> permissions) {
        if (activity == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || Util.isEmpty(permissions) || Util.isEmpty(permissions.keySet())) {
            return false;
        }

        HashMap<String, String> noPermissions = new HashMap<>();
        HashMap<String, String> showDialogPermissions = new HashMap<>();
        for (String permission : permissions.keySet()) {
            if (TextUtils.isEmpty(permission))
                continue;
            if (!hasPermission(activity, permission)) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    showDialogPermissions.put(permission, permissions.get(permission));
                } else {
                    noPermissions.put(permission, permissions.get(permission));
                }
            }
        }
        if (!Util.isEmpty(showDialogPermissions)) {
            showDialogPermissions.putAll(noPermissions);
            String permissionString = Util.array2Strings('\n', showDialogPermissions.values());
            Dialog dialog = WindowUtil.createAlertDialog(activity, Device.App.getAppName(activity),
                    activity.getString(R.string.permission_tip_message, permissionString),
                    activity.getString(R.string.permission_to_setting),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package",
                                    Device.App.getPackageName(activity), null));
                            activity.startActivityForResult(intent, requestCode);
                        }
                    },
                    activity.getString(android.R.string.cancel), null);
            if (dialog != null) {
                dialog.show();
            }
            return true;
        }
        if (!Util.isEmpty(noPermissions)) {
            activity.requestPermissions(noPermissions.keySet()
                    .toArray(new String[noPermissions.size()]), requestCode);
            return true;
        }
        return false;
    }
}

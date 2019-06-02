package yixia.lib.core.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import yixia.lib.core.R;

/**
 * Created by zhangwy on 2018/6/5 下午6:13.
 * Updated by zhangwy on 2018/6/5 下午6:13.
 * Description 权限判断，申请
 */
@SuppressWarnings("unused")
public class VSPermission {

    public static VSPermission newInstance() {
        return newInstance(null, null);
    }

    public static VSPermission newInstance(Activity activity) {
        return newInstance(activity, null);
    }

    public static VSPermission newInstance(Activity activity, HashMap<String, String> permissions) {
        return new VSPermission(activity, permissions);
    }

    private Activity activity;
    private HashMap<String, String> permissions = new HashMap<>();

    private VSPermission(Activity activity, HashMap<String, String> permissions) {
        this.activity = activity;
        if (!Util.isEmpty(permissions)) {
            this.permissions.putAll(permissions);
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context == null || Util.isEmpty(permissions)) {
            return true;
        }
        for (String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPermission(Context context, String permission) {
        if (context == null || TextUtils.isEmpty(permission)) {
            return true;
        }
        try {
            return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    public void requestPermission(final int requestCode, String permission, String message) {
        if (this.activity == null || TextUtils.isEmpty(permission) || TextUtils.isEmpty(message)) {
            return;
        }
        if (!this.permissions.containsKey(permission)) {
            this.permissions.put(permission, message);
        }
        if (this.hasPermission(this.activity, permission)) {
            return;
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(permission, message);
            this.showDialog(requestCode, hashMap);
        } else {
            ActivityCompat.requestPermissions(this.activity, new String[]{permission}, requestCode);
        }
    }

    public void requestPermission(int requestCode) {
        this.requestPermissions(requestCode, this.permissions);
    }

    public void requestPermissions(int requestCode, HashMap<String, String> permissions) {
        if (this.activity == null || Util.isEmpty(permissions) || Util.isEmpty(permissions.entrySet())) {
            return;
        }

        HashMap<String, String> noPermissions = new HashMap<>();
        HashMap<String, String> showDialogPermissions = new HashMap<>();

        for (Map.Entry<String, String> entry : permissions.entrySet()) {
            if (entry == null) {
                continue;
            }
            String permission = entry.getKey();
            String message = entry.getValue();
            if (TextUtils.isEmpty(permission) || TextUtils.isEmpty(message)) {
                continue;
            }
            if (!this.permissions.containsKey(permission)) {
                this.permissions.put(permission, message);
            }
            if (!this.hasPermission(this.activity, permission)) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, permission)) {
                    showDialogPermissions.put(permission, message);
                } else {
                    noPermissions.put(permission, message);
                }
            }
        }

        if (!Util.isEmpty(showDialogPermissions)) {
            showDialogPermissions.putAll(noPermissions);
            this.showDialog(requestCode, showDialogPermissions);
        } else if (!Util.isEmpty(noPermissions)) {
            this.requestPermissions(noPermissions, requestCode);
        }
    }

    private void showDialog(int requestCode, HashMap<String, String> permissions) {
        if (Util.isEmpty(permissions)) {
            return;
        }
        String permissionString = Util.array2Strings('\n', permissions.values());
        permissionString = this.activity.getString(R.string.permission_tip_message, permissionString);
        String button = this.activity.getString(R.string.permission_to_setting);
        Dialog dialog = WindowUtil.createAlertDialog(this.activity, Device.App.getAppName(this.activity), permissionString,
                button, (dialog1, which) -> requestPermissions(permissions, requestCode),
                this.activity.getString(android.R.string.cancel), null);
        if (dialog != null) {
            dialog.show();
        }
    }

    private void requestPermissions(HashMap<String, String> permissions, int requestCode) {
        if (Util.isEmpty(permissions)) {
            return;
        }
        ActivityCompat.requestPermissions(this.activity, permissions.keySet().toArray(new String[0]), requestCode);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (this.activity == null || Util.isEmpty(this.permissions) || Util.isEmpty(this.permissions.entrySet())) {
            return;
        }
        HashMap<String, String> noPermissions = new HashMap<>();
        for (int i = 0; i < permissions.length && i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            String permission = permissions[i];
            if (TextUtils.isEmpty(permission)) {
                continue;
            }
            if (this.permissions.containsKey(permission)) {
                noPermissions.put(permission, this.permissions.get(permission));
            }
        }

        if (Util.isEmpty(noPermissions))
            return;
        String permissionString = Util.array2Strings('\n', noPermissions.values());
        Dialog dialog = WindowUtil.createAlertDialog(this.activity, Device.App.getAppName(this.activity),
                this.activity.getString(R.string.permission_tip_message, permissionString), this.activity.getString(R.string.permission_to_setting),
                (dialog1, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", Device.App.getPackageName(activity), null));
                    activity.startActivityForResult(intent, requestCode);
                }, this.activity.getString(android.R.string.cancel), null);
        if (dialog != null) {
            dialog.show();
        }
    }

    public interface OnPermissionCallback {
    }
}

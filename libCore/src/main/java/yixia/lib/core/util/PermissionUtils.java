package yixia.lib.core.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoliangtai on 17/3/16.
 */

public class PermissionUtils {

    private static volatile PermissionUtils instance;
    public static final int REQUEST_STATUS_CODE = 10086;
    //必须要申请的权限,可通过setMustRequirePermission 改变
    public String[] permissionGroupSort = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    //    private PermissionCallbacks callbacks;
    private ArrayList<String> currentHandlerDenyArray;

    private PermissionUtils() {
    }

    public static PermissionUtils getInstance() {
        if (null == instance) {
            synchronized (PermissionUtils.class) {
                if (null == instance)
                    instance = new PermissionUtils();
            }
        }
        return instance;
    }

    /**
     * 1.先设置必须的权限组,这些权限必须要在AndroidManifest.xml中声明
     *
     * @param mustPermissionsArray
     */
    public void setMustRequirePermission(String[] mustPermissionsArray) {
        permissionGroupSort = mustPermissionsArray;
    }

    /**
     * 2. 获取所有必须权限中还未被允许的权限
     *
     * @param activity
     * @param callback
     */
    public void checkAndRequestAllPermissions(final Activity activity,
                                              PermissionCallbacks callback) {
        if (activity == null || callback == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> denidArray = new ArrayList<>();
            for (String permission : permissionGroupSort) {
                if (isPermissionDenyed(activity, permission)) {
                    Log.d("permission_deny", permission);
                    denidArray.add(permission);
                }
            }
            if (denidArray.size() > 0) {
                currentHandlerDenyArray = new ArrayList<>();
                currentHandlerDenyArray.addAll(denidArray);
                String[] denidPermissions = currentHandlerDenyArray
                        .toArray(new String[currentHandlerDenyArray.size()]);
                requestPermissions(activity, denidPermissions);
            } else {
                callback.onPermissionsGranted(true);
            }
        } else {
            callback.onPermissionsGranted(true);
        }
    }

    public void checkAllPermissions(Activity activity, PermissionCallbacks callbacks) {
        if (null == activity || null == callbacks)
            return;
        ArrayList<String> denidArray = new ArrayList<>();
        for (String permission : permissionGroupSort) {
            if (isPermissionDenyed(activity, permission)) {
                denidArray.add(permission);
            }
        }
        if (denidArray.size() > 0) {
            callbacks.onPermissionsDeniedForever(denidArray.toArray(new String[denidArray.size()]));
        } else {
            callbacks.onPermissionsGranted(true);
        }
    }

    public ArrayList<String> getDeniedPermissions(Activity activity, List<String> permissions) {
        if (null == activity) return null;
        if (permissions == null || permissions.isEmpty()) {
            return null;
        }
        ArrayList<String> deniedArray = new ArrayList<>();
        for (String permission : permissionGroupSort) {
            if (isPermissionDenyed(activity, permission)) {
                deniedArray.add(permission);
            }
        }
        return deniedArray;
    }



    public interface PermissionCallbacks {

        /**
         * 所有要求的权限都被允许
         */
        void onPermissionsGranted(boolean hasPermissionAlready);

        /**
         * 监测到有权限还没被允许
         *
         * @param permissions
         */
        void onPermissionsDeniedForever(String[] permissions);

    }

    /**
     * 获取当前处理的权限
     *
     * @return
     */
    public ArrayList<String> getCurrentHandlerDenyArray() {
        return currentHandlerDenyArray;
    }

    public boolean isPermissionDenyed(Activity activity, String permission) {
        int grantCode = ActivityCompat.checkSelfPermission(activity, permission);
        return grantCode == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermissions(Activity activity, String[] permissions) {
        Log.d("permission_deny_handler", permissions[0]);
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_STATUS_CODE);
    }
}

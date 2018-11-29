package yixia.lib.core.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

import yixia.lib.core.base.Constant;

/**
 * Created by liutao on 5/23/16.
 */
public class DeviceInfoUtils {
    private static final String TAG = DeviceInfoUtils.class.getSimpleName();
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static int mNotchType;

    public static void init(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                mScreenWidth = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                mScreenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                mScreenWidth = realSize.x;
                mScreenHeight = realSize.y;
            } catch (Exception ignored) {

            }
        }
        Log.d(TAG, "Screen width: " + mScreenWidth + ", screen height: " + mScreenHeight);
        if (Rom.isOppo()) {
            if (DeviceInfoUtils.hasNotchInScreenAtOppo(context)) {
                mNotchType = Constant.NOTCH_TYPE_OPPO;
            } else {
                mNotchType = Constant.NOTCH_TYPE_NORMAL;
            }
        } else if (Rom.isVivo()) {
            if (DeviceInfoUtils.hasNotchInScreenAtVoio(context,
                    DeviceInfoUtils.NOTCH_IN_SCREEN_VOIO)) {
                mNotchType = Constant.NOTCH_TYPE_VIVO;
            } else {
                mNotchType = Constant.NOTCH_TYPE_NORMAL;
            }
        } else if (Rom.isEmui()) {
            if (DeviceInfoUtils.hasNotchInScreen(context)) {
                mNotchType = Constant.NOTCH_TYPE_HW;
            } else {
                mNotchType = Constant.NOTCH_TYPE_NORMAL;
            }
        } else {
            mNotchType = Constant.NOTCH_TYPE_NORMAL;
        }
    }

    public static int getScreenWidth() {
        return mScreenWidth;
    }

    public static int getScreenHeight() {
        return mScreenHeight;
    }

    public static int getNotchType() {
        return mNotchType;
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    public static int getScreenRawHeight(Context context) {
        int dpi = 0;
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getScreenRawHeight(context);

        int contentHeight = mScreenHeight;

        return totalHeight - contentHeight;
    }

    /**
     * 判断是否为华为刘海屏
     *
     * @param context
     * @return
     */
    public static boolean hasNotchInScreen(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }

    //获取华为刘海的高宽
    public static int[] getNotchSize(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = hwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(hwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "getNotchSize Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 是否为oppo刘海屏
     *
     * @param context
     * @return
     */
    public static boolean hasNotchInScreenAtOppo(Context context) {
        return context.getPackageManager()
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * vido刘海屏的相关判断
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;  //是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;    //是否有圆角

    public static boolean hasNotchInScreenAtVoio(Context context, int type) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class ftFeature = cl.loadClass("com.util.FtFeature");
            Method get = ftFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(ftFeature, type);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInScreen Exception");
        } finally {
            return ret;
        }
    }

    /**
     * oppo vivo和部分小米手机的刘海高度不会高于状态栏高度
     * 所以我们这个时候用状态栏高度即可
     * 还有一部分小米手机的刘海 可以通过android P的API拿到
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(
                "status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;

    }
}

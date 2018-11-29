package yixia.lib.core.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * Author: zhangwy(张维亚)
 * Email:  zhangweiya@yixia.com
 * 创建时间:2017/5/3 下午3:49
 * 修改时间:2017/5/3 下午3:49
 * Description:
 */
@SuppressWarnings("unused")
public class Screen {

    private static DisplayMetrics getDisplayMetrics(Context ctx) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics;
    }

    /**
     * 获取手机分辨率
     */
    public static String getResolution(Context ctx) {
        if (ctx == null) {
            return "";
        }
        DisplayMetrics metrics = getDisplayMetrics(ctx);
        return metrics.widthPixels + "*" + metrics.heightPixels;
    }

    public static int[] getScreen(Context context) {
        if (context == null) {
            return new int[]{1, 1};
        }
        WindowManager mWindowManager = (WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mDisplay.getRealMetrics(mDisplayMetrics);
        }
        Log.d("Screen", "width = " + mDisplayMetrics.widthPixels);
        Log.d("Screen", "height = " + mDisplayMetrics.heightPixels);
        return new int[]{mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
    }

    public static int getScreenWidth(Context ctx) {
        if (ctx == null) {
            return 0;
        }
        return getDisplayMetrics(ctx).widthPixels;
    }

    public static int getScreenHeight(Context ctx) {
        if (ctx == null) {
            return 0;
        }
        return getDisplayMetrics(ctx).heightPixels;
    }

    public static float getScreenDensity(Context ctx) {
        if (ctx == null) {
            return 1;
        }
        return getDisplayMetrics(ctx).density;
    }

    public static float getScreenScaledDensity(Context ctx) {
        if (ctx == null) {
            return 1;
        }
        DisplayMetrics dm = getDisplayMetrics(ctx);
        return dm.scaledDensity;
    }

    public static float getScreenDpi(Context ctx) {
        if (ctx == null) {
            return 1;
        }
        return getDisplayMetrics(ctx).densityDpi;
    }

    /**
     * According to the resolution of the phone from the dp unit will become a px (pixels)
     */
    public static int dip2px(Context ctx, int dip) {
        float density = getScreenDensity(ctx);
        return (int) (dip * density + 0.5f);
    }

    /**
     * According to the resolution of the phone from the dp unit will become a px (pixels)
     */
    public static float dip2px(Context ctx, float dip) {
        float density = getScreenDensity(ctx);
        return (dip * density + 0.5f);
    }

    /**
     * Turn from the units of px (pixels) become dp according to phone resolution
     */
    public static int px2dip(Context ctx, float px) {
        float density = getScreenDensity(ctx);
        return (int) (px / density + 0.5f);
    }

    public static int px2sp(Context ctx, float px) {
        float scale = getScreenScaledDensity(ctx);
        return (int) (px / scale + 0.5f);
    }

    public static int sp2px(Context ctx, int sp) {
        float scale = getScreenScaledDensity(ctx);
        return (int) (sp * scale + 0.5f);
    }

    /**
     * 判断设备是否为大尺寸屏幕
     */
    public static boolean isScreenSizeLarge(Context context) {
        if (context == null) {
            return false;
        }
        return (context.getResources().getConfiguration().screenLayout & Configuration
                .SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isLandscape(Context context) {
        if (context == null) {
            return false;
        }
        return context.getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_LANDSCAPE; //获取屏幕方向
    }
}
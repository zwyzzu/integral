package yixia.lib.core.util;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by liutao on 5/6/16.
 */
public class DensityUtils {

    public static float sp2px(float sp) {
        float scale = Resources.getSystem().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static float px2sp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    public static float px2dp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  (int) (dp * scale + 0.5f);
    }

    public static float dp2px(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}

package yixia.lib.core.util;

import android.view.View;

/**
 * Created by liutao on 7/8/16.
 */
public class ViewUtils {
    /**
     * Determines if given points are inside view
     *
     * @param x    - mX coordinate of point
     * @param y    - mY coordinate of point
     * @param view - view object to compare
     * @return true if the points are within view bounds, false otherwise
     */
    public static boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //point is inside view bounds
        return (x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()));
    }

    public static int[] getLocationOnScreen(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }
}

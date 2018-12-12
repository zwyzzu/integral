package yixia.lib.core.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Locale;

import yixia.lib.core.R;

/**
 * Created by liutao on 14/06/2018.
 */
public class AppUtils {

    private static final String TAG_FAKE_STATUS_BAR_VIEW = "tag_fake_status_bar_view";
    private static final String TAG_MARGIN_ADDED = "tag_margin_added";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColorAndroidL(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View mContentChild = mContentView.getChildAt(0);
        int statusBarHeight = getStatusBarHeight(activity.getResources());
        removeFakeStatusBarViewIfExist(activity);
        addFakeStatusBarView(activity, statusColor, statusBarHeight);
        addMarginTopToContentChild(mContentChild, statusBarHeight);
        if (mContentChild != null) {
            ViewCompat.setFitsSystemWindows(mContentChild, false);
        }
        int actionBarId = activity.getResources().getIdentifier("action_bar", "id",
                activity.getPackageName());
        View view = activity.findViewById(actionBarId);
        if (view != null) {
            TypedValue typedValue = new TypedValue();
            if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
                int actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data,
                        activity.getResources().getDisplayMetrics());
                setContentTopPadding(activity, actionBarHeight);
            }
        }
    }

    public static int getStatusBarHeight(Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static void removeFakeStatusBarViewIfExist(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup mDecorView = (ViewGroup) window.getDecorView();

        View fakeView = mDecorView.findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
        if (fakeView != null) {
            mDecorView.removeView(fakeView);
        }
    }

    private static View addFakeStatusBarView(Activity activity, int statusBarColor,
                                             int statusBarHeight) {
        Window window = activity.getWindow();
        ViewGroup mDecorView = (ViewGroup) window.getDecorView();

        View mStatusBarView = new View(activity);
        FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
        layoutParams.gravity = Gravity.TOP;
        mStatusBarView.setLayoutParams(layoutParams);
        mStatusBarView.setBackgroundColor(statusBarColor);
        mStatusBarView.setTag(TAG_FAKE_STATUS_BAR_VIEW);

        mDecorView.addView(mStatusBarView);
        return mStatusBarView;
    }

    private static void addMarginTopToContentChild(View mContentChild, int statusBarHeight) {
        if (mContentChild == null) {
            return;
        }
        if (!TAG_MARGIN_ADDED.equals(mContentChild.getTag())) {
            FrameLayout.LayoutParams lp
                    = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
            lp.topMargin += statusBarHeight;
            mContentChild.setLayoutParams(lp);
            mContentChild.setTag(TAG_MARGIN_ADDED);
        }
    }

    public static void setContentTopPadding(Activity activity, int padding) {
        ViewGroup mContentView = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        mContentView.setPadding(0, padding, 0, 0);
    }

}

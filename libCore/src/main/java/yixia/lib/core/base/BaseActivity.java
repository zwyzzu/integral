package yixia.lib.core.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import yixia.lib.core.BuildConfig;
import yixia.lib.core.util.AppUtils;
import yixia.lib.core.util.Device;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Util;

/**
 */
@SuppressWarnings("unused")
public class BaseActivity extends AppCompatActivity {
    private static ArrayList<Activity> activityArray = new ArrayList<>();
    private View mDecorView;
    private static Handler handler = new Handler();
    private static final int RESULTCODE_CLOSE = -Integer.MAX_VALUE;
    private boolean destroyed = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDecorView = getWindow().getDecorView();
        this.pushActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.destroyed = true;
        this.popActivity();
    }

    public void addFragment(@NonNull Fragment fragment, int frameId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commitAllowingStateLoss();
    }

    public void removeFragment(@NonNull Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }

    public void addFragment(@NonNull Fragment fragment, int frameId, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment, tag);
        transaction.commitAllowingStateLoss();
    }

    public void screenOn() {
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void setFullscreenFlag() {
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    // This snippet hides the system bars.
    public void hideSystemUI() {
        if (mDecorView == null) {
            return;
        }
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public void setStatusBarColor(int color) {
        int version = Device.OS.getAndroidOSVersion();
        if (version >= Build.VERSION_CODES.KITKAT && version < Build.VERSION_CODES.LOLLIPOP) {
            AppUtils.setStatusBarColor(this, color);
        } else if (version >= Build.VERSION_CODES.LOLLIPOP) {
            AppUtils.setStatusBarColorAndroidL(this, color);
        }
    }

    // TODO 其他时间requestCode不得使用65535
    @Override
    public void startActivity(Intent intent) {
        super.startActivityForResult(intent, 65535);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULTCODE_CLOSE) {
            this.closeForResult();
        }
    }

    protected final void close() {
        if (Util.isEmpty(activityArray) || activityArray.size() <= 1) {
            this.closeForResult();
        } else {
            this.closeForActivities();
        }
    }

    // TODO 其他时间resultCode不得使用@RESULTCODE_CLOSE
    protected final void closeForResult() {
        setResult(RESULTCODE_CLOSE);
        this.finish();
    }

    protected final void closeForActivities() {
        if (Util.isEmpty(activityArray)) {
            this.closeForResult();
            return;
        }
        this.showMessage(false, "size:" + activityArray.size());
        try {
            Activity activity;
            while ((activity = activityArray.remove(activityArray.size() - 1)) != null) {
                try {
                    activity.finish();
                    Logger.d( "close " + activity.getClass().getSimpleName());
                } catch (Throwable e) {
                    Logger.d("close", e);
                }
                if (Util.isEmpty(activityArray)) {
                    break;
                }
            }
        } catch (Throwable e) {
            Logger.d("close", e);
        }
    }

    private void pushActivity() {
        activityArray.add(this);
    }

    private void popActivity() {
        activityArray.remove(this);
    }

    protected void postDelayed(Runnable runnable, long delayMillis) {
        if (handler == null || runnable == null) {
            return;
        }
        handler.postDelayed(runnable, delayMillis);
    }

    protected void showMessage(boolean always, int resId) {
        this.showMessage(always, false, resId);
    }

    protected void showMessage(boolean always, boolean center, int resId) {
        if (always || TextUtils.equals(BuildConfig.BUILD_TYPE, "debug")) {
            Toast toast = Toast.makeText(this.getApplicationContext(), resId, Toast.LENGTH_LONG);
            if (center) {
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.show();
        }
        Logger.d(getString(resId));
    }

    protected void showMessage(boolean always, String message) {
        this.showMessage(always, false, message);
    }

    protected void showMessage(boolean always, boolean center, String message) {
        if (always || TextUtils.equals(BuildConfig.BUILD_TYPE, "debug")) {
            Toast toast = Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG);
            if (center) {
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.show();
        }
        Logger.d(message);
    }

    public boolean isDestroyed() {
        if (Device.OS.getAndroidOSVersion() >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return super.isDestroyed();
        }
        return this.destroyed;
    }
}

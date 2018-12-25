package yixia.lib.core.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import yixia.lib.core.BuildConfig;
import yixia.lib.core.util.Logger;

public abstract class BaseFragment extends Fragment {

    protected Activity mActivity;

    protected Context mContext;

    protected View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mRootView = view;
        init(view, savedInstanceState);
    }

    protected abstract int getLayoutId();

    protected abstract void init(View view, Bundle saveInstanceState);

    protected final void addToolbar(@IdRes int toolbarId) {
        AppCompatActivity activity = getAppCompatActivity();
        if (activity == null) {
            return;
        }
        Toolbar toolbar = this.mRootView.findViewById(toolbarId);
        activity.setSupportActionBar(toolbar);
    }

    protected final ActionBar getSupportActionBar() {
        AppCompatActivity activity = getAppCompatActivity();
        if (activity == null) {
            return null;
        }
        return activity.getSupportActionBar();
    }

    protected final AppCompatActivity getAppCompatActivity() {
        Activity activity = getActivity();
        if (activity == null || !(activity instanceof AppCompatActivity)) {
            return null;
        }
        return (AppCompatActivity) activity;
    }

    protected void showMessage(boolean always, int resId) {
        this.showMessage(always, false, resId);
    }

    protected void showMessage(boolean always, boolean center, int resId) {
        if (always || TextUtils.equals(BuildConfig.BUILD_TYPE, "debug")) {
            Toast toast = Toast.makeText(this.getContext(), resId, Toast.LENGTH_LONG);
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
            Toast toast = Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG);
            if (center) {
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.show();
        }
        Logger.d(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

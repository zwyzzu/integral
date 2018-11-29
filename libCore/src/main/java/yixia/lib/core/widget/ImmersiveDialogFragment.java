package yixia.lib.core.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment2;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;

import com.yixia.thirdparty.lib.viewanimator.ViewAnimator;

import yixia.lib.core.R;


public abstract class ImmersiveDialogFragment extends DialogFragment2 {

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        // Make the dialog non-focusable before showing it
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.EmptyDialogAnimation;
        }
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.showAllowingStateLoss(manager, tag);
        showImmersive(manager);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        if (getDialog().getWindow() == null) {
            return;
        }
        if (getDialog().getWindow().getDecorView() == null) {
            return;
        }
        final View decorView = getDialog().getWindow().getDecorView();
        ViewAnimator.animate(decorView)
                .alpha(0, 1)
                .scaleX(0.7f, 1f)
                .scaleY(0.7f, 1f)
                .duration(400)
                .interpolator(new SpringInterpolator(0.7f))
                .start();
    }

    private void showImmersive(FragmentManager manager) {
        // It is necessary to call executePendingTransactions() on the FragmentManager
        // before hiding the navigation bar, because otherwise getWindow() would raise a
        // NullPointerException since the window was not yet created.
        manager.executePendingTransactions();
        int visi = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        getDialog().getWindow().getDecorView().setSystemUiVisibility(visi);

        // Make the dialogs window focusable again
        getDialog().getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        );
    }

    @Override
    public void dismiss() {
        super.dismissAllowingStateLoss();
    }
}
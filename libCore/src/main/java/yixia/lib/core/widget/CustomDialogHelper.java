package yixia.lib.core.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import yixia.lib.core.base.Constant;

/**
 * Created by zhaoliangtai on 2018/6/14.
 */

public class CustomDialogHelper {

    public static void showCustomDialog(String content, String confirm, String cancel,
                                        boolean isSupportBackBtn,
                                        Fragment targetFragment,
                                        FragmentManager fragmentManager,
                                        OnCancelButtonClickListener cancelButtonClickListener,
                                        OnPositiveButtonClickListener positiveButtonClickListener) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.EXTRA_DIALOG_FRAGMENT_BACK_PRESSED, isSupportBackBtn);
        AlertDialogFragment fragment = AlertDialogFragment.newInstance(
                content,
                confirm,
                cancel,
                bundle);
        if (targetFragment != null) {
            fragment.setTargetFragment(targetFragment, 0);
        }
        fragment.setDialogWidthRatio(AlertDialogFragment.RATIO_80);
        fragment.show(fragmentManager, "alert_dialog");
        fragment.setOnCancelButtonClickListener(cancelButtonClickListener);
        fragment.setOnPositionButtonClickListener(positiveButtonClickListener);
    }
}

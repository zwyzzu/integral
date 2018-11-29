package com.yixia.widget.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.yixia.widget.R;

import yixia.lib.core.util.SizeConverter;
import yixia.lib.core.widget.ImmersiveDialogFragment;

/**
 * Created by liutao on 7/26/16.
 */
public class ProcessingDialogFragment extends ImmersiveDialogFragment {

    public interface OnConfirmListener {
        void onConfirm();
    }

    TextView mProgress;
    private OnDialogCreatedListener mOnDialogCreatedListener;
    private ProcessingDialogFragment.OnConfirmListener mOnConfirmListener;
    private DialogInterface.OnCancelListener mOnCancleListener;
    private RotateAnimation animation;
    boolean mIsSize = false;

    public void setOnDialogCreatedListener(OnDialogCreatedListener listener) {
        mOnDialogCreatedListener = listener;
    }


    public void setOnConfirmListener(OnConfirmListener listener) {
        mOnConfirmListener = listener;
    }

    public void setOnCancleListener(DialogInterface.OnCancelListener listener) {
        mOnCancleListener = listener;
    }

    public void setIsSize(boolean mIsSize) {
        this.mIsSize = mIsSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        animation = new RotateAnimation(0f,
//                359f, Animation.RELATIVE_TO_SELF,
//                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        animation.setFillAfter(true); // 设置保持动画最后的状态
//        animation.setDuration(3000); // 设置动画时间
//        animation.setRepeatCount(-1); // 重复次数 -1无限次
//        animation.setInterpolator(new LinearInterpolator()); // 不停顿
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getDialog().setCanceledOnTouchOutside(true);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme);

        View view = inflater.inflate(R.layout.common_processing_dialog_frg, container);
//        mProgressWheel = view.findViewById(R.id.pw_spinner);
        mProgress = view.findViewById(R.id.tv_progress);
        if (mContent != null) {
            mProgress.setText(mContent);
        } else {
            setProgress(0, 0);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mOnDialogCreatedListener != null) {
            mOnDialogCreatedListener.onDialogCreated();
        }
    }

    String mContent = null;

    public void setContent(String content) {
        mContent = content;
        if (mProgress != null) {
            mProgress.setText(mContent);
        }
    }

    public void setProgress(int soFarBytes, int totalBytes) {
        float percent = 0;
        if (soFarBytes > 0 && totalBytes > 0) {
            percent = soFarBytes / (float) totalBytes;
        }
        if (mProgress == null) {
            return;
        }
        if (mIsSize) {
            mProgress.setText(getString(R.string.dialog_loading) + (int) (percent * 100) + "%"
                    + "\n" + SizeConverter.BTrim.convert(soFarBytes)
                    + "/" + SizeConverter.BTrim.convert(totalBytes));

        } else {
            mProgress.setText(getString(R.string.dialog_loading) + (int) (percent * 100) + "%");

        }

//        mProgressWheel.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        try {
            super.dismissAllowingStateLoss();
            if (mOnDialogCreatedListener != null) {
                mOnDialogCreatedListener = null;
            }
            if (mOnCancleListener != null) {
                mOnCancleListener = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mOnCancleListener != null) {
            mOnCancleListener.onCancel(dialog);
        }
    }

}

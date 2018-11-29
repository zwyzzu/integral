package yixia.lib.core.widget;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import yixia.lib.core.R;
import yixia.lib.core.util.DeviceInfoUtils;


/**
 * Created by liutao on 7/5/16.
 */
public class AlertDialogFragment extends ImmersiveDialogFragment implements View.OnClickListener {

    public static final float RATIO_60 = 0.60f;
    public static final float RATIO_68 = 0.68f;
    public static final float RATIO_80 = 0.80f;

    private static final String EXTRA_CONTENT = "content";
    private static final String EXTRA_CONFIRM = "confirm";
    private static final String EXTRA_CANCEL = "cancel";
    private static final String EXTRA_BUNDLE = "bundle";
    private static final String EXTRA_TIP_STATUS = "isTipStatus";
    private static final String EXTRA_BACK_PRESSED = "isRespondBackBtn";
    private OnPositiveButtonClickListener mListener;
    private OnCancelButtonClickListener mCancelListener;
    private boolean isSupportBackBtn = true;
    private boolean mIsTipSTATUS = false;
    private float mWidthRatio = RATIO_80;

    public void setOnPositionButtonClickListener(OnPositiveButtonClickListener listener) {
        mListener = listener;
    }

    public void setOnCancelButtonClickListener(OnCancelButtonClickListener listener) {
        mCancelListener = listener;
    }


    public static AlertDialogFragment newInstance(String ct, String cf, String c, Bundle d) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_CONTENT, ct);
        args.putString(EXTRA_CONFIRM, cf);
        args.putString(EXTRA_CANCEL, c);
        args.putBundle(EXTRA_BUNDLE, d);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle save) {
        return inflater.inflate(R.layout.common_dialog_frag, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
        TextView alertContentTextView = view.findViewById(R.id.tv_alert_content);
        TextView confirmTextView = view.findViewById(R.id.tv_confirm);
        TextView cancelTextView = view.findViewById(R.id.tv_cancel);
        confirmTextView.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
        if (getArguments() == null) {
            return;
        }
        if (savedInstanceState != null) {
            mWidthRatio = savedInstanceState.getFloat("sRatio");
        }
        String content = getArguments().getString(EXTRA_CONTENT);
        String confirm = getArguments().getString(EXTRA_CONFIRM);
        String cancel = getArguments().getString(EXTRA_CANCEL);
        mIsTipSTATUS = getArguments().getBoolean(EXTRA_TIP_STATUS, false);
        Bundle d = getArguments().getBundle(EXTRA_BUNDLE);
        if (d != null) {
            isSupportBackBtn = d.getBoolean(EXTRA_BACK_PRESSED);
        }
        alertContentTextView.setText(content);
        confirmTextView.setText(confirm);
        cancelTextView.setText(cancel);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (isSupportBackBtn) dismiss();
                    return true;
                }
                return false;
            }
        });
        if (cancel == null) {
            cancelTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("sRatio", mWidthRatio);
    }

    @Override
    public void onStart() {
        int width = DeviceInfoUtils.getScreenWidth();
        int hg = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout((int) (width * mWidthRatio), hg);
        super.onStart();
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.tv_confirm) {
            if (mIsTipSTATUS) {
                cancel();
            } else {
                confirm();
            }

        } else if (i == R.id.tv_cancel) {
            if (mIsTipSTATUS) {
                confirm();
            } else {
                cancel();
            }

        } else {
        }
    }

    private void confirm() {
        OnPositiveButtonClickListener listener;
        try {
            listener = (OnPositiveButtonClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            listener = null;
        }
        if (listener != null) {
            mListener = listener;
        }
        Bundle data = getArguments().getBundle(EXTRA_BUNDLE);
        if (mListener != null) {
            mListener.onPositiveButtonClicked(data);
        }
        dismiss();
    }

    private void cancel() {
        OnCancelButtonClickListener listener;
        try {
            listener = (OnCancelButtonClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            listener = null;
        }
        if (listener != null) {
            mCancelListener = listener;
        }
        Bundle data = getArguments().getBundle(EXTRA_BUNDLE);
        if (mCancelListener != null) {
            mCancelListener.onCancelButtonClicked(data);
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (null != mListener) {
            mListener = null;
        }
        if (null != mCancelListener) {
            mCancelListener = null;
        }
    }

    public void setDialogWidthRatio(float ratio) {
        mWidthRatio = ratio;
    }
}

package com.yixia.widget.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yixia.widget.R;

import java.text.DecimalFormat;

/**
 * Created by zhangjd on 2018/8/21
 */
public class NewProcessingDialogFragment extends ProcessingDialogFragment {

    private TextView currentTV;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getDialog().setCanceledOnTouchOutside(true);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme);

        View view = inflater.inflate(R.layout.common_processing_dialog_frg_new, container);
        mProgress = view.findViewById(R.id.tv_progress);
        currentTV = view.findViewById(R.id.tv_current);
        if (mContent != null) {
            mProgress.setText(mContent);
        } else {
            setProgress(0, 0);
        }
        return view;
    }

    private DecimalFormat df = new DecimalFormat("#.0");

    public void setProgress(int soFarBytes, int totalBytes) {
        float percent = 0;
        if (soFarBytes > 0 && totalBytes > 0) {
            percent = soFarBytes / (float) totalBytes;
        }
        if (mProgress == null) {
            return;
        }
        mProgress.setText(getString(R.string.dialog_controling) + (int) (percent * 100) + "%");
        currentTV.setText(getBytesValues(soFarBytes) + "/" + getBytesValues(totalBytes));
    }

    private String getBytesValues(int bytes) {
        if (bytes < 1024 * 1024) {
            return String.valueOf(bytes / 1024) + "KB";
        } else {
            return df.format(bytes * 1.0f / (1024 * 1024)) + "MB";
        }
    }

    public interface OnProgressListener {
        void onProgress(int soFarBytes, int totalBytes);
    }
}

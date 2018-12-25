package com.codbking.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.codbking.widget.bean.DateType;

import java.text.SimpleDateFormat;
import java.util.Date;

import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Screen;

@SuppressWarnings("unused")
public class DatePickDialog extends Dialog implements OnChangeListener {

    private TextView messageTv;

    private String title;
    private String format;
    private DateType type = DateType.TYPE_ALL;

    //开始时间
    private Date startDate = new Date();
    //年分限制，默认上下5年
    private int yearLimt = 5;

    private OnChangeListener onChangeListener;

    private OnSureListener onSureListener;

    private DatePicker mDatePicker;

    //设置标题
    public void setTitle(String title) {
       this.title=title;
    }

    //设置模式
    public void setType(DateType type) {
        this.type = type;
    }

    //设置选择日期显示格式，设置显示message,不设置不显示message
    public void setMessageFormat(String format) {
        this.format = format;
    }

    //设置开始时间
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    //设置年份限制，上下年份
    public void setYearLimt(int yearLimt) {
        this.yearLimt = yearLimt;
    }

    //设置选择回调
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    //设置点击确定按钮，回调
    public void setOnSureListener(OnSureListener onSureListener) {
        this.onSureListener = onSureListener;
    }

    public DatePickDialog(Context context) {
        super(context, R.style.dialog_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cbk_dialog_pick_time);

        initView();
        initParas();
    }

    private DatePicker getDatePicker() {
        DatePicker picker = new DatePicker(getContext(), type);
        picker.setStartDate(startDate);
        picker.setYearLimt(yearLimt);
        picker.setOnChangeListener(this);
        picker.init();
        return picker;
    }

    private void initView() {
        TextView sure = findViewById(R.id.sure);
        TextView cancel = findViewById(R.id.cancel);
        FrameLayout wheelLayout = findViewById(R.id.wheelLayout);
        TextView titleTv = findViewById(R.id.title);
        messageTv = findViewById(R.id.message);

        mDatePicker = getDatePicker();
        wheelLayout.addView(mDatePicker);

        //setValue
        titleTv.setText(title);

        cancel.setOnClickListener(v -> dismiss());

        sure.setOnClickListener(v -> {
            dismiss();
            if (onSureListener != null) {
                onSureListener.onSure(mDatePicker.getSelectDate());
            }
        });
    }

    private void initParas() {
        if (getWindow() == null)
            return;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = Screen.getScreenWidth(getContext());
        getWindow().setAttributes(params);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onChanged(Date date) {

        if (onChangeListener != null) {
            onChangeListener.onChanged(date);
        }

        if (!TextUtils.isEmpty(format)) {
            String message = "";
            try {
                message = new SimpleDateFormat(format).format(date);
            } catch (Exception e) {
                Logger.d("onChanged", e);
            }
            messageTv.setText(message);
        }
    }

}

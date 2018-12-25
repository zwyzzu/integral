package com.yixia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/12/22 上午11:47.
 * Updated by zhangwy on 2018/12/22 上午11:47.
 * Description
 */
public class VSVerificationLayout extends FrameLayout {

    private int inputId;
    public VSVerificationLayout(@NonNull Context context) {
        super(context);
        this.init(context, null);
    }

    public VSVerificationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public VSVerificationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    @SuppressLint("NewApi")
    public VSVerificationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VSVerificationLayout);
        this.inputId = typedArray.getResourceId(R.styleable.VSVerificationLayout_inputId, -1);
        typedArray.recycle();
    }

    public void setInput(@IdRes int id) {
        this.inputId = id;
    }

    public void verify(Command... commands) {
        if (Util.isEmpty(commands))
            return;
        for (Command command : commands) {
            if (command == null) {
                continue;
            }
            if (command.verify(this.getInput())) {
                //TODO
            }
        }
    }

    private String getInput() {
        TextView input = this.findViewById(this.inputId);
        if (input == null) {
            return "";
        }
        return input.getText().toString();
    }

    public interface Command {
        String hint();

        boolean verify(String text);
    }
}

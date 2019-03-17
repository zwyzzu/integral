package com.yixia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/12/22 上午11:47.
 * Updated by zhangwy on 2018/12/22 上午11:47.
 * Description
 */
@SuppressWarnings("unused")
public class VSVerificationLayout extends FrameLayout implements View.OnFocusChangeListener {

    private int inputId;
    private TextView inputView;
    private TextView hintView;

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
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setInputFocusChanged();
            }
        });
    }

    public void setInput(@IdRes int id) {
        this.inputId = id;
        this.setInputFocusChanged();
    }

    private void setInputFocusChanged() {
        this.addHint();
        if (this.inputId == -1) {
            return;
        }
        if (this.inputView != null) {
            this.inputView.setOnFocusChangeListener(null);
        }
        this.inputView = this.findViewById(this.inputId);
        if (this.inputView == null) {
            return;
        }
        this.inputView.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            this.hintView.setVisibility(GONE);
        } else {
            this.empty();
        }
    }

    public void empty() {
        this.verify(new Command() {
            @Override
            public String hint() {
                return getResources().getString(R.string.hint_empty);
            }

            @Override
            public boolean verify(String text) {
                return TextUtils.isEmpty(text);
            }
        });
    }

    private void addHint() {
        if (this.hintView != null) {
            this.removeView(this.hintView);
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_hint_view, this, false);
        this.hintView = (TextView) view;
        this.addView(this.hintView);
        this.hintView.setVisibility(GONE);
    }

    public void verify(Command... commands) {
        if (Util.isEmpty(commands))
            return;
        for (Command command : commands) {
            if (command == null) {
                continue;
            }
            if (command.verify(this.getInput())) {
                if (this.hintView == null) {
                    return;
                }
                this.hintView.setVisibility(VISIBLE);
                this.hintView.setText(command.hint());
            } else {
                if (this.hintView == null) {
                    return;
                }
                this.hintView.setVisibility(GONE);
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

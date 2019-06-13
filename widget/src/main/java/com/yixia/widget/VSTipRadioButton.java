package com.yixia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by zhangwy on 2019/6/13.
 * Updated by zhangwy on 2019/6/13.
 * Description 带红点提示的radiobutton
 */
@SuppressWarnings("unused")
@SuppressLint("AppCompatCustomView")
public class VSTipRadioButton extends RadioButton {
    private boolean showing = false;
    private Dot mDot;

    public VSTipRadioButton(Context context) {
        super(context);
        this.init();
    }

    public VSTipRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public VSTipRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @SuppressLint("NewApi")
    public VSTipRadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private class Dot {
        int color;
        int radius;
        int marginTop;
        int marginRight;

        private Dot() {
            float density = getContext().getResources().getDisplayMetrics().density;
            radius = (int) (5 * density);
            marginTop = (int) (3 * density);
            marginRight = (int) (3 * density);
            color = Color.RED;
        }

    }

    public boolean showing() {
        return showing;
    }

    private void init() {
        mDot = new Dot();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.onDrawDot(canvas);
    }

    private void onDrawDot(Canvas canvas) {
        if (!this.showing)
            return;
        float cx = getWidth() - mDot.marginRight - mDot.radius;
        float cy = mDot.marginTop + mDot.radius;

        Drawable drawableTop = getCompoundDrawables()[1];
        if (drawableTop != null) {
            int drawableTopWidth = drawableTop.getIntrinsicWidth();
            if (drawableTopWidth > 0) {
                int dotLeft = getWidth() / 2 + drawableTopWidth / 2;
                cx = dotLeft + mDot.radius;
            }
        }

        Paint paint = getPaint();
        int tempColor = paint.getColor();
        paint.setColor(mDot.color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, mDot.radius, paint);

        paint.setColor(tempColor);
    }

    public void show() {
        if (!this.showing) {
            this.showing = true;
            this.invalidate();
        }
    }

    public void hide() {
        if (this.showing) {
            this.showing = false;
            this.invalidate();
        }
    }
}

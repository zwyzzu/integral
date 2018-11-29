package com.yixia.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

/**
 * Created by zhangwy on 2018/6/16 上午10:55.
 * Updated by zhangwy on 2018/6/16 上午10:55.
 * Description 正方形布局
 */
@SuppressWarnings("unused")
public class VSSquareConstraintLayout extends ConstraintLayout {

    public VSSquareConstraintLayout(Context context) {
        super(context);
    }

    public VSSquareConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VSSquareConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();

        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec =
                MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        //设定高是宽的比例
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

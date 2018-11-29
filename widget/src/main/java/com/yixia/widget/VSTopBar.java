package com.yixia.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by zhangwy on 2018/5/29 下午6:39.
 * Updated by zhangwy on 2018/5/29 下午6:39.
 * Description
 */
@SuppressWarnings("unused")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VSTopBar extends FrameLayout {

    private ViewGroup leftHome;
    private ImageView leftImage;
    private TextView leftText;

    private ViewGroup centreHome;
    private TextView centreText;

    private ViewGroup rightHome;
    private ImageView rightImage;
    private TextView rightText;

    public VSTopBar(@NonNull Context context) {
        super(context);
        this.initialize();
    }

    public VSTopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public VSTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    public VSTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initialize();
    }

    private void initialize() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.widget_vs_topbar, this, true);
        this.leftHome = findViewById(R.id.topBar_left);
        this.leftImage = findViewById(R.id.topBar_left_Icon);
        this.leftText = findViewById(R.id.topBar_left_text);

        this.centreHome = findViewById(R.id.topBar_centre);
        this.centreText = findViewById(R.id.topBar_centre_text);

        this.rightHome = findViewById(R.id.topBar_right);
        this.rightImage = findViewById(R.id.topBar_right_Icon);
        this.rightText = findViewById(R.id.topBar_right_text);
    }

    /**
     * Set the visibility state of this view.
     */
    public void setLeftVisibility(boolean show) {
        this.leftHome.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    /**
     * 设置topBar左侧图片
     *
     * @param resId    图片资源ID
     * @param hideText 是否隐藏文本
     */
    public void setLeftImage(@DrawableRes int resId, boolean hideText) {
        this.setLeftVisibility(true);
        this.leftImage.setVisibility(VISIBLE);
        this.leftText.setVisibility(hideText ? GONE : VISIBLE);
        this.leftImage.setImageResource(resId);
    }

    /**
     * 设置topBar左侧文本
     *
     * @param text      文本
     * @param hideImage 是否隐藏图片
     */
    public void setLeftText(String text, boolean hideImage) {
        this.setLeftVisibility(true);
        this.leftText.setVisibility(VISIBLE);
        this.leftImage.setVisibility(hideImage ? GONE : VISIBLE);
        this.leftText.setText(text);
    }

    /**
     * 设置topBar左侧文本
     *
     * @param resId     文本资源ID
     * @param hideImage 是否隐藏图片
     */
    public void setLeftText(@StringRes int resId, boolean hideImage) {
        this.setLeftVisibility(true);
        this.leftText.setVisibility(VISIBLE);
        this.leftImage.setVisibility(hideImage ? GONE : VISIBLE);
        this.leftText.setText(resId);
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色资源ID
     */
    public void setLeftTextColor(@ColorInt int color) {
        this.leftText.setTextColor(color);
    }

    /**
     * 设置字体颜色
     *
     * @param colors 颜色
     */
    public void setLeftTextColor(ColorStateList colors) {
        this.leftText.setTextColor(colors);
    }

    /**
     * Set the visibility state of this view.
     */
    public void setCentreVisibility(boolean show) {
        this.centreHome.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    /**
     * 设置topBar中间文本
     *
     * @param text 文本
     */
    public void setCentreText(String text) {
        if (this.centreText != null) {
            this.setCentreVisibility(true);
            this.centreText.setVisibility(VISIBLE);
            this.centreText.setText(text);
        }
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色资源ID
     */
    public void setCentreTextColor(@ColorInt int color) {
        this.centreText.setTextColor(color);
    }

    /**
     * 设置字体颜色
     *
     * @param colors 颜色
     */
    public void setCentreTextColor(ColorStateList colors) {
        this.centreText.setTextColor(colors);
    }

    /**
     * 设置topBar中间文本
     *
     * @param resId 文本资源ID
     */
    public void setCentreText(@StringRes int resId) {
        if (this.centreText != null) {
            this.setCentreVisibility(true);
            this.centreText.setVisibility(VISIBLE);
            this.centreText.setText(resId);
        }
    }

    /**
     * 设置topBar中间部分内容
     *
     * @param view 中间部分要显示的View
     */
    public void setCentreContent(View view) {
        this.centreHome.removeAllViews();
        this.centreHome.addView(view);
        if (this.centreText != null) {
            this.centreText.setOnClickListener(null);
            this.centreText = null;
        }
    }

    /**
     * 设置topBar中间部分内容
     *
     * @param resource 中间部分要显示的View 资源ID
     */
    public View setCentreContent(@LayoutRes int resource) {
        try {
            View view = LayoutInflater.from(this.getContext()).inflate(resource, this.centreHome, false);
            this.setCentreContent(view);
            return view;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "setCentreContent", e);
            return null;
        }
    }

    /**
     * Set the visibility state of this view.
     */
    public void setRightVisibility(boolean show) {
        this.rightHome.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    /**
     * 设置topBar右侧图片
     *
     * @param resId    图片资源ID
     * @param hideText 是否隐藏文本
     */
    public void setRightImage(@DrawableRes int resId, boolean hideText) {
        this.setRightVisibility(true);
        this.rightImage.setVisibility(VISIBLE);
        this.rightText.setVisibility(hideText ? GONE : VISIBLE);
        this.rightImage.setImageResource(resId);
    }


    /**
     * 设置topBar右侧文本
     *
     * @param text      文本
     * @param hideImage 是否隐藏图片
     */
    public void setRightText(String text, boolean hideImage) {
        this.setRightVisibility(true);
        this.rightText.setVisibility(VISIBLE);
        this.rightImage.setVisibility(hideImage ? GONE : VISIBLE);
        if ("".equals(text)) {
            this.rightText.setVisibility(GONE);
        }
        this.rightText.setText(text);
    }

    /**
     * 设置topBar右侧侧文本
     *
     * @param resId     文本资源ID
     * @param hideImage 是否隐藏图片
     */
    public void setRightText(@StringRes int resId, boolean hideImage) {
        this.setRightVisibility(true);
        this.rightText.setVisibility(VISIBLE);
        this.rightImage.setVisibility(hideImage ? GONE : VISIBLE);
        this.rightText.setText(resId);
    }

    /**
     * 设置字体颜色
     *
     * @param color 颜色资源ID
     */
    public void setRightTextColor(@ColorInt int color) {
        this.rightText.setTextColor(color);
    }

    /**
     * 设置字体颜色
     *
     * @param colors 颜色
     */
    public void setRightTextColor(ColorStateList colors) {
        this.rightText.setTextColor(colors);
    }

    public void setOnLeftClickListener(OnClickListener listener) {
        this.leftHome.setOnClickListener(listener);
    }

    public void setOnCentreClickListener(OnClickListener listener) {
        this.centreHome.setOnClickListener(listener);
    }

    public void setOnRightClickListener(OnClickListener listener) {
        this.rightHome.setOnClickListener(listener);
    }

}

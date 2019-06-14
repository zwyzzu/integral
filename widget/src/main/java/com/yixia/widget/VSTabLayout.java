package com.yixia.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/10/20 下午2:28.
 * Updated by zhangwy on 2018/10/20 下午2:28.
 * Description
 */
@SuppressWarnings("unused")
public class VSTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    private static final String TAG = "PagerSlidingTabStrip";

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor};
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private RadioGroup.LayoutParams expandedTabLayoutParams;

    public ViewPager.OnPageChangeListener delegatePageListener;

    private RadioGroup tabsContainer;
    private ViewPager pager;

    private int tabCount = 0;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int dividerPadding = 0;
    private int dividerWidth = 1;
    private int dividerHeight = -1;
    private int tabPadding = 24;
    private int endPadding = 0;
    private int tabDividerPadding = 6;

    private int simplifyDividerWidth = 28;
    private int simplifyDividerHeight = 6;

    private RectF rectF = new RectF(0, 0, 0, 0);

    private int tabTextSize = 12;
    private ColorStateList tabTextColor;
    private int tabTextColorR = R.color.tab_color;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private int tabBackgroundResId = R.drawable.index_tab_bg;

    private boolean specialScreenDensity = false;

    private Locale locale;

    public VSTabLayout(Context context) {
        this(context, null);
    }

    public VSTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VSTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        tabsContainer = new RadioGroup(context);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        tabsContainer.setGravity(Gravity.CENTER);
        addView(tabsContainer);
        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
        endPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, endPadding, dm);

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        a.recycle();
        a = context.obtainStyledAttributes(attrs, R.styleable.VSTabLayout);
        tabTextColor = a.getColorStateList(R.styleable.VSTabLayout_tabItemTextColor);
        tabTextSize = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabItemTextSize, tabTextSize);
        indicatorColor = a.getColor(R.styleable.VSTabLayout_tabIndicatorColor, indicatorColor);
        toCenter = a.getBoolean(R.styleable.VSTabLayout_tabScrollToCenter, toCenter);
        underlineColor = a.getColor(R.styleable.VSTabLayout_tabUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.VSTabLayout_tabDividerColor, dividerColor);
        dividerWidth = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabDividerWidth, dividerWidth);
        dividerHeight = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabDividerHeight, dividerHeight);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabItemPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.VSTabLayout_tabItemBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.VSTabLayout_tabShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.VSTabLayout_tabTextAllCaps, textAllCaps);
        endPadding = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabEndPadding, endPadding);

        int tabWidth = a.getDimensionPixelSize(R.styleable.VSTabLayout_tabItemWidth, -1);
        tabDividerPadding = a.getDimensionPixelOffset(R.styleable.VSTabLayout_tabItemDividerPadding, tabDividerPadding);

        a.recycle();

        simplifyDividerWidth = getResources().getDimensionPixelSize(R.dimen.margin_14);
        simplifyDividerHeight = getResources().getDimensionPixelSize(R.dimen.margin_3);

        specialScreenDensity = specialScreenDensity();

        setBackgroundResource(tabBackgroundResId);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(tabWidth == -1 ? LayoutParams.WRAP_CONTENT : tabWidth, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new RadioGroup.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    private boolean specialScreenDensity() {
        DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
        float density = mDisplayMetrics.density;
        int densityDpi = mDisplayMetrics.densityDpi;
        return density < 1.5 || (density == 1.5 && densityDpi == 240);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void setStringTabs(String... tabs) {
        this.setStringTabs(Util.array2List(tabs));
    }

    public void setStringTabs(List<String> tabs) {
        final List<String> newTabs = tabs == null ? new ArrayList<>() : tabs;
        this.notifyDataSetChanged(new NotifyTabs() {
            @Override
            public int tabCount() {
                return newTabs.size();
            }

            @Override
            public void add(int position) {
                addTab(position, this.getTitle(position));
            }

            private String getTitle(int position) {
                return newTabs.size() < position + 1 ? "" : newTabs.get(position);
            }

            @Override
            public int currentPosition() {
                return 0;
            }
        });
        this.setPosition(0);
    }

    public void setTabs(@StringRes Integer... tabs) {
        this.setTabs(Util.array2List(tabs));
    }

    public void setTabs(List<Integer> tabs) {
        final List<Integer> newTabs = tabs == null ? new ArrayList<>() : tabs;
        this.notifyDataSetChanged(new NotifyTabs() {
            @Override
            public int tabCount() {
                return newTabs.size();
            }

            @Override
            public void add(int position) {
                addTab(position, this.getTitle(position));
            }

            private String getTitle(int position) {
                int titleId = newTabs.size() < position + 1 ? -1 : newTabs.get(position);
                return titleId < 0 ? "" : getResources().getString(titleId);
            }

            @Override
            public int currentPosition() {
                return 0;
            }
        });
        this.setPosition(0);
    }

    public void setViewPager(final ViewPager pager) {
        if (pager == null) {
            return;
        }
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.setOnPageChangeListener(this);
        notifyDataSetChanged(new NotifyTabs() {
            @Override
            public int tabCount() {
                return pager.getAdapter().getCount();
            }

            @Override
            public void add(int position) {
                if (pager.getAdapter() instanceof IconTabProvider) {
                    int icon = ((IconTabProvider) pager.getAdapter()).getPageIconResId(position);
                    addTab(position, icon);
                } else {
                    String title = String.valueOf(pager.getAdapter().getPageTitle(position));
                    addTab(position, title);
                }
            }

            @Override
            public int currentPosition() {
                return pager.getCurrentItem();
            }
        });
    }

    private void notifyDataSetChanged(final NotifyTabs notifyTabs) {
        tabsContainer.removeAllViews();
        if (notifyTabs == null || (this.tabCount = notifyTabs.tabCount()) <= 0) {
            return;
        }

        for (int i = 0; i < tabCount; i++) {
            notifyTabs.add(i);
        }
        updateTabStyles();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @SuppressLint({"NewApi", "ObsoleteSdkInt"})
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                currentPosition = notifyTabs.currentPosition();
                if (tabsContainer.getChildCount() > currentPosition) {
                    refreshTabItem(currentPosition);
                    scrollToChild(currentPosition, 0);
                }
            }
        });
    }

    private void addTab(final int position, String title) {
        RadioButton tab = new RadioButton(getContext());
        tab.setButtonDrawable(new ColorDrawable());
        tab.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tab.setText(title);
        tab.setTextColor(tabTextColor);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
        tab.setTypeface(tabTypeface, tabTypefaceStyle);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addTab(final int position, @DrawableRes int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    public interface OnTabClickListener {
        void onClickTab(int position);
    }

    private OnTabClickListener tabClickListener;
    public void setTabClickListener(OnTabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
    }

    public void addTab(final int position, View tab) {
        tab.setOnClickListener(v -> setPosition(position));

        if (position == tabCount - 1) {
            tab.setPadding(tabPadding, 0, tabPadding + endPadding, 0);
        } else {
            tab.setPadding(tabPadding, 0, tabPadding, 0);
        }
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    public void setTabCount(int count) {
        this.tabCount = count;
    }

    public void setPosition(int position) {
        if (tabClickListener != null) {
            tabClickListener.onClickTab(position);
        }
        if (pager != null) {
            pager.setCurrentItem(position, false);
        } else {
            this.refreshTabItem(position);
            this.currentPosition = position;
            this.updateTabStyles();
        }
    }

    public View getTabViewByIndex(int index) {
        if (index < tabCount) {
            return tabsContainer.getChildAt(index);
        }
        return null;
    }

    @SuppressLint("ObsoleteSdkInt")
    private void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            if (v instanceof FrameLayout) {
                int size = ((FrameLayout) v).getChildCount();
                if (size > 0) {
                    View childView = ((FrameLayout) v).getChildAt(0);
                    if (childView != null && childView instanceof TextView) {
                        v = childView;
                    }
                }
            }

            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setSelected(i == currentPosition);
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);

                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }
    }

    private boolean toCenter = false;

    /**
     * 设置是否当前选中滑动屏幕中央
     *
     * @param center "
     */
    public void setSelectTabToCenter(boolean center) {
        this.toCenter = center;
    }

    private int screenWidth = 0;

    private void scrollToChild(int position, int offset) {
        if (tabCount == 0) {
            return;
        }

        if (screenWidth == 0) {
            screenWidth = getResources().getDisplayMetrics().widthPixels;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }
        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            if (toCenter) {
                View child = tabsContainer.getChildAt(position);
                int childWith = child.getMeasuredWidth();
                int left = child.getLeft();
                smoothScrollTo(left + childWith / 2 - screenWidth / 2, 0);
            } else {
                scrollTo(newScrollX, 0);
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line
        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        // set under line width
        int padding = tabPadding + tabDividerPadding;
        float lineLeft = currentTab.getLeft() + padding;
        float lineRight = currentTab.getRight() - padding;

        // if there is an offset, start interpolating left and right coordinates between current
        // and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            float nextTabLeft = nextTab.getLeft() + padding;
            float nextTabRight = nextTab.getRight() - padding;

            /*最后一个tab需要特殊处理，这里处理滑动的下划线长度，在滑动过程中减去4个tabPadding*/
            if (currentPosition + 1 == tabCount - 1) {
                lineRight = (currentPositionOffset * (nextTabRight - endPadding) + (1f - currentPositionOffset) * lineRight);
            } else {
                lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
            }
            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
        }

        float cornerRadius;
        int dividerBottomPadding = tabDividerPadding * 3;
        if (currentPosition == tabCount - 1) {
            rectF.left = lineLeft + (lineRight - endPadding - lineLeft) / 2 - simplifyDividerWidth / 2;
            if (specialScreenDensity) {
                rectF.top = height - indicatorHeight - tabDividerPadding * 2;
            } else {
                rectF.top = height - indicatorHeight - dividerBottomPadding;
            }
            rectF.right = lineRight - endPadding - (lineRight - endPadding - lineLeft) / 2 + simplifyDividerWidth / 2;
            rectF.bottom = rectF.top + simplifyDividerHeight;
            cornerRadius = (rectF.bottom - rectF.top) / 2;
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, rectPaint);
        } else {
            rectF.left = lineLeft + (lineRight - lineLeft) / 2 - simplifyDividerWidth / 2;
            if (specialScreenDensity) {
                rectF.top = height - indicatorHeight - tabDividerPadding * 2;
            } else {
                rectF.top = height - indicatorHeight - dividerBottomPadding;
            }
            rectF.right = lineRight - (lineRight - lineLeft) / 2 + simplifyDividerWidth / 2;
            rectF.bottom = rectF.top + simplifyDividerHeight;
            cornerRadius = (rectF.bottom - rectF.top) / 2;
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, rectPaint);
        }

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw divider
        dividerPaint.setColor(dividerColor);
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            int startY = dividerHeight <= 0 ? 0 : (height - dividerHeight) / 2;
            int stopY = dividerHeight <= 0 ? height : (height + dividerHeight) / 2;
            canvas.drawLine(tab.getRight(), startY, tab.getRight(), stopY, dividerPaint);
        }
    }

    /*------ViewPager.OnPageChangeListener------*/
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        currentPosition = position;
        /*最后一个tab需要特殊处理，这里处理滑动的下划线长度，在滑动过程中减去4个tabPadding*/
        if (position == tabCount - 1) {
            currentPositionOffset = positionOffset - tabPadding * 5;
        } else {
            currentPositionOffset = positionOffset;
        }
        updateTabStyles();
        if (!toCenter) {
            if (tabsContainer.getChildAt(position) != null) {
                if (position == tabCount - 1) {
                    scrollToChild(position, (int) (currentPositionOffset * (tabsContainer.getChildAt(position).getWidth()) - tabPadding * 4));
                } else {
                    scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
                }
            }
        }
        invalidate();
        if (delegatePageListener != null) {
            delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (pager != null) {
                scrollToChild(pager.getCurrentItem(), 0);
            }
        }
        if (delegatePageListener != null) {
            delegatePageListener.onPageScrollStateChanged(state);
        }
    }

    @Override
    public void onPageSelected(int position) {
        this.refreshTabItem(position);
        if (delegatePageListener != null) {
            delegatePageListener.onPageSelected(position);
        }
    }

    /*------ViewPager.OnPageChangeListener------*/

    private void refreshTabItem(int position) {
        if (toCenter) {
            scrollToChild(position, 0);
        }
        View view = tabsContainer.getChildAt(position);
        if (view == null) {
            return;
        }
        if (view instanceof FrameLayout) {
            int size = ((FrameLayout) view).getChildCount();
            if (size > 0) {
                View childView = ((FrameLayout) view).getChildAt(0);
                if (childView != null && childView instanceof RadioButton) {
                    ((RadioButton) childView).setChecked(true);
                }
            }
        } else if (view instanceof RadioButton) {
            ((RadioButton) view).setChecked(true);
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getTabPadding() {
        return tabPadding;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(ColorStateList textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColorR = resId;
        updateTabStyles();
    }

    public ColorStateList getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public LinearLayout getTabsContainer() {
        return tabsContainer;
    }

    private interface NotifyTabs {
        int tabCount();

        void add(int position);

        int currentPosition();
    }
}
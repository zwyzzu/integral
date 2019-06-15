package com.yixia.widget.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yixia.widget.R;

import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.util.Screen;
import yixia.lib.core.util.Util;

@SuppressWarnings("unused")
public class FlowLayout<E> extends RelativeLayout {
    private final int ADD_END = -1;
    private final int MIN_LINES_LIMITED = 0;
    private int horizontalSpacing;// 水平间距，单位为dp
    private int verticalSpacing;// 垂直间距，单位为dp
    private List<Line> lines = new ArrayList<>();// 行的集合
    private Line line;// 当前的行
    private int maxLines;
    private List<E> data = new ArrayList<>();
    private OnItemClickListener<E> listener;
    private OnItemLoading<E> loading;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        horizontalSpacing = this.getPixelSize(typedArray, R.styleable.FlowLayout_horizontalSpacing);
        verticalSpacing = this.getPixelSize(typedArray, R.styleable.FlowLayout_verticalSpacing);
        this.maxLines = typedArray.getInteger(R.styleable.FlowLayout_maxLines, MIN_LINES_LIMITED);
        typedArray.recycle();
    }

    private int getPixelSize(TypedArray typedArray, @StyleableRes int index) {
        return typedArray.getDimensionPixelSize(index, Screen.dip2px(this.getContext(), 10));
    }

    public void setOnItemListener(OnItemClickListener<E> listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 实际可以用的宽和高
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom() - getPaddingTop();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            widthMode = MeasureSpec.AT_MOST;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            heightMode = MeasureSpec.AT_MOST;
        }

        // Line初始化
        restoreLine();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 测量所有的childView
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            // 如果使用的宽度小于可用的宽度，这时候childView能够添加到当前的行上
            if (this.line != null && this.line.getFutureWidth(child.getMeasuredWidth()) <= width) {
                line.addChild(child);
            } else {
                this.newLine(child, i);
            }
        }

        // 把最后一行记录到集合中
        if (line != null && !lines.contains(line)) {
            lines.add(line);
        }

        int totalHeight = 0;
        // 把所有行的高度加上
        for (int i = 0; i < lines.size(); i++) {
            totalHeight += lines.get(i).getHeight();
        }
        // 加上行的竖直间距
        totalHeight += verticalSpacing * (lines.size() - 1);
        // 加上上下padding
        totalHeight += getPaddingBottom();
        totalHeight += getPaddingTop();

        // 设置自身尺寸
        // 设置布局的宽高，宽度直接采用父view传递过来的最大宽度，而不用考虑子view是否填满宽度
        // 因为该布局的特性就是填满一行后，再换行
        // 高度根据设置的模式来决定采用所有子View的高度之和还是采用父view传递过来的高度
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = resolveSize(totalHeight, heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private void restoreLine() {
        lines.clear();
        line = new Line();
    }

    private void newLine(View child, int position) {
        // 把之前的行记录下来
        if (line != null) {
            lines.add(line);
        }
        if (this.maxLines > MIN_LINES_LIMITED && this.lines.size() >= this.maxLines) {
            int count = this.getChildCount() - position;
            this.removeViews(position, count);
            return;
        }
        // 创建新的一行
        line = new Line();
        line.addChild(child);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            line.layout(left, top);
            // 计算下一行的起点y轴坐标
            top = top + line.getHeight() + verticalSpacing;
        }
    }

    public void loadData(List<E> items, OnItemLoading<E> loading) {
        this.loading = loading;
        this.reload(items);
    }

    public void refresh() {
        this.loadingData();
    }

    public int getCount() {
        return this.data.size();
    }

    public boolean has(E entity) {
        return this.data.contains(entity);
    }

    public void reload(E entity) {
        List<E> array = new ArrayList<>();
        array.add(entity);
        this.reload(array);
    }

    public void reload(List<E> items) {
        this.data.clear();
        if (!Util.isEmpty(items)) {
            this.data.addAll(items);
        }
        this.loadingData();
    }

    public void remove(E entity) {
        if (this.data.remove(entity)) {
            this.loadingData();
        }
    }

    public void add(E entity) {
        if (this.addData(entity, this.ADD_END)) {
            this.loadingData();
        }
    }

    public void add(E entity, int position) {
        if (this.addData(entity, position)) {
            this.loadingData();
        }
    }

    public void addAll(List<E> items) {
        if (this.addAllData(items, this.ADD_END)) {
            this.loadingData();
        }
    }

    public void addAll(List<E> items, int position) {
        if (this.addAllData(items, position)) {
            this.loadingData();
        }
    }

    public void replace(E entity, int position) {
        if (this.replaceData(entity, position)) {
            this.notifyItemChanged(position);
        }
    }

    public void notifyItemChanged(int position) {
        if (this.isLast(position)) {
            return;
        }
        if (this.loading == null) {
            return;
        }
        final E entity = this.data.get(position);
        int type = this.loading.getItemViewType(entity, position);
        View view = this.getChildAt(position);
        view.setOnClickListener(this.createClickListener(entity, type, position));
        this.loading.onLoadView(view, type, entity, position);
    }

    private boolean addData(E entity, int position) {
        if (isLast(position)) {
            return this.data.add(entity);
        } else {
            this.data.add(position, entity);
            return true;
        }
    }

    private boolean addAllData(List<E> items, int position) {
        if (Util.isEmpty(items)) {
            return false;
        }
        if (this.isLast(position)) {
            return this.data.addAll(items);
        } else {
            return this.data.addAll(position, items);
        }
    }

    private boolean replaceData(E entity, int position) {
        if (this.isLast(position)) {
            return false;
        }
        try {
            this.data.set(position, entity);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLast(int position) {
        return position == ADD_END || position < 0 || position >= this.getCount();
    }

    private void loadingData() {
        if (Util.isEmpty(this.data) || this.loading == null) {
            return;
        }

        this.removeAllViews();
        for (int position = 0; position < this.data.size(); position++) {
            E entity = this.data.get(position);
            if (entity == null) {
                continue;
            }
            int type = this.loading.getItemViewType(entity, position);
            View view = this.loading.onCreateView(this, type);
            view.setOnClickListener(this.createClickListener(entity, type, position));
            this.loading.onLoadView(view, type, entity, position);
            addView(view, this.childLayoutParams());
        }
    }

    private LayoutParams childLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    private OnClickListener createClickListener(final E entity, int type, int position) {
        return v -> {
            if (listener == null) {
                return;
            }
            listener.onItemClick(v, type, entity, position);
        };
    }

    /**
     * 设置文字水平间距
     *
     * @param horizontalSpacing 间距/dp
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = Screen.dip2px(this.getContext(), horizontalSpacing);
    }

    /**
     * 设置文字垂直间距
     *
     * @param verticalSpacing 间距/dp
     */
    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = Screen.dip2px(this.getContext(), verticalSpacing);
    }

    /**
     * 设置最大行数
     * @param maxLines 最大行数不得小于1否则不限制
     */
    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    /**
     * 管理每行上的View对象
     */
    private class Line {
        // 子控件集合
        private List<View> children = new ArrayList<>();
        // 行高
        private int height = 0;
        private int width = 0;

        /**
         * 添加childView
         *
         * @param childView 子控件
         */
        private void addChild(View childView) {
            children.add(childView);

            // 让当前的行高是最高的一个childView的高度
            if (this.height < childView.getMeasuredHeight()) {
                this.height = childView.getMeasuredHeight();
            }
            this.width += childView.getMeasuredWidth();
            this.width += horizontalSpacing;
        }

        /**
         * 指定childView的绘制区域
         *
         * @param left 左上角x轴坐标
         * @param top  左上角y轴坐标
         */
        public void layout(int left, int top) {
            // 当前childView的左上角x轴坐标
            int currentLeft = left;
            for (int i = 0; i < this.children.size(); i++) {
                View view = this.children.get(i);
                // 指定childView的绘制区域
                int right = currentLeft + view.getMeasuredWidth();
                int bottom = top + view.getMeasuredHeight();
                view.layout(currentLeft, top, right, bottom);
                // 计算下一个childView的位置
                currentLeft = currentLeft + view.getMeasuredWidth() + horizontalSpacing;
            }
        }

        public int getHeight() {
            return this.height;
        }

        private int getFutureWidth(int childWidth) {
            return this.width + childWidth;
        }

        public int getWidth() {
            return this.width;
        }

        public int getChildCount() {
            return this.children.size();
        }
    }

    public interface OnItemClickListener<OC> {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)Ø
         * @param viewType "
         * @param entity   The entity within the adapter data that was clicked item
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View view, int viewType, OC entity, int position);
    }

    public static abstract class OnItemLoading<OL> {

        public int getItemViewType(OL entity, int position) {
            return 0;
        }

        public abstract View onCreateView(ViewGroup parent, int viewType);

        public abstract void onLoadView(View root, int viewType, OL entity, int position);
    }
}

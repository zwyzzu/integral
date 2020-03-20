package com.yixia.widget.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by zhangwy on 2020-03-20.
 * Updated by zhangwy on 2020-03-20.
 * Description
 */
@SuppressWarnings("unused")
public class RecyclerTouchHelper extends ItemTouchHelper.Callback {

    private OnSwipedListener swipedListener;
    private OnDragListener dragListener;

    public RecyclerTouchHelper() {
        this(null, null);
    }

    public RecyclerTouchHelper(OnSwipedListener swipedListener) {
        this(swipedListener, null);
    }

    public RecyclerTouchHelper(OnDragListener dragListener) {
        this(null, dragListener);
    }

    public RecyclerTouchHelper(OnSwipedListener swipedListener, OnDragListener dragListener) {
        this.setOnSwipedListener(swipedListener);
        this.setOnDragListener(dragListener);
    }

    public void setOnSwipedListener(OnSwipedListener listener) {
        this.swipedListener = listener;
    }

    public void setOnDragListener(OnDragListener listener) {
        this.dragListener = listener;
    }

    /**
     * 设置滑动类型标记
     *
     * @param recyclerView 当前RecyclerView
     * @param viewHolder   选择的ViewHolder
     * @return 返回一个整数类型的标识，用于判断Item那种移动行为是允许的
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    /**
     * Item是否支持长按拖动
     *
     * @return true  支持长按操作 false 不支持长按操作
     */

    @Override
    public boolean isLongPressDragEnabled() {
        return this.dragListener != null;
    }

    /**
     * Item是否支持滑动
     *
     * @return true  支持滑动操作 false 不支持滑动操作
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return this.swipedListener != null;
    }

    /**
     * @param recyclerView 当前ReceiverView
     * @param source       源ViewHolder
     * @param target       目标ViewHolder
     * @return 如果Item切换了位置，返回true；反之，返回false
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        if (this.dragListener != null) {
            this.dragListener.onDrag(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 滑动Item
     *
     * @param viewHolder 当前滑动的ViewHolder
     * @param direction  Item滑动的方向
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (this.swipedListener != null) {
            this.swipedListener.onSwiped(viewHolder.getAdapterPosition(), direction);
        }
    }

    /**
     * Item被选中时候回调
     *
     * @param viewHolder  "
     * @param actionState 当前Item的状态
     *                    ItemTouchHelper.ACTION_STATE_IDLE   闲置状态
     *                    ItemTouchHelper.ACTION_STATE_SWIPE  滑动中状态
     *                    ItemTouchHelper#ACTION_STATE_DRAG   拖拽中状态
     */
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
    }

    public static interface OnSwipedListener {
        void onSwiped(int position, int direction);
    }

    public static interface OnDragListener {
        void onDrag(int fromPosition, int toPosition);
    }
}

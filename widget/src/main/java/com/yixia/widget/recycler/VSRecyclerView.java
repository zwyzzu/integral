package com.yixia.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.yixia.widget.adapter.RefreshAdapterCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张维亚(zhangwy) on 2016/12/13 上午11:09.
 * Updated by zhangwy on 2016/12/13 上午11:09.
 * Description 自定义recycleView
 */
@SuppressWarnings("unused")
public class VSRecyclerView<T> extends RecyclerView {
    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
    public static final int VERTICAL = OrientationHelper.VERTICAL;
    private RecyclerAdapter<T> adapter;
    private RecyclerAdapter.OnItemClickListener<T> itemClickListener;

    public VSRecyclerView(Context ctx) {
        super(ctx);
        initialize();
    }

    public VSRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public VSRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        this.setItemAnimator(new DefaultItemAnimator());
    }

    public void setGridLayoutManager(int spanCount, int orientation, boolean full) {
        GridLayoutManager layoutManager;
        if (full) {
            layoutManager = new FullGridLayoutManager(getContext(), spanCount, orientation, false);
        } else {
            layoutManager = new GridLayoutManager(getContext(), spanCount, orientation, false);
        }
        this.setLayoutManager(layoutManager);
    }

    public void setLinearLayoutManager(int orientation, boolean full) {
        LinearLayoutManager layoutManager;
        if (full) {
            layoutManager = new FullLinearLayoutManager(getContext(), orientation, false);
        } else {
            layoutManager = new LinearLayoutManager(getContext(), orientation, false);
        }
        this.setLayoutManager(layoutManager);
    }

    public void loadData(List<T> array, RecyclerAdapter.OnItemLoading<T> itemLoading) {
        adapter = new RecyclerAdapter<>(array, itemLoading);
        adapter.setOnItemClickListener(itemClickListener);
        this.setAdapter(adapter);
    }

    public void setOnItemClickListener(RecyclerAdapter.OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
        if (adapter != null) {
            adapter.setOnItemClickListener(listener);
        }
    }

    public void notifyDataSetChanged() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public boolean has(T entity) {
        return this.adapter != null && this.adapter.has(entity);
    }

    public void reload(T entity) {
        if (adapter != null)
            adapter.reload(entity);
    }

    public void reload(List<T> items) {
        if (adapter != null)
            adapter.reload(items);
    }

    public void remove(T t) {
        if (adapter != null)
            this.adapter.remove(t);
    }

    public void add(T t) {
        if (adapter != null)
            adapter.add(t);
    }

    public void add(T t, int position) {
        if (adapter != null)
            adapter.add(t, position);
    }

    public void addAll(List<T> items) {
        if (adapter != null)
            adapter.addAll(items);
    }

    public void addAll(List<T> items, int position) {
        if (adapter != null)
            adapter.addAll(items, position);
    }

    public void addCurrents(List<RefreshAdapterCallBack.Current<T>> list) {
        if (adapter != null)
            adapter.addCurrents(list);
    }

    public void replace(T t, int position) {
        if (adapter != null)
            adapter.replace(t, position);
    }

    public void replace(List<RefreshAdapterCallBack.Current<T>> list) {
        if (adapter != null)
            adapter.replaceCurrents(list);
    }

    public int getCount() {
        return adapter == null ? 0 : adapter.getItemCount();
    }

    public void notifyItemChanged(int position) {
        if (adapter != null)
            this.adapter.notifyItemChanged(position);
    }

    public void notifyItemMoved(int fromPosition, int toPosition) {
        if (adapter != null)
            this.adapter.notifyItemMoved(fromPosition, toPosition);
    }

    public List<T> getData() {
        if (adapter != null)
            return this.adapter.getData();
        return new ArrayList<>();
    }
}
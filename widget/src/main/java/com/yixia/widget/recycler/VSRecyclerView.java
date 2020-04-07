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
        this.adapter = new RecyclerAdapter<>(array, itemLoading);
        this.adapter.setOnItemClickListener(itemClickListener);
        this.setAdapter(this.adapter);
    }

    public void setOnItemClickListener(RecyclerAdapter.OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
        if (this.adapter != null) {
            this.adapter.setOnItemClickListener(listener);
        }
    }

    public void notifyDataSetChanged() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public boolean has(T entity) {
        return this.adapter != null && this.adapter.has(entity);
    }

    public void reload(T entity) {
        if (this.adapter != null) {
            this.adapter.reload(entity);
        }
    }

    public void reload(List<T> items) {
        if (this.adapter != null) {
            this.adapter.reload(items);
        }
    }

    public void remove(T t) {
        if (this.adapter != null) {
            this.adapter.remove(t);
        }
    }

    public void remove(int position) {
        if (this.adapter != null) {
            this.adapter.remove(position);
        }
    }

    public void remove(List<T> items) {
        if (this.adapter != null) {
            this.adapter.remove(items);
        }
    }

    public void remove(int formPosition, int count) {
        if (this.adapter != null) {
            this.adapter.remove(formPosition, count);
        }
    }

    public void add(T t) {
        if (this.adapter != null) {
            this.adapter.add(t);
        }
    }

    public void add(T t, int position) {
        if (this.adapter != null) {
            this.adapter.add(t, position);
        }
    }

    public void addAll(List<T> items) {
        if (this.adapter != null) {
            this.adapter.addAll(items);
        }
    }

    public void addAll(List<T> items, int position) {
        if (this.adapter != null) {
            this.adapter.addAll(items, position);
        }
    }

    public void addCurrents(List<RefreshAdapterCallBack.Current<T>> list) {
        if (this.adapter != null) {
            this.adapter.addCurrents(list);
        }
    }

    public void replace(T t, int position) {
        if (this.adapter != null) {
            this.adapter.replace(t, position);
        }
    }

    public void replace(List<RefreshAdapterCallBack.Current<T>> list) {
        if (this.adapter != null) {
            this.adapter.replaceCurrents(list);
        }
    }

    public int getCount() {
        return this.adapter == null ? 0 : this.adapter.getItemCount();
    }

    public void notifyItemChanged(int position) {
        if (this.adapter != null) {
            this.adapter.notifyItemChanged(position);
        }
    }

    public void notifyItemMoved(int fromPosition, int toPosition) {
        if (this.adapter != null) {
            this.adapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public List<T> getData() {
        if (this.adapter != null) {
            return this.adapter.getData();
        }
        return new ArrayList<>();
    }

    public T getData(int position) {
        if (this.adapter != null) {
            return this.adapter.getItem(position);
        }
        return null;
    }
}
package com.yixia.widget.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yixia.widget.adapter.RefreshAdapterCallBack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 张维亚(zhangwy) on 2016/12/13 下午2:11.
 * Updated by zhangwy on 2016/12/13 下午2:11.
 * Description RecyclerView 的适配器
 */
@SuppressWarnings("unused")
public class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> implements View.OnClickListener, RefreshAdapterCallBack<T> {
    private static final int ADD_END = -1;
    private List<T> array = new ArrayList<>();
    private OnItemClickListener<T> itemClickListener;
    private OnItemLoading<T> itemLoading;
    private long lastClickTime = 0;
    private final long CLICK_INTERVAL = 1000;

    public RecyclerAdapter(List<T> array, OnItemLoading<T> itemLoading) {
        super();
        this.setItems(array);
        this.itemLoading = itemLoading;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
    }

    public List<T> getData() {
        return this.array;
    }

    @Override
    public boolean has(T entity) {
        return this.array.contains(entity);
    }

    @Override
    public void add(T t) {
        this.add(t, ADD_END);
    }

    @Override
    public void add(T t, int position) {
        this.addItem(t, position);
        if (this.addLast(position)) {
            this.notifyItemInserted(getItemCount());
        } else {
            this.notifyItemInserted(position);
        }
    }

    @Override
    public void addAll(List<T> list) {
        this.addAll(list, ADD_END);
    }

    @Override
    public void addAll(List<T> list, int position) {
        this.addItems(list, position);
        if (this.addLast(position)) {
            position = this.array.size() - list.size();
        }
        this.notifyItemRangeInserted(position, list.size());
    }

    @Override
    public void addCurrents(List<Current<T>> list) {
        if (this.isEmpty(list))
            return;

        for (Current<T> item : list)
            this.addItem(item.t, item.position);

        this.notifyDataSetChanged();
    }

    @Override
    public void remove(T t) {
        int position = this.array.indexOf(t);
        if (position < 0 || position >= this.array.size()) {
            return;
        }
        this.array.remove(t);
        this.notifyItemRemoved(position);
    }

    @Override
    public void remove(int position) {
        final int count = getItemCount();
        this.removeItem(position);
        if (position >= 0 && position < count) {
            this.notifyItemRemoved(position);
        } else {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void remove(List<T> list) {
        if (this.isEmpty(list))
            return;
        this.array.removeAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public void remove(int formPosition, int count) {
        if (formPosition < 0) {
            formPosition = 0;
        }
        if (formPosition >= this.array.size() || count <= 0) {
            return;
        }
        if (formPosition + count >= this.array.size()) {
            count = this.array.size() - formPosition;
        }
        for (int i = 0; i < count; i++) {
            this.array.remove(formPosition);
        }
        this.notifyItemRangeRemoved(formPosition, count);
    }

    @Override
    public void replace(T t, int position) {
        this.replaceItem(t, position);
        this.notifyItemChanged(position);
    }

    @Override
    public void replaceCurrents(List<Current<T>> list) {
        if (this.isEmpty(list))
            return;

        for (Current<T> item : list)
            this.replaceItem(item.t, item.position);

        this.notifyDataSetChanged();
    }

    @Override
    public void reload(T entity) {
        this.setItem(entity);
        this.notifyDataSetChanged();
    }

    @Override
    public void reload(List<T> list) {
        this.setItems(list);
        this.notifyDataSetChanged();
    }

    @Override
    public void clear() {
        this.array.clear();
        this.notifyDataSetChanged();
    }

    private void setItem(T entity) {
        this.array.clear();
        if (entity == null) {
            return;
        }
        this.array.add(entity);
    }

    private void setItems(List<T> list) {
        if (this.array == list)
            return;
        this.array.clear();
        if (this.isEmpty(list))
            return;
        this.array.addAll(list);
    }

    public boolean addItem(T t, int position) {
        if (addLast(position))
            return array.add(t);

        array.add(position, t);
        return true;
    }

    private boolean addItems(List<T> items, int position) {
        if (this.isEmpty(items))
            return false;
        if (addLast(position))
            return this.array.addAll(items);

        return this.array.addAll(position, items);
    }

    private boolean addLast(int position) {
        return position == ADD_END || position < 0 || position >= getItemCount();
    }

    private void replaceItem(T t, int position) {
        this.removeItem(position);
        this.addItem(t, position);
    }

    private T removeItem(int position) {
        return this.array.remove(position);
    }

    @Override
    public int getItemViewType(int position) {
        return itemLoading.getItemViewType(getItem(position), position);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return itemLoading.onCreateViewHolder(itemLoading.onCreateView(parent, viewType), viewType).setOnClick(this);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.bindPosition(position);
        itemLoading.onBind(holder, getItemViewType(position), getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return array == null ? 0 : array.size();
    }

    public T getItem(int position) {
        if (this.isEmpty(array) || position > array.size() - 1) {
            return null;
        }
        return array.get(position);
    }

    private boolean isEmpty(Collection<?> items) {
        return items == null || items.isEmpty();
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener == null)
            return;
        if (System.currentTimeMillis() - this.lastClickTime < CLICK_INTERVAL) {
            return;
        }
        this.lastClickTime = System.currentTimeMillis();
        int position = (int) v.getTag();
        T entity = getItem(position);
        itemClickListener.onItemClick(v, getItemViewType(position), entity, position);
    }

    public abstract static class OnItemLoading<E> {
        public int getItemViewType(E entity, int position) {
            return 0;
        }

        public abstract View onCreateView(ViewGroup parent, int viewType);

        public RecyclerViewHolder onCreateViewHolder(View root, int viewType) {
            return new RecyclerViewHolder(root);
        }

        public void onBind(RecyclerViewHolder holder, int viewType, E entity, int position) {
            this.onLoadView(holder.itemView, holder.getItemViewType(), entity, position);
        }

        public void onLoadView(View root, int viewType, E entity, int position) {
        }
    }

    public interface OnItemClickListener<E> {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p/>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)Ø
         * @param entity   The entity within the adapter data that was clicked item
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View view, int viewType, E entity, int position);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private View.OnClickListener listener;
        public RecyclerViewHolder(View view) {
            super(view);
        }

        public RecyclerViewHolder setOnClick(View.OnClickListener listener) {
            this.itemView.setOnClickListener(listener);
            return this;
        }

        public void bindPosition(int position) {
            itemView.setTag(position);
        }
    }
}

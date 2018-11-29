package com.yixia.widget.adapter;

import java.util.List;

/**
 * Author: 张维亚
 * 创建时间：2014年7月5日 上午10:23:06
 * 修改时间：2014年7月5日 上午10:23:06
 * Description: 刷新适配器data的接口
 **/
public interface RefreshAdapterCallBack<T> {

    boolean has(T entity);

    void add(T entity);

    void add(T entity, int position);

    void addAll(List<T> list);

    void addAll(List<T> list, int position);

    void addCurrents(List<Current<T>> list);

    void remove(T entity);

    void remove(int position);

    void remove(List<T> list);

    void replace(T entity, int position);

    void replaceCurrents(List<Current<T>> list);

    void reload(T entity);

    void reload(List<T> list);

    void clear();

    class Current<T> {
        public int position;
        public T t;
    }
}
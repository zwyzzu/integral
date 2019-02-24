package com.yixia.album.model;

import android.content.Context;

import java.util.List;

/**
 * Created by zhangwy on 2018/6/2 下午5:29.
 * Updated by zhangwy on 2018/6/2 下午5:29.
 * Description 媒体资源查询model
 */
@SuppressWarnings("unused")
public interface VSMediaDataSourceModel<F, M> {
    /**
     * 加载媒体资源
     */
    void loadDataSourceModel(Context context);

    /**
     * 获取所有的目录
     *
     * @return 媒体资源文件目录列表
     */
    List<F> getFolders();

    /**
     * 获取所有的媒体资源
     *
     * @return 所有的媒体资源
     */
    List<M> getAllMedias();

    /**
     * 获取文件夹下的媒体资源
     *
     * @param folder 文件夹
     * @return 当前文件夹下的媒体资源
     */
    List<M> getMedias(F folder);

    /**
     * 获取文件夹下的媒体资源
     *
     * @param folder 文件夹
     * @return 当前文件夹下的媒体资源
     */
    List<M> getMedias(String folder);

    boolean hasLoaded();

    void destroy();

    interface OnMediaDataLoadingListener {
        /**
         * 开始加载
         */
        void onLoadingStart();

        /**
         * 加载取消
         */
        void onLoadingCancel();
        /**
         * 加载结束
         */
        void onLoadingEnd();
    }
}

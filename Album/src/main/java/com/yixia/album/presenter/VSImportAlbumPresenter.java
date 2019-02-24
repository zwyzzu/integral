package com.yixia.album.presenter;

import android.content.Context;

import com.yixia.album.model.VSMediaDataSourceModel;
import com.yixia.album.view.VSImportAlbumView;

import yixia.lib.core.base.mvp.BasePresenter;

/**
 * Created by zhangwy on 2018/10/17 下午6:51.
 * Updated by zhangwy on 2018/10/17 下午6:51.
 * Description
 */
@SuppressWarnings("unused")
public abstract class VSImportAlbumPresenter<F, M> extends BasePresenter<VSImportAlbumView<F, M>>
        implements VSMediaDataSourceModel.OnMediaDataLoadingListener{

    VSMediaDataSourceModel<F, M> sourceModel;

    public void requestAlbums(Context context) {
        if (this.sourceModel != null) {
            this.sourceModel.loadDataSourceModel(context);
        }
    }

    public abstract void switchFolder(F folder);

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.sourceModel != null) {
            this.sourceModel.destroy();
            this.sourceModel = null;
        }
    }

    /**
     * 开始加载
     */
    @Override
    public void onLoadingStart() {
    }

    /**
     * 加载取消
     */
    @Override
    public void onLoadingCancel() {
    }

    /**
     * 加载结束
     */
    @Override
    public abstract void onLoadingEnd();

    boolean canCallView() {
        return this.mView != null && this.sourceModel != null;
    }
}

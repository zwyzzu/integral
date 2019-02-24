package com.yixia.album.presenter;

import android.content.Context;

import com.yixia.album.model.VSFolder;
import com.yixia.album.model.VSVideo;
import com.yixia.album.model.VSMediaDataSourceModel;
import com.yixia.album.model.VSSystemVideoDataSourceModel;
import com.yixia.album.view.VSImportAlbumView;

import java.util.Collections;
import java.util.List;

import yixia.lib.core.base.mvp.BasePresenter;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/6/2 下午3:23.
 * Updated by zhangwy on 2018/6/2 下午3:23.
 * Description
 */
public class VSImportVideoPresenter extends VSImportAlbumPresenter<VSFolder, VSVideo> {

    public VSImportVideoPresenter(Context context, VSImportAlbumView view) {
        this.setView(view);
        this.sourceModel = new VSSystemVideoDataSourceModel(context, this);
    }

    public void switchFolder(VSFolder folder) {
        if (this.mView != null && this.sourceModel != null) {
            List<VSVideo> medias = this.sourceModel.getMedias(folder);
            if (!Util.isEmpty(medias)) {
                Collections.sort(medias);
            }
            this.mView.refreshMedia(folder, medias);
        }
    }

    @Override
    public void onLoadingEnd() {
        if (this.mView != null && this.sourceModel != null) {
            List<VSFolder> folders = this.sourceModel.getFolders();
            this.mView.refreshFolder(folders);
            if (!Util.isEmpty(folders)) {
                this.switchFolder(folders.get(0));
            }
        }
    }
}
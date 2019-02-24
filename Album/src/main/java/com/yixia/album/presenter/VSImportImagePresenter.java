package com.yixia.album.presenter;

import android.content.Context;

import com.yixia.album.model.VSFolder;
import com.yixia.album.model.VSMedia;
import com.yixia.album.model.VSSystemImageDataSourceModel;
import com.yixia.album.view.VSImportAlbumView;

import java.util.Collections;
import java.util.List;

import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/10/17 下午7:00.
 * Updated by zhangwy on 2018/10/17 下午7:00.
 * Description
 */
@SuppressWarnings("unused")
public class VSImportImagePresenter extends VSImportAlbumPresenter<VSFolder, VSMedia> {

    public VSImportImagePresenter(Context context, VSImportAlbumView view) {
        this.setView(view);
        this.sourceModel = new VSSystemImageDataSourceModel(context, this);
    }

    @Override
    public void switchFolder(VSFolder folder) {
        if (this.canCallView()) {
            List<VSMedia> medias = this.sourceModel.getMedias(folder);
            if (!Util.isEmpty(medias)) {
                Collections.sort(medias);
            }
            this.mView.refreshMedia(folder, medias);
        }
    }

    /**
     * 加载结束
     */
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

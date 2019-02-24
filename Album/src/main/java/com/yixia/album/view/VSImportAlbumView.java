package com.yixia.album.view;

import java.util.List;

import yixia.lib.core.base.mvp.BaseView;

/**
 * Created by zhangwy on 2018/6/2 下午3:19.
 * Updated by zhangwy on 2018/6/2 下午3:19.
 * Description
 */
public interface VSImportAlbumView<F, M> extends BaseView {

    void refreshMedia(F folder, List<M> medias);

    void refreshFolder(List<F> folders);
}

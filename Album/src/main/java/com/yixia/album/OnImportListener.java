package com.yixia.album;

import android.view.View;

import com.yixia.album.model.VSMedia;
import com.yixia.album.model.VSVideo;

import java.util.List;

/**
 * Created by zhangwy on 2018/10/17 下午4:44.
 * Updated by zhangwy on 2018/10/17 下午4:44.
 * Description
 */
public interface OnImportListener {

    void loading();

    void loadEnd();

    void showPreview(View itemView, VSMedia media);

    void nextForVideo(VSVideo video);

    void nextForImages(List<VSMedia> images);

    void sendRunnableDelayed(Runnable runnable, long delayed);
}

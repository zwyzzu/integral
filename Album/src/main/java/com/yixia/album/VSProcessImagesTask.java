package com.yixia.album;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import yixia.lib.core.util.DirMgmt;
import yixia.lib.core.util.FileUtil;
import yixia.lib.core.util.ImageUtils;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/10/24 下午5:50.
 * Updated by zhangwy on 2018/10/24 下午5:50.
 * Description
 */
@SuppressWarnings("unused")
public class VSProcessImagesTask extends AsyncTask<Context, String, Boolean> {

    private String directory;
    private List<String> oldPaths = new ArrayList<>();
    private List<String> newPaths = new ArrayList<>();
    private int progressCount = 0;
    private OnProcessCallback callback;
    static final int PROCESS_STATUS_PROGRESS = 0;
    static final int PROCESS_STATUS_SUCCESS = 1;
    static final int PROCESS_STATUS_FAILED = 2;

    public VSProcessImagesTask(List<String> paths, String directory) {
        this.oldPaths.addAll(paths);
        this.directory = directory;
        if (TextUtils.isEmpty(this.directory)) {
            this.directory = UUID.randomUUID().toString();
        }
    }

    public VSProcessImagesTask setCallback(OnProcessCallback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.callback != null) {
            callback.onProcessStart();
        }
    }

    @Override
    protected Boolean doInBackground(Context... contexts) {
        if (Util.isEmpty(contexts)) {
            return false;
        }
        final int maxLength = 1080;
        final int quality = 80;
        Context context = contexts[0];
        DirMgmt.getInstance().init(context);
        String rootPath = DirMgmt.getInstance().getPath(DirMgmt.WorkDir.OTHER);
        rootPath = FileUtil.pathAddBackslash(rootPath);
        rootPath += this.directory;
        FileUtil.makeDirs(rootPath);
        rootPath = FileUtil.pathAddBackslash(rootPath);
        for (String src : this.oldPaths) {
            String name = UUID.randomUUID().toString() + ".jpg";
            String dest = rootPath + name;
            this.publishProgress(src, dest, String.valueOf(PROCESS_STATUS_PROGRESS));
            boolean success = ImageUtils.compress(src, dest, maxLength, maxLength, quality);
            String result = success ? String.valueOf(PROCESS_STATUS_SUCCESS) //
                    : String.valueOf(PROCESS_STATUS_FAILED);
            this.publishProgress(src, dest, result);
            try {
                Thread.sleep(100);
            } catch (Throwable throwable) {
                Logger.d("sleep", throwable);
            }
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (values == null || values.length < 3) {
            return;
        }
        final String srcFile = values[0];
        final String destFile = values[1];
        final int status = Integer.parseInt(values[2]);
        switch (status) {
            case PROCESS_STATUS_PROGRESS: {
                this.progressCount++;
                break;
            }
            case PROCESS_STATUS_SUCCESS: {
                newPaths.add(destFile);
                break;
            }
            case PROCESS_STATUS_FAILED:
                break;
        }
        if (this.callback != null) {
            callback.onProcessProgress(progressCount, status, //
                    srcFile, destFile);

        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success && !Util.isEmpty(this.newPaths)) {
            if (this.callback != null) {
                callback.onProcessSuccess(newPaths);
            }
        } else {
            if (this.callback != null) {
                callback.onProcessFailed();
            }
        }
    }

    interface OnProcessCallback {
        void onProcessStart();

        /**
         * 图片转移进度
         *
         * @param index    index
         * @param status   状态 0：正在转移；1：转移成功；2转移失败
         * @param srcFile  源文件地址
         * @param destFile 目的文件地址
         */
        void onProcessProgress(int index, int status, String srcFile, String destFile);

        void onProcessSuccess(List<String> destFiles);

        void onProcessFailed();
    }
}

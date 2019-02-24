package com.yixia.album.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Pair;

import com.yixia.album.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yixia.lib.core.cipher.MD5;
import yixia.lib.core.util.DirMgmt;
import yixia.lib.core.util.FileUtil;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/6/3 上午10:04.
 * Updated by zhangwy on 2018/6/3 上午10:04.
 * Description 本地媒体资源管理
 * 操作步骤：
 * 1：创建管理类
 * 2：扫描媒体资源
 * 3：通过回调通知使用者获取资源
 */
@SuppressWarnings("unused")
public class VSSystemVideoDataSourceModel implements VSMediaDataSourceModel<VSFolder, VSVideo> {

    //由于存在音频长度大于视频长度的情况，原3000ms的，目前设定为3000ms + default_fault_tolerant.
    private static final int MIN_VIDEO_DEFAULT_DURATION = 3000;
    private static final int MIN_VIDEO_DURATION = MIN_VIDEO_DEFAULT_DURATION + 700;

    private static final String DEFAULT_CAMERA_ALBUM = "Camera";
    private LoadingAsyncTask task;
    private OnMediaDataLoadingListener loadingListener;

    private List<VSFolder> folders = new ArrayList<>();
    private HashMap<String, List<VSVideo>> medias = new HashMap<>();
    private boolean hasLoaded = false;
    private String allMediasName = "allMedias";
    private String allMediasKey = MD5.md5Encode("allMedias");

    public VSSystemVideoDataSourceModel(Context context, OnMediaDataLoadingListener listener) {
        this.loadingListener = listener;
        if (context != null) {
            this.allMediasName = context.getResources().getString(R.string.importAlbumAllMedia);
        }
    }

    @Override
    public void loadDataSourceModel(Context context) {
        if (this.task != null) {
            if (this.task.usable()) {
                return;
            }
            this.task.destroy();
        }
        this.task = new LoadingAsyncTask(this);
        this.task.execute(context);
    }

    /**
     * 获取所有的目录
     *
     * @return 媒体资源文件目录列表
     */
    @Override
    public List<VSFolder> getFolders() {
        return this.folders;
    }

    /**
     * 获取所有的媒体资源
     *
     * @return 所有的媒体资源
     */
    @Override
    public List<VSVideo> getAllMedias() {
        return this.getMedias(this.allMediasKey);
    }

    /**
     * 获取文件夹下的媒体资源
     *
     * @param folder 文件夹
     * @return 当前文件夹下的媒体资源
     */
    @Override
    public List<VSVideo> getMedias(VSFolder folder) {
        if (folder == null)
            return new ArrayList<>();
        return this.getMedias(folder.getPath());
    }

    /**
     * 获取文件夹下的媒体资源
     *
     * @param folderPath 文件夹路径
     * @return 当前文件夹下的媒体资源
     */
    @Override
    public List<VSVideo> getMedias(String folderPath) {
        List<VSVideo> array = medias.get(folderPath);
        return array == null ? new ArrayList<VSVideo>() : array;
    }

    @Override
    public boolean hasLoaded() {
        return this.hasLoaded;
    }

    @Override
    public void destroy() {
        if (this.task != null) {
            this.task.destroy();
            this.task.cancel(true);
            this.task = null;
        }
    }

    public void loadingStart() {
        if (this.loadingListener != null) {
            this.loadingListener.onLoadingStart();
        }
    }

    public void loadingCancel() {
        if (this.loadingListener != null) {
            this.loadingListener.onLoadingCancel();
        }
    }

    public void loadingEnd(HashMap<String, VSFolder> folders, HashMap<String, List<VSVideo>>
            medias) {
        this.hasLoaded = true;
        this.folders.addAll(folders.values());
        this.medias.putAll(medias);
        this.addAllFolder();
        if (this.loadingListener != null) {
            this.loadingListener.onLoadingEnd();
        }
    }

    private void addAllFolder() {
        List<VSVideo> all = new ArrayList<>();
        for (List<VSVideo> array : this.medias.values()) {
            all.addAll(array);
        }
        if (Util.isEmpty(all))
            return;
        VSFolder folder = new VSFolder();
        folder.setId(this.allMediasKey);
        folder.setPath(this.allMediasKey);
        folder.setName(this.allMediasName);
        folder.setDisplay(folder.getName());
        folder.setThumbnail(all.get(0).getThumbnail());
        this.folders.add(0, folder);
        this.medias.put(this.allMediasKey, all);
    }

    private static class LoadingAsyncTask extends AsyncTask<Context, Integer, Pair<HashMap<String, VSFolder>, HashMap<String, List<VSVideo>>>> {

        private boolean destroy = false;
        private SoftReference<VSSystemVideoDataSourceModel> reference;

        LoadingAsyncTask(VSSystemVideoDataSourceModel mediaDataSourceModel) {
            this.reference = new SoftReference<>(mediaDataSourceModel);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            VSSystemVideoDataSourceModel model = this.getModel();
            if (model != null) {
                model.loadingStart();
            }
        }

        @Override
        protected Pair<HashMap<String, VSFolder>, HashMap<String, List<VSVideo>>> doInBackground(
                Context... contexts) {
            HashMap<String, VSFolder> folders = new HashMap<>();
            HashMap<String, List<VSVideo>> medias = new HashMap<>();
            if (contexts == null || contexts.length == 0 || contexts[0] == null) {
                return Pair.create(folders, medias);
            }
            DirMgmt.getInstance().init(contexts[0]);
            final HashMap<String, String> thumbs = this.queryThumb(contexts[0]);
            final String[] projection = {
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.MIME_TYPE,
                    MediaStore.Video.Media.HEIGHT,
                    MediaStore.Video.Media.WIDTH,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DATE_ADDED,
                    MediaStore.Video.Media.DATE_MODIFIED,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.VideoColumns.ALBUM,
                    MediaStore.Video.VideoColumns.BUCKET_ID,
                    MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME
            };
            Context context = contexts[0];
            Cursor cursor = null;
            try {
                cursor = MediaStore.Video.query(context.getContentResolver(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection);
            } catch (Exception e) {
                Logger.e("queryMedia", e);
            }
            if (cursor == null) {
                return Pair.create(folders, medias);
            }
            while (cursor.moveToNext()) {
                String album = cursor.getString(11);
                if (this.parseLong(cursor.getString(10))
                        < ((TextUtils.equals(album, DEFAULT_CAMERA_ALBUM))//拍摄视频无此问题;
                            ? MIN_VIDEO_DEFAULT_DURATION : MIN_VIDEO_DURATION)) {
                    continue;
                }
                final VSVideo media = new VSVideo();
                media.setId(cursor.getString(0));
                media.setTitle(cursor.getString(1));
                media.setDisplay(cursor.getString(2));
                media.setPath(cursor.getString(3));
                media.setMimeType(cursor.getString(4));
                media.setHeight(this.parseInt(cursor.getString(5)));
                media.setWidth(this.parseInt(cursor.getString(6)));
                media.setSize(this.parseLong(cursor.getString(7)));
                media.setCreated(this.parseLong(cursor.getString(8)));
                media.setModified(this.parseLong(cursor.getString(9)));
                media.setDuration(this.parseLong(cursor.getString(10)));
                media.setFolderId(album);
                media.setThumbnail(this.getThumb(new Command() {
                    @Override
                    public String execute() {
                        return thumbs.get(media.getId());
                    }
                }, new Command() {
                    @Override
                    public String execute() {
                        return media.getPath();
                    }
                }));
                {
                    if (!folders.containsKey(media.getFolder())) {
                        VSFolder folder = new VSFolder();
                        folder.setId(media.getFolderId());
                        folder.setPath(media.getFolder());
                        folder.setName(cursor.getString(12));
                        folder.setDisplay(cursor.getString(13));
                        folder.setThumbnail(media.getThumbnail());
                        folders.put(media.getFolder(), folder);
                    }
                }
                {
                    if (!medias.containsKey(media.getFolder())
                            || medias.get(media.getFolder()) == null) {
                        medias.put(media.getFolder(), new ArrayList<VSVideo>());
                    }
                    List<VSVideo> array = medias.get(media.getFolder());
                    array.add(media);
                }
            }
            cursor.close();
            return Pair.create(folders, medias);
        }

        private HashMap<String, String> queryThumb(Context context) {
            HashMap<String, String> hashMap = new HashMap<>();
            if (context == null) {
                return hashMap;
            }
            String[] columns = {
                    MediaStore.Video.Thumbnails.VIDEO_ID,
                    MediaStore.Video.Thumbnails.DATA};
            ContentResolver resolver = context.getContentResolver();
            Uri uri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
            try {
                Cursor cursor = resolver.query(uri, columns, null, null, null);
                if (cursor == null)
                    return hashMap;
                while (cursor.moveToNext()) {
                    hashMap.put(cursor.getString(0), cursor.getString(1));
                }
                cursor.close();
            } catch (Exception e) {
                Logger.e("queryThumb", e);
            }
            return hashMap;
        }

        private String getThumb(Command... queries) {
            if (Util.isEmpty(queries))
                return "";
            for (Command command : queries) {
                String path = command.execute();
                if (TextUtils.isEmpty(path) || !FileUtil.fileExists(path)) {
                    continue;
                }
                return path;
            }
            return "";
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            VSSystemVideoDataSourceModel model = this.getModel();
            if (model != null) {
                model.loadingCancel();
            }
            this.destroy();
        }

        @Override
        protected void onPostExecute(Pair<HashMap<String, VSFolder>,
                HashMap<String, List<VSVideo>>> hashMapHashMapPair) {
            super.onPostExecute(hashMapHashMapPair);
            VSSystemVideoDataSourceModel model = this.getModel();
            if (model != null) {
                model.loadingEnd(hashMapHashMapPair.first, hashMapHashMapPair.second);
            }
            this.destroy();
        }

        private VSSystemVideoDataSourceModel getModel() {
            return this.reference == null ? null : this.reference.get();
        }

        private boolean usable() {
            return (this.getStatus() == Status.RUNNING || this.getStatus() == Status.PENDING)
                    && !this.isCancelled() && !this.destroy;
        }

        private void destroy() {
            this.destroy = true;
            if (this.reference != null) {
                this.reference.clear();
                this.reference = null;
            }
        }

        private int parseInt(String s) {
            try {
                if (TextUtils.isEmpty(s))
                    return 0;
                return Integer.parseInt(s);
            } catch (Exception e) {
                return 0;
            }
        }

        private long parseLong(String s) {
            try {
                if (TextUtils.isEmpty(s))
                    return 0;
                return Long.parseLong(s);
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public interface Command {
        String execute();
    }
}

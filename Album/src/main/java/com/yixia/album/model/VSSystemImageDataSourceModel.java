package com.yixia.album.model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Pair;

import com.yixia.album.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
public class VSSystemImageDataSourceModel implements VSMediaDataSourceModel<VSFolder, VSMedia> {

    private static final String DEFAULT_CAMERA_ALBUM = "Camera";
    private static final int minLength = 300;
    private static final HashSet<String> unwantedFolder = new HashSet<>();
    static {
        unwantedFolder.add("resources");
        unwantedFolder.add("cache");
    }
    private LoadingAsyncTask task;
    private OnMediaDataLoadingListener loadingListener;

    private List<VSFolder> folders = new ArrayList<>();
    private HashMap<String, List<VSMedia>> medias = new HashMap<>();
    private boolean hasLoaded = false;
    private String allImagesName = "allImages";
    private String allImagesKey = MD5.md5Encode("allImages");

    public VSSystemImageDataSourceModel(Context context, OnMediaDataLoadingListener listener) {
        this.loadingListener = listener;
        if (context != null) {
            this.allImagesName = context.getResources().getString(R.string.importAlbumAllImage);
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
    public List<VSMedia> getAllMedias() {
        return this.getMedias(this.allImagesKey);
    }

    /**
     * 获取文件夹下的媒体资源
     *
     * @param folder 文件夹
     * @return 当前文件夹下的媒体资源
     */
    @Override
    public List<VSMedia> getMedias(VSFolder folder) {
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
    public List<VSMedia> getMedias(String folderPath) {
        List<VSMedia> array = medias.get(folderPath);
        return array == null ? new ArrayList<VSMedia>() : array;
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

    public void loadingEnd(HashMap<String, VSFolder> folders, HashMap<String, List<VSMedia>> medias) {
        this.hasLoaded = true;
        this.folders.addAll(folders.values());
        this.medias.putAll(medias);
        this.addAllFolder();
        if (this.loadingListener != null) {
            this.loadingListener.onLoadingEnd();
        }
    }

    private void addAllFolder() {
        List<VSMedia> all = new ArrayList<>();
        for (List<VSMedia> array : this.medias.values()) {
            all.addAll(array);
        }
        if (Util.isEmpty(all))
            return;
        VSFolder folder = new VSFolder();
        folder.setId(this.allImagesKey);
        folder.setPath(this.allImagesKey);
        folder.setName(this.allImagesName);
        folder.setDisplay(folder.getName());
        folder.setThumbnail(all.get(0).getThumbnail());
        this.folders.add(0, folder);
        this.medias.put(this.allImagesKey, all);
    }

    private static class LoadingAsyncTask extends AsyncTask<Context, Integer, Pair<HashMap<String, VSFolder>, HashMap<String, List<VSMedia>>>> {

        private boolean destroy = false;
        private SoftReference<VSSystemImageDataSourceModel> reference;

        LoadingAsyncTask(VSSystemImageDataSourceModel mediaDataSourceModel) {
            this.reference = new SoftReference<>(mediaDataSourceModel);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            VSSystemImageDataSourceModel model = this.getModel();
            if (model != null) {
                model.loadingStart();
            }
        }

        @Override
        protected Pair<HashMap<String, VSFolder>, HashMap<String, List<VSMedia>>> doInBackground(Context... contexts) {
            HashMap<String, VSFolder> folders = new HashMap<>();
            HashMap<String, List<VSMedia>> medias = new HashMap<>();
            if (contexts == null || contexts.length == 0 || contexts[0] == null) {
                return Pair.create(folders, medias);
            }
            DirMgmt.getInstance().init(contexts[0]);
            final HashMap<String, String> thumbs = this.queryThumb(contexts[0]);
            final String[] projection = {
                    Images.Media._ID,
                    Images.Media.TITLE,
                    Images.Media.DISPLAY_NAME,
                    Images.Media.DATA,
                    Images.Media.MIME_TYPE,
                    Images.Media.HEIGHT,
                    Images.Media.WIDTH,
                    Images.Media.SIZE,
                    Images.Media.DATE_ADDED,
                    Images.Media.DATE_MODIFIED,
                    Images.ImageColumns.DATA,
                    Images.ImageColumns.BUCKET_ID,
                    Images.ImageColumns.BUCKET_DISPLAY_NAME
            };
            Context context = contexts[0];
            Cursor cursor = null;
            try {
                cursor = Images.Media.query(context.getContentResolver(), Images.Media.EXTERNAL_CONTENT_URI, projection);
            } catch (Exception e) {
                Logger.e("queryMedia", e);
            }
            if (cursor == null) {
                return Pair.create(folders, medias);
            }
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(Images.Media.DATA));
                if (!isImage(path)) {
                    continue;
                }
                if (!FileUtil.fileExists(path)) {
                    continue;
                }
                final VSMedia media = new VSMedia();
                int columnIndex = 0;
                media.setId(cursor.getString(columnIndex++));
                media.setTitle(cursor.getString(columnIndex++));
                media.setDisplay(cursor.getString(columnIndex++));
                media.setPath(cursor.getString(columnIndex++));
                media.setMimeType(cursor.getString(columnIndex++));
                media.setHeight(this.parseInt(cursor.getString(columnIndex++)));
                media.setWidth(this.parseInt(cursor.getString(columnIndex++)));
                media.setSize(this.parseLong(cursor.getString(columnIndex++)));
                media.setCreated(this.parseLong(cursor.getString(columnIndex++)));
                media.setModified(this.parseLong(cursor.getString(columnIndex++)));
                media.setFolderId(cursor.getString(columnIndex++));
                if ((media.getWidth() <= minLength && media.getHeight() <= minLength)
                        || (media.getWidth() <= 0 || media.getHeight() <= 0)) {
                    continue;
                }
                float ratio = media.getWidth() * 1f / media.getHeight();
                if ((ratio > 5f || ratio < 0.2f)
                        && (media.getHeight() < minLength || media.getWidth() < minLength)) {
                    continue;
                }
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

                //Create folder
                if (!folders.containsKey(media.getFolder())) {
                    VSFolder folder = new VSFolder();
                    folder.setId(media.getFolderId());
                    folder.setPath(media.getFolder());
                    folder.setName(cursor.getString(columnIndex++));
                    folder.setDisplay(cursor.getString(columnIndex++));
                    folder.setThumbnail(media.getThumbnail());
                    folders.put(media.getFolder(), folder);
                }

                //
                if (!medias.containsKey(media.getFolder()) || medias.get(media.getFolder()) == null) {
                    medias.put(media.getFolder(), new ArrayList<VSMedia>());
                }
                List<VSMedia> array = medias.get(media.getFolder());
                array.add(media);

                Logger.d("last columnIndex " + columnIndex);
            }
            cursor.close();
            List<VSFolder> allFolder = new ArrayList<>(folders.values());
            for (VSFolder folder: allFolder) {
                if (folder == null) {
                    continue;
                }
                if (unwantedFolder.contains(folder.getDisplay())) {
                    folders.remove(folder.getPath());
                    medias.remove(folder.getPath());
                }
            }
            return Pair.create(folders, medias);
        }

        private boolean isImage(String path) {
            String imageExtensions = "jpg||jpeg||gif||png||bmp||wbmp||webp";
            String extension = FileUtil.extractFileExtension(path);
            return !TextUtils.isEmpty(extension) &&
                    imageExtensions.contains(extension.toLowerCase());
        }

        private HashMap<String, String> queryThumb(Context context) {
            HashMap<String, String> hashMap = new HashMap<>();
            if (context == null) {
                return hashMap;
            }
            String[] columns = {Images.Thumbnails.IMAGE_ID, Images.Thumbnails.DATA};
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Images.Thumbnails.EXTERNAL_CONTENT_URI;
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
            VSSystemImageDataSourceModel model = this.getModel();
            if (model != null) {
                model.loadingCancel();
            }
            this.destroy();
        }

        @Override
        protected void onPostExecute(Pair<HashMap<String, VSFolder>,
                HashMap<String, List<VSMedia>>> hashMapHashMapPair) {
            super.onPostExecute(hashMapHashMapPair);
            VSSystemImageDataSourceModel model = this.getModel();
            if (model != null) {
                model.loadingEnd(hashMapHashMapPair.first, hashMapHashMapPair.second);
            }
            this.destroy();
        }

        private VSSystemImageDataSourceModel getModel() {
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

package com.yixia.album.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import yixia.lib.core.util.FileUtil;

/**
 * Created by zhangwy on 2018/10/17 下午5:02.
 * Updated by zhangwy on 2018/10/17 下午5:02.
 * Description
 */
@SuppressWarnings("unused")
public class VSMedia implements Parcelable, Comparable<VSMedia> {
    private String id;
    private String title;
    private String display;
    private String path;
    private String folderId;
    private String folder;
    private String mimeType;
    private String thumbnail;
    private int height;
    private int width;
    private int position = -1;
    private long size;
    private long created;
    private long modified;
    private boolean checked = false;

    public VSMedia() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolder() {
        if (TextUtils.isEmpty(folder)) {
            folder = FileUtil.extractFilePath(this.path);
        }
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void readFromParcel(Parcel in) {
        this.setId(in.readString());
        this.setTitle(in.readString());
        this.setDisplay(in.readString());
        this.setPath(in.readString());
        this.setFolderId(in.readString());
        this.setFolder(in.readString());
        this.setMimeType(in.readString());
        this.setThumbnail(in.readString());
        this.setHeight(in.readInt());
        this.setWidth(in.readInt());
        this.setPosition(in.readInt());
        this.setChecked(in.readInt() == 1);
        this.setSize(in.readLong());
        this.setCreated(in.readLong());
        this.setModified(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        dest.writeString(this.getTitle());
        dest.writeString(this.getDisplay());
        dest.writeString(this.getPath());
        dest.writeString(this.getFolderId());
        dest.writeString(this.getFolder());
        dest.writeString(this.getMimeType());
        dest.writeString(this.getThumbnail());
        dest.writeInt(this.getHeight());
        dest.writeInt(this.getWidth());
        dest.writeInt(this.getPosition());
        dest.writeInt(this.isChecked() ? 1 : 0);
        dest.writeLong(this.getSize());
        dest.writeLong(this.getCreated());
        dest.writeLong(this.getModified());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VSMedia> CREATOR = new Creator<VSMedia>() {
        @Override
        public VSMedia createFromParcel(Parcel in) {
            return new VSMedia(in);
        }

        @Override
        public VSMedia[] newArray(int size) {
            return new VSMedia[size];
        }
    };

    VSMedia(Parcel in) {
        this.readFromParcel(in);
    }

    @Override
    public int compareTo(@NonNull VSMedia old) {
        try {
            return (int) (old.getCreated() - this.getCreated());
        } catch (Exception e) {
            return 0;
        }
    }
}

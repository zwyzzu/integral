package com.yixia.album.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangwy on 2018/6/2 下午5:30.
 * Updated by zhangwy on 2018/6/2 下午5:30.
 * Description 目录类
 */
@SuppressWarnings("unused")
public class VSFolder implements Parcelable {

    public VSFolder() {
        super();
    }

    private String id;
    private String path;
    private String name;
    private String display;
    private String thumbnail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        dest.writeString(this.getPath());
        dest.writeString(this.getName());
        dest.writeString(this.getDisplay());
        dest.writeString(this.getThumbnail());
    }

    public static final Parcelable.Creator<VSFolder> CREATOR = new Parcelable.Creator<VSFolder>() {
        public VSFolder createFromParcel(Parcel in) {
            return new VSFolder(in);
        }

        public VSFolder[] newArray(int size) {
            return new VSFolder[size];
        }
    };

    private VSFolder(Parcel parcel) {
        super();
        this.setId(parcel.readString());
        this.setPath(parcel.readString());
        this.setName(parcel.readString());
        this.setDisplay(parcel.readString());
        this.setThumbnail(parcel.readString());
    }
}

package com.yixia.album.model;

import android.os.Parcel;

/**
 * Created by zhangwy on 2018/6/2 下午5:31.
 * Updated by zhangwy on 2018/6/2 下午5:31.
 * Description 媒体资源类
 */
@SuppressWarnings("unused")
public class VSVideo extends VSMedia {
    private long duration;

    public VSVideo() {
        super();
    }
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        this.setDuration(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.getDuration());
    }

    private VSVideo(Parcel in) {
        super(in);
    }

    public static final Creator<VSVideo> CREATOR = new Creator<VSVideo>() {
        @Override
        public VSVideo createFromParcel(Parcel in) {
            return new VSVideo(in);
        }

        @Override
        public VSVideo[] newArray(int size) {
            return new VSVideo[size];
        }
    };
}

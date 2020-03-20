package com.zhangwy.integral.entity;

import android.os.Parcel;

/**
 * Created by zhangwy on 2020-03-20.
 * Updated by zhangwy on 2020-03-20.
 * Description
 */
@SuppressWarnings("unused")
public class BookingEntity extends BaseEntity implements Comparable<BookingEntity> {
    private String text;
    private String desc;
    private long lastUseTime;

    public BookingEntity() {
        super();
    }

    public BookingEntity(Parcel in) {
        super(in);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(long lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    @Override
    void readParcel(Parcel in) {
        this.setText(in.readString());
        this.setDesc(in.readString());
        this.setLastUseTime(in.readLong());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeString(this.getText());
        dest.writeString(this.getDesc());
        dest.writeLong(this.getLastUseTime());
    }

    @Override
    public int compareTo(BookingEntity entity) {
        long poor = entity.getLastUseTime() - this.getLastUseTime();
        return (int) poor;
    }

    public static final Creator<BookingEntity> CREATOR = new Creator<BookingEntity>() {
        @Override
        public BookingEntity createFromParcel(Parcel in) {
            return new BookingEntity(in);
        }

        @Override
        public BookingEntity[] newArray(int size) {
            return new BookingEntity[size];
        }
    };
}

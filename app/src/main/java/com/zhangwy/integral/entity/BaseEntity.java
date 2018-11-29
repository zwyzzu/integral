package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhangwy on 2018/11/29 下午6:33.
 * Updated by zhangwy on 2018/11/29 下午6:33.
 * Description 基类
 */
@SuppressWarnings("unused")
public abstract class BaseEntity implements Parcelable {
    private String id;

    BaseEntity(Parcel in) {
        this.setId(in.readString());
        this.readParcel(in);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getId());
        this.write2Parcel(dest, flags);
    }

    abstract void readParcel(Parcel in);

    abstract void write2Parcel(Parcel dest, int flags);

    @Override
    public int describeContents() {
        return 0;
    }

    /**

     public static final Creator<MemberEntity> CREATOR = new Creator<MemberEntity>() {
    @Override
    public MemberEntity createFromParcel(Parcel in) {
    return new MemberEntity(in);
    }

    @Override
    public MemberEntity[] newArray(int size) {
    return new MemberEntity[size];
    }
    };
     */
}

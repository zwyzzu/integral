package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by zhangwy on 2018/11/29 下午7:10.
 * Updated by zhangwy on 2018/11/29 下午7:10.
 * Description 地址信息
 */
public class AddressEntity extends BaseEntity implements Comparable<AddressEntity> {

    private String address;
    private String desc;
    private String bind;
    private int position;

    private AddressEntity(Parcel in) {
        super(in);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    void readParcel(Parcel in) {
        this.setAddress(in.readString());
        this.setDesc(in.readString());
        this.setBind(in.readString());
        this.setPosition(in.readInt());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeString(this.getAddress());
        dest.writeString(this.getDesc());
        dest.writeString(this.getBind());
        dest.writeInt(this.getPosition());
    }

    public static final Creator<AddressEntity> CREATOR = new Creator<AddressEntity>() {
        @Override
        public AddressEntity createFromParcel(Parcel in) {
            return new AddressEntity(in);
        }

        @Override
        public AddressEntity[] newArray(int size) {
            return new AddressEntity[size];
        }
    };

    @Override
    public int compareTo(@NonNull AddressEntity o) {
        return 0;
    }
}

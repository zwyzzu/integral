package com.zhangwy.integral.entity;

import android.os.Parcel;

/**
 * Created by zhangwy on 2018/12/8 上午8:22.
 * Updated by zhangwy on 2018/12/8 上午8:22.
 * Description
 */
@SuppressWarnings("unused")
public class IntegralEntity extends BaseEntity {

    private int score;//分数
    private String name;
    private String desc;//描述

    IntegralEntity(Parcel in) {
        super(in);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    void readParcel(Parcel in) {
        this.setScore(in.readInt());
        this.setName(in.readString());
        this.setDesc(in.readString());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeInt(this.getScore());
        dest.writeString(this.getName());
        dest.writeString(this.getDesc());
    }

    public static final Creator<IntegralEntity> CREATOR = new Creator<IntegralEntity>() {
        @Override
        public IntegralEntity createFromParcel(Parcel in) {
            return new IntegralEntity(in);
        }

        @Override
        public IntegralEntity[] newArray(int size) {
            return new IntegralEntity[size];
        }
    };
}

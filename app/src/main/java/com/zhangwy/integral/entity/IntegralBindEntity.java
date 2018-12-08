package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by zhangwy on 2018/11/29 下午7:23.
 * Updated by zhangwy on 2018/11/29 下午7:23.
 * Description
 */
@SuppressWarnings("unused")
public class IntegralBindEntity extends BaseEntity implements Comparable<IntegralBindEntity> {

    private int score;//分数
    private long date;//日期
    private String desc;//描述
    private String bind;//绑定人物ID
    private String scoreBind;//绑定积分ID

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public String getScoreBind() {
        return scoreBind;
    }

    public void setScoreBind(String scoreBind) {
        this.scoreBind = scoreBind;
    }

    IntegralBindEntity(Parcel in) {
        super(in);
    }

    @Override
    void readParcel(Parcel in) {
        this.setScore(in.readInt());
        this.setDate(in.readLong());
        this.setDesc(in.readString());
        this.setBind(in.readString());
        this.setScoreBind(in.readString());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeInt(this.getScore());
        dest.writeLong(this.getDate());
        dest.writeString(this.getDesc());
        dest.writeString(this.getBind());
        dest.writeString(this.getScoreBind());
    }

    @Override
    public int compareTo(@NonNull IntegralBindEntity entity) {
        return 0;
    }

    public static final Creator<IntegralBindEntity> CREATOR = new Creator<IntegralBindEntity>() {
        @Override
        public IntegralBindEntity createFromParcel(Parcel in) {
            return new IntegralBindEntity(in);
        }

        @Override
        public IntegralBindEntity[] newArray(int size) {
            return new IntegralBindEntity[size];
        }
    };
}

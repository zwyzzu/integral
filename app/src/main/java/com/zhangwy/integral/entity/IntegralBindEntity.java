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

    private int score = 0;//分数
    private int usedScore = 0;//分数
    private long createDate;//积分日期
    private long usedDate;//使用日期
    private String desc;//描述
    private String bind;//绑定ID
    private String scoreBind;//绑定积分ID

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getUsedScore() {
        return usedScore;
    }

    public void setUsedScore(int usedScore) {
        this.usedScore = usedScore;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(long usedDate) {
        this.usedDate = usedDate;
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

    public boolean useable() {
        return this.useableScore() > 0;
    }

    public int useableScore() {
        return this.getScore() - this.getUsedScore();
    }

    public IntegralBindEntity() {
        super();
    }

    IntegralBindEntity(Parcel in) {
        super(in);
    }

    @Override
    void readParcel(Parcel in) {
        this.setScore(in.readInt());
        this.setUsedScore(in.readInt());
        this.setCreateDate(in.readLong());
        this.setUsedDate(in.readLong());
        this.setDesc(in.readString());
        this.setBind(in.readString());
        this.setScoreBind(in.readString());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeInt(this.getScore());
        dest.writeInt(this.getUsedScore());
        dest.writeLong(this.getCreateDate());
        dest.writeLong(this.getUsedDate());
        dest.writeString(this.getDesc());
        dest.writeString(this.getBind());
        dest.writeString(this.getScoreBind());
    }

    @Override
    public int compareTo(@NonNull IntegralBindEntity entity) {
        long poor = this.getCreateDate() - entity.getCreateDate();
        return (int) poor;
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

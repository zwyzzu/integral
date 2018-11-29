package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by zhangwy on 2018/11/29 下午7:23.
 * Updated by zhangwy on 2018/11/29 下午7:23.
 * Description TODO
 */
public class IntegralEntity extends BaseEntity implements Comparable<IntegralEntity> {

    private int score;
    private long date;
    private String bind;
    private String scoreBind;

    private IntegralEntity(Parcel in) {
        super(in);
    }

    @Override
    void readParcel(Parcel in) {

    }

    @Override
    void write2Parcel(Parcel dest, int flags) {

    }

    @Override
    public int compareTo(@NonNull IntegralEntity o) {
        return 0;
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

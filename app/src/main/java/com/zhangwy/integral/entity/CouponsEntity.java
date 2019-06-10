package com.zhangwy.integral.entity;

import android.os.Parcel;

/**
 * Created by zhangwy on 2019/6/4.
 * Updated by zhangwy on 2019/6/4.
 * Description 优惠券实体类
 */
@SuppressWarnings("unused")
public class CouponsEntity extends BaseEntity {
    private float amount;//金额
    private String name;//名称
    private String desc;//描述
    private boolean checkCoefficient = false;//自动勾选系数
    public CouponsEntity() {
        super();
    }

    CouponsEntity(Parcel in) {
        super(in);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
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

    public boolean isCheckCoefficient() {
        return checkCoefficient;
    }

    public void setCheckCoefficient(boolean checkCoefficient) {
        this.checkCoefficient = checkCoefficient;
    }

    @Override
    void readParcel(Parcel in) {
        this.setAmount(in.readFloat());
        this.setName(in.readString());
        this.setDesc(in.readString());
        this.setCheckCoefficient(in.readInt() == 1);
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeFloat(this.getAmount());
        dest.writeString(this.getName());
        dest.writeString(this.getDesc());
        dest.writeInt(this.isCheckCoefficient() ? 1 : 0);
    }

    public static final Creator<CouponsEntity> CREATOR = new Creator<CouponsEntity>() {
        @Override
        public CouponsEntity createFromParcel(Parcel in) {
            return new CouponsEntity(in);
        }

        @Override
        public CouponsEntity[] newArray(int size) {
            return new CouponsEntity[size];
        }
    };
}

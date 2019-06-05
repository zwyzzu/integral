package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.text.TextUtils;
import com.zhangwy.integral.entity.CouponsExpiryEntity.Expiry;

import java.util.Date;

import yixia.lib.core.util.TimeUtil;

/**
 * Created by zhangwy on 2019/6/4.
 * Updated by zhangwy on 2019/6/4.
 * Description
 */
@SuppressWarnings("unused")
public class CouponsBindEntity extends BaseEntity {
    private float amount;//金额
    private long createDate;//产生优惠券日期
    private long expiryDate;//有效期
    private long usedDate;//使用日期
    private String expiryCode;//有效期类型
    private String name;//优惠券名称
    private String desc;//描述
    private String bind;//绑定ID
    private String couponsBind;//绑定优惠券ID
    private String expiryBind;//绑定有效期ID

    public CouponsBindEntity() {
        super();
    }

    CouponsBindEntity(Parcel in) {
        super(in);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public long getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(long usedDate) {
        this.usedDate = usedDate;
    }

    public String getExpiryCode() {
        return expiryCode;
    }

    public void setExpiryCode(String expiryCode) {
        this.expiryCode = expiryCode;
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

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public String getCouponsBind() {
        return couponsBind;
    }

    public void setCouponsBind(String couponsBind) {
        this.couponsBind = couponsBind;
    }

    public String getExpiryBind() {
        return expiryBind;
    }

    public void setExpiryBind(String expiryBind) {
        this.expiryBind = expiryBind;
    }

    public void setExpiry(CouponsExpiryEntity expiry) {
        if (expiry == null) {
            return;
        }
        this.setExpiryBind(expiry.getId());
        this.setExpiryCode(expiry.getExpiryCode());
        Date create = TimeUtil.dateLong2Date(this.getCreateDate());
        String time = "";
        switch (expiry.getExpiry()) {
            case DAY:
                TimeUtil.getFutureDateByDay(create, TimeUtil.PATTERN_DAY4Y, expiry.getCount());
                break;
            case WEEK:
                TimeUtil.getFutureDateByWeek(create, TimeUtil.PATTERN_DAY4Y, expiry.getCount());
                break;
            case MONTH:
                TimeUtil.getFutureDateByMonth(create, TimeUtil.PATTERN_DAY4Y, expiry.getCount());
                break;
            case YEAR:
                TimeUtil.getFutureDateByYear(create, TimeUtil.PATTERN_DAY4Y, expiry.getCount());
                break;
            case FOREVER:
                time = "";
                break;
        }
        if (TextUtils.isEmpty(time)) {
            this.setExpiryDate(-1);
        } else {
            long timeMs = TimeUtil.dateString2Long(time, TimeUtil.PATTERN_DAY4Y);
            this.setExpiryDate(timeMs - 1);
        }
    }

    public boolean used() {
        return this.getUsedDate() > this.getCreateDate();
    }

    public boolean overdue() {
        if (this.used()) {
            return false;
        }
        if (TextUtils.equals(this.getExpiryCode(), Expiry.FOREVER.code)) {
            return false;
        }
        return System.currentTimeMillis() < this.getExpiryDate();
    }

    public boolean useable() {
        return !this.used() && !this.overdue();
    }

    @Override
    void readParcel(Parcel in) {
        this.setAmount(in.readFloat());
        this.setCreateDate(in.readLong());
        this.setExpiryDate(in.readLong());
        this.setExpiryCode(in.readString());
        this.setName(in.readString());
        this.setDesc(in.readString());
        this.setBind(in.readString());
        this.setCouponsBind(in.readString());
        this.setExpiryBind(in.readString());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeFloat(this.getAmount());
        dest.writeLong(this.getCreateDate());
        dest.writeLong(this.getExpiryDate());
        dest.writeString(this.getExpiryCode());
        dest.writeString(this.getName());
        dest.writeString(this.getDesc());
        dest.writeString(this.getBind());
        dest.writeString(this.getCouponsBind());
        dest.writeString(this.getExpiryBind());
    }

    public static final Creator<CouponsBindEntity> CREATOR = new Creator<CouponsBindEntity>() {
        @Override
        public CouponsBindEntity createFromParcel(Parcel in) {
            return new CouponsBindEntity(in);
        }

        @Override
        public CouponsBindEntity[] newArray(int size) {
            return new CouponsBindEntity[size];
        }
    };
}
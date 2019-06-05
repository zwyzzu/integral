package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.zhangwy.integral.R;

/**
 * Created by zhangwy on 2019/6/4.
 * Updated by zhangwy on 2019/6/4.
 * Description
 */
@SuppressWarnings("unused")
public class CouponsExpiryEntity extends BaseEntity {
    private int count;
    private String expiryCode;

    public CouponsExpiryEntity() {
        super();
    }

    CouponsExpiryEntity(Parcel in) {
        super(in);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getExpiryCode() {
        return expiryCode;
    }

    public void setExpiryCode(String expiryCode) {
        this.expiryCode = expiryCode;
    }

    public Expiry getExpiry() {
        return Expiry.find(this.expiryCode);
    }

    public void setExpiry(Expiry expiry) {
        this.setExpiryCode(expiry.code);
    }

    @Override
    void readParcel(Parcel in) {
        this.setCount(in.readInt());
        this.setExpiryCode(in.readString());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeInt(this.getCount());
        dest.writeString(this.getExpiryCode());
    }

    public static final Creator<CouponsExpiryEntity> CREATOR = new Creator<CouponsExpiryEntity>() {
        @Override
        public CouponsExpiryEntity createFromParcel(Parcel in) {
            return new CouponsExpiryEntity(in);
        }

        @Override
        public CouponsExpiryEntity[] newArray(int size) {
            return new CouponsExpiryEntity[size];
        }
    };

    public enum Expiry {
        DAY("expiryDay", "天", R.string.coupons_expiry_day),
        WEEK("expiryWeek", "周", R.string.coupons_expiry_forever),
        MONTH("expiryMonth", "月", R.string.coupons_expiry_month),
        YEAR("expiryYear", "年", R.string.coupons_expiry_year),
        FOREVER("expiryForever", "长期", R.string.coupons_expiry_forever);

        public String code;
        public String name;
        public @StringRes
        int res;

        Expiry(String code, String name, @StringRes int res) {
            this.code = code;
            this.name = name;
            this.res = res;
        }

        public static Expiry find(String code) {
            for (Expiry expiry : Expiry.values()) {
                if (TextUtils.equals(code, expiry.code)) {
                    return expiry;
                }
            }
            return DAY;
        }
    }
}

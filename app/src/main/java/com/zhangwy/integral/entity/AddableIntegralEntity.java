package com.zhangwy.integral.entity;

import android.os.Parcel;

/**
 * Created by zhangwy on 2018/12/8 下午2:34.
 * Updated by zhangwy on 2018/12/8 下午2:34.
 * Description
 */
@SuppressWarnings("unused")
public class AddableIntegralEntity extends IntegralEntity implements Addable {
    private boolean addable = true;

    private AddableIntegralEntity(Parcel in) {
        super(in);
    }

    @Override
    public void setAddable(boolean addable) {
        this.addable = addable;
    }

    @Override
    public boolean canAdd() {
        return this.addable;
    }

    @Override
    void readParcel(Parcel in) {
        super.readParcel(in);
        this.setAddable(in.readInt() == 1);
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        super.write2Parcel(dest, flags);
        dest.writeInt(this.addable ? 1 : 0);
    }

    public static final Creator<AddableIntegralEntity> CREATOR = new Creator<AddableIntegralEntity>() {
        @Override
        public AddableIntegralEntity createFromParcel(Parcel in) {
            return new AddableIntegralEntity(in);
        }

        @Override
        public AddableIntegralEntity[] newArray(int size) {
            return new AddableIntegralEntity[size];
        }
    };
}

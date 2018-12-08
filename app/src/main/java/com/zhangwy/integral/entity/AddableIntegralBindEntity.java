package com.zhangwy.integral.entity;

import android.os.Parcel;

/**
 * Created by zhangwy on 2018/12/8 下午2:31.
 * Updated by zhangwy on 2018/12/8 下午2:31.
 * Description
 */
@SuppressWarnings("unused")
public class AddableIntegralBindEntity extends IntegralBindEntity implements Addable {
    private boolean addable = true;

    private AddableIntegralBindEntity(Parcel in) {
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

    public static final Creator<AddableIntegralBindEntity> CREATOR = new Creator<AddableIntegralBindEntity>() {
        @Override
        public AddableIntegralBindEntity createFromParcel(Parcel in) {
            return new AddableIntegralBindEntity(in);
        }

        @Override
        public AddableIntegralBindEntity[] newArray(int size) {
            return new AddableIntegralBindEntity[size];
        }
    };
}

package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwy on 2018/11/29 下午6:38.
 * Updated by zhangwy on 2018/11/29 下午6:38.
 * Description
 */
@SuppressWarnings("unused")
public class MemberEntity extends BaseEntity implements Parcelable {

    private String name;
    private String icon;
    private String desc;
    private int sex;
    private int age;
    private int marital;
    private int children;
    private List<AddressEntity> address = new ArrayList<>();

    private MemberEntity(Parcel in) {
        super(in);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getMarital() {
        return marital;
    }

    public void setMarital(int marital) {
        this.marital = marital;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public List<AddressEntity> getAddress() {
        return address;
    }

    public void setAddress(List<AddressEntity> address) {
        this.address = address;
    }

    public void putAddress(AddressEntity address) {
        this.address.add(address);
    }

    @Override
    void readParcel(Parcel in) {
        this.setName(in.readString());
        this.setIcon(in.readString());
        this.setDesc(in.readString());
        this.setSex(in.readInt());
        this.setAge(in.readInt());
        this.setMarital(in.readInt());
        this.setChildren(in.readInt());
        this.setAddress(in.createTypedArrayList(AddressEntity.CREATOR));
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeString(this.getName());
        dest.writeString(this.getIcon());
        dest.writeString(this.getDesc());
        dest.writeInt(this.getSex());
        dest.writeInt(this.getAge());
        dest.writeInt(this.getMarital());
        dest.writeInt(this.getChildren());
        dest.writeTypedList(this.getAddress());
    }

    public static final Creator<MemberEntity> CREATOR = new Creator<MemberEntity>() {
        @Override
        public MemberEntity createFromParcel(Parcel in) {
            return new MemberEntity(in);
        }

        @Override
        public MemberEntity[] newArray(int size) {
            return new MemberEntity[size];
        }
    };
}

package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by zhangwy on 2018/11/29 下午7:10.
 * Updated by zhangwy on 2018/11/29 下午7:10.
 * Description 地址信息
 */
@SuppressWarnings("unused")
public class AddressEntity extends BaseEntity implements Comparable<AddressEntity> {

    private String tag;//标签
    private String phone;//电话
    private String consignee;//收件人
    private String province;//省
    private String city;//市
    private String area;//县/区
    private String town;//街道
    private String address;//地址
    private String desc;//描述
    private String bind;//绑定人物
    private int position;//顺序

    public AddressEntity() {
        super();
    }

    AddressEntity(Parcel in) {
        super(in);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String address() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(this.getProvince());
        buffer.append(this.getCity());
        buffer.append(this.getArea());
        buffer.append(this.getTown());
        buffer.append(this.getAddress());
        return buffer.toString();
    }

    @Override
    void readParcel(Parcel in) {
        this.setTag(in.readString());
        this.setPhone(in.readString());
        this.setConsignee(in.readString());
        this.setProvince(in.readString());
        this.setCity(in.readString());
        this.setArea(in.readString());
        this.setTown(in.readString());
        this.setAddress(in.readString());
        this.setDesc(in.readString());
        this.setBind(in.readString());
        this.setPosition(in.readInt());
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeString(this.getTag());
        dest.writeString(this.getPhone());
        dest.writeString(this.getConsignee());
        dest.writeString(this.getProvince());
        dest.writeString(this.getCity());
        dest.writeString(this.getArea());
        dest.writeString(this.getTown());
        dest.writeString(this.getAddress());
        dest.writeString(this.getDesc());
        dest.writeString(this.getBind());
        dest.writeInt(this.getPosition());
    }

    public static final Creator<AddressEntity> CREATOR = new Creator<AddressEntity>() {
        @Override
        public AddressEntity createFromParcel(Parcel in) {
            return new AddressEntity(in);
        }

        @Override
        public AddressEntity[] newArray(int size) {
            return new AddressEntity[size];
        }
    };

    @Override
    public int compareTo(@NonNull AddressEntity entity) {
        return this.getPosition() - entity.getPosition();
    }
}

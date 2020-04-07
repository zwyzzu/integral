package com.zhangwy.integral.entity;

import android.os.Parcel;

/**
 * Created by zhangwy on 2020-03-22.
 * Updated by zhangwy on 2020-03-22.
 * Description 预订绑定对象
 *             该对象绑定预订项ID和memberId
 *             预订项ID非严格绑定，删除预订项不影响客户预订显示，但是在预订列表页不可见仅客户界面可见
 */
@SuppressWarnings("unused")
public class BookingBindEntity extends BaseEntity {

    private String text;//预订项文本
    private String desc;//预订项描述
    private String bookingId;//预订项ID
    private String bind;//绑定ID
    private String addressId;//地址ID
    private String bindName;
    private String bindIcon;
    private int count;//下单份数
    private long createTime;//创建时间
    private long orderTime;//下单时间
    private long invalidTime;//作废时间
    private boolean ordered = false;//是否已下单
    private boolean invalid = false;//是否已作废
    private AddressEntity address;

    public BookingBindEntity() {
        super();
    }

    public BookingBindEntity(Parcel in) {
        super(in);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getBindName() {
        return bindName;
    }

    public void setBindName(String bindName) {
        this.bindName = bindName;
    }

    public String getBindIcon() {
        return bindIcon;
    }

    public void setBindIcon(String bindIcon) {
        this.bindIcon = bindIcon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public long getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(long invalidTime) {
        this.invalidTime = invalidTime;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public String getAddressText() {
        return this.address == null ? "" : this.address.address();
    }

    @Override
    void readParcel(Parcel in) {
        this.setText(in.readString());
        this.setDesc(in.readString());
        this.setBookingId(in.readString());
        this.setBind(in.readString());
        this.setAddressId(in.readString());
        this.setBindName(in.readString());
        this.setBindIcon(in.readString());
        this.setCreateTime(in.readLong());
        this.setOrderTime(in.readLong());
        this.setInvalidTime(in.readLong());
        this.setCount(in.readInt());
        this.setOrdered(in.readInt() == 1);
        this.setInvalid(in.readInt() == 1);
        this.setAddress(in.readParcelable(AddressEntity.class.getClassLoader()));
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeString(this.getText());
        dest.writeString(this.getDesc());
        dest.writeString(this.getBookingId());
        dest.writeString(this.getBind());
        dest.writeString(this.getAddressId());
        dest.writeString(this.getBindName());
        dest.writeString(this.getBindIcon());
        dest.writeLong(this.getCreateTime());
        dest.writeLong(this.getOrderTime());
        dest.writeLong(this.getInvalidTime());
        dest.writeInt(this.getCount());
        dest.writeInt(this.isOrdered() ? 1 : 0);
        dest.writeInt(this.isInvalid() ? 1 : 0);
        dest.writeParcelable(this.getAddress(), flags);
    }

    public static final Creator<BookingBindEntity> CREATOR = new Creator<BookingBindEntity>() {
        @Override
        public BookingBindEntity createFromParcel(Parcel in) {
            return new BookingBindEntity(in);
        }

        @Override
        public BookingBindEntity[] newArray(int size) {
            return new BookingBindEntity[size];
        }
    };
}

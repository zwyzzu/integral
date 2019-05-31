package com.zhangwy.integral.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.zhangwy.integral.R;

import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/11/29 下午6:38.
 * Updated by zhangwy on 2018/11/29 下午6:38.
 * Description
 */
@SuppressWarnings("unused")
public class MemberEntity extends BaseEntity implements Parcelable {

    private final String dbNull = "unknown";
    private String name;
    private String icon;
    private String desc;
    private String phone;
    private String birthday;
    private int sex;
    private int age;
    private int marital;//婚否
    private int sonCount;
    private int daughterCount;
    private long created;//创建时间
    private long modified;//最后修改时间
    private List<AddressEntity> address = new ArrayList<>();
    private List<IntegralBindEntity> integrals = new ArrayList<>();
    private List<MemberEntity> children = new ArrayList<>();
    private List<MemberEntity> parents = new ArrayList<>();

    public MemberEntity() {
        super();
    }

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

    public String getDBIcon() {
        return TextUtils.isEmpty(this.getIcon()) ? this.dbNull : this.getIcon();
    }

    public void setDBIcon(String icon) {
        if (TextUtils.equals(this.dbNull, icon)) {
            return;
        }
        this.setIcon(icon);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDBPhone() {
        return TextUtils.isEmpty(this.getPhone()) ? this.dbNull : this.getPhone();
    }

    public void setDBPhone(String phone) {
        if (TextUtils.equals(this.dbNull, phone)) {
            return;
        }
        this.setPhone(phone);
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDBBirthday() {
        return TextUtils.isEmpty(this.getBirthday()) ? this.dbNull : this.getBirthday();
    }

    public void setDBBirthday(String birthday) {
        if (TextUtils.equals(this.dbNull, birthday)) {
            return;
        }
        this.setBirthday(birthday);
    }

    public int getSex() {
        return sex;
    }

    public Sex findSex() {
        return Sex.find(this.getSex());
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setSex(Sex sex) {
        this.setSex(sex.code);
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

    public Marital findMarital() {
        return Marital.find(this.getMarital());
    }

    public void setMarital(int marital) {
        this.marital = marital;
    }

    public void setMarital(Marital marital) {
        this.setMarital(marital.code);
    }

    public int getSonCount() {
        return sonCount;
    }

    public void setSonCount(int sonCount) {
        this.sonCount = sonCount;
    }

    public int getDaughterCount() {
        return daughterCount;
    }

    public void setDaughterCount(int daughterCount) {
        this.daughterCount = daughterCount;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
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

    public List<IntegralBindEntity> getIntegrals() {
        return integrals;
    }

    public int getIntegral() {
        if (Util.isEmpty(this.getIntegrals())) {
            return 0;
        }
        int sum = 0;
        for (IntegralBindEntity integral : this.getIntegrals()) {
            if (integral == null || !integral.useable()) {
                continue;
            }
            sum += integral.getScore();
            sum -= integral.getUsedScore();
        }
        return sum;
    }

    public void setIntegrals(List<IntegralBindEntity> integrals) {
        this.integrals = integrals;
    }

    public void putIntegral(IntegralBindEntity integral) {
        this.integrals.add(integral);
    }

    public List<MemberEntity> getChildren() {
        return children;
    }

    public void setChildren(List<MemberEntity> children) {
        this.children = children;
    }

    public void putChild(MemberEntity child) {
        this.children.add(child);
    }

    public List<MemberEntity> getParents() {
        return parents;
    }

    public void setParents(List<MemberEntity> parents) {
        this.parents = parents;
    }

    public void putParent(MemberEntity member) {
        this.parents.add(member);
    }

    @Override
    void readParcel(Parcel in) {
        this.setName(in.readString());
        this.setIcon(in.readString());
        this.setDesc(in.readString());
        this.setPhone(in.readString());
        this.setBirthday(in.readString());
        this.setSex(in.readInt());
        this.setAge(in.readInt());
        this.setMarital(in.readInt());
        this.setSonCount(in.readInt());
        this.setDaughterCount(in.readInt());
        this.setCreated(in.readLong());
        this.setModified(in.readLong());
        this.setAddress(in.createTypedArrayList(AddressEntity.CREATOR));
        this.setIntegrals(in.createTypedArrayList(IntegralBindEntity.CREATOR));
        this.setChildren(in.createTypedArrayList(MemberEntity.CREATOR));
        this.setParents(in.createTypedArrayList(MemberEntity.CREATOR));
    }

    @Override
    void write2Parcel(Parcel dest, int flags) {
        dest.writeString(this.getName());
        dest.writeString(this.getIcon());
        dest.writeString(this.getDesc());
        dest.writeString(this.getPhone());
        dest.writeString(this.getBirthday());
        dest.writeInt(this.getSex());
        dest.writeInt(this.getAge());
        dest.writeInt(this.getMarital());
        dest.writeInt(this.getSonCount());
        dest.writeInt(this.getDaughterCount());
        dest.writeLong(this.getCreated());
        dest.writeLong(this.getModified());
        dest.writeTypedList(this.getAddress());
        dest.writeTypedList(this.getIntegrals());
        dest.writeTypedList(this.getChildren());
        dest.writeTypedList(this.getParents());
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

    public enum Sex {
        UNKNOWN("未知", 0, R.string.unknown),
        MALE("男", 1, R.string.sex_male),
        GIRL("女", 2, R.string.sex_girl);
        public int code;
        public @StringRes int res;
        public String desc;

        Sex(String desc, int code, @StringRes int res) {
            this.desc = desc;
            this.code = code;
            this.res = res;
        }

        public static Sex find(int code) {
            for (Sex marital : Sex.values()) {
                if (marital.code == code) {
                    return marital;
                }
            }
            return UNKNOWN;
        }
    }

    public enum Marital {
        UNKNOWN("未知", 0, R.string.unknown),
        UNMARRIED("未婚", 1, R.string.marital_unmarried),
        MARRIED("已婚", 2, R.string.marital_married),
        REMARRIAGE("再婚", 3, R.string.marital_remarriage),
        REMARRIED("复婚", 4, R.string.marital_remarried),
        WIDOWED("丧偶", 5, R.string.marital_widowed),
        DIVORCE("离异", 6, R.string.marital_divorce);
        public int code;
        public @StringRes int res;
        public String desc;

        Marital(String desc, int code, @StringRes int res) {
            this.desc = desc;
            this.code = code;
            this.res = res;
        }

        public static Marital find(int code) {
            for (Marital marital : Marital.values()) {
                if (marital.code == code) {
                    return marital;
                }
            }
            return UNKNOWN;
        }
    }
}

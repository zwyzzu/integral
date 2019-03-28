package com.zhangwy.integral.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwy on 2019/3/17.
 * Updated by zhangwy on 2019/3/17.
 * Description
 */
public class MemberItemEntity {
    public int viewType;
    public Object data;

    public MemberItemEntity(int viewType, Object obj) {
        this.viewType = viewType;
        this.data = obj;
    }

    public MemberItemEntity(int viewType) {
        this.viewType = viewType;
    }

    public static final int TYPE_INTEGRAL_HEAD = 0;
    public static final int TYPE_INTEGRAL = 1;
    public static final int TYPE_INTEGRAL_MORE = 2;
    public static final int TYPE_ADDRESS_HEAD = 3;
    public static final int TYPE_ADDRESS = 4;
    public static final int TYPE_ADDRESS_MORE = 5;

    public static List<MemberItemEntity> createMembers(MemberEntity member) {
        if (member == null) {
            return new ArrayList<>();
        }
        List<MemberItemEntity> array = new ArrayList<>();
        MemberItemEntity entity = new MemberItemEntity(TYPE_INTEGRAL_HEAD);
        array.add(entity);
        List<IntegralBindEntity> integrals = member.getIntegrals();
        int start = array.size();
        for (int i = integrals.size() - 1; i >= integrals.size() - 2 & i > 0; i--) {
            IntegralBindEntity bindEntity = integrals.get(i);
            if (bindEntity != null) {
                MemberItemEntity integral = new MemberItemEntity(TYPE_INTEGRAL, bindEntity);
                array.add(integral);
            }
        }
        if ((array.size() - start) > 0 && integrals.size() > 0) {
            entity = new MemberItemEntity(TYPE_INTEGRAL_MORE);
            array.add(entity);
        }

        entity = new MemberItemEntity(TYPE_ADDRESS_HEAD);
        array.add(entity);
        start = array.size();
        List<AddressEntity> addresses = member.getAddress();
        for (int i = addresses.size() - 1; i >= addresses.size() - 2 & i > 0; i--) {
            AddressEntity addressEntity = addresses.get(i);
            if (addressEntity != null) {
                MemberItemEntity address = new MemberItemEntity(TYPE_ADDRESS, addressEntity);
                array.add(address);
            }
        }
        if ((array.size() - start) >=1 && addresses.size() > 0) {
            entity = new MemberItemEntity(TYPE_ADDRESS_MORE);
            array.add(entity);
        }
        return array;
    }
}

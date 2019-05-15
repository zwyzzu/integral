package com.zhangwy.address.entities;

import java.util.List;

/**
 * Created by zhangwy on 2019/3/29.
 * Updated by zhangwy on 2019/3/29.
 * Description 县区实体类，包含镇、街道、办事处
 */
public class AreaEntity extends AddressEntity {
    private static final long serialVersionUID = -8347485408331932738L;
    private List<AddressEntity> town;

    public List<AddressEntity> getTown() {
        return town;
    }

    public void setTown(List<AddressEntity> town) {
        this.town = town;
    }
}

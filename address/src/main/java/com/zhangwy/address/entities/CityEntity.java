package com.zhangwy.address.entities;

import java.util.List;

/**
 * Created by zhangwy on 2019/3/29.
 * Updated by zhangwy on 2019/3/29.
 * Description 县市包含区县
 */
public class CityEntity extends AddressEntity {
    private List<AreaEntity> area;

    public List<AreaEntity> getArea() {
        return area;
    }

    public void setArea(List<AreaEntity> area) {
        this.area = area;
    }
}

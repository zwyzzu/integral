package com.zhangwy.address.entities;

import java.util.List;

/**
 * Created by zhangwy on 2019/3/29.
 * Updated by zhangwy on 2019/3/29.
 * Description 省实体类，包含市或辖县
 */
public class ProvinceEntity extends AddressEntity {
    private static final long serialVersionUID = 1048082519629259419L;
    private List<CityEntity> city;

    public List<CityEntity> getCity() {
        return city;
    }

    public void setCity(List<CityEntity> city) {
        this.city = city;
    }
}

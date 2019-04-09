package com.zhangwy.address.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwy on 2019/3/29.
 * Updated by zhangwy on 2019/3/29.
 * Description 省实体类，包含市或辖县
 */
public class ProvinceEntity {
    private String name;
    private String code;
    private List<CityEntity> city = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<CityEntity> getCity() {
        return city;
    }

    public void setCity(List<CityEntity> city) {
        this.city = city;
    }

    public void putCity(CityEntity entity) {
        this.city.add(entity);
    }
}

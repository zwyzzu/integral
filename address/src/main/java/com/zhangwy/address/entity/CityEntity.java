package com.zhangwy.address.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwy on 2019/3/29.
 * Updated by zhangwy on 2019/3/29.
 * Description 县市包含区县
 */
public class CityEntity {
    private String name;
    private String code;
    private List<AreaEntity> area = new ArrayList<>();

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

    public List<AreaEntity> getArea() {
        return area;
    }

    public void setArea(List<AreaEntity> area) {
        this.area = area;
    }

    public void putArea(AreaEntity entity) {
        this.area.add(entity);
    }
}

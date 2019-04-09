package com.zhangwy.address.entity;

/**
 * Created by zhangwy on 2019/3/29.
 * Updated by zhangwy on 2019/3/29.
 * Description 县区实体类，包含镇、街道、办事处
 */
public class AreaEntity {
    private String name;
    private String code;

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
}

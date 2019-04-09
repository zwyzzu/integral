package com.zhangwy.address.entity;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangwy on 2019/3/30.
 * Updated by zhangwy on 2019/3/30.
 * Description TODO
 */
public class Address {
    public String name;
    public String code;
    public String pCode;
    public String cCode;

    public Address(String name, String code) {
        this.name = name;
        code = code.trim();
        code = replace(code);
        this.code = code;
        this.pCode = code.substring(0, 2);
        this.pCode += "0000";
        this.cCode = code.substring(0, 4);
        this.cCode += "00";
    }

    public String replace(String string) {
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.replaceAll("").trim();
    }
    public boolean isProvince() {
        return TextUtils.equals(this.pCode, this.code);
    }

    public boolean isCity() {
        return !this.isProvince() && TextUtils.equals(this.cCode, this.code);
    }
}

package com.zhangwy.upgrade;

import java.io.Serializable;

/**
 * Created by zhangwy on 2018/1/19 下午2:58.
 * Updated by zhangwy on 2018/1/19 下午2:58.
 * Description
 */
@SuppressWarnings("unused")
public class AppBaseEntity implements Serializable {
    private static final long serialVersionUID = -8122346261544710981L;
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
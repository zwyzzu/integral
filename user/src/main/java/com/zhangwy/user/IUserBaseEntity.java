package com.zhangwy.user;

import java.io.Serializable;

/**
 * Author: zhangwy
 * 创建时间：2015年11月25日 下午5:45:14
 * 修改时间：2015年11月25日 下午5:45:14
 * Description: User系统所使用BaseEntity
 **/
@SuppressWarnings("unused")
public class IUserBaseEntity implements Serializable {
    private static final long serialVersionUID = -5997278037549333551L;

    public final String CODE_SUCCESS = "200";
    /**
     * return code by server
     */
    private String code = null;

    /**
     * return message by server
     */
    private String msg = null;

    /**
     * check if the return message is 200 ok,
     * if not there must be return error,
     * use the retMsg to get the error information
     *
     * @return "
     */
    public boolean isOK() {
        return CODE_SUCCESS.equals(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getNetCode() {
        try {
            return Integer.valueOf(code);
        } catch (Exception e) {
            return 200;
        }
    }
}

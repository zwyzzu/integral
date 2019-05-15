package com.zhangwy.common.entities;

import android.text.TextUtils;

import yixia.lib.core.util.TimeUtil;

/**
 * Author: zhangwy
 * 创建时间：2015年12月1日 下午5:36:27
 * 修改时间：2015年12月1日 下午5:36:27
 * Description: 用户实体类
 **/
@SuppressWarnings("unused")
public class IUserEntity extends IHttpBaseEntity {
    private static final long serialVersionUID = 8925511102300072548L;

    private String nickName;
    private String sex;
    private String message;
    private String userId;
    private String token;
    private String icon;
    private boolean isVip;
    private int vipLevel;
    private long allVipDays;
    private long startTime;
    private long vipDays;
    private long reqTime;
    private long reqLocalTime;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public long getAllVipDays() {
        return allVipDays;
    }

    public void setAllVipDays(long allVipDays) {
        this.allVipDays = allVipDays;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getVipDays() {
        return vipDays;
    }

    public void setVipDays(long vipDays) {
        this.vipDays = vipDays;
    }

    public long getReqTime() {
        return reqTime;
    }

    public void setReqTime(long reqTime) {
        this.reqTime = reqTime;
    }

    public long getReqLocalTime() {
        return reqLocalTime;
    }

    public void setReqLocalTime(long reqLocalTime) {
        this.reqLocalTime = reqLocalTime;
    }

    public boolean isAvailable() {
        String today = TimeUtil.getCurrentDate(TimeUtil.PATTERN_DAY4Y);
        String requestDay = TimeUtil.dateMilliSecond2String(this.reqTime, TimeUtil.PATTERN_DAY4Y);
        return this.useable() && TextUtils.equals(today, requestDay);
    }

    public boolean useable() {
        return this.isOK() && !TextUtils.isEmpty(this.getUserId()) && !TextUtils.isEmpty(this.getToken());
    }
}
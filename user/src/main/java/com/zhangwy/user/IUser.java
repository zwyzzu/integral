package com.zhangwy.user;

import android.content.Context;

import yixia.lib.core.exception.CodeException;

/**
 * Author: zhangwy
 * 创建时间：2015年12月1日 下午3:31:24
 * 修改时间：2015年12月1日 下午3:31:24
 * Description: 用户登录
 **/
@SuppressWarnings("unused")
public abstract class IUser {

    private static IUser mInstance = null;

    public static IUser getInstance() {
        if (mInstance == null) {
            synchronized (IUser.class) {
                if (mInstance == null) {
                    mInstance = new IUserImpl();
                }
            }
        }
        return mInstance;
    }

    public abstract void init(Context context);

    /**
     * 自动校验登录
     *
     * @param force 是否强制登录
     */
    public abstract void autoLogin(boolean force);

    /**
     * 获取登录值
     *
     * @return true 已经登陆且校验；false 未登录或者未校验(通过)
     */
    public abstract boolean isLogin();

    /**
     * 获取isVip值
     *
     * @return true 该已登录用户为vip用户；false 用户未登录或者不是vip用户
     */
    public abstract boolean isVip();

    /**
     *
     * @param userid   用户Id
     * @param token    用户临时密码
     * @param callBack 返回处理结果
     * @throws CodeException 登录时异常信息
     */
    public abstract void login(String userid, String token, LoginCallBack callBack) throws CodeException;

    /**
     * 服务器校验用户VIP信息
     *
     * @param userid   用户id
     * @param token    用户token
     * @param callBack 回调接口
     * @throws CodeException 校验时异常信息
     */
    public abstract void verifyVip(String userid, String token, VipCallBack callBack) throws CodeException;

    /**
     * 用户登出：
     */
    public abstract void logout();

    /**
     * 用户信息
     */
    public abstract IUserInfoEntity getUserInfo();

    /**
     * 注册监听登录状态事件
     *
     * @param login 监听接口
     */
    public abstract void register(Login login);

    /**
     * 移除注册监听登录状态事件
     *
     * @param login 监听接口
     */
    public abstract void unRegister(Login login);

    public interface Login {
        void login(IUserInfoEntity userInfo, IUserVipInfoEntity vipInfo);

        void logout();
    }

    public interface LoginCallBack {

        void onSuccess(IUserInfoEntity userInfo);

        void onException(CodeException e);

        boolean isDestroy();
    }

    public interface VipCallBack {

        void onSuccess(boolean isVip);

        void onException(CodeException e);

        boolean isDestroy();
    }
}
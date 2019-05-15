package com.zhangwy.user.auth;

import android.app.Activity;

import yixia.lib.core.exception.CodeException;

/**
 * Author: zhangwy
 * 创建时间：2015年12月3日 下午4:38:30
 * 修改时间：2015年12月3日 下午4:38:30
 * Description:
 **/
@SuppressWarnings("unused")
public interface AuthLogin<T> {

    T login(Activity ctx, String appkey, AuthCallback callBack) throws CodeException;

    T createApiEntity(Activity ctx, String appkey) throws CodeException;

    T getApiEntity();

    boolean unRegister(AuthCallback callBack);
}

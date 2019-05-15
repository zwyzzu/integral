package com.zhangwy.user.pay;

import android.app.Activity;

import yixia.lib.core.exception.CodeException;

/**
 * Author: 张维亚
 * 创建时间：2015年12月21日 下午3:55:18
 * 修改时间：2015年12月21日 下午3:55:18
 * Description:
 **/
@SuppressWarnings("unused")
public interface Pay<T> {

    T pay(Activity ctx, String payInfo, PayCallback callBack) throws CodeException;

    T createApiEntity(Activity ctx, String appkey) throws CodeException;

    T getApiEntity();

    boolean unRegister(PayCallback callBack);
}

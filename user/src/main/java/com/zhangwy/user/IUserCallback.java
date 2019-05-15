package com.zhangwy.user;

import com.zhangwy.common.entities.IUserEntity;

import yixia.lib.core.exception.CodeException;

/**
 * Author: 张维亚
 * 创建时间：2015年12月21日 下午6:51:13
 * 修改时间：2015年12月21日 下午6:51:13
 * Description:
 **/
interface IUserCallback {

    void onSuccess(IUserEntity userInfo);

    void onException(CodeException e);
}

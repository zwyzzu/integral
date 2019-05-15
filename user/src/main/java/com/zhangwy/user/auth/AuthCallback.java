package com.zhangwy.user.auth;

import yixia.lib.core.exception.CodeException;

/**
 * Created by zhangwy on 2019/5/3.
 * Updated by zhangwy on 2019/5/3.
 * Description
 */
public interface AuthCallback {
    void onAuthSuccess(String openId, String accessToken);

    void onAuthException(CodeException e);
}

package com.zhangwy.user.pay;

import yixia.lib.core.exception.CodeException;

/**
 * Author: zhangwy
 * 创建时间：2015年12月10日 下午3:42:03
 * 修改时间：2015年12月10日 下午3:42:03
 * Description:
 **/
public interface PayCallback {
    void onPaySuccess();

    void onPayException(CodeException e);
}

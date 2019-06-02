package com.zhangwy.integral;

import java.util.HashMap;

/**
 * Created by zhangwy on 2019/6/2.
 * Updated by zhangwy on 2019/6/2.
 * Description
 */
public interface OnRequestPermission {
    void requestPermission(String permission, String message);

    void requestPermission(HashMap<String, String> permissions);
}

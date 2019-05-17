package com.yixia.http;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwy on 2017/10/3 下午7:02.
 * Updated by zhangwy on 2017/10/3 下午7:02.
 * Description http包中所需要的工具方法
 */

public class Util {

    /**
     * 判断array(list)是否为空
     */
    public static boolean isEmpty(List<?> array) {
        return array == null || array.size() <= 0;
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.size() <= 0;
    }

    /**
     * MD5加密字符串 生成32位md5码
     *
     * @return 返回32位md5码
     */
    static String md5Encode(String string) {
        if (TextUtils.isEmpty(string)) {
            string =  System.currentTimeMillis() + "";
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(byte2hex(b));
            }
            return result.toString();
        } catch (Exception e) {
            return System.currentTimeMillis() + "";
        }
    }

    /**
     * byteHEX()，用来把一个byte类型的数转换成十六进制的ASCII表示，
     * 因为java中的byte的toString无法实现这一点，我们又没有C语言中的 sprintf(outbuf,"%02X",ib)
     */
    private static String byte2hex(byte ib) {
        final char[] digestValues = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] ob = new char[2];
        ob[0] = digestValues[(ib >>> 4) & 0X0F];
        ob[1] = digestValues[ib & 0X0F];
        return new String(ob);
    }
}

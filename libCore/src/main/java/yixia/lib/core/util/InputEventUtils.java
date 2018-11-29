package yixia.lib.core.util;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;

import yixia.lib.core.cipher.MD5;


/**
 * 输入相关工具.
 * 1、重复点击.
 */
public class InputEventUtils {

    private static final String TAG = InputEventUtils.class.getSimpleName();
    private static final int MAX_REPEAT_POOL_SIZE = 10;

    private static ArrayMap<String, Long> repeactCheckPool //
            = new ArrayMap<>((MAX_REPEAT_POOL_SIZE << 1));

    /**
     * 判断是否重复点击.
     *
     * @param view    统计的View
     * @param delayMs 第一次执行的delayMs才会生效.  0<=delayMs<10000
     */
    public static synchronized boolean isRepeatClick(@NonNull View view, int delayMs) {
        //Assert.assertTrue(delayMs >= 0 && delayMs < 10000);
        String key = MD5.md5Encode(view.toString() + view.hashCode());
        return isRepeatClick(key, delayMs);
    }

    /**
     * 是否在delayMs里面重复触发
     *
     * @param key     keyword 自定义
     * @param delayMs 重复触发的时间范围  0<=delayMs<10000
     * @return
     */
    public static synchronized boolean isRepeatClick(@NonNull String key, int delayMs) {
        if (delayMs < 0 || delayMs > 10000) {
            Log.e(TAG, "delayMs must >=0 && <=10000");
            return false;
        }
        long curTime = System.currentTimeMillis();
        try {
            if (repeactCheckPool.containsKey(key)) {
                long execTime = repeactCheckPool.get(key);
                if (curTime - execTime > 0) {
                    //replace
                    repeactCheckPool.put(key, curTime + delayMs);
                    return false;
                } else {
                    return true;
                }
            } else {
                repeactCheckPool.put(key, curTime + delayMs);
                return false;
            }
        } finally {
            //remove some old.
            if (repeactCheckPool.size() > MAX_REPEAT_POOL_SIZE) {
                repeactCheckPool.removeAt(0);
            }
        }
    }
}
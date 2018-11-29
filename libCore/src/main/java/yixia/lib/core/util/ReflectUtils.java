package yixia.lib.core.util;

import java.lang.reflect.ParameterizedType;

/**
 * Created by zhaoliangtai on 2018/3/13.
 */

public class ReflectUtils {

    public static <T> T getParameterizeTypeInstance(Object o, int index) {
        ParameterizedType parameterize = (ParameterizedType) o.getClass().getGenericSuperclass();
        try {
            return ((Class<T>) parameterize.getActualTypeArguments()[index]).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

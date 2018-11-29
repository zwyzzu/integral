package yixia.lib.core.util;

import java.util.Arrays;

/**
 * Created by liutao on 27/08/2018.
 */
public class Objects {

    /**
     * Null safe comparison of two objects.
     * @return true if the objects are identical.
     */
    public static boolean equal(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null) return false;
        return o1.equals(o2);
    }

    public static int hashCode(Object... values) {
        return Arrays.hashCode(values);
    }
}

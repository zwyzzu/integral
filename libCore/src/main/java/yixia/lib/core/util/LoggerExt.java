package yixia.lib.core.util;

public class LoggerExt {
    public static void startTime(String key) {
        Logger.d("time" + key, "Start " + System.currentTimeMillis());
    }

    public static void endTime(String key) {
        Logger.d("time" + key, "  End " + System.currentTimeMillis());
    }
}

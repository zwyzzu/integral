package yixia.lib.core.util;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by liutao on 6/22/16.
 */
public class TimeUtils {

    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());

    public static String getDuration(int timeMs) {
        if (timeMs < 0) {
            timeMs = 0;
        }
        int millisecond = timeMs % 1000;

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        //如果大于500毫秒，秒加一
        if (millisecond >= 500) {
            seconds = seconds + 1;
        }
        sFormatBuilder.setLength(0);
        if (hours > 0) {
            return sFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return sFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String getDurationCentisecond(long timeMs) {
        if (timeMs < 0) {
            timeMs = 0;
        }
        long millisecond = timeMs % 1000;

        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long centisecond = millisecond / 100 * 10;
        centisecond += millisecond % 100 / 10;
        sFormatBuilder.setLength(0);
        return sFormatter.format("%02d:%02d.%02d", minutes, seconds, centisecond).toString();
    }

    public static String getDurationMillisecond(final long timeMs) {
        long millisecond = timeMs % 1000;
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        //如果大于500毫秒，秒加一
        if (millisecond >= 500) {
            seconds = seconds;
        }
        sFormatBuilder.setLength(0);
        millisecond = millisecond / 10;
        if (millisecond >= 99) {
            millisecond = 99;
        }
        if (hours > 0) {
            return sFormatter.format("%d:%02d:%02d:%02d", hours, minutes, seconds, millisecond)
                    .toString();
        } else if (minutes > 0) {
            return sFormatter.format("%02d:%02d:%02d", minutes, seconds, millisecond).toString();
        } else {
            return sFormatter.format("%02d:%02d", seconds, millisecond).toString();
        }
    }

}

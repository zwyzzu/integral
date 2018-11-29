package yixia.lib.core.util;

import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import yixia.lib.core.BuildConfig;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/6 上午10:46
 * 修改时间：2017/4/6 上午10:46
 * Description:
 */
@SuppressWarnings("unused")
public class Logger {

    private static final String Tag = "logger";

    public static final int LEVEL_ASSERT = Log.ASSERT; //7
    public static final int LEVEL_ERROR = Log.ERROR; //6
    public static final int LEVEL_WARN = Log.WARN; //5
    public static final int LEVEL_INFO = Log.INFO; //4
    public static final int LEVEL_DEBUG = Log.DEBUG; //3
    public static final int LEVEL_VERBOSE = Log.VERBOSE; //2

    private static boolean printLog = TextUtils.equals(BuildConfig.BUILD_TYPE, "debug");
    @LoggerLevel
    private static int level = printLog ? LEVEL_VERBOSE : LEVEL_ASSERT;

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @IntDef({LEVEL_ASSERT, LEVEL_ERROR, LEVEL_WARN, LEVEL_INFO, LEVEL_DEBUG, LEVEL_VERBOSE})
    public @interface LoggerLevel {
    }

    /**
     * 判断此Level的log是否可以打印出来.
     * 如果拼接了大量字符串
     * @param level
     * @return
     */
    public static boolean needLogger(@LoggerLevel int level) {
        return level >= Logger.level;
    }

    public static void debugMode(boolean debug) {
        if (debug) {
            setLevel(LEVEL_VERBOSE);
        }
        Logger.printLog = debug;
    }

    public static void setLevel(int level) {
        Logger.printLog = true;
        Logger.level = level;
    }

    public static void e(String msg) {
        e(Tag, msg);
    }

    public static void e(String msg, Throwable e) {
        e(Tag, msg, e);
    }

    public static void e(String tag, String msg) {
        if (printLog(LEVEL_ERROR, msg)) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        final String eMsg = throwableMsg(msg, e);
        if (printLog(LEVEL_ERROR, eMsg)) {
            Log.e(tag, msg, e);
        }
    }

    public static void w(String msg) {
        w(Tag, msg);
    }

    public static void w(String msg, Throwable e) {
        w(Tag, msg, e);
    }

    public static void w(String tag, String msg) {
        if (printLog(LEVEL_WARN, msg)) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable e) {
        final String eMsg = throwableMsg(msg, e);
        if (printLog(LEVEL_WARN, eMsg)) {
            Log.w(tag, msg, e);
        }
    }

    public static void i(String msg) {
        i(Tag, msg);
    }

    public static void i(String msg, Throwable e) {
        i(Tag, msg, e);
    }

    public static void i(String tag, String msg) {
        if (printLog(LEVEL_INFO, msg)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable e) {
        final String eMsg = throwableMsg(msg, e);
        if (printLog(LEVEL_INFO, eMsg)) {
            Log.i(tag, msg, e);
        }
    }

    public static void d(String msg) {
        d(Tag, msg);
    }

    public static void d(String msg, Throwable e) {
        d(Tag, msg, e);
    }

    public static void d(String tag, String msg) {
        if (printLog(LEVEL_DEBUG, msg)) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable e) {
        final String eMsg = throwableMsg(msg, e);
        if (printLog(LEVEL_DEBUG, eMsg)) {
            Log.d(tag, msg, e);
        }
    }

    public static void v(String msg) {
        v(Tag, msg);
    }

    public static void v(String msg, Throwable e) {
        v(Tag, msg, e);
    }

    public static void v(String tag, String msg) {
        if (printLog(LEVEL_VERBOSE, msg)) {
            Log.d(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable e) {
        final String eMsg = throwableMsg(msg, e);
        if (printLog(LEVEL_VERBOSE, eMsg)) {
            Log.v(tag, msg, e);
        }
    }

    private static boolean printLog(int type, String msg) {
        return printLog && (level <= type) && !TextUtils.isEmpty(msg);
    }

    private static String throwableMsg(String msg, Throwable e) {
        String text = "";
        if (!TextUtils.isEmpty(msg)) {
            text += msg;
            text += "      ";
        }
        if (e != null) {
            text += e.getMessage();
        }
        return text;
    }
}

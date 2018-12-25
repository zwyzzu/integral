package yixia.lib.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author: 张维亚
 * 创建时间：2015年12月25日 下午12:07:45
 * 修改时间：2015年12月25日 下午18:00
 * Description:
 **/
@SuppressWarnings("unused")
public class TimeUtil {

    public static final String PATTERN_MONTH = "yyyy-MM";
    public static final String PATTERN_DAY2Y = "yy-MM-dd";
    public static final String PATTERN_DAY4Y = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_MMSS = "mm:ss";
    public static final String PATTERN_S = "yyyyMMddHHmmss";
    public static final String PATTERN_MS = "yyyyMMddHHmmssSSS";
    public static final String PATTERN_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_WEEHOURS = "yyyy-MM-dd 00:00:00";

    public static String getCurrentDate(String pattern) {
        return date2String(new Date(), pattern);
    }

    // Time Past
    public static long getCurrentDateCutWeek(int cutWeek) {
        return getCurrentDateCutDay(cutWeek * 7);
    }

    public static long getCurrentDateCutMonth(int cutMonth) {
        return getCurrentDateCutDay(cutMonth * 30);
    }

    public static long getCurrentDateCutDay(int cutDay) {
        return getDateCutDay(new Date(), cutDay);
    }

    // Time Past
    public static String getCurrentDateCutWeek(String pattern, int cutWeek) {
        return getCurrentDateCutDay(pattern, cutWeek * 7);
    }

    public static String getCurrentDateCutMonth(String pattern, int cutMonth) {
        return getCurrentDateCutDay(pattern, cutMonth * 30);
    }

    public static String getCurrentDateCutDay(String pattern, int countDay) {
        return getDateCutDay(new Date(), pattern, countDay);
    }

    public static String getDateCutDay(String date, String srcPattern, String destPattern,
                                       int cutDay) {
        try {
            return getDateCutDay(dateString2Date(date, srcPattern), destPattern, cutDay);
        } catch (Exception e) {
            Logger.e("getDateStringCutDay", e);
        }
        return "";
    }

    public static String getDateCutDay(Date date, String pattern, int cutDay) {
        if (date == null)
            return "";
        date.setDate(date.getDate() - cutDay);
        return date2String(date, pattern);
    }

    public static long getDateCutDay(Date date, int cutDay) {
        try {
            date.setDate(date.getDate() - cutDay);
            return date.getTime();
        } catch (Exception e) {
            Logger.e("getDateLongCutDay", e);
        }
        return 0;
    }

    public static String changeDatePattern(String timeString, String srcPattern,
                                           String destPattern) {
        Date dt = dateString2Date(timeString, srcPattern);
        return date2String(dt, destPattern);
    }

    public static Date dateString2Date(String timeString, String pattern) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern, Locale.getDefault());
            return dateFormatter.parse(timeString);
        } catch (Exception e) {
            return new Date();
        }

    }

    public static Date dateLong2Date(long milliseconds) {
        return new Date(milliseconds);
    }

    public static long dateString2Long(String date, String pattern) {
        try {
            return dateString2Date(date, pattern).getTime();
        } catch (Exception e) {
            Logger.e("dateString2DateLong", e);
        }
        return 0;
    }

    public static String date2String(Date date, String pattern) {
        if (date == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        return sdf.format(date);
    }

    public static String dateSecond2String(long seconds, String pattern) {
        return dateMilliSecond2String(seconds * 1000, pattern);
    }

    public static String dateMilliSecond2String(long milliseconds, String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(milliseconds);
    }

    //获取小时
    public static int getHour(Date date) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.HOUR_OF_DAY);
    }

    //获取分钟
    public static int getMinute(Date date) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.MINUTE);
    }

    //获取周
    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //获取周
    public static int getWeek(int year,int moth,int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,moth-1,day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //获取年
    public static int getYear(Date date) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.YEAR);
    }

    //获取月
    public static int getMoth(Date date) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.MONTH)+1;
    }

    //获取日
    public static int getDay(Date date) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(calendar.DATE);
    }

    public static Date getDate(int year, int moth, int day,int hour,int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, moth - 1, day, hour, minute);
        return calendar.getTime();
    }
}
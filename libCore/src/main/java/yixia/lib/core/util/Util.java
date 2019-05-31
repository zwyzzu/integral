package yixia.lib.core.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/6 下午4:41
 * 修改时间：2017/4/6 下午4:41
 * Description:
 */
@SuppressWarnings("unused")
public class Util {

    public static final int REQUEST_CODE_APP_INSTALL = 65531;
    /**
     * 获取当前应用程序的包名
     *
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppPackageName(Context context) {
        if (context != null) {
            try {
                return context.getPackageName();
            } catch (Exception e) {
                Logger.e("getAppPackageName", e);
            }
        }
        return "";
    }

    /**
     * 判断array(list)是否为空
     */
    public static boolean isEmpty(List<?> array) {
        return array == null || array.size() <= 0;
    }

    public static boolean isEmpty(HashSet<?> set) {
        return set == null || set.size() <= 0;
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.size() <= 0;
    }

    public static <T> boolean isEmpty(T[] obj) {
        return obj == null || obj.length <= 0;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean arrayEmpty(T... array) {
        return array == null || array.length <= 0;
    }

    /**
     * map转json
     *
     * @param map Map<String, Object>转成json 其中Object指基本数据类型和String
     * @return String
     */
    public static <V> String map2Json(Map<String, V> map) {
        if (isEmpty(map))
            return "{}";
        return new JSONObject(map).toString();
    }

    public static <V> JSONObject map2JsonObject(Map<String, V> map) {
        if (isEmpty(map))
            return new JSONObject();
        return new JSONObject(map);
    }

    public static String pair2Json(String key, Object value) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt(key, value);
        } catch (Exception e) {
            Logger.e("pair2Json", e);
        }
        return jsonObject.toString();
    }

    /**
     * 仅使用一个字符进行分割
     */
    public static ArrayList<String> string2List(String text, char splitter) {
        return array2List(string2Array(text, splitter));
    }

    public static <E> ArrayList<E> array2List(E[] arr) {
        if (isEmpty(arr))
            return new ArrayList<>();
        try {
            List<E> eList = Arrays.asList(arr);
            ArrayList<E> list = new ArrayList<>(eList.size());
            list.addAll(eList);
            return list;
        } catch (Exception e) {
            Logger.e("array2List", e);
            return new ArrayList<>();
        }
    }

    public static ArrayList<Integer> string2IntList(String text, char splitter,
                                                    final boolean sort) {
        ArrayList<Integer> array = string2IntList(text, splitter);
        if (isEmpty(array))
            return null;
        Collections.sort(array, (o1, o2) -> sort ? o1 - o2 : o2 - o1);
        return array;
    }

    public static ArrayList<Integer> string2IntList(String text, char splitter) {
        String[] strings = string2Array(text, splitter);
        if (isEmpty(strings))
            return null;
        ArrayList<Integer> array = new ArrayList<>();
        for (String string : strings) {
            try {
                array.add(Integer.valueOf(string));
            } catch (Exception e) {
                Logger.e("string2IntArray", e);
            }
        }
        if (isEmpty(array))
            return null;
        return array;
    }

    /**
     * 仅使用一个字符进行分割
     */
    public static String[] string2Array(String text, char splitter) {
        if (TextUtils.isEmpty(text))
            text = "";

        String reg = String.format("\\%s", Character.toString(splitter));
        return text.split(reg);
    }

    public static String array2ArrayString(char splitter, List<String> array) {
        if (isEmpty(array))
            return "[]";
        return "[\"" + array2Strings(array, "\"" + splitter + "\"") + "\"]";
    }

    public static String array2ArrayString(char splitter, String... array) {
        if (isEmpty(array))
            return "[]";
        return "[\"" + array2Strings(Arrays.asList(array), "\"" + splitter + "\"") + "\"]";
    }

    public static <T> String array2Strings(char splitter, T... array) {
        if (isEmpty(array)) {
            return "";
        }
        return array2Strings(Arrays.asList(array), splitter);
    }

    public static <T> String array2Strings(List<T> array, char splitter) {
        return array2Strings(array, String.valueOf(splitter));
    }

    public static <T> String array2Strings(List<T> arr, String splitter) {
        if (isEmpty(arr))
            return "";

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            if (i > 0) {
                buffer.append(splitter);
            }
            buffer.append(arr.get(i) == null ? "" : String.valueOf(arr.get(i)));
        }
        return buffer.toString();
    }

    public static String urlEncoder(String params) {
        String paramsResult = "";
        try {
            paramsResult = URLEncoder.encode(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.e("encoder", e);
        }
        return paramsResult;
    }

    public static int parseInt(String numberStr) {
        if (TextUtils.isEmpty(numberStr))
            return 0;
        try {
            return Integer.parseInt(numberStr);
        } catch (Exception e) {
            Logger.e("parseInt", e);
            return 0;
        }
    }

    public static String float2String(float number, int decimal) {
        if (decimal < 0) {
            decimal = 0;
        }
        String format = "%1." + decimal + "f";
        return String.format(Locale.getDefault(), format, number);
    }

    public static String byte2hex(byte[] bytes) {
        return byte2hex(bytes, null);
    }

    public static String byte2hex(byte[] bytes, String splitter) {
        if (arrayEmpty(bytes))
            return "";
        StringBuffer buffer = new StringBuffer();
        try {
            for (int i = 0; i < bytes.length; i++) {
                if (!TextUtils.isEmpty(splitter) && i > 0) {
                    buffer.append(splitter);
                }
                buffer.append(String.format("%02X", bytes[i]));
            }
        } catch (Exception e) {
            Logger.d("byte2hex", e);
        }

        return String.valueOf(buffer);
    }

    public static void installApp(Context context, String filePath) {
        if (hasRootPermission()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("su");
                PrintWriter printWriter = new PrintWriter(process.getOutputStream());
                printWriter.println("chmod 777 " + filePath);
                printWriter.println("export LD_LIBRARY_PATH=/vendor/lib:/system/lib");
                printWriter.println("pm install -r " + filePath);
                printWriter.flush();
                printWriter.close();
                process.waitFor();
            } catch (Exception e) {
                Logger.e("installApp", e);
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        } else {
            if (context == null) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri contentUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !hasInstallPermissionWithO(context)) {
                    startInstallPermission(context);
                }
                File file = new File(FileUtil.extractFilePath(filePath), FileUtil.extractFileName(filePath));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String authority = Device.App.getPackageName(context) + ".fileprovider";
                contentUri = FileProvider.getUriForFile(context, authority, file);
            } else {
                contentUri = Uri.fromFile(new File(filePath));
            }
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    public static boolean hasRootPermission() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter printWriter = new PrintWriter(process.getOutputStream());
            printWriter.flush();
            printWriter.close();
            return process.waitFor() == 0;
        } catch (Exception e) {
            Logger.d("hasRootPermission", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static boolean hasInstallPermissionWithO(Context context){
        if (context == null){
            return false;
        }
        return context.getPackageManager().canRequestPackageInstalls();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermission(Context context) {
        if (context == null) {
            return;
        }
        try {
            String content= Device.App.getAppName(context);
            content = "请找到『" + content + "』并打开安装未知应用权限";
            Toast.makeText(context, content, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE_APP_INSTALL);
        } catch (Throwable throwable) {
            Logger.e("startInstallPermission", throwable);
        }
    }

    public static int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getWebUserAgent(Context context) {
        if (context == null)
            return "";
        try {
            WebSettings settings = new WebView(context).getSettings();
            return settings == null ? "" : settings.getUserAgentString();
        } catch (Exception e) {
            Logger.e("getWebUserAgent", e);
            return "";
        }
    }

    public static boolean isScreenOn(PowerManager pm) {
        try {
            Class[] classes = new Class[]{};
            Method mReflectScreenState = PowerManager.class.getMethod("isScreenOn", classes);
            return (Boolean) mReflectScreenState.invoke(pm);
        } catch (Exception e) {
            Logger.e("error:", e);
        }
        return false;
    }

    public static boolean isScreenOn(Context context) {
        try {
            PowerManager manager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return manager != null && manager.isScreenOn();
        } catch (Exception e) {
            Logger.e("error:", e);
        }
        return false;
    }

    public static boolean isScreenLocked(Context context) {
        try {
            KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return manager != null && manager.inKeyguardRestrictedInputMode();
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressLint("WakelockTimeout")
    public static PowerManager.WakeLock screenOn(Context context) {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm == null) {
                throw new NullPointerException();
            }
            @SuppressLint("InvalidWakeLockTag")
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "==KeepScreenOn==");
            wakeLock.acquire();
            return wakeLock;
        } catch (Throwable e) {
            Logger.e("screenOn", e);
            return null;
        }
    }

    public static void screenOff(PowerManager.WakeLock wakeLock) {
        if (wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Throwable e) {
                Logger.e("screenOff", e);
            }
        }
    }

    public static float getTextLength(float textSize, String text) {
        try {
            Paint paint = new Paint();
            paint.setTextSize(textSize);
            return paint.measureText(text);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isMobile(String str) {
        Pattern p = Pattern.compile("^[1][3,4,5,6,7,8][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isPhone(String str) {
        String regex = "0\\d{2,3}[-]?\\d{7,8}|0\\d{2,3}\\s?\\d{7,8}|13[0-9]\\d{8}|15[1089]\\d{8}";
        if (Pattern.compile(regex).matcher(str).matches()) {
            return true;
        }

        Pattern p1 = Pattern.compile("^[0][1-9]{2,3}[-]{0,1}[0-9]{5,10}$");  // 验证带区号的
        Pattern p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            Matcher m = p1.matcher(str);
            return m.matches();
        } else {
            Matcher m = p2.matcher(str);
            return m.matches();
        }
    }

    public static void copy2Clipboard(Context context, String content) {
        if (context == null) {
            return;
        }
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb == null) {
            return;
        }
        cmb.setText(content.trim());
    }

    public static <T> T parseObject(String text, Class<T> clazz, char splitterArr, char splitterObj) {
        if (TextUtils.isEmpty(text) || clazz == null)
            return null;

        T entity;
        HashMap<String, Field> fields = new HashMap<>();
        try {
            entity = clazz.newInstance();
            Field[] arr = clazz.getDeclaredFields();
            for (Field field : arr) {
                fields.put(field.getName().toLowerCase(Locale.getDefault()), field);
            }
        } catch (Exception e) {
            //非法构造器 || 构造器为非public
            return null;
        }

        String[] arr = string2Array(text, splitterArr);
        if (isEmpty(arr))
            return null;

        for (String string : arr) {
            Pair<String, String> pair = splitString(string, splitterObj);
            try {
                Field field = fields.get(pair.first.toLowerCase(Locale.getDefault()));
                if (field != null) {
                    field.setAccessible(true);
                    field.set(entity, pair.second);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    public static Pair<String, String> splitString(String text, char splitter) {
        return splitString(text, String.valueOf(splitter));
    }

    public static Pair<String, String> splitString(String text, String splitter) {
        if (TextUtils.isEmpty(text))
            return null;
        int index = text.indexOf(splitter);
        if (index <= 0)
            return null;
        return Pair.create(text.substring(0, index), text.substring(index + 1));
    }
}
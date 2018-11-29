package yixia.lib.core.util;

import android.content.Context;
import android.content.SharedPreferences;

import yixia.lib.core.base.BaseApplication;
import yixia.lib.core.base.Constant;


public class SharedPreferencesUtils {

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void setParam(String key, Object object) {
        Context context = BaseApplication.sInstance;
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }


    public static void setParam(Context context, String key, Object value) {
        String type = value.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) value);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) value);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) value);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) value);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }

    public static Object getParam(Context context, String key, Object defValue) {
        String type = defValue.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defValue);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defValue);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defValue);
        }
        return null;
    }

    public static void setString(String key, String value) {
        Context context = BaseApplication.sInstance;
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object getParam(String key, Object defaultObject) {
        Context context = BaseApplication.sInstance;
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        }
        return defaultObject;
    }

    public static void removeKey(String key) {
        Context context = BaseApplication.sInstance;
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean constainKey(String key) {
        Context context = BaseApplication.sInstance;
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 删库跑路的方法 慎用
     */
    public static void clear() {
        Context context = BaseApplication.sInstance;
        SharedPreferences sp = context.getSharedPreferences(Constant.PREFERENCE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}

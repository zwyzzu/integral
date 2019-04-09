package yixia.lib.core.sharePreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;

import java.util.List;

import yixia.lib.core.util.Util;

/**
 * Author: zhangwy(张维亚)
 * 创建时间：2017/4/1 下午3:32
 * 修改时间：2017/4/1 下午3:32
 * Description: SharePreferences封装实现
 */
class PreferencesHelperImpl extends PreferencesHelper {
    private String name;
    private SharedPreferences preferences;

    PreferencesHelperImpl(String name) {
        this.name = name;
    }

    @Override
    public void init(Context ctx) {
        if (preferences == null) {
            synchronized (PreferencesHelperImpl.class) {
                if (preferences == null) {
                    preferences = ctx.getApplicationContext().getSharedPreferences(name,
                            Context.MODE_PRIVATE);
                }
            }
        }
    }

    @Override
    public boolean hasInitialized() {
        return this.preferences != null;
    }

    @Override
    public boolean contains(String key) {
        return this.hasInitialized() && preferences.contains(key);
    }

    @Override
    public boolean commitString(String key, String value) {
        return this.hasInitialized() && preferences.edit().putString(key, value).commit();
    }

    @Override
    public void applyString(String key, String value) {
        if (this.hasInitialized()) {
            preferences.edit().putString(key, value).apply();
        }
    }

    @Override
    public boolean commitStrings(List<Pair<String, String>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return false;
        Editor editor = preferences.edit();
        for (Pair<String, String> pair : list) {
            editor.putString(pair.first, pair.second);
        }
        return editor.commit();
    }

    @Override
    public void applyStrings(List<Pair<String, String>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return;
        Editor editor = preferences.edit();
        for (Pair<String, String> pair : list) {
            editor.putString(pair.first, pair.second);
        }
        editor.apply();
    }

    @Override
    public String getString(String key, String defaultValue) {
        if (this.hasInitialized()) {
            return preferences.getString(key, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public boolean commitInt(String key, int value) {
        return this.hasInitialized() && preferences.edit().putInt(key, value).commit();
    }

    @Override
    public void applyInt(String key, int value) {
        if (this.hasInitialized()) {
            preferences.edit().putInt(key, value).apply();
        }
    }

    @Override
    public boolean commitInts(List<Pair<String, Integer>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return false;
        Editor editor = preferences.edit();
        for (Pair<String, Integer> pair : list) {
            editor.putInt(pair.first, pair.second);
        }
        return editor.commit();
    }

    @Override
    public void applyInts(List<Pair<String, Integer>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return;
        Editor editor = preferences.edit();
        for (Pair<String, Integer> pair : list) {
            editor.putInt(pair.first, pair.second);
        }
        editor.apply();
    }

    @Override
    public int getInt(String key, int defaultValue) {
        if (this.hasInitialized()) {
            return preferences.getInt(key, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public boolean commitLong(String key, long value) {
        return this.hasInitialized() && preferences.edit().putLong(key, value).commit();
    }

    @Override
    public void applyLong(String key, long value) {
        if (this.hasInitialized()) {
            preferences.edit().putLong(key, value).apply();
        }
    }

    @Override
    public boolean commitLongs(List<Pair<String, Long>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return false;
        Editor editor = preferences.edit();
        for (Pair<String, Long> pair : list) {
            editor.putLong(pair.first, pair.second);
        }
        return editor.commit();
    }

    @Override
    public void applyLongs(List<Pair<String, Long>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return;
        Editor editor = preferences.edit();
        for (Pair<String, Long> pair : list) {
            editor.putLong(pair.first, pair.second);
        }
        editor.apply();
    }

    @Override
    public long getLong(String key, long defaultValue) {
        if (this.hasInitialized()) {
            return preferences.getLong(key, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public boolean commitFloat(String key, float value) {
        return this.hasInitialized() && preferences.edit().putFloat(key, value).commit();
    }

    @Override
    public void applyFloat(String key, float value) {
        if (this.hasInitialized()) {
            preferences.edit().putFloat(key, value).apply();
        }
    }

    @Override
    public boolean commitFloats(List<Pair<String, Float>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return false;
        Editor editor = preferences.edit();
        for (Pair<String, Float> pair : list) {
            editor.putFloat(pair.first, pair.second);
        }
        return editor.commit();
    }

    @Override
    public void applyFloats(List<Pair<String, Float>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return;
        Editor editor = preferences.edit();
        for (Pair<String, Float> pair : list) {
            editor.putFloat(pair.first, pair.second);
        }
        editor.apply();
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        if (this.hasInitialized()) {
            return preferences.getFloat(key, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public boolean commitBoolean(String key, boolean value) {
        return this.hasInitialized() && preferences.edit().putBoolean(key, value).commit();
    }

    @Override
    public void applyBoolean(String key, boolean value) {
        if (this.hasInitialized()) {
            preferences.edit().putBoolean(key, value).apply();
        }
    }

    @Override
    public boolean commitBooleans(List<Pair<String, Boolean>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return false;
        Editor editor = preferences.edit();
        for (Pair<String, Boolean> pair : list) {
            editor.putBoolean(pair.first, pair.second);
        }
        return editor.commit();
    }

    @Override
    public void applyBooleans(List<Pair<String, Boolean>> list) {
        if (Util.isEmpty(list) || !this.hasInitialized())
            return;
        Editor editor = preferences.edit();
        for (Pair<String, Boolean> pair : list) {
            editor.putBoolean(pair.first, pair.second);
        }
        editor.apply();
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        if (this.hasInitialized()) {
            return preferences.getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public boolean remove(String key) {
        return this.hasInitialized() && preferences.edit().remove(key).commit();
    }

    @Override
    public boolean clear() {
        return this.hasInitialized() && preferences.edit().clear().commit();
    }
}

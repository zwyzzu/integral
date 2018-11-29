package yixia.lib.core.util;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class EnvUtils {

    private Bundle mBundle = new Bundle();

    public void putShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
    }

    public void putCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
    }

    public CharSequence getCharSequence(@Nullable String key) {
        return mBundle.getCharSequence(key);
    }

    public CharSequence getCharSequence(@Nullable String key, CharSequence defaultValue) {
        return mBundle.getCharSequence(key, defaultValue);
    }

    public short getShort(String key) {
        return mBundle.getShort(key);
    }

    public short getShort(String key, short defaultValue) {
        return mBundle.getShort(key, defaultValue);
    }

    public void putBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
    }

    public boolean getBoolean(String key) {
        return mBundle.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean value) {
        return mBundle.getBoolean(key, value);
    }

    private void save() {
        throw new UnsupportedOperationException("不支持");
    }

    public void restore() {
        throw new UnsupportedOperationException("不支持");
    }


    /**
     * 获得单例
     *
     * @return
     */
    public static EnvUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * 单例持有器
     */
    private static final class InstanceHolder {
        private static final EnvUtils INSTANCE = new EnvUtils();
    }

    /**
     * 禁止构造
     */
    private EnvUtils() {
    }

}

package com.zhangwy.integral.data;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2018/11/22 下午3:03.
 * Updated by zhangwy on 2019/06/14 下午3:03.
 * Description 标签历史使用管理
 */
@SuppressWarnings("unused")
public abstract class ITagManager {
    public final static String KEY_TAG_COUPONS = "CouponsTag";

    private static HashMap<String, ITagManager> managers = new HashMap<>();

    public static ITagManager create(Context context) {
        return create(context, "Default");
    }

    public static ITagManager create(Context context, String key) {
        if (!managers.containsKey(key)) {
            synchronized (ITagManager.class) {
                if (!managers.containsKey(key)) {
                    ITagManager manager = new ITagManagerImpl(context, key);
                    managers.put(key, manager);
                }
            }
        }
        return managers.get(key);
    }

    public abstract List<String> get();

    public abstract void put(String tag);

    public abstract void remove(String tag);

    public abstract void clear();
    /*-----------------------------------------------------------------------------------*/
    private static class ITagManagerImpl extends ITagManager {
        private PreferencesHelper helper = PreferencesHelper.defaultInstance();
        private String SP_TAG_HISTORY = "tagHistory";
        private ITagManagerImpl(Context context, String key) {
            helper.init(context);
            if (!TextUtils.isEmpty(key)) {
                SP_TAG_HISTORY += key;
            }
        }

        @Override
        public List<String> get() {
            String tags = helper.getString(this.SP_TAG_HISTORY, "");
            if (TextUtils.isEmpty(tags)) {
                return new ArrayList<>();
            }
            return Util.string2List(tags, ',');
        }

        @Override
        public void put(String tag) {
            if (TextUtils.isEmpty(tag)) {
                return;
            }
            List<String> histories = this.get();
            if (Util.isEmpty(histories)) {
                helper.applyString(this.SP_TAG_HISTORY, tag);
                return;
            }
            histories.remove(tag);
            histories.add(0, tag);
            this.put(histories);
        }

        @Override
        public void remove(String tag) {
            if (TextUtils.isEmpty(tag)) {
                return;
            }
            List<String> histories = this.get();
            if (Util.isEmpty(histories)) {
                return;
            }
            histories.remove(tag);
            this.put(histories);
        }

        @Override
        public void clear() {
            this.helper.applyString(this.SP_TAG_HISTORY, "");
        }

        private void put(List<String> array) {
            helper.applyString(this.SP_TAG_HISTORY, Util.array2Strings(array, ","));
        }
    }
}

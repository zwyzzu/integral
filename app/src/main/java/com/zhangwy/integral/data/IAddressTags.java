package com.zhangwy.integral.data;

import android.content.Context;

import com.zhangwy.integral.R;

import java.util.List;

import yixia.lib.core.sharePreferences.PreferencesHelper;
import yixia.lib.core.util.Util;

/**
 * Created by zhangwy on 2019/4/2.
 * Updated by zhangwy on 2019/4/2.
 * Description 标签管理
 */
public abstract class IAddressTags {

    private static final IAddressTags instance = new IAddressTagsImpl();
    public static void initialized(Context context) {
        instance.init(context);
    }

    public static IAddressTags getInstance() {
        return instance;
    }

    abstract void init(Context context);

    public abstract List<String> getTags();

    public abstract boolean addTag(String tag);
    /*--------------------------------------------------------------------------*/
    private static class IAddressTagsImpl extends IAddressTags {
        private final String ADDRESS_TAG = "preferences_key_address_tag";
        private final char splitter = ',';
        @Override
        void init(Context context) {
            if (context != null) {
                PreferencesHelper.defaultInstance().init(context.getApplicationContext());
                if (!PreferencesHelper.defaultInstance().contains(ADDRESS_TAG)) {
                    String tags = context.getString(R.string.address_tags_default);
                    PreferencesHelper.defaultInstance().applyString(ADDRESS_TAG, tags);
                }
            }
        }

        @Override
        public List<String> getTags() {
            String tags = PreferencesHelper.defaultInstance().getString(ADDRESS_TAG, "");
            return Util.string2List(tags, splitter);
        }

        @Override
        public boolean addTag(String tag) {
            List<String> tags = this.getTags();
            if (tags.contains(tag)) {
                return false;
            }
            tags.add(tag);
            PreferencesHelper.defaultInstance().commitString(ADDRESS_TAG, Util.array2ArrayString(splitter, tags));
            return true;
        }
    }

}

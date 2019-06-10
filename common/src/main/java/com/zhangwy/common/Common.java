package com.zhangwy.common;

import yixia.lib.core.sharePreferences.PreferencesHelper;

/**
 * Created by zhangwy on 2019/5/20.
 * Updated by zhangwy on 2019/5/20.
 * Description TODO
 */
public interface Common {
    String PFRC_SHOW_MEMBER_AVATAR = "pfrcShowMemberAvatar";//是否显示成员头像
    String PFRC_NEAR_OVERDUE_DAY = "pfrcNearOverdueDay";//临近过期天数

    long NEAR_OVERDUE_SM = PreferencesHelper.defaultInstance().getInt(PFRC_NEAR_OVERDUE_DAY, 3) * 24 * 3600000;
}

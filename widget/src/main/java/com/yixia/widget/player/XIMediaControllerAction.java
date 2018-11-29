package com.yixia.widget.player;

/**
 * Created by zhangwy on 2017/8/1 下午9:02.
 * Updated by zhangwy on 2017/8/1 下午9:02.
 * Description
 */
@SuppressWarnings("unused")
public interface XIMediaControllerAction {

    void setOnSwitchListener(OnSwitchListener listener);

    void switchScreen(boolean toLandscape);

    interface OnSwitchListener {
        void onSwitchScreen(boolean toLandscape);
    }
}

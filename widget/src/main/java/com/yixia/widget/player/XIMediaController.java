package com.yixia.widget.player;

/**
 * Created by zhangwy on 2017/8/1 下午8:30.
 * Updated by zhangwy on 2017/8/1 下午8:30.
 * Description
 */

public interface XIMediaController {

    void setOnPlayListener(OnPlayListener listener);

    void setOnUpProgressListener(OnUpProgressListener listener);

    void updateProgress(long playTime, long totalTime);

    void updateBuffering(int bufferProgress);

    void buffering();

    void play(boolean toPlay);

    interface OnPlayListener {
        /**
         * 将要更新的播放状态
         *
         * @param toPlay true 开始播放 false 停止播放
         */
        void onPlay(boolean toPlay);

        void onProgressChanged(int progress);
    }

    interface OnUpProgressListener {
        void onUpdateProgress(UpProgressCmd progressCmd);
    }

    interface UpProgressCmd {
        void onUpdateProgress(long playTime, long totalTime);
    }
}

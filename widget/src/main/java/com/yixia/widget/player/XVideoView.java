package com.yixia.widget.player;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;
import android.widget.VideoView;

import com.yixia.widget.R;

import java.util.Map;

import yixia.lib.core.util.Device;
import yixia.lib.core.util.Logger;
import yixia.lib.core.util.NetWorkObservable;
import yixia.lib.core.util.Screen;
import yixia.lib.core.util.WindowUtil;

/**
 * Created by zhangwy on 2017/8/1 下午5:51.
 * Updated by zhangwy on 2017/8/1 下午5:51.
 * Description 播放器封装
 */

@SuppressWarnings("unused")
public class XVideoView extends VideoView implements MediaPlayer.OnPreparedListener, MediaPlayer
        .OnBufferingUpdateListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnCompletionListener, XIMediaController
                .OnPlayListener, XIMediaController.OnUpProgressListener {

    public static final int ERROR_CODE_UNKNOWN = 1000;
    public static final int ERROR_CODE_DIED = 1001;
    public static final int ERROR_CODE_NETWORK = 1002;
    public static final int ERROR_CODE_TIME_OUT = 1003;
    public static final int INFO_CODE_UNKNOWN = 1100;
    public static final int INFO_CODE_GOBACK = 1101;
    public static final int INFO_CODE_PLAY_FIRST = 1151;
    public static final int INFO_CODE_PLAY_START = 1152;
    public static final int INFO_CODE_PLAY_STOP = 1153;
    public static final int INFO_CODE_PLAY_COMPLETE = 1154;

    private static final String TAG = "XVideoView";

    private XIMediaController xiMediaController;
    private OnXVideoViewListener xVideoViewListener;
    private OnXBufferingListener xBufferingListener;
    private OnXVideoViewUpdateProgressListener xVideoViewUpdateProgressListener;
    private OnXVideoViewUpdateCurrentPositionListener xVideoViewUpdateCurrentPositionListener;

    private XVideoViewNetWorkObserver xVideoViewNetWorkObserver;
    private UseMobileConfig.MonitorType monitorType = UseMobileConfig.MonitorType.CURRENTSTART;
    private Device.NetType netType = Device.NetType.WIFI;

    private boolean activeStopPlaying = false;
    private boolean useMobile = false;
    private boolean monitoring = true;
    private Dialog mobileDialog;
//    private String path;
//    private Uri uri;
//    private Map<String, String> headers;
    private boolean firstPlay = true;
    private boolean soundOff = false;
    private boolean localVideo = false;

    public XVideoView(Context context) {
        super(context);
        this.init();
    }

    public XVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public XVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        this.setOnPreparedListener(this);
        UseMobileConfig.initialize(this.getContext());
        NetWorkObservable.initialize(this.getContext().getApplicationContext());
        this.setMobileNetworkMonitoring(true, UseMobileConfig.MonitorType.CURRENTSTART);
    }

    public void setXMediaController(XIMediaController controller) {
        if (controller != null) {
            this.xiMediaController = controller;
            this.xiMediaController.setOnPlayListener(this);
        } else if (this.xiMediaController != null) {
            this.xiMediaController.setOnPlayListener(null);
            this.xiMediaController = null;
        }
    }

    public void setLocalVideo(boolean localVideo) {
        this.localVideo = localVideo;
    }

    public void setMobileNetworkMonitoring(boolean monitoring,
                                           UseMobileConfig.MonitorType monitorType) {
        this.monitoring = monitoring;
        this.monitorType = monitorType;
        if (monitoring && !UseMobileConfig.getInstance().useMobile(monitorType)) {
            this.xVideoViewNetWorkObserver = this.xVideoViewNetWorkObserver == null
                    ? new XVideoViewNetWorkObserver() : this.xVideoViewNetWorkObserver;
            NetWorkObservable.getInstance().register(this.xVideoViewNetWorkObserver);
        } else {
            NetWorkObservable.getInstance().unRegister(this.xVideoViewNetWorkObserver);
            this.xVideoViewNetWorkObserver = null;
            try {
                if (this.mobileDialog != null && this.mobileDialog.isShowing()) {
                    this.mobileDialog.dismiss();
                }
            } catch (Exception e) {
                Logger.e("setMobileNetworkMonitoring.closeDialog", e);
            }
        }
    }

    public void setOnBufferingListener(OnXBufferingListener listener) {
        this.xBufferingListener = listener;
    }

    public void setOnVideoViewListener(OnXVideoViewListener listener) {
        this.xVideoViewListener = listener;
    }

    public void setOnVideoViewUpdateProgressListener(OnXVideoViewUpdateProgressListener listener) {
        this.xVideoViewUpdateProgressListener = listener;
    }

    public void setOnVideoViewUpdateCurrentPositionListener(
            OnXVideoViewUpdateCurrentPositionListener listener) {
        this.xVideoViewUpdateCurrentPositionListener = listener;
    }

    public void destroy() {
        this.xVideoViewListener = null;
        this.xVideoViewUpdateProgressListener = null;
        this.xVideoViewUpdateCurrentPositionListener = null;
        if (this.xiMediaController != null) {
            this.xiMediaController.setOnPlayListener(null);
            this.xiMediaController.setOnUpProgressListener(null);
            this.xiMediaController = null;
        }
        if (this.xVideoViewNetWorkObserver != null) {
            NetWorkObservable.getInstance().unRegister(this.xVideoViewNetWorkObserver);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mobileNetwork()) {
            return;
        }
        this.updateBuffering(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        this.onFinish();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Logger.i(TAG, "onError : what = " + what + ", extra = " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                //release MediaPlayer and create a new MediaPlayer
                return onError(ERROR_CODE_DIED);
            case MediaPlayer.MEDIA_ERROR_UNKNOWN: {
                //Unspecified media player error.未指明异常，根据extra处理
                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO: {
                        //File or network related operation errors.
                        // 网络||io问题
                        return onError(ERROR_CODE_NETWORK);
                    }
                    case MediaPlayer.MEDIA_ERROR_MALFORMED: {
                        //Bitstream is not conforming to the related coding standard or file spec.
                        // 编码不支持
                        this.showToastMsg(R.string.widget_toast_coding_unusable);
                        return true;
                    }
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED: {
                        //Bitstream is conforming to the related coding standard or file spec,
                        // but the media framework does not support the feature.
                        //编码规范支持但是媒体底层不支持特性
                        this.showToastMsg(R.string.widget_toast_coding_unusable);
                        return true;
                    }
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                        //Some operation takes too long to complete, usually more than 3-5 seconds.
                        //操作超时
                        return onError(ERROR_CODE_TIME_OUT);
                    }
                    case -2147483648: {
                        return this.nonNetwork() || onError(ERROR_CODE_UNKNOWN);
                    }
                }
                break;
            }
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Logger.i(TAG, "onInfo : what = " + what + ", extra = " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_UNKNOWN: {
                //Unspecified media player info.
                //未知错误，无法处理
                break;
            }
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING: {
                //The video is too complex for the decoder: it can't decode frames fast enough.
                // Possibly only the audio plays fine at this stage.
                // 视频解码复杂
                this.showToastMsg(R.string.widget_toast_coding_unusable);
                return true;
            }
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                this.onStart();
                return true;
            }
            case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                //MediaPlayer is temporarily pausing playback internally in order to buffer more
                // data.
                // 暂停播放且开始缓冲
                if (nonNetwork() || mobileNetwork()) {
                    return true;
                } else {
                    this.onStandStillStart();
                }
                return true;
            }
            case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                //MediaPlayer is resuming playback after filling buffers.
                // 缓冲结束开始播放
                this.onStandStillEnd();
                return true;
            }
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING: {
                //Bad interleaving means that a media has been improperly interleaved or not
                // interleaved at all, e.g has all the video samples first then all the audio
                // ones. Video is playing but a lot of disk seeks may be happening.
                break;
            }
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE: {
                // The media cannot be seeked (e.g live stream)
                // 不能跳转到指定位置
                this.showToastMsg(R.string.widget_toast_coding_unsupported_seeked);
                return true;
            }
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE: {
                // A new set of metadata is available.
                // 新的视频元素可以使用
                this.updateCurrentPosition();
                break;
            }
            case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE: {
                // Subtitle track was not supported by the media framework.
                // 字幕轨迹不受媒体架构支持
                this.showToastMsg(R.string.widget_toast_coding_unsupported_subtitle);
                return true;
            }
            case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT: {
                // Reading the subtitle track takes too long.
                // 读取字幕超时
                this.showToastMsg(R.string.widget_toast_coding_outtime_read_subtitle);
                return true;
            }
        }
        return false;
    }

    private void soundOffImpl(MediaPlayer mediaPlayer) {
        if (this.soundOff) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(0, 0);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Logger.d(TAG, "onPrepared");
        mp.setOnBufferingUpdateListener(this);
        mp.setOnSeekCompleteListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnInfoListener(this);
        this.soundOffImpl(mp);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        this.updateProgress();
    }

    private void onStart() {
        this.mediaControllerPlay(true);
        if (this.xVideoViewListener != null) {
            if (this.firstPlay) {
                this.firstPlay = false;
                this.xVideoViewListener.onVideoInfo(INFO_CODE_PLAY_FIRST);
            }
            this.xVideoViewListener.onVideoStart();
        }
    }

    private void onStandStillStart() {
        this.mediaControllerPlay(false);
        this.mediaControllerBuffering();
        if (this.xBufferingListener != null) {
            this.xBufferingListener.onBuffering(true);
        }
    }

    private void onStandStillEnd() {
        this.mediaControllerPlay(true);
        if (this.xBufferingListener != null) {
            this.xBufferingListener.onBuffering(false);
        }
    }

    private boolean onError(int what) {
        this.mediaControllerPlay(false);
        if (this.xVideoViewListener == null)
            return false;
        this.xVideoViewListener.onVideoError(what);
        return true;
    }

    private boolean onInfo(int what) {
        if (this.xVideoViewListener == null)
            return false;
        this.xVideoViewListener.onVideoInfo(what);
        return true;
    }

    private void updateBuffering(int percent) {
        if (this.xiMediaController != null) {
            this.xiMediaController.updateBuffering(percent);
        }
        if (this.xVideoViewUpdateProgressListener != null) {
            this.xVideoViewUpdateProgressListener.onUpdateBuffering(percent);
        }
    }

    private void updateProgress() {
        if (this.xiMediaController != null) {
            this.xiMediaController.updateProgress(this.getCurrentPosition(), this.getDuration());
        }

        if (this.xVideoViewUpdateProgressListener != null) {
            this.xVideoViewUpdateProgressListener.onUpdateProgress(this.getCurrentPosition(),
                    this.getDuration());
        }
    }

    private void onStop() {
        this.mediaControllerPlay(false);
        if (this.xVideoViewListener != null) {
            this.xVideoViewListener.onVideoStop();
        }
    }

    private void onFinish() {
        this.updateProgress();
        this.mediaControllerPlay(false);
        if (this.xVideoViewListener != null) {
            this.xVideoViewListener.onVideoCompletion();
        }
    }

    @Override
    public void onPlay(boolean toPlay) {
        if (toPlay) {
            this.activeStopPlaying = false;
            if (nonNetwork() || mobileNetwork()) {
                return;
            }
            this.start();
            this.onStart();
        } else {
            this.activeStopPlaying = true;
            this.pause();
        }
    }

    @Override
    public void onProgressChanged(int progress) {
        int total = this.getDuration();
        if (total <= 0)
            return;
        this.seekTo(progress * total / 100);
    }

    @Override
    public void onUpdateProgress(XIMediaController.UpProgressCmd progressCmd) {
        if (progressCmd != null) {
            progressCmd.onUpdateProgress(this.getCurrentPosition(), this.getDuration());
        }
    }

    @Override
    public void setVideoPath(String path) {
        super.setVideoPath(path);
        this.resetVideo();
    }

    @Override
    public void setVideoURI(Uri uri) {
        super.setVideoURI(uri);
        this.resetVideo();
    }

    @Override
    public void setVideoURI(Uri uri, Map<String, String> headers) {
        super.setVideoURI(uri, headers);
        this.resetVideo();
    }

    private void saveVideoInfo(String path, Uri uri, Map<String, String> headers) {
//        this.path = path;
//        this.uri = uri;
//        this.headers = headers;
    }

    private void resetVideo() {
        this.useMobile = false;
        this.firstPlay = true;
        this.activeStopPlaying = false;
        this.setOnErrorListener(this);
        if (this.xiMediaController != null) {
            this.xiMediaController.updateProgress(0, 0);
            this.xiMediaController.updateBuffering(0);
        }
        this.mediaControllerBuffering();
    }

    public void soundOff() {
        this.soundOff = true;
    }

    @Override
    public void start() {
        if (this.activeStopPlaying || nonNetwork() || mobileNetwork()) {
            return;
        }
        this.setOnErrorListener(this);
        super.start();
    }

    public void resumeStart() {
        if (this.activeStopPlaying || nonNetwork() || mobileNetwork())
            return;
        this.start();
        this.mediaControllerPlay(true);
    }

    @Override
    public void pause() {
        super.pause();
        this.onStop();
    }

    @Override
    public void stopPlayback() {
        super.stopPlayback();
        this.onStop();
    }

    private void mediaControllerBuffering() {
        if (this.xiMediaController != null) {
            this.xiMediaController.buffering();
        }
    }

    private void mediaControllerPlay(boolean toPlay) {
        if (this.xiMediaController != null) {
            this.xiMediaController.setOnUpProgressListener(toPlay ? this : null);
            if (!toPlay) {
                this.updateCurrentPosition();
            }
            this.xiMediaController.play(toPlay);
        }
    }

    private void updateCurrentPosition() {
        long currentPosition, duration;
        if (this.xVideoViewUpdateCurrentPositionListener != null
                && (currentPosition = this.getCurrentPosition()) > 0
                && (duration = this.getDuration()) > 0) {
            Logger.d(TAG, "onInfo cPosition=" + currentPosition + ", duration=" + duration);
            this.xVideoViewUpdateCurrentPositionListener
                    .onUpdateCurrentPosition(currentPosition, duration);
        }
    }

    private void showToastMsg(int resId) {
        Toast toast = Toast.makeText(this.getContext().getApplicationContext(), resId,
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, Screen.getScreenWidth(getContext()) / 3);
        toast.show();
    }

    private class XVideoViewNetWorkObserver extends NetWorkObservable.NetWorkObserver {

        @Override
        public void updateNetWork(Device.NetType netType) {
            XVideoView.this.updateNetWork(netType);
        }
    }

    private void updateNetWork(Device.NetType netType) {
        this.netType = netType;
        if (this.getBufferPercentage() < 100) {
            this.mobileNetwork();
        }
    }

    private boolean nonNetwork() {
        return !this.localVideo
                && this.netType == Device.NetType.NONE && this.onError(ERROR_CODE_NETWORK);
    }

    private boolean mobileNetwork() {
        return this.monitoring && !this.localVideo && this.isMobile() && this.mobileNetworkDialog();
    }

    private boolean mobileNetworkDialog() {
        if (this.activeStopPlaying
                || (this.monitorType == UseMobileConfig.MonitorType.CURRENTMEDIA && this.useMobile)
                || (this.monitorType == UseMobileConfig.MonitorType.CURRENTSTART
                && UseMobileConfig.getInstance().useMobile(UseMobileConfig.MonitorType
                .CURRENTSTART))
                || (this.monitorType == UseMobileConfig.MonitorType.ETERNAL
                && UseMobileConfig.getInstance().useMobile(UseMobileConfig.MonitorType.ETERNAL)))
            return false;
        if (this.mobileDialog != null && this.mobileDialog.isShowing())
            return true;
        this.pause();
        mobileDialog = WindowUtil.createAlertDialog(this.getContext(), R.string.app_name,
                this.getContext().getString(R.string.widget_dialog_message_mobile),
                R.string.widget_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mobileDialog = null;
                        dialog.dismiss();
                        useMobile = true;
                        UseMobileConfig.getInstance().setUseMobile(monitorType, true);
                        start();
                    }
                }, R.string.widget_dialog_goback, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mobileDialog = null;
                        dialog.dismiss();
                        onInfo(INFO_CODE_GOBACK);
                    }
                });
        if (mobileDialog != null && !mobileDialog.isShowing()) {
            mobileDialog.show();
            mobileDialog.setCanceledOnTouchOutside(false);
            mobileDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            });
        }
        return true;
    }

    private boolean isMobile() {
        switch (this.netType) {
            case MOBILE2:
            case MOBILE3:
            case MOBILE4:
                return true;
        }
        return false;
    }

    public interface OnXVideoViewListener {
        void onVideoStart();

        void onVideoStop();

        void onVideoError(int what);

        void onVideoInfo(int what);

        void onVideoCompletion();
    }

    public interface OnXVideoViewUpdateCurrentPositionListener {
        void onUpdateCurrentPosition(long playTime, long totalTime);
    }

    public interface OnXVideoViewUpdateProgressListener {
        void onUpdateProgress(long playTime, long totalTime);

        void onUpdateBuffering(int percent);
    }

    public interface OnXBufferingListener {
        void onBuffering(boolean buffering);
    }
}

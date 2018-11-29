package com.yixia.widget.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yixia.widget.R;

import yixia.lib.core.util.TimeUtil;

/**
 * Created by zhangwy on 2017/7/29 下午4:15.
 * Updated by zhangwy on 2017/7/29 下午4:15.
 * Description 控制器
 */

@SuppressWarnings("unused")
public class XMediaController extends FrameLayout implements XIMediaController,
        XIMediaController.UpProgressCmd, XIMediaControllerAction, SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, Handler.Callback {

    private static final int WHAT_HIDE_WIDGET = 100;
    private static final int WHAT_UPDATE_PROGRESS = 101;
    private Handler handler = new Handler(this);
    private TextView timeCurrent;
    private TextView timeTotal;
    private SeekBar seekBar;
    private View switchScreen;
    private ImageView switchScreenIcon;
    private View centerBar;
    private ImageView startAndStop;
    private View progress;

    private XIMediaControllerAction.OnSwitchListener onSwitchListener;
    private XIMediaController.OnPlayListener onPlayListener;
    private XIMediaController.OnUpProgressListener onUpProgressListener;
    private boolean landscape = false;
    private boolean playing = false;

    public XMediaController(@NonNull Context context) {
        super(context);
        this.init();
    }

    public XMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public XMediaController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public XMediaController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int
            defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        LayoutInflater.from(this.getContext().getApplicationContext())
                .inflate(R.layout.widget_media_controller, this, true);
        this.timeCurrent = findViewById(R.id.widget_media_controller_current);
        this.timeTotal = findViewById(R.id.widget_media_controller_total);
        this.seekBar = findViewById(R.id.widget_media_controller_progress);
        this.switchScreen = findViewById(R.id.widget_media_controller_switch_screen);
        this.switchScreenIcon = findViewById(R.id.widget_media_controller_switch_screen_icon);
        this.centerBar = findViewById(R.id.widget_media_controller_centerbar);
        this.startAndStop = findViewById(R.id.widget_media_controller_start_stop);
        this.progress = findViewById(R.id.widget_media_controller_buffering);
        this.switchScreen.setVisibility(GONE);
        this.setOnClickListener();
        this.setOrientation(this.landscape);
        this.setPlayStatus(this.playing);
        this.setVisibility(true, false);
    }

    private void setOnClickListener() {
        this.setOnClickListener(this);
        this.startAndStop.setOnClickListener(this);
        this.seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void setOnPlayListener(XIMediaController.OnPlayListener listener) {
        this.onPlayListener = listener;
    }

    @Override
    public void setOnSwitchListener(XIMediaControllerAction.OnSwitchListener listener) {
        this.switchScreen.setVisibility(VISIBLE);
        this.switchScreen.setOnClickListener(this);
        this.onSwitchListener = listener;
    }

    @Override
    public void setOnUpProgressListener(XIMediaController.OnUpProgressListener listener) {
        if (listener == null) {
            this.removeUpdateMessage();
        } else {
            this.sendUpdateMessage(false);
        }
        this.onUpProgressListener = listener;
    }

    @Override
    public final void onUpdateProgress(long playTime, long totalTime) {
        updateProgress(playTime, totalTime);
    }

    @Override
    public void updateProgress(long playTime, long totalTime) {
        this.setCurrentTime(playTime);
        this.setTotalTime(totalTime);
        this.updateSeekBarProgress(playTime, totalTime);
    }

    @Override
    public void updateBuffering(int bufferProgress) {
        this.seekBar.setSecondaryProgress(bufferProgress < 0
                ? 0 : (bufferProgress > 100 ? 100 : bufferProgress));
    }

    private void updateSeekBarProgress(long playTime, long totalTime) {
        if (totalTime > 0) {
            int progress = (int) (playTime * 100 / totalTime);
            progress = progress < 0 ? 0 : (progress > 100 ? 100 : progress);
            this.seekBar.setProgress(progress);
        } else {
            this.seekBar.setProgress(0);
        }
    }

    @Override
    public void buffering() {
        this.setVisibility(true, false);
        this.setPlayStatus(true);
        this.setBuffering(true);
    }

    @Override
    public void play(boolean toPlay) {
        this.playing = toPlay;
        this.setBuffering(false);
        this.setPlayStatus(this.playing);
        if (toPlay) {
            this.sendUpdateMessage(true);
            this.sendHideMessage();
        } else {
            this.removeUpdateMessage();
            this.removeHideMessage();
            this.setVisibility(true, false);
        }
    }

    @Override
    public void switchScreen(boolean toLandscape) {
        this.landscape = toLandscape;
        this.setOrientation(this.landscape);
    }

    @Override
    public final void onClick(View v) {
        this.setVisibility(true, this.playing);
        if (v == this.switchScreen) {
            if (this.onSwitchListener != null) {
                this.onSwitchListener.onSwitchScreen(!this.landscape);
            }
        } else if (v == this.startAndStop) {
            if (this.onPlayListener != null) {
                this.onPlayListener.onPlay(!this.playing);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && this.onPlayListener != null) {
            this.onPlayListener.onProgressChanged(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.removeHideMessage();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.setVisibility(true, this.playing);
    }

    private void setCurrentTime(long ms) {
        if (ms < 0)
            return;
        this.timeCurrent.setText(TimeUtil.dateMilliSecond2String(ms, TimeUtil.PATTERN_MMSS));
    }

    private void setTotalTime(long ms) {
        if (ms < 0)
            return;
        this.timeTotal.setText(TimeUtil.dateMilliSecond2String(ms, TimeUtil.PATTERN_MMSS));
    }

    private void setOrientation(boolean orientation) {
        this.switchScreenIcon.setImageResource(orientation
                ? R.drawable.icon_widget_video_shrink : R.drawable.icon_widget_video_expand);
    }

    private void setBuffering(boolean buffering) {
        this.progress.setVisibility(buffering ? VISIBLE : GONE);
    }

    private void setPlayStatus(boolean play) {
        this.startAndStop.setImageResource(play
                ? R.drawable.icon_widget_video_pause : R.drawable.iocn_widget_video_play);
    }

    private void setVisibility(boolean show, boolean autoHide) {
        this.findViewById(R.id.widget_media_controller_bottom_bar).setVisibility(show
                ? VISIBLE : INVISIBLE);
        this.centerBar.setVisibility(show ? VISIBLE : INVISIBLE);
        if (show && autoHide) {
            this.sendHideMessage();
        }
    }

    private void sendUpdateMessage(boolean now) {
        this.removeUpdateMessage();
        this.handler.sendEmptyMessageDelayed(WHAT_UPDATE_PROGRESS, now ? 0 : 1000);
    }

    private void removeUpdateMessage() {
        this.handler.removeMessages(WHAT_UPDATE_PROGRESS);
    }

    private void sendHideMessage() {
        this.removeHideMessage();
        this.handler.sendEmptyMessageDelayed(WHAT_HIDE_WIDGET, 4000);
    }

    private void removeHideMessage() {
        this.handler.removeMessages(WHAT_HIDE_WIDGET);
    }

    @Override
    public final boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WHAT_HIDE_WIDGET:
                this.hideWidget();
                break;
            case WHAT_UPDATE_PROGRESS:
                if (this.onUpProgressListener != null) {
                    this.onUpProgressListener.onUpdateProgress(this);
                    this.sendUpdateMessage(false);
                }
                break;
        }
        return true;
    }

    private void hideWidget() {
        setVisibility(false, false);
    }

}

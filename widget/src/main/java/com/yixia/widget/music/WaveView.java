package com.yixia.widget.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by zhaoliangtai on 2018/5/9.
 */

public class WaveView extends View {

    private static final String BGCOLOR = "#ffffff";
    private static final String SELECTED_COLOR = "#FD415F";

    private Paint mPaint;

    private Xfermode mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    private int mLineWidth;
    private int mBackgroundColor;
    private int mSelectedColor;
    private int mRealWidth;
    private int mLineStep;

    private int mWaveHeight;

    private float mDownX;
    private float mDownY;
    private boolean isPressedHandle = false;

    private float selectedAreaRightLoc = 0;
    private float mSelectedAreaLeftLoc = 0;
    private float mStartLoc = 0;
    private float mMoveDistance = 0f;

    private float mSelectedAreaWidth;

    private long mMaxDuration;
    private long mSelectedDuration;
    private long mStartDuration;

    private RectF mWaveLineRect;
    private Rect mBgRect;

    private OnMusicTimeSelectedListener mOnMusicTimeSelectedListener;

    // 音谱数据 这里用的假数据
    private static final float[] LINE_DATA = {
            .5f, .7f, .35f, .5f, .35f, .7f, 1f, .7f,
            .5f, .35f, .5f, .3f, .7f, .5f, .7f, .9f,
            .7f, .5f, .35f, .5f, .3f, .7f, .5f, .7f,
            .2f, .4f, .35f, .7f, 1f, .7f, .5f, .35f,
            .9f, .7f, .5f, .35f, .5f, .35f, .7f, .5f,
            .7f, .3f, .5f, .35f, .5f, .7f, .35f, .5f,
            .35f, .7f, .9f, .7f, .5f, .35f, .5f, .35f,
            .7f
    };


    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mBackgroundColor = Color.parseColor(BGCOLOR);
        mSelectedColor = Color.parseColor(SELECTED_COLOR);
        mWaveHeight = dp2px(context, 50);
        mWaveLineRect = new RectF();
        mBgRect = new Rect();
        setDrawingCacheEnabled(false);
    }

    public void setOnMusicTimeSelectedListener(OnMusicTimeSelectedListener listener) {
        mOnMusicTimeSelectedListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mRealWidth = width - getPaddingLeft() - getPaddingRight();
        mSelectedAreaWidth = ((float) mSelectedDuration / mMaxDuration * mRealWidth);
        mStartLoc = ((float) mStartDuration / mMaxDuration * mRealWidth) + getPaddingLeft();
        mSelectedAreaLeftLoc = mStartLoc;
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setMaxDuration(long duration) {
        mMaxDuration = duration;
    }

    public void setStartDuration(long duration) {
        mStartDuration = duration;
        mStartLoc = ((float) mStartDuration / mMaxDuration * mRealWidth) + getPaddingLeft();
        mSelectedAreaLeftLoc = mStartLoc;
    }

    public void setSelectedDuration(long duration) {
        if (duration > mMaxDuration) {
            duration = mMaxDuration;
        }
        mSelectedDuration = duration;
        mSelectedAreaWidth = ((float) mSelectedDuration / mMaxDuration * mRealWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mLineStep = mRealWidth / LINE_DATA.length;
        int baseLine = mWaveHeight >> 1;
        mLineWidth = (int) ((float) mLineStep / 3 * 2);
        mPaint.setColor(mBackgroundColor);
        for (int i = 0, len = LINE_DATA.length; i < len; i++) {
            mWaveLineRect.left = getPaddingLeft() + mLineStep * i;
            mWaveLineRect.right = getPaddingLeft() + mLineStep * i + mLineWidth;
            int lineHeight = (int) (LINE_DATA[i] * mWaveHeight);
            mWaveLineRect.top = baseLine - (lineHeight >> 1);
            mWaveLineRect.bottom = baseLine + (lineHeight >> 1);
            canvas.drawRoundRect(mWaveLineRect, 10, 10, mPaint);
        }
        mPaint.setXfermode(mXfermode);
        mPaint.setColor(mSelectedColor);
        mSelectedAreaLeftLoc = mStartLoc + mMoveDistance;
        selectedAreaRightLoc = mSelectedAreaLeftLoc + mSelectedAreaWidth;
        if (mSelectedAreaLeftLoc < getPaddingLeft()) {
            mSelectedAreaLeftLoc = getPaddingLeft();
            selectedAreaRightLoc = mSelectedAreaLeftLoc + mSelectedAreaWidth;
        }
        if (selectedAreaRightLoc > (canvas.getWidth() - getPaddingRight())) {
            selectedAreaRightLoc = (canvas.getWidth() - getPaddingRight());
            mSelectedAreaLeftLoc = selectedAreaRightLoc - mSelectedAreaWidth;
        }
        mBgRect.left = (int) mSelectedAreaLeftLoc;
        mBgRect.top = 0;
        mBgRect.right = (int) selectedAreaRightLoc;
        mBgRect.bottom = mWaveHeight;
        canvas.drawRect(mBgRect, mPaint);
        mPaint.setXfermode(null);
    }

    public void callbackInit() {
        if (null != mOnMusicTimeSelectedListener) {
            mOnMusicTimeSelectedListener.onMusicTimeSelected(mStartDuration,
                    mStartDuration + mSelectedDuration,
                    mStartDuration,
                    mStartDuration + mSelectedDuration);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                isPressedHandle = checkTouchedHandle(mDownX, mDownY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isPressedHandle) {
                    mMoveDistance = event.getX() - mDownX;
                    if (null != mOnMusicTimeSelectedListener) {
                        long start =
                                (long) ((mStartLoc + mMoveDistance - getPaddingLeft()) / mRealWidth
                                        * mMaxDuration);
                        float left = mStartLoc + mMoveDistance;
                        if (left < getPaddingLeft()) {
                            start = 0;
                        } else if ((left + mSelectedAreaWidth) > (getWidth() - getPaddingRight())) {
                            start = mMaxDuration - mSelectedDuration;
                        }
                        mOnMusicTimeSelectedListener.onMusicTimeChanged(start,
                                start + mSelectedDuration,
                                mStartDuration,
                                mStartDuration + mSelectedDuration);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isPressedHandle) {
                    mStartLoc = mSelectedAreaLeftLoc;
                    mSelectedAreaLeftLoc = 0;
                    mMoveDistance = 0;
                    if (null != mOnMusicTimeSelectedListener) {
                        long start = (long) ((mStartLoc - getPaddingLeft()) / mRealWidth
                                * mMaxDuration);
                        mOnMusicTimeSelectedListener.onMusicTimeSelected(start,
                                start + mSelectedDuration, mStartDuration,
                                mStartDuration + mSelectedDuration);
                        mStartDuration = start;
                    }
                }
                break;
        }
        return isPressedHandle;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mOnMusicTimeSelectedListener != null) {
            mOnMusicTimeSelectedListener = null;
        }
    }

    private boolean checkTouchedHandle(float x, float y) {
        return x > mSelectedAreaLeftLoc && x < selectedAreaRightLoc && y > 0 && y < mWaveHeight;
    }

    public interface OnMusicTimeSelectedListener {
        void onMusicTimeChanged(long start, long end, long oldStart, long oldEnd);

        void onMusicTimeSelected(long start, long end, long oldStart, long oldEnd);
    }
}

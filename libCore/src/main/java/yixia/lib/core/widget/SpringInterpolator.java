package yixia.lib.core.widget;

import android.view.animation.Interpolator;

/**
 * Created by liutao on 30/10/2017.
 */

public class SpringInterpolator implements Interpolator {

    private float mFactor;

    public SpringInterpolator(float factor) {
        mFactor = factor;
    }

    @Override
    public float getInterpolation(float input) {
        return (float) (Math.pow(2, -10 * input) * Math.sin((input - mFactor / 4) * (2 * Math.PI) / mFactor) + 1);
    }
}

package yixia.lib.core.widget.tooltip;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Helper class to create Animator Objects
 */
public class AnimationUtils {

    /**
     * Fade Animation
     * @param view View to be animated
     * @param fromAlpha initial alpha
     * @param toAlpha final alpha
     * @param duration animation duration in milliseconds
     * @return Animator Object
     */
    @NonNull
    public static Animator fade(@NonNull final View view, int duration, float... alpha) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", alpha);
        animator.setDuration(duration);
        return animator;
    }

    /**
     * Circular Reveal Animation
     * @param view View to be animated
     * @param cx x coordinate of the center of the circle
     * @param cy y coordinate of the center of the circle
     * @param startRadius initial circle radius
     * @param finalRadius final circle radius
     * @param duration animation duration in milliseconds
     * @return Animator Object
     */
    @NonNull
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Animator reveal(@NonNull final View view, int cx, int cy, int startRadius, int finalRadius, int duration) {
        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, finalRadius);
        animator.setDuration(duration);
        return animator;
    }

    /**
     * Animator to animate Y scale of the view. X scale is constant
     *
     * @param view View to be animated
     * @param pivotX x coordinate of the pivot
     * @param pivotY y coordinate of the pivot
     * @param fromScale initial scale
     * @param toScale final scale
     * @param duration animation duration in milliseconds
     * @return Animator Object
     */
    @NonNull
    public static Animator scaleY(@NonNull View view, int pivotX, int pivotY, int duration, float... scaleY) {
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        Animator animator = ObjectAnimator.ofFloat(view, "scaleY", scaleY);
        animator.setDuration(duration);
        return animator;
    }

    /**
     * Animator to animate X scale of the view. Y scale is constant
     *
     * @param view View to be animated
     * @param pivotX x coordinate of the pivot
     * @param pivotY y coordinate of the pivot
     * @param fromScale initial scale
     * @param toScale final scale
     * @param duration animation duration in milliseconds
     * @return Animator Object
     */
    @NonNull
    public static Animator scaleX(@NonNull View view, int pivotX, int pivotY, int duration, float... scaleX) {
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        Animator animator = ObjectAnimator.ofFloat(view, "scaleX", scaleX);
        animator.setDuration(duration);
        return animator;
    }

    @NonNull
    public static Animator scale(@NonNull View view, int pivotX, int pivotY, int duration, float... scale) {
        view.setPivotX(pivotX);
        view.setPivotY(pivotY);
        PropertyValuesHolder holder1 = PropertyValuesHolder.ofFloat("scaleX", scale);
        PropertyValuesHolder holder2 = PropertyValuesHolder.ofFloat("scaleY", scale);
        Animator animator = ObjectAnimator.ofPropertyValuesHolder(view, holder1, holder2);
        animator.setDuration(duration);
        return animator;
    }

}

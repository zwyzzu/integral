package yixia.lib.core.anotation;

import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef({View.VISIBLE, View.INVISIBLE, View.GONE})
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Visibility {
}
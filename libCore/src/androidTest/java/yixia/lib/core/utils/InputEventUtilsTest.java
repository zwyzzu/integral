package yixia.lib.core.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.AttributeSet;
import android.widget.Button;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import yixia.lib.core.util.InputEventUtils;

@RunWith(AndroidJUnit4.class)
public class InputEventUtilsTest {

    class MineButton extends Button {
        private String mTag;

        public MineButton(Context context, String tag) {
            super(context);
            mTag = tag;
        }

        public MineButton(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MineButton(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public MineButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public String toString() {
            return mTag;
        }

        @Override
        public int hashCode() {
            return mTag.hashCode();
        }
    }

    @Test
    public void testDoubleClickOneReapeat() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Button btn = new MineButton(appContext, 1 + "");
        for(int i = 0 ; i < 300; i++) {
            boolean isRep = InputEventUtils.isRepeatClick(btn, 200);
            System.out.println("" + btn.toString() + " " + isRep);
            try {
                Thread.sleep(2);
            }catch (Exception e){

            }
        }
    }

    @Test
    public void testDoubleClick() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        List<Button> btnList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Button btn = new MineButton(appContext, i + "");
            btnList.add(btn);
            boolean isRep = InputEventUtils.isRepeatClick(btn, 200);
            System.out.println("" + btn.toString() + " " + isRep);
        }
        try {
            Thread.sleep(170);
        } catch (Exception e) {
        }
        for (int i = 0; i < 20; i++) {
            Button btn = new MineButton(appContext, i + "");
            boolean isRep = InputEventUtils.isRepeatClick(btn, 0);
            System.out.println(" " + btn.toString() + " " + isRep);
        }
    }

}

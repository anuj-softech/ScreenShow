package com.rock.screenshow.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class AnimUtils {
    public static ObjectAnimator blink(View fv) {
        ObjectAnimator ob = new ObjectAnimator();
        ob.setDuration(1500);
        ob.setProperty(View.ALPHA);
        ob.setFloatValues(0.5f, 1.0f);
        ob.setRepeatMode(ValueAnimator.REVERSE);
        ob.setRepeatCount(ValueAnimator.INFINITE);
        ob.setTarget(fv);
        return ob;
    }
}

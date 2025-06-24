package com.rock.screenshow.views;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class TVButton extends androidx.appcompat.widget.AppCompatButton {

    public TVButton(Context context) {
        super(context);
        init(null);
    }

    public TVButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TVButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressLint("ResourceType")
    private void init(AttributeSet attrs) {
        ObjectAnimator ob = AnimUtils.blink(this);

        setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ob.start();
            } else {
                ob.cancel();
            }
        });

    }


}


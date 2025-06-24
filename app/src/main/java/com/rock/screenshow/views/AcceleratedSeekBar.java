package com.rock.screenshow.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.SeekBar;

@SuppressLint("AppCompatCustomView")
public class AcceleratedSeekBar extends SeekBar {

    private static final int DEFAULT_ACCELERATION_FACTOR = 7;
    private int accelerationFactor = DEFAULT_ACCELERATION_FACTOR;

    public AcceleratedSeekBar(Context context) {
        super(context);
    }

    public AcceleratedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AcceleratedSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAccelerationFactor(int accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean handled = super.dispatchKeyEvent(event);
        if (!handled && event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                int progress = getProgress();
                int max = getMax();
                int step = max / 100; // Define your step size here
                if (step == 0) step = 1;

                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    progress += step * accelerationFactor;
                } else {
                    progress -= step * accelerationFactor;
                }
                progress = Math.max(0, Math.min(max, progress));
                setProgress(progress);
                return true;
            }
        }
        return handled;
    }


}

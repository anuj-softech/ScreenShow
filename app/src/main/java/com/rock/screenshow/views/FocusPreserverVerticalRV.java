package com.rock.screenshow.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class FocusPreserverVerticalRV extends RecyclerView {
    public FocusPreserverVerticalRV(@NonNull Context context) {
        super(context);
        initRV();
    }


    public FocusPreserverVerticalRV(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRV();
    }

    public FocusPreserverVerticalRV(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRV();
    }

    private void initRV() {

    }

}

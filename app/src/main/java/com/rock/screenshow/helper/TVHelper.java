package com.rock.screenshow.helper;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TVHelper {
    public static @NonNull LinearLayoutManager getAdaptiveLM(Context context) {
        LinearLayoutManager layout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null) {
            if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
                layout.setOrientation(RecyclerView.HORIZONTAL);
            }
        }

        return layout;
    }
}

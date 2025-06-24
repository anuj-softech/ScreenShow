package com.rock.screenshow.player;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.media.audiofx.LoudnessEnhancer;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;


public class PlayerUtils {
    @SuppressLint("DefaultLocale")
    public static String getCDText(long milliseconds) {
        if (milliseconds > 1000 * 60 * 60) {
            return String.format("%02d:%02d:%02d", milliseconds / (1000 * 60 * 60), (milliseconds / (1000 * 60)) % 60, (milliseconds / (1000)) % 60);
        } else {
            return String.format("%02d:%02d", (milliseconds / (1000 * 60)) % 60, (milliseconds / (1000)) % 60);
        }
    }


}

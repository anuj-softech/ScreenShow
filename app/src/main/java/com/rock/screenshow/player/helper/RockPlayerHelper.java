package com.rock.screenshow.player.helper;

import androidx.media3.exoplayer.source.MediaSource;

import com.rock.screenshow.model.PlayInfo;
import com.rock.screenshow.model.RockPlayerConfig;

public class RockPlayerHelper {
    private static final String TAG = "RockPlayerHelper";
    public final PlayInfo playInfo;
    public RockPlayerConfig playerConf;


    public RockPlayerHelper(PlayInfo playInfo) {
        this.playInfo = playInfo;
        this.playerConf = playInfo.getPlayerConfig();
    }

    public void addToHistory() {
    }

    public MediaSource getMediaSource(Object ob) {
        return null;
    }

    public String getTitle() {
        return "RockPlayer";
    }

    public String getMetaData() {
        return "";
    }

    public String getSecondTitle() {
        return "video";
    }

    public String getID() {
        return playInfo.getId();
    }

    public long getDefaultPlayOffset() {
        return 0;
    }

    public boolean isAudioBoost() {
        if(playerConf != null)
            return playerConf.isAudioBoost();
        return false;
    }

    public boolean isColorBoost(){
        if(playerConf != null)
            return playerConf.isColorBoost();
        return false;
    }

    public boolean isAutoPlayNext() {
        if(playerConf != null)
            return playerConf.isAutoPlayNext();
        return false;
    }

    public boolean isStretched() {
        if(playerConf != null)
            return playerConf.isStretched();
        return false;
    }

    public int[] maxQuality() {
        if(playerConf != null) {
            String[] qt = playerConf.getMaxQuality().split(",");
            return new int[]{Integer.parseInt(qt[0]), Integer.parseInt(qt[1])};
        }
        return new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
    }

    public boolean isSubtitleEnabled() {
        if(playerConf != null)
            return playerConf.isSubtitleEnabled();
        return true;
    }

    public boolean isFFMPEGDecoder() {
        return playInfo.getDecoder() != null && playInfo.getDecoder().equalsIgnoreCase("ffmpeg");
    }
}

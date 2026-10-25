package com.rock.screenshow.player.helper;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;

import com.rock.screenshow.model.PlayInfo;

public class ScreenShowPlayerHelper extends RockPlayerHelper {


    public ScreenShowPlayerHelper(PlayInfo playInfo) {
        super(playInfo);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public MediaSource getMediaSource(Object ob) {
        MediaItem mediaItem = MediaItem.fromUri(playInfo.getVideoUrl());
        return new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory()).createMediaSource(mediaItem);
    }

}

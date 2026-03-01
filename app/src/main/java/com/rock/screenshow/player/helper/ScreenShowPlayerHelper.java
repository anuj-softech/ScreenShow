package com.rock.screenshow.player.helper;

import android.content.Context;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;

import com.rock.screenshow.model.PlayInfo;
import com.rock.screenshow.repository.MediaRepository;

public class ScreenShowPlayerHelper extends RockPlayerHelper {
    private final String fileId;
    private final Context context;
    private String title  = "";

    public ScreenShowPlayerHelper(String fileId,Context context) {
        this.fileId = fileId;
        this.context = context;
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public MediaSource getMediaItem(Object ob) {
        MediaRepository mediaRepository = new MediaRepository(context);
        PlayInfo playInfo = mediaRepository.getPlayInfoBlocking(fileId);
        MediaItem mediaItem = MediaItem.fromUri(playInfo.getVideoUrl());
        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(new DefaultHttpDataSource.Factory()).createMediaSource(mediaItem);
        return mediaSource;
    }

    @Override
    public String getTitle() {
        return title;
    }

}

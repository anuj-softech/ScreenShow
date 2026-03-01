package com.rock.screenshow.model

/*
    defines different type of media content supported be app
    Series - opens up the series details page
    Channel - live channel starts playing in player immediately
    Movie - opens up the movie details page
    Episode - play the episode directly in player
    video - for single video to be played in player
 */
enum class VideoItemType{
    Series,Channel,Movie,Episode,Video
}

data class VideoItem(
    val id: String,
    val title: String,
    val type: VideoItemType,
    val size: Long? = null,
    val thumbnail: String? = null,
    val meta: String? = null,
    val duration: Int? = null
)

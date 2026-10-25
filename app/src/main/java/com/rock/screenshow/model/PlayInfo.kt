package com.rock.screenshow.model

import okhttp3.Headers

data class PlayInfo(
    //id of video
    val id: String,
    //title of video
    val title: String,
    // type of player helper that player must create to be able to play this video
    val type: String,
    // url of video
    val videoUrl: String,
    // subtitles urls as per language like eng for english
    val subUrls: Map<String, String>? = null,
    // playerConfig
    val playerConfig: RockPlayerConfig?,
    // other meta data like description
    val meta: String? = null,
    // type of decoder like FFMPEG
    val decoder: String? = null,
    // drm type
    val drmType: String? = null,
    // licence value
    val licenceValue: String? = null,
    // headers
    val headers: Headers?
)
package com.rock.screenshow.model

import okhttp3.Headers

data class PlayInfo(
    val id: String,
    val title: String,
    val type: String,
    val videoUrl: String,
    val subUrls: Map<String, String>? = null,
    val resolution: String? = null,
    val bitrate: Int? = null,
    val meta: String? = null,
    val decoder: String? = null,
    val drmType: String? = null,
    val licenceValue: String? = null,
    val headers: Headers?
)
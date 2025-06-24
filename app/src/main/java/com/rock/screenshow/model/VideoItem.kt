package com.rock.screenshow.model

data class VideoItem(
    val id: String,
    val title: String,
    val type: String,
    val size: Long? = null,
    val thumbnail: String? = null,
    val meta: String? = null,
    val duration: Int? = null
)

package com.rock.screenshow.model

data class RockPlayerConfig(
    val isLive:Boolean = false,
    val playerOffset: Int = 0,
    val isAudioBoost:Boolean = false,
    val isColorBoost:Boolean = false,
    val isAutoPlayNext:Boolean = false,
    val isStretched:Boolean = false,
    val maxQuality: String = Int.MAX_VALUE.toString() +","+ Int.MAX_VALUE.toString(),
    val isSubtitleEnabled:Boolean = false,
    )

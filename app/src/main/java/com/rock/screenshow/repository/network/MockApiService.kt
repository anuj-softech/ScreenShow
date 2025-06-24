package com.rock.screenshow.repository.network

import com.rock.screenshow.model.*
import kotlinx.coroutines.delay
import kotlin.random.Random

class MockApiService : ApiService {

    override suspend fun fetchHomeRows(): List<VideoRow> {
        delay(500)

        return listOf(
            VideoRow(
                title = "Trending Now",
                videos = generateMockVideos(5)
            ),
            VideoRow(
                title = "Documentaries",
                videos = generateMockVideos(3, "doc")
            ),
            VideoRow(
                title = "Tech Talks",
                videos = generateMockVideos(4, "tech")
            )
        )
    }

    override suspend fun searchVideos(query: String): List<VideoItem> {
        delay(300)
        return List(5) {
            VideoItem(
                id = "vid$it",
                title = "$query Result #$it",
                type = "mp4",
                thumbnail = "https://placehold.co/320x180?text=$query+$it",
                duration = Random.nextInt(60, 600)
            )
        }
    }

    override suspend fun getPlayInfo(videoId: String): PlayInfo {
        delay(200)
        return PlayInfo(
            id = videoId,
            title = "Video $videoId",
            type = "mp4",
            videoUrl = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4",
            subUrls = mapOf(
                "en" to "https://sample-videos.com/subtitles/sample-en.vtt",
                "hi" to "https://sample-videos.com/subtitles/sample-hi.vtt"
            ),
            resolution = "720p",
            bitrate = 1200,
            meta = "Mock metadata for video $videoId"
        )
    }

    private fun generateMockVideos(count: Int, prefix: String = "vid"): List<VideoItem> {
        return List(count) { i ->
            VideoItem(
                id = "$prefix-$i",
                title = "Sample $prefix Video #$i",
                type = "mp4",
                size = Random.nextLong(10_000_000, 100_000_000),
                thumbnail = "https://placehold.co/300x200?text=Video+$i",
                duration = Random.nextInt(100, 1000),
                meta = "Generated video data"
            )
        }
    }
}

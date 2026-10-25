package com.rock.screenshow.repository.network

import com.rock.screenshow.model.PlayInfo
import com.rock.screenshow.model.VideoItem
import com.rock.screenshow.model.VideoItemType
import com.rock.screenshow.model.VideoRow
import kotlinx.coroutines.delay
import kotlin.random.Random

const val TAG = "MockApiService";

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
                type = VideoItemType.Movie,
                thumbnail = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSTTq3MPKMKn8Gw-uCvZtwqWZu9xptQ8Je5Rf-DQb2OdA&s=10",
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
            videoUrl = "https://raw.githubusercontent.com/chthomos/video-media-samples/refs/heads/master/big-buck-bunny-1080p-60fps-30sec.mp4",
            subUrls = mapOf(
                "en" to "https://sample-videos.com/subtitles/sample-en.vtt",
                "hi" to "https://sample-videos.com/subtitles/sample-hi.vtt"
            ),
            playerConfig = null,
            meta = "Generated video data",
            drmType = null,
            licenceValue = null,
            headers = null
        )
    }

    override suspend fun getEpisodes(seriesId: String): List<VideoRow> {
        delay(500)
        return listOf(
            VideoRow(
                title = "Season 1",
                videos = generateMockVideos(5)
            ),
            VideoRow(
                title = "Season 2",
                videos = generateMockVideos(3, "doc")
            ),
            VideoRow(
                title = "Season 3",
                videos = generateMockVideos(4, "tech")
            )
        )
    }

    override suspend fun getSuggestions(query: String): List<VideoRow> {
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

    private fun generateMockVideos(count: Int, prefix: String = "vid"): List<VideoItem> {
        return List(count) { i ->
            VideoItem(
                id = "$prefix-$i",
                title = "Sample $prefix Video #$i",
                type = VideoItemType.Movie,
                size = Random.nextLong(10_000_000, 100_000_000),
                thumbnail = "https://placehold.co/300x200.png?text=Video$i",
                duration = Random.nextInt(100, 1000),
                meta = "Generated video data"
            )
        }
    }
}

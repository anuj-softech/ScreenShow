package com.rock.screenshow.repository.network

import android.app.appsearch.SearchResult
import com.rock.screenshow.model.PlayInfo
import com.rock.screenshow.model.VideoItem
import com.rock.screenshow.model.VideoRow
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("home")
    suspend fun fetchHomeRows(): List<VideoRow>

    @GET("search")
    suspend fun searchVideos(@Query("q") query: String): List<VideoItem>

    @GET("play")
    suspend fun getPlayInfo(@Query("id") videoId: String): PlayInfo
}
package com.rock.screenshow.repository

import android.content.Context
import com.rock.screenshow.CF
import com.rock.screenshow.model.PlayInfo
import com.rock.screenshow.model.VideoItem
import com.rock.screenshow.model.VideoRow
import com.rock.screenshow.repository.network.ApiService
import com.rock.screenshow.repository.network.MockApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.jvm.java

class MediaRepository(context: Context) {

    private val apiService: ApiService
    private val serverUrl: String

    init {
        val apiSP = context.getSharedPreferences(CF.API_SP, Context.MODE_PRIVATE)
        serverUrl = apiSP.getString(CF.SERVER_URL, "http://localhost:12566/") ?: "http://localhost:12566/"

        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
        apiService = MockApiService()
        //apiService = retrofit.create(MockApiService::class.java)
    }

    suspend fun getHomeRows(): List<VideoRow> {
        return apiService.fetchHomeRows()
    }
    suspend fun searchVideos(query: String): List<VideoItem> {
        return apiService.searchVideos(query)
    }
    suspend fun getPlayInfo(videoId: String): PlayInfo {
        return apiService.getPlayInfo(videoId)
    }
}

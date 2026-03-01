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
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MediaRepository(context: Context) {

    private val apiService: ApiService
    private val serverUrl: String
    val MOCK = true

    init {

        val apiSP = context.getSharedPreferences(CF.API_SP, Context.MODE_PRIVATE)
        serverUrl = apiSP.getString(CF.SERVER_URL, "http://localhost:12566/").toString()
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
        if (MOCK) {
            apiService = MockApiService()
        } else {
            apiService = retrofit.create(ApiService::class.java)
        }
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

    fun getPlayInfoBlocking(videoId: String): PlayInfo = runBlocking {
        getPlayInfo(videoId)
    }

}

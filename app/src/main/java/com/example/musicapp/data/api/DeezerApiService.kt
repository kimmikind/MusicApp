package com.example.musicapp.data.api

import com.example.musicapp.data.ApiTrack
import com.example.musicapp.data.SearchTracksResponse
import com.example.musicapp.data.TopTracksResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface DeezerApiService {

    // Метод для получения популярных треков
    @GET("chart")
    suspend fun getTopTracks(): Response<TopTracksResponse>

    // Метод для поиска треков по запросу
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): Response<SearchTracksResponse>

    @GET("track/{id}")
    suspend fun getTrackById(@Path("id") trackId: Long): Response<ApiTrack>
}
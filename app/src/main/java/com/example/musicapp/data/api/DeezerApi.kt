package com.example.musicapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeezerApi {

    private const val BASE_URL = "https://api.deezer.com/"

    // Инициализация Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Создание сервиса
    val retrofitService: DeezerApiService by lazy {
        retrofit.create(DeezerApiService::class.java)
    }
}
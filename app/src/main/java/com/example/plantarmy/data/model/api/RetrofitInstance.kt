package com.example.plantarmy.data.api

import com.example.plantarmy.data.model.api.PerenualApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://perenual.com/api/"

    // Erstellt die Verbindung zur API
    val api: PerenualApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Nutzt Gson für JSON
            .build()
            .create(PerenualApiService::class.java)
    }
}
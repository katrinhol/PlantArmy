package com.example.plantarmy.data.api

import com.example.plantarmy.data.model.api.PerenualApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/*object RetrofitInstance {

    private const val BASE_URL = "https://perenual.com/"


    val api: PerenualApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Nutzt Gson für JSON
            .build()
            .create(PerenualApiService::class.java)
    }
}

 */

object RetrofitInstance {

    private const val BASE_URL = "https://perenual.com/"
    // Erstellt die Verbindung zur API
    val api: PerenualApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PerenualApiService::class.java)
    }
}
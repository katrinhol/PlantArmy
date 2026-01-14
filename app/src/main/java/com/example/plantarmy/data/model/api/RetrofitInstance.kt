package com.example.plantarmy.data.api

import com.example.plantarmy.data.model.api.PerenualApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Zentrale Kotlin-Singleton-Klasse (nur einmal im Speicher erzeugt)
object RetrofitInstance {

    // Basis-URL für API-Calls
    private const val BASE_URL = "https://perenual.com/"

    // lazy -> wird erst erstellt, wenn das erste Mal benutzt (danach wiederverwendet)
    // Erstellt die Verbindung zur API
    val api: PerenualApiService by lazy {

        // Loggt Request URL, Parameter und Response-JSON
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        //OkHttp Client bauen:
        val client = OkHttpClient.Builder()
            //Interceptor: kümmert sich um Verbindung, Timeout, Caching
            .addInterceptor(logging)
            .build()

        //Retrofit bauen:
        Retrofit.Builder()
            //Base URL setzen
            .baseUrl(BASE_URL)
            //OkHttpClient setzen (Retrofit nutzt konfigurierten Client)
            .client(client)
            //JSON-Converter
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            // Erzeugung API-Interface
            .create(PerenualApiService::class.java)
    }
}
package com.example.plantarmy.data.model.api

import com.example.plantarmy.BuildConfig // WICHTIG: Damit auf den gespeicherten Key zugegriffen werden kann
import com.example.plantarmy.data.api.PerenualResponse
import retrofit2.http.GET
import retrofit2.http.Query

//Retrofit Service
interface PerenualApiService {

    // Funktion, um Pflanzenliste zu holen
    // Dokumentation: https://perenual.com/docs/api
    @GET("api/v2/species-list")
    suspend fun getPlants(
        // 1. API Key aus Konfiguration laden
        @Query("key") apiKey: String = BuildConfig.PERENUAL_API_KEY,

        // 2. Seitennummer (für Blättern durch Ergebnisse)
        @Query("page") page: Int = 1,

        // 3. Suchbegriff (z.B. "Rose")
        @Query("q") query: String? = null,

        // 4. Filter für Zimmerpflanzen (1 = ja, 0 = nein).
        // Standardmäßig auf 1, Zimmerpflanzen zuerst.
        @Query("indoor") indoor: Int = 1
    ): PerenualResponse
}
package com.example.plantarmy.data.api

import com.google.gson.annotations.SerializedName

// Die Hauptantwort der API (eine Liste von Pflanzen)
data class PerenualResponse(
    val data: List<PerenualPlantDto>
)

// Eine einzelne Pflanze, wie sie von der API kommt
data class PerenualPlantDto(
    val id: Int,
    @SerializedName("common_name") val commonName: String, // JSON heißt "common_name", Kotlin "commonName"
    @SerializedName("scientific_name") val scientificName: List<String>,
    val watering: String?, // z.B. "Average", "Frequent"
    val sunlight: Any?, // z.B. ["full sun", "part shade"]
    @SerializedName("default_image") val defaultImage: PerenualImageDto?
)

data class PerenualImageDto(
    @SerializedName("original_url") val originalUrl: String?,
    @SerializedName("regular_url") val regularUrl: String?
)
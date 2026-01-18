package com.example.plantarmy.data.api

import com.google.gson.annotations.SerializedName

// DTO (=Data Transder Object) -> reine Datenklassen, die:
// 1:1 die JSON-Struktur der API wiederspiegelt
// nicht die internen App-Modelle (Plant) sind
// nur für Netzwerk + Parsind da ist (-> später PlanTemplate)

// Hauptantwort der API - Wrapper (= Liste von Pflanzen):
data class PerenualResponse(
    val data: List<PerenualPlantDto>
)

/** M4-1: Gießintervall für Pflanzen aus Register
 * - hier werden die Intervalle aus der API übernommen
 * - diese werden in Mappers umgerechnet
 * - wenn notwendig in PlanTemplate default gesetzt
 * */

// Einzelne Pflanze, wie sie von der API kommt:
data class PerenualPlantDto(
    val id: Int, // eindeutige ID der Pflanze aus API
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
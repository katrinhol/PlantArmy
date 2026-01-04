package com.example.plantarmy.data.model

import com.example.plantarmy.data.api.PerenualPlantDto

// Diese Funktion erweitert "PerenualPlantDto" um die Fähigkeit,
// sich selbst in ein "PlantTemplate" zu verwandeln.
fun PerenualPlantDto.toPlantTemplate(): PlantTemplate {

    // Einfache Logik für Gießintervalle
    val wInterval = when (this.watering?.lowercase()) {
        "frequent" -> 3
        "average" -> 7
        "minimum" -> 14
        "none" -> 30
        else -> 7
    }

    // Sonnenlicht formatieren
    val rawSunlight = this.sunlight
    val light = when (rawSunlight) {
        // Fall 1: Es ist eine Liste (z.B. ["Full sun", "Part shade"])
        is List<*> -> rawSunlight.joinToString(", ") { it.toString() }
        // Fall 2: Es ist bereits ein Text (z.B. "Full sun")
        is String -> rawSunlight
        // Fall 3: Es ist null oder etwas anderes
        else -> "Unbekannt"
    }

    // Ein sicheres PlantTemplate zurückgeben
    return PlantTemplate(
        id = this.id,
        name = this.commonName.ifBlank { "Unbekannte Pflanze" }, // *** Fallback-Wert
        // Standard wird erster wissenschaftlicher Name oder Platzhalter gewählt
        botanicName = this.scientificName.firstOrNull() ?: "",
        defaultWateringIntervalDays = wInterval,
        defaultFertilizingIntervalDays = 14, // Standardwert
        lightRequirement = light,
        description = "Automatisch importiert von Perenual API",
        difficultyLevel = "Mittel"
    )
}
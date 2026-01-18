package com.example.plantarmy.data.model

import com.example.plantarmy.data.api.PerenualPlantDto

// Diese Funktion erweitert "PerenualPlantDto" um die Fähigkeit,
// sich selbst in ein "PlantTemplate" zu verwandeln.
fun PerenualPlantDto.toPlantTemplate(): PlantTemplate {

    /** M4-2: Gießintervall für Pflanzen aus Register
     * - Umwandlung der Beschreibung aus API in Integer
     * */

    // Einfache Logik für Gießintervalle
    val wInterval = when (this.watering?.lowercase()) {
        "frequent" -> 3
        "average" -> 7
        "minimum" -> 14
        "none" -> 30
        else -> 7
    }

// Sunlight formatieren
    val rawSunlight = this.sunlight
    val lightList: List<String> = when (rawSunlight) {
        is List<*> -> rawSunlight.filterIsInstance<String>()
        is String -> listOf(rawSunlight)
        else -> emptyList()
    }

    val light = if (lightList.isEmpty()) {
        "" // <- wichtig: leer statt "Unknown", dann wird es in Favorites nicht angezeigt
    } else {
        lightList.joinToString(", ") { s ->
            when (s.lowercase().trim()) {
                "full sun" -> "Sunny"
                "part shade", "partial shade" -> "Partial shade"
                "full shade" -> "Shade"
                "sun-part shade", "sun/part shade", "sun - part shade" -> "Sun / partial shade"
                else -> s.replaceFirstChar { it.uppercase() } // fallback: hübsch formatieren
            }
        }
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
        difficultyLevel = "Mittel" ,
                imageUrl = this.defaultImage?.regularUrl,
        wateringLevel = this.watering?.lowercase()
    )
}
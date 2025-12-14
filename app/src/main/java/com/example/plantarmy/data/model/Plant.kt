package com.example.plantarmy.data.model

import java.time.LocalDate
import java.util.UUID

/**
 * Repräsentiert eine einzelne Pflanze des Users.
 */

data class Plant(
    val id: String = UUID.randomUUID().toString(), // Automatische Eindeutige ID
    var customName: String,
    var location: String,
    val templateId: Int? = null, // Verweis auf das Template (optional)

    // Pflege-Intervalle (in Tagen)
    var wateringIntervalDays: Int,
    var fertilizingIntervalDays: Int,

    // Status-Daten
    var lastWateringDate: LocalDate? = null,
    var lastFertilizingDate: LocalDate? = null,
    var remindersEnabled: Boolean = true,
    var notes: String = "",

    // Verknüpfte Daten (Listen werden anfangs leer initialisiert)
    val photos: MutableList<Photo> = mutableListOf(),
    val careHistory: MutableList<CareAction> = mutableListOf(),

    val createdAt: LocalDate = LocalDate.now()
) {
    // Logik: Berechnet das nächste Gießdatum
    fun calculateNextWateringDate(): LocalDate {
        // Wenn noch nie gegossen wurde -> Heute fällig
        val lastDate = lastWateringDate ?: return LocalDate.now()
        return lastDate.plusDays(wateringIntervalDays.toLong())
    }

    // Logik: Berechnet das nächste Düngedatum
    fun calculateNextFertilizingDate(): LocalDate {
        val lastDate = lastFertilizingDate ?: return LocalDate.now()
        return lastDate.plusDays(fertilizingIntervalDays.toLong())
    }

    // Logik: Ist Gießen heute (oder früher) fällig?
    fun isWateringDue(): Boolean {
        val nextDate = calculateNextWateringDate()
        // true wenn Heute >= nächstes Datum
        return !LocalDate.now().isBefore(nextDate)
    }
}
package com.example.plantarmy.data.model

import java.time.LocalDate
import java.util.UUID

/**
 * Repräsentiert eine einzelne Pflanze des Users.
 */

data class Plant(
    val id: String = UUID.randomUUID().toString(), // Automatische Eindeutige ID in APP
    var customName: String,
    var location: String,
    val templateId: Int? = null,
    var imageUrl: String? = null, //

    // PFLEGE INTERVALL (in Tagen)

    /** M4-3: Gießintervall für Pflanzen aus Register
     * - Speicherung Integer als waeringInterval pro Pflanze
     * */
    var wateringIntervalDays: Int,
    var fertilizingIntervalDays: Int,

    // STATUS-DATEN
    var lastWateringDate: LocalDate? = null,
    var lastFertilizingDate: LocalDate? = null,
    var remindersEnabled: Boolean = true,
    var notes: String = "",

    //Speichert Fotos
    val photos: MutableList<Photo> = mutableListOf(),

) {
    // LOGIK: NÄCHSTES GIESSDATUM
    fun calculateNextWateringDate(): LocalDate {
        // Wenn noch nie gegossen wurde -> Heute fällig
        val lastDate = lastWateringDate ?: return LocalDate.now()
        return lastDate.plusDays(wateringIntervalDays.toLong())
    }

    // LOGIK: NÄCHSTES DÜNGEDATUM
    fun calculateNextFertilizingDate(): LocalDate {
        val lastDate = lastFertilizingDate ?: return LocalDate.now()
        return lastDate.plusDays(fertilizingIntervalDays.toLong())
    }

    /** M5-1: Benachrichtigung, sobald Intervall abläuft
     * - Check, ob Gießintervall abgelaufen ist
     * - Next: PlantReminderWorker
     * */

    // LOGIK: GIESSEN HEUTE (ODER FRÜHER) FÄLLIG
    fun isWateringDue(): Boolean {
        val nextDate = calculateNextWateringDate()
        // true wenn Heute >= nächstes Datum
        return !LocalDate.now().isBefore(nextDate)
    }

    fun isFertilizingDue(): Boolean {
        val nextDate = calculateNextFertilizingDate()
        return !LocalDate.now().isBefore(nextDate)
    }


}
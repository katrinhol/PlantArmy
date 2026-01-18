package com.example.plantarmy.data.model

import com.example.plantarmy.data.api.PerenualPlantDto

/**
 * Die Vorlage aus dem Pflanzenregister.
 */

data class PlantTemplate(
    val id: Int,
    val name: String,
    val botanicName: String,
    val defaultWateringIntervalDays: Int,
    val defaultFertilizingIntervalDays: Int,
    val lightRequirement: String,
    val description: String,
    val difficultyLevel: String,
    val imageUrl: String? = null,
    val wateringLevel: String? = null
) {
    /**
     * Erstellt eine echte Pflanze basierend auf dieser Vorlage.
     */

    fun createPlant(customName: String, location: String): Plant {
        return Plant(
            customName = customName,
            location = location,
            templateId = this.id,
            imageUrl = this.imageUrl, // Foto aus dem Register übernehmen

            /** M4-4: Gießintervall für Pflanzen aus Register
             * - Default, wenn kein Intervall vorhanden
             * -> wird in Mappers definiert
             * */

            wateringIntervalDays = this.defaultWateringIntervalDays,
            fertilizingIntervalDays = this.defaultFertilizingIntervalDays

        )
    }

}
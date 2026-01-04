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
    val difficultyLevel: String
) {
    /**
     * Erstellt eine echte Pflanze basierend auf dieser Vorlage.
     */

    fun createPlant(customName: String, location: String): Plant {
        return Plant(
            customName = customName,
            location = location,
            templateId = this.id,
            wateringIntervalDays = this.defaultWateringIntervalDays,
            fertilizingIntervalDays = this.defaultFertilizingIntervalDays
            //Description, difficulty, light requirement nicht eingebaut
        )
    }

}
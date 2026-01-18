package com.example.plantarmy.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.plantarmy.data.model.*
import com.example.plantarmy.data.repository.PlantRepository
import java.time.LocalDate

class CareActionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlantRepository(application)

    // Pflanzen, die heute gepflegt werden müssen
    var duePlants by mutableStateOf<List<DuePlant>>(emptyList())
        private set

    /** Lädt alle Pflanzen und filtert die fälligen */
    fun loadPlants() {
        val plants = repository.getAllPlants()
        val result = mutableListOf<DuePlant>()

        plants.forEach { plant ->

            if (plant.isWateringDue()) {
                result.add(
                    DuePlant(
                        plant = plant,
                        careType = CareType.WATER
                    )
                )
            }

            if (plant.isFertilizingDue()) {
                result.add(
                    DuePlant(
                        plant = plant,
                        careType = CareType.FERTILIZE
                    )
                )
            }
        }

        duePlants = result
    }

    /** Pflegeaktion durchführen */
    fun applyCareAction(duePlant: DuePlant) {

        when (duePlant.careType) {

            CareType.WATER -> {
                repository.saveCareAction(
                    WateringAction(
                        plantId = duePlant.plant.id
                    )
                )
                duePlant.plant.lastWateringDate = LocalDate.now()
            }

            CareType.FERTILIZE -> {
                repository.saveCareAction(
                    FertilizingAction(
                        plantId = duePlant.plant.id
                    )
                )
                duePlant.plant.lastFertilizingDate = LocalDate.now()
            }
        }

        // Pflanze aktualisieren
        repository.updatePlant(duePlant.plant)

        // Aus Liste entfernen (UI reagiert sofort)
        duePlants = duePlants.filterNot { it == duePlant }
    }
}

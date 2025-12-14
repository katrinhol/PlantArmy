package com.example.plantarmy.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.plantarmy.data.model.Plant
import com.example.plantarmy.data.repository.PlantRepository

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlantRepository(application)

    // Die Liste der Pflanzen, die wir anzeigen
    var plants by mutableStateOf<List<Plant>>(emptyList())
        private set

    // Lädt die Daten neu (z.B. wenn man auf den Screen zurückkommt)
    fun loadPlants() {
        plants = repository.getAllPlants()
    }
}
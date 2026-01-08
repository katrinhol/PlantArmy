package com.example.plantarmy.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantarmy.data.api.RetrofitInstance
import com.example.plantarmy.data.model.PlantTemplate
import com.example.plantarmy.data.model.toPlantTemplate
import kotlinx.coroutines.launch

class PlantRegisterViewModel : ViewModel() {

    // Zustand: Welche Pflanzen haben wir gefunden?
    var foundPlants by mutableStateOf<List<PlantTemplate>>(emptyList())
        private set

    // Zustand: Laden wir gerade?
    var isLoading by mutableStateOf(false)
        private set

    // Zustand: Gab es einen Fehler?
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Die Funktion, die vom Such-Button aufgerufen wird
    fun searchPlants(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // 1. API Aufruf (läuft im Hintergrund)
                // Wir suchen nach Zimmerpflanzen (indoor=1), 0 -> alle Pflanzen
                val response = RetrofitInstance.api.getPlants(query = query, indoor = 1)

                // 2. Umwandlung (Mapping) von API-Format zu unserem PlantTemplate
                foundPlants = response.data.map { dto ->
                    dto.toPlantTemplate()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Fehler beim Laden: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
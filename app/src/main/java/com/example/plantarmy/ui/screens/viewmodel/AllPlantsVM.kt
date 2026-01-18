package com.example.plantarmy.ui.screens.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantarmy.data.api.RetrofitInstance
import com.example.plantarmy.data.model.PlantTemplate
import com.example.plantarmy.data.model.toPlantTemplate
import kotlinx.coroutines.launch

class AllPlantsViewModel : ViewModel() {

    var plants by mutableStateOf<List<PlantTemplate>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var currentPage = 1
    private var canLoadMore = true

    var selectedWatering by mutableStateOf<String?>(null) // frequent/average/...
    var selectedSunlight by mutableStateOf<String?>(null) // full_sun/...

    fun loadFirstPage() {
        currentPage = 1
        canLoadMore = true
        plants = emptyList()
        loadMore()
    }

    fun loadMore() {
        if (isLoading || !canLoadMore) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitInstance.api.getPlants(
                    page = currentPage,
                    indoor = null,
                    watering = selectedWatering,
                    sunlight = selectedSunlight
                )

                val newItems = response.data.map { it.toPlantTemplate() }
                plants = plants + newItems

                // Wenn keine neuen Items kommen -> Ende
                if (newItems.isEmpty()) canLoadMore = false else currentPage++

            } catch (e: Exception) {
                errorMessage = "Error while loading: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
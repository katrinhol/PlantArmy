package com.example.plantarmy.ui.screens.viewmodel


import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantarmy.data.model.api.PerenualPlantDetailDto
import com.example.plantarmy.data.api.RetrofitInstance
import kotlinx.coroutines.launch

class PlantDetailsViewModel : ViewModel() {
    var detail by mutableStateOf<PerenualPlantDetailDto?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun load(id: Int) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                detail = RetrofitInstance.api.getPlantDetails(id)
            } catch (e: Exception) {
                error = "Error while loading: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
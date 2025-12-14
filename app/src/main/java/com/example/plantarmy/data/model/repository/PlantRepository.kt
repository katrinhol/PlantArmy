package com.example.plantarmy.data.repository

import android.content.Context
import com.example.plantarmy.data.model.Plant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class PlantRepository(private val context: Context) {

    private val gson = Gson()
    private val fileName = "my_plants.json"

    // Speichert eine neue Pflanze
    fun addPlant(plant: Plant) {
        val currentList = getAllPlants().toMutableList()
        currentList.add(plant)
        saveList(currentList)
    }

    // --- NEU: Pflanze aktualisieren ---
    fun updatePlant(updatedPlant: Plant) {
        val currentList = getAllPlants().toMutableList()
        // Wir suchen den Index der Pflanze mit der gleichen ID
        val index = currentList.indexOfFirst { it.id == updatedPlant.id }
        if (index != -1) {
            currentList[index] = updatedPlant
            saveList(currentList)
        }
    }

    // --- NEU: Pflanze löschen ---
    fun deletePlant(plantId: String) {
        val currentList = getAllPlants().toMutableList()
        currentList.removeAll { it.id == plantId }
        saveList(currentList)
    }

    // --- NEU: Einzelne Pflanze laden ---
    fun getPlantById(id: String): Plant? {
        return getAllPlants().find { it.id == id }
    }

    // Lädt alle Pflanzen
    fun getAllPlants(): List<Plant> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()

        return try {
            val jsonString = file.readText()
            val type = object : TypeToken<List<Plant>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun saveList(plants: List<Plant>) {
        val jsonString = gson.toJson(plants)
        val file = File(context.filesDir, fileName)
        file.writeText(jsonString)
    }
}
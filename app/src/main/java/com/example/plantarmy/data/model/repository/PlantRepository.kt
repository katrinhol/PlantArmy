package com.example.plantarmy.data.repository

import android.content.Context
import com.example.plantarmy.data.model.Plant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.time.LocalDate

class PlantRepository(private val context: Context) {

    private val gson = Gson()
    private val fileName = "my_plants.json"

    // NEUE PFLANZE SPEICHERN
    fun addPlant(plant: Plant) {
        val currentList = getAllPlants().toMutableList()
        currentList.add(plant)
        saveList(currentList)
    }

    // PFLANZE AKTUALISIEREN
    fun updatePlant(updatedPlant: Plant) {
        val currentList = getAllPlants().toMutableList()
        // Wir suchen den Index der Pflanze mit der gleichen ID
        val index = currentList.indexOfFirst { it.id == updatedPlant.id }
        if (index != -1) {
            currentList[index] = updatedPlant
            saveList(currentList)
        }
    }

    //PFLANZE LÖSCHEN
    fun deletePlant(plantId: String) {
        val currentList = getAllPlants().toMutableList()
        currentList.removeAll { it.id == plantId }
        saveList(currentList)
    }

    // EINZELNE PFLANZE LADEN
    fun getPlantById(id: String): Plant? {
        return getAllPlants().find { it.id == id }
    }

    // LALLE PFLANZEN LADEN
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

    // LISTE SPEICHERN
    private fun saveList(plants: List<Plant>) {
        val jsonString = gson.toJson(plants)
        val file = File(context.filesDir, fileName)
        file.writeText(jsonString)
    }


    fun markPlantWatered(plantId: String) {
        val plant = getPlantById(plantId) ?: return
        plant.lastWateringDate = LocalDate.now()
        updatePlant(plant)
    }

    fun markPlantFertilized(plantId: String) {
        val plant = getPlantById(plantId) ?: return
        plant.lastFertilizingDate = LocalDate.now()
        updatePlant(plant)
    }

    private val reminderPrefs =
        context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)

    private fun reminderKey(type: String, plantId: String) =
        "reminder_${type}_$plantId"

    /**
     * true = heute wurde für diese Pflanze + Typ (water/fert) schon erinnert
     */
    fun wasReminderSentToday(
        type: String,
        plantId: String,
        today: LocalDate = LocalDate.now()
    ): Boolean {
        val storedDate = reminderPrefs.getString(reminderKey(type, plantId), null)
        return storedDate == today.toString()
    }

    /**
     * merkt: heute wurde erinnert
     */
    fun markReminderSentToday(
        type: String,
        plantId: String,
        today: LocalDate = LocalDate.now()
    ) {
        reminderPrefs.edit()
            .putString(reminderKey(type, plantId), today.toString())
            .apply()
    }

    /**
     * optional: löschen (z.B. wenn gegossen bestätigt wurde)
     */
    fun clearReminder(
        type: String,
        plantId: String
    ) {
        reminderPrefs.edit()
            .remove(reminderKey(type, plantId))
            .apply()
    }


}
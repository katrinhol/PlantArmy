package com.example.plantarmy.data.repository

import android.content.Context
import com.example.plantarmy.data.model.Plant
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.time.LocalDate

class PlantRepository(private val context: Context) {

    private val gson = Gson()

    /** M9-1: lokale Speicherung in JSON
     * - Lokale Datei im App-internen Speicher
     * */
    private val fileName = "my_plants.json"

    // NEUE PFLANZE SPEICHERN (mit eindeutigem Namen)
    fun addPlant(plant: Plant) {
        val currentList = getAllPlants().toMutableList()

        //eindeutigen Namen erzeugen
        val uniqueName = generateUniqueName(plant.customName, currentList)

        val plantWithUniqueName = plant.copy(
            customName = uniqueName,

            location = plant.location.ifBlank { "Unknown" },

            wateringIntervalDays = plant.wateringIntervalDays.takeIf { it > 0 } ?: 7,
            fertilizingIntervalDays = plant.fertilizingIntervalDays.takeIf { it > 0 } ?: 14
        )

        currentList.add(plantWithUniqueName)
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

    /** M7-4: Favorites bearbeiten & löschen
     * - Pflanze dauerhaft aus Repository löschen
     * - Liste neu speichern
     * */

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

    /** M9-3: lokale Speicherung in JSON
     * - Datei wird beim Zugriff gelesen (JSON->KOTLIN)
     * */

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

    /** M9-2: lokale Speicherung in JSON
     * - Pflanzen inkl. Pflegeinformation
     * */

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

    // erzeugt einen eindeutigen Namen wie "Ficus", "Ficus (1)", "Ficus (2)"
    private fun generateUniqueName(baseName: String, plants: List<Plant>): String {
        val existingNames = plants.map { it.customName }

        if (baseName !in existingNames) {
            return baseName
        }

        var counter = 1
        var newName: String

        do {
            newName = "$baseName ($counter)"
            counter++
        } while (newName in existingNames)

        return newName
    }


}
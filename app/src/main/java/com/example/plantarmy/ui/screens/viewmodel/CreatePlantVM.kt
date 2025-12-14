package com.example.plantarmy.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.plantarmy.data.model.Photo
import com.example.plantarmy.data.model.Plant
import com.example.plantarmy.data.repository.PlantRepository

class CreatePlantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlantRepository(application)

    // Merkt sich die ID, falls wir bearbeiten (null = neue Pflanze)
    private var currentPlantId: String? = null

    // Formular-Daten (State für die Eingabefelder)
    var name by mutableStateOf("")
    var location by mutableStateOf("")
    var wateringInterval by mutableStateOf("7")
    var fertilizingInterval by mutableStateOf("14")
    var selectedImageUri by mutableStateOf<Uri?>(null)

    // --- NEU: Initialisierung ---
    // Diese Funktion wird vom Screen aufgerufen, sobald er startet.
    // Wenn eine ID übergeben wird, laden wir die Daten der Pflanze in die Felder.
    fun init(plantId: String?) {
        currentPlantId = plantId

        if (plantId != null) {
            // MODUS: BEARBEITEN -> Daten aus Repository laden
            val plant = repository.getPlantById(plantId)
            if (plant != null) {
                name = plant.customName
                location = plant.location
                wateringInterval = plant.wateringIntervalDays.toString()
                fertilizingInterval = plant.fertilizingIntervalDays.toString()

                // Bild laden: String-URI zurück in Uri-Objekt wandeln
                val photoStr = plant.photos.firstOrNull()?.uri
                selectedImageUri = if (photoStr != null) Uri.parse(photoStr) else null
            }
        } else {
            // MODUS: NEU ERSTELLEN -> Felder leeren
            // Wichtig, weil das ViewModel im Speicher bleiben kann
            resetFields()
        }
    }

    // Hilfsfunktion zum Leeren der Felder
    private fun resetFields() {
        name = ""
        location = ""
        wateringInterval = "7"
        fertilizingInterval = "14"
        selectedImageUri = null
        currentPlantId = null
    }

    // Prüft, ob alles ausgefüllt ist
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                wateringInterval.toIntOrNull() != null &&
                fertilizingInterval.toIntOrNull() != null
    }

    // Speichern (entscheidet selbst ob Neu oder Update)
    fun savePlant() {
        if (!isValid()) return

        val wInt = wateringInterval.toIntOrNull() ?: 7
        val fInt = fertilizingInterval.toIntOrNull() ?: 14

        // Foto-Liste erstellen
        val photosList = mutableListOf<Photo>()
        selectedImageUri?.let { uri ->
            photosList.add(Photo(uri = uri.toString()))
        }

        if (currentPlantId == null) {
            // FALL A: Neue Pflanze anlegen
            val newPlant = Plant(
                customName = name,
                location = location,
                wateringIntervalDays = wInt,
                fertilizingIntervalDays = fInt,
                photos = photosList
            )
            repository.addPlant(newPlant)
        } else {
            // FALL B: Bestehende Pflanze aktualisieren
            // Wir laden das Original, um ID und Erstell-Datum zu behalten
            val oldPlant = repository.getPlantById(currentPlantId!!) ?: return

            // Wir erstellen eine Kopie mit den neuen Werten
            val updatedPlant = oldPlant.copy(
                customName = name,
                location = location,
                wateringIntervalDays = wInt,
                fertilizingIntervalDays = fInt,
                photos = photosList // Achtung: Überschreibt alte Fotos mit dem neuen
            )
            repository.updatePlant(updatedPlant)
        }
    }

    // --- NEU: Löschen ---
    fun deletePlant() {
        currentPlantId?.let { id ->
            repository.deletePlant(id)
        }
    }
}
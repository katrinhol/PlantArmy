package com.example.plantarmy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // Wichtig!
import com.example.plantarmy.data.model.PlantTemplate
import com.example.plantarmy.ui.viewmodel.PlantRegisterViewModel

@Composable
fun PlantRegisterScreen(
    // Das ViewModel wird hier automatisch erstellt oder geholt
    viewModel: PlantRegisterViewModel = viewModel(),
    onBack: () -> Unit // Funktion um zurück zu gehen
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // --- 1. Überschrift & Suchleiste ---
        Text("Pflanzenregister durchsuchen", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Suche (z.B. Rose, Ficus)") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.searchPlants(searchQuery) },
                enabled = !viewModel.isLoading
            ) {
                Icon(Icons.Default.Search, contentDescription = "Suchen")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. Ladebalken, Fehler oder Liste ---
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.errorMessage != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = "Fehler", tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = viewModel.errorMessage ?: "", color = Color.Red)
            }
        } else {
            // Die Liste der Ergebnisse
            LazyColumn {
                items(viewModel.foundPlants) { plantTemplate ->
                    PlantListItem(plantTemplate)
                }
            }
        }

        // Button um zurück zu gehen
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = onBack) {
            Text("Zurück zum Hauptmenü")
        }
    }
}

// Eine einzelne Zeile in der Liste
@Composable
fun PlantListItem(plant: PlantTemplate) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = plant.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Botanisch: ${plant.botanicName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                SuggestionChip(onClick = {}, label = { Text("Gießen: Alle ${plant.defaultWateringIntervalDays} Tage") })
                Spacer(modifier = Modifier.width(8.dp))
                SuggestionChip(onClick = {}, label = { Text("Licht: ${plant.lightRequirement}") })
            }
        }
    }
}
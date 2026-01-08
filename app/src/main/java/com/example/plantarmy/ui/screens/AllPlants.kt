package com.example.plantarmy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantarmy.data.model.PlantTemplate
import com.example.plantarmy.ui.screens.viewmodel.AllPlantsViewModel
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPlantsScreen(
    onPlantClick: (Int) -> Unit,
    viewModel: AllPlantsViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadFirstPage() }

    val wateringOptions = listOf(null, "frequent", "average", "minimum", "none")
    val sunlightOptions = listOf(null, "full_shade", "part_shade", "sun-part_shade", "full_sun")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pflanzenbibliothek", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        // Filter Row
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            FilterDropdown(
                label = "Watering",
                value = viewModel.selectedWatering,
                options = wateringOptions,
                onSelect = { viewModel.selectedWatering = it }
            )
            FilterDropdown(
                label = "Sunlight",
                value = viewModel.selectedSunlight,
                options = sunlightOptions,
                onSelect = { viewModel.selectedSunlight = it }
            )
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { viewModel.loadFirstPage() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Filter anwenden") }

        Spacer(Modifier.height(12.dp))

        if (viewModel.errorMessage != null) {
            Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.plants) { plant ->
                AllPlantRow(plant = plant, onClick = { onPlantClick(plant.id) })
            }
        }

        Button(
            onClick = { viewModel.loadMore() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (viewModel.isLoading) "Lädt..." else "Load more")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    value: String?,
    options: List<String?>,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value ?: "Alle",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.width(170.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt ?: "Alle") },
                    onClick = {
                        onSelect(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AllPlantRow(plant: PlantTemplate, onClick: () -> Unit) {
    // Du kannst hier 1:1 deine Register-Row-Komponente verwenden (mit Bild)
    PlantListItem(plant = plant, onClick = onClick)
}
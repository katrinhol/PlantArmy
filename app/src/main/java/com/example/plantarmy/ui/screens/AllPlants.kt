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
//import androidx.compose.material3.ExposedDropdownMenuBox
//import androidx.compose.material3.DropdownMenuItem
//import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
//import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width


private val sunlightLabelMap = mapOf(
    null to "All",
    "full_sun" to "🌞 Full sun",
    "sun-part_shade" to "🌤️ Sun / partial shade",
    "part_shade" to "🌥️ Partial shade",
    "full_shade" to "🌑 Shade"
)

private val wateringLabelMap = mapOf(
    null to "All",
    "frequent" to "💧 Frequent",
    "average" to "🚿 Average",
    "minimum" to "🌵 Low"
)


@Composable
fun AllPlantsScreen(
    onPlantClick: (Int) -> Unit,
    viewModel: AllPlantsViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.loadFirstPage() }

    val wateringOptions = listOf(null, "frequent", "average", "minimum")
    val sunlightOptions = listOf(null, "full_shade", "part_shade", "sun-part_shade", "full_sun")

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Plant Library", style = MaterialTheme.typography.headlineSmall)
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
        ) { Text("Apply filters") }

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
            Text(if (viewModel.isLoading) "Loading..." else "Load more")
        }
    }
}

@Composable
private fun FilterDropdown(
    label: String,
    value: String?,
    options: List<String?>,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.width(170.dp)
        ) {
            val displayValue = when (label) {
                "Sunlight" -> sunlightLabelMap[value] ?: (value ?: "All")
                "Watering" -> wateringLabelMap[value] ?: (value ?: "All")
                else -> value ?: "All"
            }

            Text(text = "$label: $displayValue")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = {
                        val displayOpt = when (label) {
                            "Sunlight" -> sunlightLabelMap[opt] ?: (opt ?: "All")
                            "Watering" -> wateringLabelMap[opt] ?: (opt ?: "All")
                            else -> opt ?: "All"
                        }
                        Text(displayOpt)
                    },
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
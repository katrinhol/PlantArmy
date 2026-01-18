package com.example.plantarmy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.plantarmy.ui.screens.viewmodel.PlantDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailsScreen(
    speciesId: Int,
    onBack: () -> Unit,
    viewModel: PlantDetailsViewModel = viewModel()
) {

    /** -----------------------------------------------------
     * M3: Laden der Detailinformationen einer Pflanze aus dem Register
     * ----------------------------------------------------- */

    LaunchedEffect(speciesId) {
        viewModel.load(speciesId)
    }

    Scaffold(

        /** -----------------------------------------------------
         * TopBar mit Zurück-Navigation
         * (Teil der Register-Navigation)
         * ----------------------------------------------------- */

        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }

    ) { inner ->

        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            /** -------------------------------
            * Lade- und Fehlerzustände
            ------------------------------------ */

            if (viewModel.isLoading) {
                CircularProgressIndicator()
                return@Column
            }

            if (viewModel.error != null) {
                Text(
                    viewModel.error!!,
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            /** -------------------------------
             Detaildaten der Pflanze
            ----------------------------------- */
            val d = viewModel.detail ?: return@Column

            // Pflanzenbild aus dem Register (API)
            AsyncImage(
                model = d.defaultImage?.regularUrl,
                contentDescription = d.commonName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Name & botanischer Name
            Text(
                d.commonName ?: "Unknown",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                d.scientificName?.firstOrNull() ?: "",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(12.dp))

            /** -------------------------------
             M3: Grundlegende Pflegeinfos
            ------------------------------------ */

            // Wasserbedarf (inkl. Umrechnung in Tage)
            val levelRaw = d.watering
            val level = levelRaw?.lowercase()?.trim() ?: "unknown"

            val days = when (level) {
                "frequent" -> 3
                "average" -> 7
                "minimum", "low" -> 14
                else -> null
            }

            val intervalText = days?.let { " (every $it days)" } ?: ""

            // Lichtverhältnisse aus API (Liste → String)
            val sunlightText = when (val s = d.sunlight) {
                is List<*> -> s.filterNotNull().joinToString(", ").ifBlank { "Unknown" }
                is String -> s.ifBlank { "Unknown" }
                else -> "Unknown"
            }

            /** -------------------------------
             Anzeige der Register-Infos
            ------------------------------------ */

            Text("Watering: ${levelRaw ?: "Unknown"}$intervalText")
            Text("Sunlight: $sunlightText")
            Text("Cycle: ${d.cycle ?: "Unknown"}")
            Text("Type: ${d.type ?: "Unknown"}")
            Text("Family: ${d.family ?: "Unknown"}")
            Text("Origin: ${d.origin?.joinToString(", ") ?: "Unknown"}")
        }
    }
}

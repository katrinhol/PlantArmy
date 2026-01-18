package com.example.plantarmy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip

import com.example.plantarmy.data.model.PlantTemplate
import com.example.plantarmy.data.repository.PlantRepository
import com.example.plantarmy.ui.viewmodel.PlantRegisterViewModel

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.indication
import androidx.compose.material.ripple.rememberRipple

/**
 * =====================================================
 * M1 – Pflanze aus Pflanzenregister anlegen
 * =====================================================
 *
 * - Dieser Screen zeigt ein Pflanzenregister (API-basiert).
 * - Der Nutzer kann nach Pflanzen suchen.
 * - Durch Klick auf einen Eintrag wird eine Pflanze
 *   aus dem Register in die eigene Sammlung übernommen.
 * - Aufruf erfolgt über den Button "Pflanze anlegen".
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantRegisterScreen(
    viewModel: PlantRegisterViewModel = viewModel(),
    onBack: () -> Unit,
    onPlantAdded: () -> Unit
) {

    /** --------------------------------------------------------------------------------------------------------------------------
     * STATE & DEPENDENCIES
     * ---------------------------------------------------------------------------------------------------------------------------
     * */

    var searchQuery by remember { mutableStateOf("") }

    // Android Context für Repository-Zugriff
    val context = LocalContext.current

    // Repository nur einmal erstellen (nicht bei jedem Recompose)
    val plantRepo = remember { PlantRepository(context) }

    /** ------------------------------------------------------------------------------------------------------------------------
     * GRUNDLAYOUT MIT TOPBAR
     * -------------------------------------------------------------------------------------------------------------------------
     * */

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Browse plant library") },

                // Zurück-Navigation
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            /** -----------------------------------------------------------------------------------------------------------------
             * 1. SUCHLEISTE
             * ------------------------------------------------------------------------------------------------------------------
             *
             * Nutzer gibt einen Suchbegriff ein (z. B. Rose).
             * Klick auf Such-Button startet API-Abfrage.
             *
             */

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search (e.g. Rose, Ficus)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.searchPlants(searchQuery) },
                    enabled = !viewModel.isLoading
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search plants"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /** -------------------------------------------------------------------------------------------------------------
             * 2. LADEZUSTÄNDE / FEHLER / ERGEBNISSE
             * --------------------------------------------------------------------------------------------------------------
             * */
            when {

                // Ladezustand während API-Abfrage
                viewModel.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Fehleranzeige (z. B. Netzwerkfehler)
                viewModel.errorMessage != null -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = Color.Red
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = viewModel.errorMessage ?: "",
                            color = Color.Red
                        )
                    }
                }

                // Ergebnisliste aus dem Pflanzenregister
                else -> {
                    LazyColumn {

                        items(viewModel.foundPlants) { plantTemplate ->

                            PlantListItem(
                                plant = plantTemplate,

                                /** ===============================================================================================
                                 * M1: Pflanze aus Pflanzenregister anlegen
                                 * ================================================================================================
                                 * - Klick auf Register-Eintrag
                                 * - Template wird in echte Pflanze umgewandelt
                                 * - Pflanze wird im Repository gespeichert
                                **/

                                    onClick = {
                                    val newPlant = plantTemplate.createPlant(
                                        customName = plantTemplate.name,
                                        location = plantTemplate.lightRequirement
                                    )
                                    plantRepo.addPlant(newPlant)

                                    //Nach Auswahl direkt zu Favorites
                                    onPlantAdded()
                                }

                            )
                        }
                    }
                }
            }
        }
    }
}


/** ===========================================================================================================================
 * EINZELNER LISTENEINTRAG IM PFLANZENREGISTER
 * ============================================================================================================================
 *
 * - Stellt eine Pflanze aus der API dar
 * - Klickbar → wird zur eigenen Pflanze hinzugefügt
 *
 */

@Composable
fun PlantListItem(
    plant: PlantTemplate,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Card(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /** -------------------------------------------------------------------------------------------------------------------
             * Pflanzenbild
             * --------------------------------------------------------------------------------------------------------------------
             * */

            AsyncImage(
                model = plant.imageUrl,
                contentDescription = plant.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            /** -----------------------------------------------------------------------------------------------------------------
             * Textinformationen
             * ------------------------------------------------------------------------------------------------------------------
             * */

            Column {
                Text(
                    text = plant.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Botanical name: ${plant.botanicName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
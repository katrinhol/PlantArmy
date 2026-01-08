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
    LaunchedEffect(speciesId) { viewModel.load(speciesId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier.padding(inner).padding(16.dp).verticalScroll(rememberScrollState())
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator()
                return@Column
            }
            if (viewModel.error != null) {
                Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
                return@Column
            }

            val d = viewModel.detail ?: return@Column

            AsyncImage(
                model = d.defaultImage?.regularUrl,
                contentDescription = d.commonName,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(d.commonName ?: "Unbekannt", style = MaterialTheme.typography.headlineSmall)
            Text(d.scientificName?.firstOrNull() ?: "", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(12.dp))

            Text("Watering: ${d.watering ?: "Unbekannt"}")
            Text("Cycle: ${d.cycle ?: "Unbekannt"}")
            Text("Type: ${d.type ?: "Unbekannt"}")
            Text("Family: ${d.family ?: "Unbekannt"}")
            Text("Origin: ${d.origin?.joinToString(", ") ?: "Unbekannt"}")
        }
    }
}
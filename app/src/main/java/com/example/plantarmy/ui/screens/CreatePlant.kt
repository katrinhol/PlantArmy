package com.example.plantarmy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.plantarmy.ui.viewmodel.CreatePlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlantScreen(
    plantIdToEdit: String? = null, // NEU: ID wird übergeben (null = neu)
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: CreatePlantViewModel = viewModel()
) {
    // INIT: Wenn der Screen startet, laden wir die Daten (oder leeren sie)
    LaunchedEffect(plantIdToEdit) {
        viewModel.init(plantIdToEdit)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.selectedImageUri = uri }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                // Titel ändert sich je nach Modus
                title = { Text(if (plantIdToEdit == null) "Pflanze erstellen" else "Pflanze bearbeiten") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                },
                // NEU: Löschen Button nur im Bearbeiten-Modus
                actions = {
                    if (plantIdToEdit != null) {
                        IconButton(onClick = {
                            viewModel.deletePlant()
                            onSaveSuccess() // Wir gehen zurück nach dem Löschen
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = Color.Red)
                        }
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. Foto Auswahl ---
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.selectedImageUri != null) {
                    AsyncImage(
                        model = viewModel.selectedImageUri,
                        contentDescription = "Pflanzenbild",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                        Text(if(plantIdToEdit != null) "Ändern" else "Foto", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. Eingabefelder ---
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Name der Pflanze") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.location,
                onValueChange = { viewModel.location = it },
                label = { Text("Standort / Lichtbedarf") },
                placeholder = { Text("z.B. Sonnig, Fensterbank") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = viewModel.wateringInterval,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.wateringInterval = it },
                    label = { Text("Gießen (Tage)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = viewModel.fertilizingInterval,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.fertilizingInterval = it },
                    label = { Text("Düngen (Tage)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. Speichern Button ---
            Button(
                onClick = {
                    viewModel.savePlant()
                    onSaveSuccess()
                },
                enabled = viewModel.isValid(),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Speichern")
            }
        }
    }
}
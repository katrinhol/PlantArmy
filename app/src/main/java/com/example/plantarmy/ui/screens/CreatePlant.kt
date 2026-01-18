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


/**
 * =====================================================
 * M2 – Pflanze manuell erstellen / bearbeiten
 * =====================================================
 *
 * - Dieser Screen ermöglicht das manuelle Erstellen
 *   und Bearbeiten einer Pflanze.
 * - Der Screen wird über den "+" Button aufgerufen.
 * - Alle relevanten Pflanzendaten können hier eingegeben werden.
 *
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlantScreen(
    plantIdToEdit: String? = null, // null = neue Pflanze | ID = bestehende Pflanze bearbeiten
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: CreatePlantViewModel = viewModel()
) {

    /** -----------------------------------------------------
     * INITIALISIERUNG
     * -----------------------------------------------------
     *
     * Beim Öffnen des Screens:
     * - neue Pflanze → Felder leeren
     * - bestehende Pflanze → Daten laden
     *
     */

    /** M6-4: Favorites bearbeiten & löschen
     * - Pflanze laden + Felder editierbar machen
     * */

    /** M7-2: Favorites bearbeiten & löschen
     * - Pflanze laden + Felder editierbar machen
     * */

    LaunchedEffect(plantIdToEdit) {
        viewModel.init(plantIdToEdit)
    }

    /** -----------------------------------------------------
     * FOTO-AUSWAHL (Activity Result Launcher)
     * -----------------------------------------------------
     *
     * Ermöglicht das Auswählen eines Pflanzenfotos
     * aus der Galerie des Geräts.
     *
     */

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            viewModel.selectedImageUri = uri
        }
    )

    /** -----------------------------------------------------
     * GRUNDLAYOUT MIT TOPBAR
     * ----------------------------------------------------- */

    Scaffold(
        topBar = {
            TopAppBar(

                // Titel abhängig vom Modus (Neu / Bearbeiten)
                title = {
                    Text(
                        if (plantIdToEdit == null)
                            "Create plant"
                        else
                            "Edit plant"
                    )
                },

                // Zurück (Pfeil) -Navigation
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurück")
                    }
                },

                /** M7-3: Favorites bearbeiten & löschen
                 * - Button zum löschen der Pflanze
                 * */

                // Löschen-Button nur im Bearbeiten-Modus
                actions = {
                    if (plantIdToEdit != null) {
                        IconButton(onClick = {
                            viewModel.deletePlant()
                            onSaveSuccess()
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete plant",
                                tint = Color.Red
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->

        /** -----------------------------------------------------
         * HAUPTINHALT
         * ----------------------------------------------------- */

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /** -------------------------------------------------
             * 1. FOTOSEKTION
             * -------------------------------------------------
             * Klickbarer Bereich:
             * - zeigt ausgewähltes Bild
             * - oder "+" Icon zum Hinzufügen
             */


            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray)
                    .clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.selectedImageUri != null) {
                    AsyncImage(
                        model = viewModel.selectedImageUri,
                        contentDescription = "Plant picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        // M2: "+" Button zum manuellen Erstellen einer Pflanze
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Create plant manually",
                            tint = Color.White
                        )

                        Text(
                            if (plantIdToEdit != null) "Edit" else "Picture",
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            /** -------------------------------------------------
             * 2. TEXTEINGABEN (Name & Standort)
             * ------------------------------------------------- */
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Name of plant") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.location,
                onValueChange = { viewModel.location = it },
                label = { Text("Location / Sunlight") },
                placeholder = { Text("e.g. Sunny, window sill") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            /** -------------------------------------------------
             * 3. INTERVALL-EINGABEN (Gießen & Düngen)
             * ------------------------------------------------- */

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.wateringInterval,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() })
                            viewModel.wateringInterval = it
                    },
                    label = { Text("Watering (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = viewModel.fertilizingInterval,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() })
                            viewModel.fertilizingInterval = it
                    },
                    label = { Text("Fertilize (days)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            /** -------------------------------------------------
             * 4. SPEICHERN
             * -------------------------------------------------
             * M2: Manuelle Pflanzenerstellung abschließen
             */

            Button(
                onClick = {
                    viewModel.savePlant() // M2: manuelle Pflanzenerstellung
                    onSaveSuccess()
                },
                enabled = viewModel.isValid(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save")
            }
        }
    }
}

package com.example.plantarmy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.plantarmy.data.model.Plant
import com.example.plantarmy.data.repository.PlantRepository
import com.example.plantarmy.ui.viewmodel.FavoritesViewModel
import java.time.LocalDate

@Composable
fun FavoritesScreen(
    highlightPlantId: String? = null,
    viewModel: FavoritesViewModel = viewModel(),
    onPlantClick: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    // Dialog-State
    var confirmWaterPlant by remember { mutableStateOf<Plant?>(null) }

    val context = LocalContext.current

    val repo = remember { PlantRepository(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Meine Favoriten",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.plants.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Noch keine Pflanzen angelegt", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(viewModel.plants) { plant ->
                    val isHighlighted = plant.id == highlightPlantId
                    val isWateringDue = plant.isWateringDue() // nutzt deine Plant-Logik

                    FavoritePlantItem(
                        plant = plant,
                        highlighted = isHighlighted,
                        showDueBadge = isHighlighted && isWateringDue,
                        onClick = {
                            // Wenn Pflanze aus Notification kommt und wirklich fällig ist:
                            if (isHighlighted && isWateringDue) {
                                confirmWaterPlant = plant
                            } else {
                                onPlantClick(plant.id)
                            }
                        }
                    )
                }
            }
        }
    }

    // ✅ Dialog "Gießen bestätigen"
    if (confirmWaterPlant != null) {
        val plant = confirmWaterPlant!!

        AlertDialog(
            onDismissRequest = { confirmWaterPlant = null },
            title = { Text("Gießen bestätigen") },
            text = { Text("Hast du „${plant.customName}“ gerade gegossen?") },
            confirmButton = {
                TextButton(onClick = {
                    // lastWateringDate = heute setzen & speichern
                    repo.markPlantWatered(plant.id)
                    confirmWaterPlant = null
                    viewModel.loadPlants()
                }) {
                    Text("Ja, gegossen")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmWaterPlant = null }) {
                    Text("Abbrechen")
                }
            }
        )
    }
}

@Composable
fun FavoritePlantItem(
    plant: Plant,
    highlighted: Boolean = false,
    showDueBadge: Boolean = false,
    onClick: () -> Unit
) {
    val borderColor = if (highlighted) Color(0xFFFFD54F) else Color.Transparent
    val borderWidth = if (highlighted) 2.dp else 0.dp

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1) Bild
            val imageUri = plant.photos.firstOrNull()?.uri

            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto von ${plant.customName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2) Text links
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.customName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                if (plant.location.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plant.location,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // ✅ Badge wenn aus Notification und fällig
                if (showDueBadge) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "FÄLLIG: Gießen",
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3) Intervalle rechts
            Column(horizontalAlignment = Alignment.End) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.InvertColors,
                        contentDescription = "Gießen",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${plant.wateringIntervalDays} Tage",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.InvertColors,
                        contentDescription = "Düngen",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${plant.fertilizingIntervalDays} Tage",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
